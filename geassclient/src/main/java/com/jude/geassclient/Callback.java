package com.jude.geassclient;

import java.io.IOException;

public interface Callback {

  void onFailure(Command command, IOException e);


  void onResponse(Response response) throws IOException;
}