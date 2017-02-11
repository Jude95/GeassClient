package com.jude.geassclient;

import android.util.Log;

import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by zhuchenxi on 16/11/5.
 */

public class Shell {
    public static final String TAG = "Shell";

    private static final String LD_LIBRARY_PATH = System.getenv("LD_LIBRARY_PATH");

    private final Process shellProcess;
    public BufferedSource source;
    public BufferedSink sink;

    private Shell(String shell)
            throws IOException {

        Util.Log("Shell Starting");
        // start shell process!
        shellProcess = new ProcessBuilder(shell).start();

        source = Okio.buffer(Okio.source(shellProcess.getInputStream()));
        sink = Okio.buffer(Okio.sink(shellProcess.getOutputStream()));

        Util.Log("Shell Started");
    }


    /**
     * Destroy shell process considering that the process could already be
     * terminated
     */
    public void destroy() {
        try {
            // Yes, this really is the way to check if the process is
            // still running.
            shellProcess.exitValue();
        } catch (IllegalThreadStateException e) {
            // Only call destroy() if the process is still running;
            // Calling it for a terminated process will not crash, but
            // (starting with at least ICS/4.0) spam the log with INFO
            // messages ala "Failed to destroy process" and "kill
            // failed: ESRCH (No such process)".
            shellProcess.destroy();
        }
        Log.d(TAG, "Shell Destroyed");
    }


    public static Shell startRootShell() throws IOException {
        return new Shell("su");
    }

    public static Shell startNormalShell() throws IOException {
        return new Shell("sh");
    }
}
