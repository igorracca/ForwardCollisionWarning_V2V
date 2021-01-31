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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoBox;
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

public class MapMarkerExample {

    private Context context;
    private MapViewLite mapView;
    private final List<Vehicle> VehicleList = new ArrayList<>();
    private final List<MapMarker> mapMarkerList = new ArrayList<>();

    public MapMarkerExample(Context context, MapViewLite mapView) {
        this.context = context;
        this.mapView = mapView;
        Camera camera = mapView.getCamera();
        camera.setTarget(new GeoCoordinates(47.481457, 19.053375));
        camera.setZoomLevel(15);

        // Setting a tap handler to pick markers from map
        setTapGestureHandler();

        Toast.makeText(context,"You can tap markers.", Toast.LENGTH_LONG).show();
    }

    public void showVehicleMapMarkers() {
//        GeoCoordinates geoCoordinates = createRandomLatLonInViewport();
        // 47.48162918672599, 19.0538202627025
        GeoCoordinates geoCoordinates = new GeoCoordinates(47.48162918672599,19.0538202627025);
        Vehicle vehicle1 = new Vehicle(geoCoordinates.latitude, geoCoordinates.longitude);
        VehicleList.add(vehicle1);
        // Centered on location. Shown below the POI image to indicate the location.
        addCircleMapMarker(vehicle1, geoCoordinates);
        // Anchored, pointing to location.
        addPOIMapMarker(vehicle1, geoCoordinates);
        //47.482800183105525, 19.052988777909846
        GeoCoordinates geoCoordinates2 = new GeoCoordinates(47.482800183105525,19.052988777909846);
//        GeoCoordinates geoCoordinates2 = createRandomLatLonInViewport();
        Vehicle vehicle2 = new Vehicle(geoCoordinates2.latitude, geoCoordinates2.longitude);
        VehicleList.add(vehicle2);
        // Centered on location. Shown below the POI image to indicate the location.
        addCircleMapMarker(vehicle2, geoCoordinates2);
        // Anchored, pointing to location.
        addPOIMapMarker(vehicle2, geoCoordinates2);
    }

    public void showCenteredMapMarkers() {
        // 47.47749786251345, 19.0555073722648
        GeoCoordinates geoCoordinates = new GeoCoordinates(47.47749786251345, 19.0555073722648);
        Vehicle vehicle1 = new Vehicle(geoCoordinates.latitude, geoCoordinates.longitude);
        VehicleList.add(vehicle1);
        // Centered on location. Shown below the POI image to indicate the location.
        addPhotoMapMarker(geoCoordinates);
        // Anchored, pointing to location.
        addCircleMapMarker(vehicle1, geoCoordinates);
        // 47.47740359419673, 19.05409384806247
        GeoCoordinates geoCoordinates2 = new GeoCoordinates(47.47740359419673, 19.05409384806247);
//        GeoCoordinates geoCoordinates2 = createRandomLatLonInViewport();
        Vehicle vehicle2 = new Vehicle(geoCoordinates2.latitude, geoCoordinates2.longitude);
        VehicleList.add(vehicle2);
        // Centered on location. Shown below the POI image to indicate the location.
        addPhotoMapMarker(geoCoordinates2);
        // Anchored, pointing to location.
        addCircleMapMarker(vehicle2, geoCoordinates2);

//        GeoCoordinates geoCoordinates = createRandomLatLonInViewport();
//
//        // Centered on location.
//        addPhotoMapMarker(geoCoordinates);
//
//        // Centered on location. Shown on top of the previous image to indicate the location.
//        addCircleMapMarker(geoCoordinates);
    }

    public void clearMap() {
        for (Vehicle v : VehicleList) {
            mapView.getMapScene().removeMapMarker(v.getMapMarker());
        }
        VehicleList.clear();
    }

    private GeoCoordinates createRandomLatLonInViewport() {
        GeoBox geoBox = mapView.getCamera().getBoundingBox();
        GeoCoordinates northEast = geoBox.northEastCorner;
        GeoCoordinates southWest = geoBox.southWestCorner;

        double minLat = southWest.latitude;
        double maxLat = northEast.latitude;
        double lat = getVehicleCoordinates(minLat, maxLat);

        double minLon = southWest.longitude;
        double maxLon = northEast.longitude;
        double lon = getVehicleCoordinates(minLon, maxLon);

        return new GeoCoordinates(lat, lon);
    }

    private double getVehicleCoordinates(double min, double max) {
        return min + Math.random() * (max - min);
    }

    private Vehicle getVehicleByMapMarker(MapMarker mapMarker) {
        for (Vehicle v : VehicleList) {
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

                Metadata metadata = topmostMapMarker.getMetadata();
                if (metadata != null) {
                    String message = "No message found.";
                    String string = metadata.getString("key_poi");
                    if (string != null) {
                        message = string;
                    }

                    Vehicle v = getVehicleByMapMarker(topmostMapMarker);
                    if(v != null) {
                        showDialog("V not null", message);
                    }
                    showDialog("Map Marker picked", message);
                    return;
                }

                showDialog("Map marker picked:", "Location: " +
                        topmostMapMarker.getCoordinates().latitude + ", " +
                        topmostMapMarker.getCoordinates().longitude);

                GeoCoordinates geoCoordinates = new GeoCoordinates(57.47749786251345, 23.0555073722648);
                topmostMapMarker.setCoordinates(geoCoordinates);
            }
        });
    }

    private void addPOIMapMarker(Vehicle v, GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.poi);

        MapMarker mapMarker = new MapMarker(geoCoordinates);
        v.setLatitude(geoCoordinates.latitude);
        v.setLongitude(geoCoordinates.longitude);

        // The bottom, middle position should point to the location.
        // By default, the anchor point is set to 0.5, 0.5.
        MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
        mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5F, 1));

        mapMarker.addImage(mapImage, mapMarkerImageStyle);

        Metadata metadata = new Metadata();
        metadata.setString("key_poi", "This is a POI.");
        mapMarker.setMetadata(metadata);

        v.setMapMarker(mapMarker);
        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void addPhotoMapMarker(GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.here_car);

        MapMarker mapMarker = new MapMarker(geoCoordinates);
        mapMarker.addImage(mapImage, new MapMarkerImageStyle());

        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void addCircleMapMarker(Vehicle v, GeoCoordinates geoCoordinates) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.circle);

        MapMarker mapMarker = new MapMarker(geoCoordinates);
        mapMarker.addImage(mapImage, new MapMarkerImageStyle());

        v.setLatitude(geoCoordinates.latitude);
        v.setLongitude(geoCoordinates.longitude);

        v.setMapMarker(mapMarker);
        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
