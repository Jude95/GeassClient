package com.jude.geassclient;

import android.util.Log;

import java.util.concurrent.ThreadFactory;

/**
 * Created by zhuchenxi on 16/11/5.
 */

public class Util {
    public static boolean DEBUG = false;
    static final String TAG = "GeassClient";
    static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    static void Log(String content){
        if (DEBUG){
            Log.i(TAG,content);
        }
    }
}
