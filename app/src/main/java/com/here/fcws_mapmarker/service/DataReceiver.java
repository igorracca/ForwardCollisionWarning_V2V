package com.here.fcws_mapmarker.service;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.here.fcws_mapmarker.App;
import com.here.fcws_mapmarker.R;
import com.here.fcws_mapmarker.VehicleMapMarker;
import com.here.fcws_mapmarker.activities.MainActivity;
import com.here.fcws_mapmarker.model.Vehicle;
import com.here.fcws_mapmarker.model.VehicleParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.Date;

public class DataReceiver extends ResultReceiver {

    public static final int STATUS_RECEIVED = 1;
    public static final int STATUS_FAILED = 0;
    Handler handler;
    VehicleParameters vp = null;

    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    public DataReceiver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if(resultCode == STATUS_RECEIVED) {
            if(resultData != null) {
                String data_rec = resultData.getString("data_rec");

                if(DEBUG) Log.d("Data Receiver", "CODE_UPDATE_SERVER_UI: " + data_rec);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        Date date = new Date();
                        Instant instant = date.toInstant();
                        String s = MainActivity.getTextViewDataFromClient().getText().toString();
                        if (data_rec.trim().length() != 0) {
                            // update textView with the message received
                            MainActivity.getTextViewDataFromClient().setText("\nAt " + instant + " from Client: " + data_rec + s + "\n");
                            MainActivity.textView_msgCounter++;
                            // clear text view if it reaches the limit defined at MainActivity
                            if(MainActivity.textView_msgCounter >= MainActivity.textView_msgLimit) {
                                MainActivity.getTextViewDataFromClient().setText("");
                                MainActivity.textView_msgCounter = 0;
                            }
                        }
                    }
                });

                vp = parseJSON(data_rec);

                if(DEBUG) Log.d("Data Receiver", "CODE_UPDATE_MAP_UI: " + data_rec);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if(VehicleMapMarker.getVehicleList() != null) {
                            VehicleMapMarker.updateVehicleAttributes(vp);
                        }
                    }
                });
            }
        }
    }

    public VehicleParameters parseJSON (String adr_data) {
        VehicleParameters vp = null;
        try {
            JSONObject jObject = new JSONObject(adr_data);

            String adrclass = (String) jObject.getString("class");
            String ts = String.valueOf(jObject.getInt("Timestamp"));
            String time = (String) jObject.get("Time");

            if(DEBUG) Log.d("readJSON", "msg read: " + adrclass + " " + ts + " " + time);

            if(jObject.getJSONObject("HV") != null) {
                double lat = jObject.getJSONObject("HV").getDouble("Lat");
                double lon = jObject.getJSONObject("HV").getDouble("Lon");
                double elev = jObject.getJSONObject("HV").getDouble("Elev");
                double heading = jObject.getJSONObject("HV").getDouble("Heading");
                double speed = jObject.getJSONObject("HV").getDouble("Speed");

                if(DEBUG) Log.d("readJSON", "HV attr: " + lat + " "+ lon + " "+ elev + " "+ heading + " "+ speed + " ");

                vp = new VehicleParameters(lat, lon, elev, heading, speed);
            } else {
                if(DEBUG) Log.d("readJSON", " HV not found");
            }
//            if(jObject.getJSONArray("RV") != null) {
//                JSONArray jArray = jObject.getJSONArray("RV");
//                        if(DEBUG) Log.d("jArray: ", jArray.toString());
////                double lat = jObject.getJSONArray("RV") .getDouble("Lat");
////                double lon = jObject.getJSONObject("RV").getDouble("Lon");
////                double elev = jObject.getJSONObject("RV").getDouble("Elev");
////                double heading = jObject.getJSONObject("RV").getDouble("Heading");
////                double speed = jObject.getJSONObject("RV").getDouble("Speed");
////
////                if(DEBUG) Log.d("readJSON", "RV attr: " + lat + " "+ lon + " "+ elev + " "+ heading + " "+ speed + " ");
//            } else {
//                if(DEBUG) Log.d("readJSON", " RV not found");
//            }

        } catch (JSONException e) {
            if(DEBUG) Log.d("readJSON", " Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return vp;
    }
}
