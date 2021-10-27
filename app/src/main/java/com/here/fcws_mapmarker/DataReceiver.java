package com.here.fcws_mapmarker;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.time.Instant;
import java.util.Date;

public class DataReceiver extends ResultReceiver {

    public static final int RESULT_CODE = 1;
    Handler handler;

    public DataReceiver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if(resultCode == RESULT_CODE) {
            if(resultData != null) {
                String msg = resultData.getString("result");
                Log.d("Data Receiver", "Msg received via resultReceiver: " + msg);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        Date date = new Date();
                        Instant instant = date.toInstant();
                        String s = UDPServerActivity.textViewDataFromClient.getText().toString();
                        if (msg.trim().length() != 0)
                            UDPServerActivity.textViewDataFromClient.setText(s + "\n" + instant + " From Client : " + msg);
                    }
                });
            }
        }
    }
}
