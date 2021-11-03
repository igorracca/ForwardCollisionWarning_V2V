package com.here.fcws_mapmarker.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.here.fcws_mapmarker.App;
import com.here.fcws_mapmarker.R;
import com.here.fcws_mapmarker.model.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.Map;

public class UDPServerService extends IntentService {

    private static boolean server_aktiv = true;
    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    public UDPServerService() {
        super("UDP Server Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d("UDP", "UDP Server Service started.");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int portNumber = intent.getIntExtra("portNumber", 0);
        if (DEBUG) Log.d("UDP Service", "Listening on port " + portNumber + "...");

        ResultReceiver rr = intent.getParcelableExtra("receiver");
        Bundle b = new Bundle();

        byte[] lMsg = new byte[1024];
        DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
        DatagramSocket ds = null;

        server_aktiv = true;

        try {
            ds = new DatagramSocket(portNumber);
            ds.setReceiveBufferSize(1);
            while (server_aktiv) {
                ds.receive(dp);

                if (DEBUG) Log.d("UDP Service", "S: Receiving...listening on port " + dp.getPort());
                String rec_str;
                rec_str = new String(dp.getData());

                if (DEBUG) Log.d("UDP Service", "packet length: " + Integer.toString(dp.getLength()));
                if (DEBUG) Log.d("UDP Service", "packet data: " + rec_str);

                b.putString("data_rec", rec_str);

                rr.send(DataReceiver.STATUS_RECEIVED,b);
                Thread.sleep(300);
            }
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
        } finally{
            server_aktiv = false;
            if (ds != null) {
                ds.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        server_aktiv = false;
        if (DEBUG) Log.d("UDP Service", "UDP Server Service terminated.");
    }

}
