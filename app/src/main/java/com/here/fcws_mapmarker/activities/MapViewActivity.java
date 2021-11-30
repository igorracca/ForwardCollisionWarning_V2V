package com.here.fcws_mapmarker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.here.fcws_mapmarker.*;
import com.here.fcws_mapmarker.R;
import com.here.fcws_mapmarker.VehicleMapMarker;
import com.here.fcws_mapmarker.model.Vehicle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;

import java.io.Serializable;
import java.util.List;

import static com.here.fcws_mapmarker.VehicleMapMarker.NORMAL_AWARENESS;
import static com.here.fcws_mapmarker.VehicleMapMarker.PRE_CRASH;
import static com.here.fcws_mapmarker.VehicleMapMarker.WARNING;

public class MapViewActivity extends AppCompatActivity {

    private static final String TAG = MapViewActivity.class.getSimpleName();

    private Context context;
    private PermissionsRequestor permissionsRequestor;
    private MapViewLite mapView;
    private static List<Vehicle> vehicleList;
    private static VehicleMapMarker vehicleMapMarker;

    private static TextView textViewDist;
    private static TextView textViewTtc;
    private static TextView textViewLevel;

    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));
    public static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // Get a MapView instance from layout
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        textViewDist = (TextView) findViewById(R.id.lbl_dist);
        textViewTtc = (TextView) findViewById(R.id.lbl_ttc);
        textViewLevel = (TextView) findViewById(R.id.lbl_level);

        handleAndroidPermissions();

        Intent intent = getIntent();
        if(intent.hasExtra("vehicleList")) {
            vehicleList = (List<Vehicle>) intent.getSerializableExtra("vehicleList");
        }
        active = true;
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(this);
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {
                    vehicleMapMarker = new VehicleMapMarker(MapViewActivity.this, mapView, vehicleList);
                } else {
                    if(DEBUG) Log.d(TAG, "onLoadScene failed: " + errorCode.toString());
                }
            }
        });
    }

    public static void updateTtc(double dist, double ttc) {
        textViewDist.setText(String.format("%.2f", dist));
        textViewTtc.setText(String.format("%.2f", ttc));
    }

    public static void updatePriorityLevel(int level) {
        switch(level) {
            case NORMAL_AWARENESS:
                textViewLevel.setText("NORMAL/AWARENESS");
                textViewLevel.setBackgroundColor(Color.parseColor("#a3e4d7"));
                break;
            case WARNING:
                textViewLevel.setText("WARNING");
                textViewLevel.setBackgroundColor(Color.parseColor("#f7dc6f"));
                break;
            case PRE_CRASH:
                textViewLevel.setText("PRE_CRASH");
                textViewLevel.setBackgroundColor(Color.parseColor("#922b21"));
                break;
        }
    }

    public void centerMapViewButtonClicked(View view) {
        vehicleMapMarker.centerMapView();
    }

    public void navigateToVehicleParameters(View view) {
        Intent i = new Intent(this, VehicleParametersActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

