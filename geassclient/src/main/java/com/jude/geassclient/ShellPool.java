package com.jude.geassclient;


import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by zhuchenxi on 16/11/4.
 */

public class ShellPool {
    private final Deque<Shell> shells = new ArrayDeque<>();

    synchronized Shell get() throws IOException{
        if (shells.size()>0){
            Util.Log("pop Shell,"+(shells.size()-1)+"left");
            return shells.pop();
        }else {
            Util.Log("no Shell,create new");
            return Shell.startRootShell();
        }
    }

    synchronized void put(Shell shell){
        Util.Log("recycler one shell");
        shells.offer(shell);
    }

}
