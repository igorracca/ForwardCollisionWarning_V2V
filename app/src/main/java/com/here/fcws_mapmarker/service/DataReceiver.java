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
import com.here.fcws_mapmarker.model.VehiclesParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.Date;

public class DataReceiver extends ResultReceiver {

    public static final int CODE_INIT = 0;
    public static final int CODE_RECEIVED = 1;

    Handler handler;
    private int numOfRV;

    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    public DataReceiver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(resultData != null) {

            if(resultCode == CODE_INIT) {

                numOfRV = resultData.getInt("numOfRV");

            } else
            if(resultCode == CODE_RECEIVED) {

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

                VehiclesParameters vp = parseJSON(data_rec);

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

    public VehiclesParameters parseJSON (String adr_data) {
        VehiclesParameters vp = new VehiclesParameters();
        try {
            JSONObject jObject = new JSONObject(adr_data);

            String adrclass = (String) jObject.getString("class");
            String ts = String.valueOf(jObject.getInt("Timestamp"));
            String time = (String) jObject.get("Time");

            if(DEBUG) Log.d("readJSON", "msg read: " + adrclass + " " + ts + " " + time);

            if(jObject.getJSONObject("HV") != null) {
                double HV_Lat = jObject.getJSONObject("HV").getDouble("Lat");
                double HV_Lon = jObject.getJSONObject("HV").getDouble("Lon");
                double HV_Elev = jObject.getJSONObject("HV").getDouble("Elev");
                double HV_Heading = jObject.getJSONObject("HV").getDouble("Heading");
                double HV_Speed = jObject.getJSONObject("HV").getDouble("Speed");

                if(DEBUG) Log.d("readJSON", "HV attr: " + HV_Lat + " "+ HV_Lon + " "+ HV_Elev + " "+ HV_Heading + " "+ HV_Speed + " ");
                vp.setHVParameters(HV_Lat, HV_Lon, HV_Elev, HV_Heading, HV_Speed);

                if(jObject.getJSONArray("RV") != null) {
                    JSONArray jArray = jObject.getJSONArray("RV");
                    if(jArray.length() != 0) {
                        if(DEBUG) Log.d("jArray: ", jArray.toString());

                        int RV_Id = jObject.getJSONArray("RV").getJSONObject(0).getInt("Id");
                        int RV_SeqNbr = jObject.getJSONArray("RV").getJSONObject(0).getInt("SeqNbr");
                        double RV_Lat = jObject.getJSONArray("RV").getJSONObject(0).getDouble("Lat");
                        double RV_Lon = jObject.getJSONArray("RV").getJSONObject(0).getDouble("Lon");
                        double RV_Elev = jObject.getJSONArray("RV").getJSONObject(0).getDouble("Elev");
                        double RV_Heading = jObject.getJSONArray("RV").getJSONObject(0).getDouble("Heading");
                        double RV_Speed = jObject.getJSONArray("RV").getJSONObject(0).getDouble("Speed");
                        int RV_RSSI = jObject.getJSONArray("RV").getJSONObject(0).getInt("RSSI");
                        int RV_RxCnt = jObject.getJSONArray("RV").getJSONObject(0).getInt("RxCnt");

                        if(DEBUG) Log.d("readJSON", "RV attr: " + RV_Id + " " + RV_SeqNbr + " " + RV_Lat + " "+ RV_Lon + " "+ RV_Elev + " "+ RV_Heading + " "+ RV_Speed + " " + RV_RSSI + " " + RV_RxCnt + " ");
                        vp.setRVParameters(RV_Id, RV_SeqNbr, RV_Lat, RV_Lon, RV_Elev, RV_Heading, RV_Speed, RV_RSSI, RV_RxCnt);
                    }
                    else {
                        if(DEBUG) Log.d("readJSON", " RV not found");
                    }
                }
            } else {
                if(DEBUG) Log.d("readJSON", " HV not found");
            }

        } catch (JSONException e) {
            if(DEBUG) Log.d("readJSON", " Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return vp;
    }
}
