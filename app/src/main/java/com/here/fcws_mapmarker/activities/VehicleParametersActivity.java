package com.here.fcws_mapmarker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.here.fcws_mapmarker.*;
import com.here.fcws_mapmarker.model.Vehicle;
import com.here.fcws_mapmarker.model.Parameters;

import java.util.ArrayList;
import java.util.List;

public class VehicleParametersActivity extends AppCompatActivity {
    private Context context;
    final Handler handler = new Handler();

    private static List<Vehicle> vehicleList = new ArrayList<>();

    public static boolean active = false;
    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    private static TextView labelHVLat;
    private static TextView labelHVLon;
    private static TextView labelHVElev;
    private static TextView labelHVHeading;
    private static TextView labelHVSpeed;
    private static TextView labelHVSpeedConv;

    private static TextView labelRVId;
    private static TextView labelRVSeqNbr;
    private static TextView labelRVLat;
    private static TextView labelRVLon;
    private static TextView labelRVElev;
    private static TextView labelRVHeading;
    private static TextView labelRVSpeed;
    private static TextView labelRVSpeedConv;
    private static TextView labelRVRssi;
    private static TextView labelRVRxCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = VehicleParametersActivity.this;
        setContentView(R.layout.activity_vehicle_parameters);

        labelHVLat = (TextView) findViewById(R.id.lbl_hv_lat);
        labelHVLon = (TextView) findViewById(R.id.lbl_hv_lon);
        labelHVElev = (TextView) findViewById(R.id.lbl_hv_elev);
        labelHVHeading = (TextView) findViewById(R.id.lbl_hv_heading);
        labelHVSpeed = (TextView) findViewById(R.id.lbl_hv_speed);
        labelHVSpeedConv = (TextView) findViewById(R.id.lbl_hv_speed_conv);

        labelRVId = (TextView) findViewById(R.id.lbl_rv_id);
        labelRVSeqNbr = (TextView) findViewById(R.id.lbl_rv_seq_nr);
        labelRVLat = (TextView) findViewById(R.id.lbl_rv_lat);
        labelRVLon = (TextView) findViewById(R.id.lbl_rv_lon);
        labelRVElev = (TextView) findViewById(R.id.lbl_rv_elev);
        labelRVHeading = (TextView) findViewById(R.id.lbl_rv_heading);
        labelRVSpeed = (TextView) findViewById(R.id.lbl_rv_speed);
        labelRVSpeedConv = (TextView) findViewById(R.id.lbl_rv_speed_conv);
        labelRVRssi = (TextView) findViewById(R.id.lbl_rv_rssi);
        labelRVRxCount = (TextView) findViewById(R.id.lbl_rv_rx_cnt);

        active = true;
    }

    public static void updateVehicleAttributes(Parameters vp) {
        labelHVLat.setText(String.valueOf(vp.HV_Lat));
        labelHVLon.setText(String.valueOf(vp.HV_Lon));
        labelHVElev.setText(String.valueOf(vp.HV_Elev));
        labelHVHeading.setText(String.valueOf(vp.HV_Heading));
        labelHVSpeed.setText(String.valueOf(vp.HV_Speed));
        labelHVSpeedConv.setText(String.valueOf(vp.HV_Speed));

//        labelRVId.setText(String.valueOf(vp.RV_Id));
//        labelRVSeqNbr.setText(String.valueOf(vp.RV_SeqNbr));
//        labelRVLat.setText(String.valueOf(vp.RV_Lat));
//        labelRVLon.setText(String.valueOf(vp.RV_Lon));
//        labelRVElev.setText(String.valueOf(vp.RV_Elev));
//        labelRVHeading.setText(String.valueOf(vp.RV_Heading));
//        labelRVSpeed.setText(String.valueOf(vp.RV_Speed));
//        labelRVSpeedConv.setText(String.valueOf(vp.RV_Speed));
//        labelRVRssi.setText(String.valueOf(vp.RV_RSSI));
//        labelRVRxCount.setText(String.valueOf(vp.RV_RxCnt));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MapViewActivity.class);
        startActivity(intent);
    }

}
