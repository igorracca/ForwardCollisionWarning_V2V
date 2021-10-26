package com.here.fcws_mapmarker;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPClientSocketActivity extends AppCompatActivity {

    final Handler handler = new Handler();

    private int portNumber = 9001;
    private boolean Server_aktiv = true;

    private Button buttonStartReceiving;
    private Button buttonStopReceiving;
    private TextView textViewDataFromServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        buttonStartReceiving = (Button) findViewById(R.id.btn_start_receiving);
        buttonStopReceiving = (Button) findViewById(R.id.btn_stop_receiving);
        textViewDataFromServer = (TextView) findViewById(R.id.tv_data_from_server);

        buttonStopReceiving.setEnabled(false);

        buttonStartReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(() -> {
                    // onPreExecute Method
                    runOnUiThread(() -> {
                        Log.d("UDP", "running server");
                        buttonStartReceiving.setEnabled(false);
                        buttonStopReceiving.setEnabled(true);
                    });

                    // doInBackground method
                    byte[] lMsg = new byte[1024];
                    DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                    DatagramSocket ds = null;
                    try {
                        ds = new DatagramSocket(portNumber);

                        while (Server_aktiv) {
                            try {
                                ds.receive(dp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.d("UDP", "S: Receiving...listening on port " + dp.getPort());
                            String rec_str;
                            rec_str = new String(dp.getData());

                            Log.d("UDP", "packet length: " + Integer.toString(dp.getLength()));
                            Log.d("UDP", "packet data: " + rec_str);
                            updateUI(rec_str);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (ds != null) {
                            ds.close();
                        }
                    }

                    // doPostExecute Method
                    runOnUiThread(() -> buttonStartReceiving.setEnabled(true));

                });
            }
        });

        buttonStopReceiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onPreExecute Method
                runOnUiThread(() -> {
                    Log.d("UDP", "server stoppedr");
                    buttonStopReceiving.setEnabled(false);
                });

                // doInBackground method of AsyncTask
                try {
                    Server_aktiv=false;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.postDelayed(() -> {
                    buttonStartReceiving.setEnabled(true);
                    Server_aktiv = true;
                }, 2000);
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
                String s = textViewDataFromServer.getText().toString();
                if (stringData.trim().length() != 0)
                    textViewDataFromServer.setText(s + "\n" + instant + " From Client : " + stringData);
            }
        });
    }
}