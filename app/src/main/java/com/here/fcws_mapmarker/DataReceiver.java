package com.here.fcws_mapmarker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import java.time.Instant;
import java.util.Date;

public class DataReceiver extends ResultReceiver {

    public static final int STATUS_RECEIVED = 1;
    public static final int STATUS_FAILED = 0;
    Handler handler;

    public DataReceiver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if(resultCode == STATUS_RECEIVED) {
            if(resultData != null) {
                String msg = resultData.getString("result");

                Log.d("Data Receiver", "CODE_UPDATE_SERVER_UI: " + msg);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        Date date = new Date();
                        Instant instant = date.toInstant();
                        String s = MainActivity.getTextViewDataFromClient().getText().toString();
                        if (msg.trim().length() != 0) {
                            MainActivity.getTextViewDataFromClient().setText(s + "\n" + instant + " From Client : " + msg);
                        }
                    }
                });

                Log.d("Data Receiver", "CODE_UPDATE_MAP_UI: " + msg);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if(VehicleMapMarker.getVehicleList() != null) {
                            VehicleMapMarker.updateVehiclePosition();
                        }
                    }
                });

            }
        }
    }
}
