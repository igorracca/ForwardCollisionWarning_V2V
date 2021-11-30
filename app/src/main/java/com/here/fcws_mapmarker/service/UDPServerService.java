package com.here.fcws_mapmarker.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.here.fcws_mapmarker.*;
import com.here.fcws_mapmarker.R;
import com.here.fcws_mapmarker.model.CSVwriterParcelable;
import com.here.fcws_mapmarker.model.Parameters;
import com.opencsv.CSVWriter;

import net.time4j.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class UDPServerService extends IntentService {

    private static File file = null;
    private CSVWriter writer = null;
    private static boolean server_aktiv = true;

    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));
    public final boolean LOG = Boolean.parseBoolean(App.getRes().getString(R.string.log_mode));

    public UDPServerService() {
        super("UDP Server Service");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.d("UDP", "UDP Server Service started.");

        if(LOG) {
            Date date = new Date();
            Instant instant = date.toInstant();

            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "ADR_" + String.valueOf(instant);
            String filePath = baseDir + "/log/HMI" + File.separator + fileName;
            if (DEBUG) Log.d("CSV", "FileName: " + fileName + " filePath: " + filePath);
            file = new File(filePath);
            try{
                // File exist
                if(file.exists()&&!file.isDirectory()) {
                    if (DEBUG) Log.d("CSV", "File exists.");
                    FileWriter mFileWriter = new FileWriter(filePath, true);
                    writer = new CSVWriter(mFileWriter);
                }
                else {
                    if (DEBUG) Log.d("CSV", "File created.");
                    writer = new CSVWriter(new FileWriter(filePath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] data = {"Time_received", "Timestamp", "Time", "HV_Lat", "HV_Lon", "HV_Elev", "HV_Heading", "HV_Speed",
                                "Id", "SeqNbr", "RV_Lat", "RV_Lon", "RV_Elev", "RV_Heading", "RV_Speed", "RV_RSSI", "RV_RxCnt",
                                "Distance", "Time to collision", "Priority_level"};
            writer.writeNext(data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
            if (DEBUG) Log.d("UDP Service", "S: Receiving...listening on port " + dp.getPort());

            while (server_aktiv) {
                ds.receive(dp);

                String rec_str;
                rec_str = new String(dp.getData());

                if (DEBUG) Log.d("UDP Service", "packet length: " + Integer.toString(dp.getLength()));
                if (DEBUG) Log.d("UDP Service", "packet data: " + rec_str);

                Date d = new Date();
                Instant instant = d.toInstant();

                Parameters vp = parseJSON(rec_str);
                b.putSerializable("data_rec", (Serializable) vp);
                b.putString("data_rec_str", rec_str);

                CSVwriterParcelable parcWriter = new CSVwriterParcelable(writer);
                b.putSerializable("file_writer",parcWriter);
                b.putString("instant_rec", String.valueOf(instant));

//                Moment m2004 = PlainTimestamp.of(2004, 1, 1, 0, 0).atUTC();
//                Moment now = SystemClock.currentMoment(); // other clocks based on NTP are possible
//                long seconds = SI.SECONDS.between(m2004, now);

                rr.send(DataReceiver.CODE_RECEIVED,b);
            }
        } catch(IOException e){
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

        try {
            writer.close();
            if (DEBUG) Log.d("CSV", "CSV file closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Parameters parseJSON (String adr_data) {
        Parameters vp = null;
        try {
            JSONObject jObject = new JSONObject(adr_data);

            String adrclass = (String) jObject.getString("class");
            int ts = jObject.getInt("Timestamp");
            String time = (String) jObject.get("Time");

            if(DEBUG) Log.d("readJSON", "msg read: " + adrclass + " " + ts + " " + time);
            vp = new Parameters(ts, time);

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
