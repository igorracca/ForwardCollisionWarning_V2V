/*
 * Copyright (C) 2019-2020 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.fcws_mapmarker;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.Camera;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PickMapItemsCallback;
import com.here.sdk.mapviewlite.PickMapItemsResult;

import java.util.ArrayList;
import java.util.List;

public class VehicleMapMarker {

    private Context context;
    private MapViewLite mapView;
    private static List<Vehicle> vehicleList;
    private final List<MapMarker> mapMarkerList = new ArrayList<>();

    public VehicleMapMarker(Context context, MapViewLite mapView, List<Vehicle> vehicleList) {
        this.context = context;
        this.mapView = mapView;
        this.vehicleList = vehicleList;

        Camera camera = mapView.getCamera();
        camera.setTarget(new GeoCoordinates(47.481457, 19.053375));
        camera.setZoomLevel(15);

        // Setting a tap handler to pick markers from map
        setTapGestureHandler();

        Toast.makeText(context,"Start the server to get vehicles data.", Toast.LENGTH_LONG).show();
        showCenteredMapMarkers();
    }

    public static List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void showCenteredMapMarkers() {
        for(Vehicle v: vehicleList) {
            GeoCoordinates geoCoordinates = new GeoCoordinates(47.47749786251345, 19.0555073722648); // 47.47749786251345, 19.0555073722648
            // Centered on location. Shown below the POI image to indicate the location.
            v.setCoordinates(addPhotoMapMarker(geoCoordinates));
        }
//        // 7.47740359419673, 19.05409384806247
    }

    public void clearMap() {
        for (Vehicle v : vehicleList) {
            mapView.getMapScene().removeMapMarker(v.getMapMarker());
        }
        vehicleList.clear();
    }

    private Vehicle getVehicleByMapMarker(MapMarker mapMarker) {
        for (Vehicle v : vehicleList) {
            if(v.getMapMarker() == mapMarker) {
                return v;
            }
        }
        return null;
    }

    private void setTapGestureHandler() {
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(Point2D touchPoint) {
                pickMapMarker(touchPoint);
            }
        });
    }

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 2;
        mapView.pickMapItems(touchPoint, radiusInPixel, new PickMapItemsCallback() {
            @Override
            public void onMapItemsPicked(@Nullable PickMapItemsResult pickMapItemsResult) {
                if (pickMapItemsResult == null) {
                    return;
                }

                MapMarker topmostMapMarker = pickMapItemsResult.getTopmostMarker();
                if (topmostMapMarker == null) {
                    return;
                }

                Vehicle v = getVehicleByMapMarker(topmostMapMarker);
                if(v == null) {
                    Log.d("MapMarker Activity", "V is null");
                    return;
                }
                Log.d("MapMarker Activity", "V not null");

                Metadata metadata = topmostMapMarker.getMetadata();
                if (metadata != null) {
                    String message = "No message found.";
                    String string = metadata.getString("key_poi");
                    if (string != null) {
                        message = string;
                    }
                }

                Log.d("MapMarker Activity", "Location: " +
                        topmostMapMarker.getCoordinates().latitude + ", " +
                        topmostMapMarker.getCoordinates().longitude);
                showDialog("Vehicle picked:", "Location: " +
                        topmostMapMarker.getCoordinates().latitude + ", " +
                        topmostMapMarker.getCoordinates().longitude);
            }
        });
    }

    private MapMarker addPhotoMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.here_car);

        MapMarker mapMarker = new MapMarker(geoCoordinates);
        mapMarker.addImage(mapImage, new MapMarkerImageStyle());
        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);

        return mapMarker;
    }

    public static void updateVehiclePosition() {
        if(vehicleList.size() == 0) {
            Log.d("updateVehiclePosition", "vehicle list empty");
        } else {
            for (Vehicle v : vehicleList) {
                if (v.hasCoordinates()) {
                    GeoCoordinates geo = new GeoCoordinates(v.getLatitude() + 0.001, v.getLongitude() + 0.0001);
                    v.getMapMarker().setCoordinates(geo);
                    Log.d("updateVehiclePosition", "vehicle " + v + ": lat " + v.getLatitude() + " lon:" + v.getLongitude());
                } else {
                    Log.d("updateVehiclePosition", "vehicle does not have coordinates");
                }
            }
        }
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
