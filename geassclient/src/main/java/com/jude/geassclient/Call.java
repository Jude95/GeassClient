package com.jude.geassclient;

import java.io.IOException;

/**
 * Created by zhuchenxi on 16/11/4.
 */

public class Call {
    private final GeassClient client;
    private Command command;
    private CommandEngine engine;
    // Guarded by this.
    private boolean executed;
    volatile boolean canceled;

    public Call(GeassClient client, Command command) {
        this.client = client;
        this.command = command;
    }

    public void enqueue(Callback responseCallback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        client.getDispatcher().enqueue(new AsyncCall(responseCallback));
    }


    public Response execute() throws IOException {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        try {
            client.getDispatcher().executed(this);
            Response result = process();
            if (result == null) throw new IOException("Canceled");
            return result;
        } finally {
            client.getDispatcher().finished(this);
        }
    }

    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Cancels the command, if possible. Commands that are already complete
     * cannot be canceled.
     */
    public void cancel() {
        canceled = true;
        if (engine != null) engine.cancel();
    }

    /**
     * process the command
     * @return the response of command
     * @throws IOException
     */
    private Response process() throws IOException{
        if (canceled){
            throw new IOException("Canceled");
        }
        engine = new CommandEngine(client,command);
        engine.write();
        engine.read();
        return engine.getResponse();
    }


    final class AsyncCall extends NamedRunnable {
        private final Callback responseCallback;

        private AsyncCall(Callback responseCallback) {
            super("GeassThread %s", command.getCommand());
            this.responseCallback = responseCallback;
        }

        Command command() {
            return command;
        }

        void cancel() {
            Call.this.cancel();
        }

        Call get() {
            return Call.this;
        }

        @Override
        protected void execute() {
            boolean signalledCallback = false;
            try {
                Response response = process();
                if (canceled) {
                    signalledCallback = true;
                    responseCallback.onFailure(command, new IOException("Canceled"));
                } else {
                    signalledCallback = true;
                    responseCallback.onResponse(response);
                }
            } catch (IOException e) {
                if (signalledCallback) {
                    // Do not signal the callback twice!
                } else {
                    responseCallback.onFailure(command, e);
                }
            } finally {
                client.getDispatcher().finished(this);
            }
        }
    }
}
