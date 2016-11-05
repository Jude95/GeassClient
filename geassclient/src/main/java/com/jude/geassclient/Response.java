package com.jude.geassclient;

/**
 * Created by zhuchenxi on 16/11/4.
 */

public class Response {

    public static final int SUCCESS = 0;

    Command command;
    String result;
    int resultCode;

    public Response(Command command) {
        this.command = command;
    }

    public Command command() {
        return command;
    }

    public String result() {
        return result;
    }

    public int code() {
        return resultCode;
    }

    public boolean success(){
        return resultCode == SUCCESS;
    }

    @Override
    public String toString() {
        return "ResultCode"+resultCode+"  Response:"+result;
    }
}
