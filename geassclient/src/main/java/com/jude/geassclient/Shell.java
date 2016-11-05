package com.jude.geassclient;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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

    private Shell(String shell, ArrayList<String> customEnv, String baseDirectory)
            throws IOException {
        Util.Log("Shell Start");

        // start shell process!
        shellProcess = runWithEnv(shell, customEnv, baseDirectory);

        source = Okio.buffer(Okio.source(shellProcess.getInputStream()));
        sink = Okio.buffer(Okio.sink(shellProcess.getOutputStream()));
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
        return startRootShell(null,null);
    }
    /**
     * Start root shell
     *
     * @param customEnv
     * @param baseDirectory
     * @return
     * @throws IOException
     */
    public static Shell startRootShell(ArrayList<String> customEnv, String baseDirectory)
            throws IOException {
        String path = "su";
        // On some versions of Android (ICS) LD_LIBRARY_PATH is unset when using
        // su
        // We need to pass LD_LIBRARY_PATH over su for some commands to work
        // correctly.
        if (customEnv == null) {
            customEnv = new ArrayList<String>();
        }
        customEnv.add("LD_LIBRARY_PATH=" + LD_LIBRARY_PATH);
        return new Shell(path, customEnv, baseDirectory);
    }

    public static Process runWithEnv(String command, ArrayList<String> customAddedEnv,
                                     String baseDirectory) throws IOException {

        Map<String, String> environment = System.getenv();
        String[] envArray = new String[environment.size()
                + (customAddedEnv != null ? customAddedEnv.size() : 0)];
        int i = 0;
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            envArray[i++] = entry.getKey() + "=" + entry.getValue();
        }
        if (customAddedEnv != null) {
            for (String entry : customAddedEnv) {
                envArray[i++] = entry;
            }
        }

        Process process;
        if (baseDirectory == null) {
            process = Runtime.getRuntime().exec(command, envArray, null);
        } else {
            process = Runtime.getRuntime().exec(command, envArray, new File(baseDirectory));
        }
        return process;
    }
}
