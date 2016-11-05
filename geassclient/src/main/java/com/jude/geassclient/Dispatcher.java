package com.jude.geassclient;

import com.jude.geassclient.Call.AsyncCall;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhuchenxi on 16/11/4.
 */

public class Dispatcher {
    private int maxRequests = 4;

    /** Executes calls. Created lazily. */
    private ExecutorService executorService;

    /** Ready calls in the order they'll be run. */
    private final Deque<AsyncCall> readyCalls = new ArrayDeque<>();

    /** Running calls. Includes canceled calls that haven't finished yet. */
    private final Deque<AsyncCall> runningCalls = new ArrayDeque<>();

    /** In-flight synchronous calls. Includes canceled calls that haven't finished yet. */
    private final Deque<Call> executedCalls = new ArrayDeque<>();

    public Dispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Dispatcher() {
    }

    public synchronized ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
        }
        return executorService;
    }

    /**
     * 设置最大并发数
     * @param maxRequests
     */
    public synchronized void setMaxRequests(int maxRequests) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }
        this.maxRequests = maxRequests;
        promoteCalls();
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    synchronized void enqueue(AsyncCall call) {
        if (runningCalls.size() < maxRequests) {
            runningCalls.add(call);
            getExecutorService().execute(call);
            Util.Log("enqueue call,start call this");
        } else {
            readyCalls.add(call);
            Util.Log("enqueue call,offer to queue");
        }
    }

    synchronized void executed(Call call) {
        executedCalls.add(call);
        Util.Log("executed call");
    }

    /** Used by {@code AsyncCall#run} to signal completion. */
    synchronized void finished(AsyncCall call) {
        if (!runningCalls.remove(call)) throw new AssertionError("AsyncCall wasn't running!");
        promoteCalls();
        Util.Log("finished AsyncCall");
    }

    /** Used by {@code Call#execute} to signal completion. */
    synchronized void finished(Call call) {
        if (!executedCalls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
        Util.Log("finished Call");
    }

    private void promoteCalls() {
        if (runningCalls.size() >= maxRequests) return; // Already running max capacity.
        if (readyCalls.isEmpty()) return; // No ready calls to promote.

        for (Iterator<AsyncCall> i = readyCalls.iterator(); i.hasNext(); ) {
            AsyncCall call = i.next();

            i.remove();
            runningCalls.add(call);
            getExecutorService().execute(call);

            if (runningCalls.size() >= maxRequests) return; // Reached max capacity.
        }
    }

    public synchronized int getRunningCallCount() {
        return runningCalls.size();
    }

    public synchronized int getQueuedCallCount() {
        return readyCalls.size();
    }

}
