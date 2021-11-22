package com.here.fcws_mapmarker.service;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.here.fcws_mapmarker.*;
import com.here.fcws_mapmarker.R;
import com.here.fcws_mapmarker.VehicleMapMarker;
import com.here.fcws_mapmarker.activities.MainActivity;
import com.here.fcws_mapmarker.activities.MapViewActivity;
import com.here.fcws_mapmarker.activities.VehicleParametersActivity;
import com.here.fcws_mapmarker.model.Parameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class DataReceiver extends ResultReceiver {

    public static final int CODE_RECEIVED = 1;

    Handler handler;

    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    public DataReceiver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultData != null) {

            if(resultCode == CODE_RECEIVED) {
                Date date = new Date();
                Instant instant = date.toInstant();

                Parameters vp = (Parameters) resultData.getSerializable("data_rec");
                String data_rec_str = resultData.getString("data_rec_str");

                if(DEBUG) Log.d("Data Receiver", "CODE_UPDATE_SERVER_UI: " + data_rec_str);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {

                        String s = MainActivity.getTextViewDataFromClient().getText().toString();
                        if (vp != null) {
                            // update textView with the message received
                            MainActivity.getTextViewDataFromClient().setText("\nAt " + instant + " from Client: " + data_rec_str + s + "\n");
                            MainActivity.textView_msgCounter++;
                            // clear text view if it reaches the limit defined at MainActivity
                            if(MainActivity.textView_msgCounter >= MainActivity.textView_msgLimit) {
                                MainActivity.getTextViewDataFromClient().setText("");
                                MainActivity.textView_msgCounter = 0;
                            }
                        }
                    }
                });

                if(DEBUG) Log.d("Data Receiver", "CODE_UPDATE_MAP_UI: " + data_rec_str);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if(MapViewActivity.active) {
                            if(VehicleMapMarker.getVehicleList() != null) {
                                double dist = VehicleMapMarker.gpsToDist(vp.HV_Lat, vp.HV_Lon, vp.RV_Lat, vp.RV_Lon);
                                double ttc = VehicleMapMarker.ttc(dist, vp.HV_Speed, vp.RV_Speed);

                                VehicleMapMarker.updateVehicleAttributes(vp);
                                MapViewActivity.updateTtc(dist, ttc);
                                vp.setTtc(dist, ttc);
                            }
                        }

                    }
                });

                if(DEBUG) Log.d("Data Receiver", "CODE_VEHICLE_PARAMETERS_UI: " + data_rec_str);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if(VehicleParametersActivity.active) {
                            VehicleParametersActivity.updateVehicleAttributes(vp);
                        }
                    }
                });
            }
        }
    }
}
