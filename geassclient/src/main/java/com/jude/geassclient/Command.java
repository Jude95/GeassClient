package com.jude.geassclient;

/**
 * Created by zhuchenxi on 16/11/4.
 */

public class Command {
    private String command;
    private long outOfTime = 60 * 1000;

    public Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getOutOfTime() {
        return outOfTime;
    }

    public void setOutOfTime(long outOfTime) {
        this.outOfTime = outOfTime;
    }

    @Override
    public String toString() {
        return "Command:"+command;
    }
}
