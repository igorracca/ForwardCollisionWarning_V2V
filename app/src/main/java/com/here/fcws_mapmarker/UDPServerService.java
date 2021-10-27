package com.here.fcws_mapmarker;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServerService extends IntentService {

    private boolean server_aktiv = true;

    public UDPServerService() {
        super("UDP Server Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UDP", "UDP Server Service started.");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int portNumber = intent.getIntExtra("portNumber", 0);
        Log.d("UDP Service", "Listening on port " + portNumber + "...");

        ResultReceiver rr = intent.getParcelableExtra("receiver");
        Bundle b = new Bundle();

        byte[] lMsg = new byte[1024];
        DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
        DatagramSocket ds = null;

        try {
            ds = new DatagramSocket(portNumber);

            while (server_aktiv) {
                ds.receive(dp);

                Log.d("UDP Service", "S: Receiving...listening on port " + dp.getPort());
                String rec_str;
                rec_str = new String(dp.getData());

                Log.d("UDP Service", "packet length: " + Integer.toString(dp.getLength()));
                Log.d("UDP Service", "packet data: " + rec_str);
                //updateUI(rec_str);
                b.putString("result", rec_str);
                rr.send(DataReceiver.RESULT_CODE,b);
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            if (ds != null) {
                ds.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        server_aktiv = false;
        Log.d("UDP Service", "UDP Server Service terminated.");
    }
}
