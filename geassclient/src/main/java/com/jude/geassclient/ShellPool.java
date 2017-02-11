package com.jude.geassclient;

import java.io.IOException;

/**
 * Created by zhuchenxi on 2017/2/11.
 */

public interface ShellPool {
    Shell get() throws IOException;
    void put(Shell shell);
}
