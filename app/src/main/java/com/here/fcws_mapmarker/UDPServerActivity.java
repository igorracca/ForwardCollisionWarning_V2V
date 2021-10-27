package com.here.fcws_mapmarker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.util.Date;

public class UDPServerActivity extends AppCompatActivity {

    private Context context;
    final Handler handler = new Handler();

    private int portNumber = 9001;

    private Button buttonStartReceiving;
    private Button buttonStopReceiving;
    static TextView textViewDataFromClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = UDPServerActivity.this;
        setContentView(R.layout.activity_client);

        buttonStartReceiving = (Button) findViewById(R.id.btn_start_receiving);
        buttonStopReceiving = (Button) findViewById(R.id.btn_stop_receiving);
        textViewDataFromClient = (TextView) findViewById(R.id.tv_data_from_server);

//        buttonStopReceiving.setEnabled(false);

        Intent intent = new Intent(this, UDPServerService.class);
        intent.putExtra("portNumber", portNumber);

        ResultReceiver dataReceiver = new DataReceiver(handler);
        intent.putExtra("receiver", dataReceiver);

        buttonStartReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonStartReceiving.setEnabled(false);
                buttonStopReceiving.setEnabled(true);
                Toast.makeText(context, "Listening on port " + portNumber + "...", Toast.LENGTH_LONG).show();

                Log.d("UDP", "Starting UDP Server Service...");
                startService(intent);
            }
        });

        buttonStopReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.d("UDP", "Stopping UDP Server Service...");
                    buttonStopReceiving.setEnabled(false);

                    stopService(intent);

                    buttonStartReceiving.setEnabled(true);
            }
        });
    }

    private void updateUI(final String stringData) {

        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Date date = new Date();
                Instant instant = date.toInstant();
                String s = textViewDataFromClient.getText().toString();
                if (stringData.trim().length() != 0)
                    textViewDataFromClient.setText(s + "\n" + instant + " From Client : " + stringData);
            }
        });
    }

    public void clearTextViewButtonClicked(View v) {
        textViewDataFromClient.setText("");
    }

}

