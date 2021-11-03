package com.here.fcws_mapmarker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.here.fcws_mapmarker.App;
import com.here.fcws_mapmarker.PermissionsRequestor;
import com.here.fcws_mapmarker.R;
import com.here.fcws_mapmarker.VehicleMapMarker;
import com.here.fcws_mapmarker.model.Vehicle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;

import java.io.Serializable;
import java.util.List;

public class MapViewActivity extends AppCompatActivity {

    private static final String TAG = MapViewActivity.class.getSimpleName();

    private PermissionsRequestor permissionsRequestor;
    private MapViewLite mapView;
    private static List<Vehicle> vehicleList;
    private static VehicleMapMarker vehicleMapMarker;

    public final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // Get a MapView instance from layout
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        handleAndroidPermissions();

        Intent intent = getIntent();
        vehicleList = (List<Vehicle>) intent.getSerializableExtra("vehicleList");
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

    public void centerMapViewButtonClicked(View view) {
        vehicleMapMarker.centerMapView();
    }

    public void clearMapButtonClicked(View view) {
        vehicleMapMarker.clearMap();
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
        intent.putExtra("vehicleList", (Serializable) vehicleMapMarker.getVehicleList());

        startActivity(intent);
    }

}

