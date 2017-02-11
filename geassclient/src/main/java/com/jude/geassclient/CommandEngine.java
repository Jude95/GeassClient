package com.jude.geassclient;

import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * Created by zhuchenxi on 16/11/5.
 */

public class CommandEngine {
    private static final String END = "geassclient*D^@#F";

    private final GeassClient client;
    private Command command;
    private Response response;
    private Shell shell;
    private BufferedSource source;
    private BufferedSink sink;

    public CommandEngine(GeassClient client, Command command) throws IOException {
        this.client = client;
        this.command = command;
        this.shell = client.getShellPool().get();
        source = shell.source;
        sink = shell.sink;
    }

    void write() throws IOException{
        Util.Log("write command "+command+" start");
        String sb = command.getCommand() + " 2>&1\n";
        sink.write(sb.getBytes());
        sink.write(("\necho "+END+" $?\n").getBytes()); //使得输出最后一行为 "{END} {resultCode}"
        sink.flush();
        Util.Log("write command "+command+" finish");
    }

    void read() throws IOException{
        Util.Log("read command "+command+" command start");
        response = new Response(command);
        StringBuilder contentBuilder = new StringBuilder();
        while (true) {
            String line = source.readUtf8Line();
            // terminate on EOF
            if (line == null) {
                throw new IOException("Unexpected Termination!");
            }
            int pos = line.indexOf(END);
            if (pos >= 0) { // 读取到命令结束标记
                line = line.substring(pos);
                String[] fields = line.split(" ");
                if (fields.length > 1) {
                    int exitCode = -1;
                    try {
                        exitCode = Integer.parseInt(fields[1]);
                    } catch (Exception e) {
                    }
                    response.resultCode = exitCode;
                }
                break;
            }
            contentBuilder.append(line).append("\n");
        }
        response.result = contentBuilder.toString();
        finish();
        Util.Log("read command "+command+" command finish.Response:"+contentBuilder.toString());
    }

    private void finish(){
        client.getShellPool().put(shell);
        shell = null;
    }

    public Response getResponse() {
        return response;
    }

    /**
     * 取消本次命令,没办法直接取消,只有强行结束当前Shell
     */
    void cancel(){
        shell.destroy();
        Util.Log("cancel command "+command);
    }
}
