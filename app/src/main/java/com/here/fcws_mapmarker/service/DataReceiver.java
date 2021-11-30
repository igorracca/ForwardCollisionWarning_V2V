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
import com.here.fcws_mapmarker.model.CSVwriterParcelable;
import com.here.fcws_mapmarker.model.Parameters;
import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class DataReceiver extends ResultReceiver {

    public static final int CODE_RECEIVED = 1;

    Handler handler;
    int priorityLevel = 0;

    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    public DataReceiver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultData != null) {

            if (resultCode == CODE_RECEIVED) {
                Date date = new Date();
                Instant instant = date.toInstant();

                Parameters vp = (Parameters) resultData.getSerializable("data_rec");
                String data_rec_str = resultData.getString("data_rec_str");

                if (DEBUG) Log.d("Data Receiver", "UPDATE_SERVER_UI: " + data_rec_str);
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
                            if (MainActivity.textView_msgCounter >= MainActivity.textView_msgLimit) {
                                MainActivity.getTextViewDataFromClient().setText("");
                                MainActivity.textView_msgCounter = 0;
                            }
                        }
                    }
                });

                if (DEBUG) Log.d("Data Receiver", "UPDATE_MAP_UI: " + data_rec_str);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if (MapViewActivity.active) {
                            if (VehicleMapMarker.getVehicleList() != null) {
                                double dist = VehicleMapMarker.gpsToDist(vp.HV_Lat, vp.HV_Lon, vp.RV_Lat, vp.RV_Lon);
                                double ttc = VehicleMapMarker.ttc(dist, vp.HV_Speed, vp.RV_Speed);
                                priorityLevel = VehicleMapMarker.FWC(ttc, vp.HV_Speed, vp.RV_Speed);

                                MapViewActivity.updateTtc(dist, ttc);
                                MapViewActivity.updatePriorityLevel(priorityLevel, App.getAppContext());
                                vp.setTtc(dist, ttc);
                                VehicleMapMarker.updateVehicleAttributes(vp);
                            }
                        }
                    }
                });

                if (DEBUG) Log.d("Data Receiver", "UPDATE_VEHICLE_PARAMETERS_UI: " + data_rec_str);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if (VehicleParametersActivity.active) {
                            VehicleParametersActivity.updateVehicleAttributes(vp);
                        }
                    }
                });

                if (DEBUG) Log.d("Data Receiver", "WRITE_LOG: " + data_rec_str);
                CSVwriterParcelable parcWriter = (CSVwriterParcelable) resultData.getSerializable("file_writer");
                CSVWriter writer = parcWriter.writer;
                String instant_rec = resultData.getString("instant_rec");
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {

                        String level = "";
                        switch(priorityLevel) {
                            case 0: level = "PRE_CRASH"; break;
                            case 1: level = "WARNING"; break;
                            case 2: level = "NORMAL_AWARENESS"; break;
                        }

                        String[] data = {
                                instant_rec,
                                String.valueOf(vp.timestamp),
                                String.valueOf(vp.time),
                                String.valueOf(vp.HV_Lat),
                                String.valueOf(vp.HV_Lon),
                                String.valueOf(vp.HV_Elev),
                                String.valueOf(vp.HV_Heading),
                                String.valueOf(vp.HV_Speed),
                                String.valueOf(vp.RV_Id),
                                String.valueOf(vp.RV_SeqNbr),
                                String.valueOf(vp.RV_Lat),
                                String.valueOf(vp.RV_Lon),
                                String.valueOf(vp.RV_Elev),
                                String.valueOf(vp.RV_Heading),
                                String.valueOf(vp.RV_Speed),
                                String.valueOf(vp.RV_RSSI),
                                String.valueOf(vp.RV_RxCnt),
                                String.valueOf(vp.Dist),
                                String.valueOf(vp.ttc),
                                level
                        };
                        writer.writeNext(data);
                        if (DEBUG) Log.d("CSV", "CSV data written.");
                    }
                });
            }
        }
    }
}
