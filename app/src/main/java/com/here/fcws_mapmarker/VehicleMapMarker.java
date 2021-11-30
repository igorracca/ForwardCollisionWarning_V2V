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
import android.util.Log;

import com.here.fcws_mapmarker.model.HV;
import com.here.fcws_mapmarker.model.RV;
import com.here.fcws_mapmarker.model.Vehicle;
import com.here.fcws_mapmarker.model.Parameters;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.gestures.GestureType;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapviewlite.Camera;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PickMapItemsCallback;
import com.here.sdk.mapviewlite.PickMapItemsResult;

import java.util.List;

public class VehicleMapMarker {

    private Context context;
    private MapViewLite mapView;
    private static List<Vehicle> vehicleList;

    Camera camera = null;

    public static final int NORMAL_AWARENESS = 2;
    public static final int WARNING = 1;
    public static final int PRE_CRASH = 0;
    public static final boolean DEBUG = Boolean.parseBoolean(App.getRes().getString(R.string.debug_mode));
    public static final double ttc_awareness = Double.parseDouble(App.getRes().getString(R.string.ttc_awareness));
    public static final double ttc_warning = Double.parseDouble(App.getRes().getString(R.string.ttc_warning));
    public static final double ttc_pre_crash = Double.parseDouble(App.getRes().getString(R.string.ttc_pre_crash));

    public VehicleMapMarker(Context context, MapViewLite mapView, List<Vehicle> vehicleList) {
        this.context = context;
        this.mapView = mapView;
        this.vehicleList = vehicleList;

        this.camera = mapView.getCamera();
        this.camera.setTarget(new GeoCoordinates(48.22, 16.42));
        this.camera.setZoomLevel(12);
        // Setting a tap handler to pick markers from map
        setTapGestureHandler();

        // disabling pinch rotate gesture
        this.mapView.getGestures().disableDefaultAction(GestureType.PINCH_ROTATE);

        showCenteredMapMarkers();
    }

    public static List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void showCenteredMapMarkers() {
        for(Vehicle v: vehicleList) {
            GeoCoordinates geoCoordinates = new GeoCoordinates(0, 0); // 47.47749786251345, 19.0555073722648
            // Centered on location. Shown below the POI image to indicate the location.
            v.setCoordinates(addPhotoMapMarker(v, geoCoordinates));
        }
    }

    public void centerMapView() {
        Vehicle v = vehicleList.get(0);
        if(v != null) {
            this.camera = mapView.getCamera();
            this.camera.setTarget(new GeoCoordinates(v.getLatitude(), v.getLongitude()));
            this.camera.setZoomLevel(16);
        }
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
                    if(DEBUG) Log.d("MapMarker Activity", "V is null");
                    return;
                }
                if(DEBUG) Log.d("MapMarker Activity", "V not null");

                Metadata metadata = topmostMapMarker.getMetadata();
                if (metadata != null) {
                    String message = "No message found.";
                    String string = metadata.getString("key_poi");
                    if (string != null) {
                        message = string;
                    }
                }

                if(DEBUG) Log.d("MapMarker Activity", "Location: " +
                        topmostMapMarker.getCoordinates().latitude + ", " +
                        topmostMapMarker.getCoordinates().longitude);
                showDialog("Vehicle picked:", "Location: " +
                        topmostMapMarker.getCoordinates().latitude + ", " +
                        topmostMapMarker.getCoordinates().longitude);
            }
        });
    }

    private MapMarker addPhotoMapMarker(Vehicle v, GeoCoordinates geo) {
        MapMarker mapMarker = new MapMarker(geo);
        MapImage mapImage = null;

        if(v != null) {
            if(v.getClass() == HV.class) {
                mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.hv);
            } else
            if( v.getClass() == RV.class) {
                mapImage = MapImageFactory.fromResource(context.getResources(), R.drawable.rv);
            }

            mapMarker.addImage(mapImage, new MapMarkerImageStyle());
            mapView.getMapScene().addMapMarker(mapMarker);
        }
        return mapMarker;
    }

    private static void updatePhotoMapMarker(Vehicle v) {
        MapMarker mapMarker = v.getMapMarker();

        MapMarkerImageStyle imgStyle = new MapMarkerImageStyle();
        // map default orientation
        imgStyle.setAngle((float) v.getHeading() + 270);
        mapMarker.updateImageStyle(imgStyle);
    }

    public static void updateVehicleAttributes(Parameters vp) {
        if(vehicleList.size() == 0) {
            if(DEBUG) Log.d("updateVehiclePosition", "vehicle list empty");
        } else {
            // UPDATE HV
            Vehicle hv = vehicleList.get(0);
            if (hv.hasCoordinates()) {
                // set vehicle parameters
                hv.updateParameters(vp);
                GeoCoordinates HV_geo = new GeoCoordinates(vp.HV_Lat, vp.HV_Lon);
                // and update vehicle in the map
                hv.getMapMarker().setCoordinates(HV_geo);
                // update heading of the vehicle in the map
                updatePhotoMapMarker(hv);
            } else {
                if(DEBUG) Log.d("updateVehiclePosition", "HV does not have coordinates");
            }
            if(DEBUG) Log.d("updateVehiclePosition", "HV updated. lat: " + hv.getLatitude() + " lon:" + hv.getLongitude() + " heading:" + hv.getHeading());

            // UPDATE RV
            Vehicle rv = vehicleList.get(1);
            if (rv.hasCoordinates()) {
                // set vehicle parameters
                rv.updateParameters(vp);
                GeoCoordinates RV_geo = new GeoCoordinates(vp.RV_Lat, vp.RV_Lon);
                // and update vehicle in the map
                rv.getMapMarker().setCoordinates(RV_geo);
                // update heading of the vehicle in the map
                updatePhotoMapMarker(rv);
            } else {
                if(DEBUG) Log.d("updateVehiclePosition", "RV does not have coordinates");
            }
            if(DEBUG) Log.d("updateVehiclePosition", "RV updated. lat: " + rv.getLatitude() + " lon:" + rv.getLongitude() + " heading:" + rv.getHeading());
        }
    }

    // Calculates metric distance from GPS latlon
    public static double gpsToDist(double lat1, double lon1, double lat2, double lon2) {
        // Approximate radius of earth in km
        double R = 6373.0;

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a = Math.pow( Math.sin(dlat/2) , 2 ) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow( Math.sin(dlon/2), 2);

        double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );

        double distance = (R * c) * 1000;

        return distance;
    }

    // Calculates actual TTC (Time to collision)
    public static double ttc(double distancevv, double speed1, double speed2) {
        double v_delta = Math.abs(speed1 - speed2);
        double ttc = 0;

        if(v_delta != 0) {
            ttc = distancevv / v_delta;
        } else {
            ttc = distancevv;
        }
        return ttc;
        // ?? distvv = floor(distanceVV / 10)
    }

    public static int FWC(double ttc, double speed1, double speed2) {
        ttc = ttc * 1000;
        if(speed1 > speed2) {
            if (ttc <= ttc_pre_crash) {
                return PRE_CRASH;
            } else if (ttc <= ttc_warning) {
                return WARNING;
            }
        }
        return NORMAL_AWARENESS;
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
