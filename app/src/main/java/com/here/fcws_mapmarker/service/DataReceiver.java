package com.here.fcws_mapmarker.service;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.util.Log;

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

    public DataReceiver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if(resultCode == STATUS_RECEIVED) {
            if(resultData != null) {
                String data_rec = resultData.getString("data_rec");

                Log.d("Data Receiver", "CODE_UPDATE_SERVER_UI: " + data_rec);
                handler.post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        Date date = new Date();
                        Instant instant = date.toInstant();
                        String s = MainActivity.getTextViewDataFromClient().getText().toString();
                        if (data_rec.trim().length() != 0) {
                            MainActivity.getTextViewDataFromClient().setText(s + "\nAt " + instant + " from Client: " + data_rec);
                        }
                    }
                });

                VehicleParameters vp = parseJSON(data_rec);

                Log.d("Data Receiver", "CODE_UPDATE_MAP_UI: " + data_rec);
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

            Log.d("readJSON", "msg read: " + adrclass + " " + ts + " " + time);

            if(jObject.getJSONObject("HV") != null) {
                double lat = jObject.getJSONObject("HV").getDouble("Lat");
                double lon = jObject.getJSONObject("HV").getDouble("Lon");
                double elev = jObject.getJSONObject("HV").getDouble("Elev");
                double heading = jObject.getJSONObject("HV").getDouble("Heading");
                double speed = jObject.getJSONObject("HV").getDouble("Speed");

                Log.d("readJSON", "HV attr: " + lat + " "+ lon + " "+ elev + " "+ heading + " "+ speed + " ");

                vp = new VehicleParameters(lat, lon, elev, heading, speed);
            } else {
                Log.d("readJSON", " HV not found");
            }
//            if(jObject.getJSONArray("RV") != null) {
//                JSONArray jArray = jObject.getJSONArray("RV");
//                        Log.d("jArray: ", jArray.toString());
////                double lat = jObject.getJSONArray("RV") .getDouble("Lat");
////                double lon = jObject.getJSONObject("RV").getDouble("Lon");
////                double elev = jObject.getJSONObject("RV").getDouble("Elev");
////                double heading = jObject.getJSONObject("RV").getDouble("Heading");
////                double speed = jObject.getJSONObject("RV").getDouble("Speed");
////
////                Log.d("readJSON", "RV attr: " + lat + " "+ lon + " "+ elev + " "+ heading + " "+ speed + " ");
//            } else {
//                Log.d("readJSON", " RV not found");
//            }


        } catch (JSONException e) {
            Log.d("readJSON", " Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return vp;
    }
}
