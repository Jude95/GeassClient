package com.jude.geassclient.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jude.geassclient.Call;
import com.jude.geassclient.Callback;
import com.jude.geassclient.Command;
import com.jude.geassclient.GeassClient;
import com.jude.geassclient.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView textView;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        textView = (TextView) findViewById(R.id.content);

        GeassClient client = new GeassClient();
        Command command = new Command("ls /data/data");
        Call call = client.newCall(command);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Command command, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setText("onFailure"+e.getMessage());
                    }
                });

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setText("onResponse"+response);
                    }
                });
            }
        });
    }
}
