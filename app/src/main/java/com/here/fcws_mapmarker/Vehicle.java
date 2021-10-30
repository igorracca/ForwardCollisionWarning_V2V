package com.here.fcws_mapmarker;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;

import java.io.Serializable;

public class Vehicle implements Serializable {

    private int id;
    private int velocity;
    private int heading;
    private MapImage mapImage;
    private MapMarker mapMarker;

    public Vehicle() {}

    public Vehicle(double latitude, double longitude, MapMarker mapMarker) {
        this.velocity = velocity;
        this.heading = heading;
        this.mapMarker = mapMarker;
    }

    public boolean hasCoordinates() {
        return getMapMarker() != null;
    }

    public void addCoordinates(double lat, double lon, MapMarker mapMarker){
        GeoCoordinates geoCoordinates = new GeoCoordinates(lat, lon);
        this.mapMarker = mapMarker;
    }

    public void setCoordinates(double lat, double lon){
        GeoCoordinates geoCoordinates = new GeoCoordinates(lat, lon);
        this.mapMarker = mapMarker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return mapMarker.getCoordinates().latitude;
    }

    public double getLongitude() {
        return mapMarker.getCoordinates().longitude;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getHeading() {
        return heading;
    }

    public MapImage getMapImage() {
        return mapImage;
    }

    public MapMarker getMapMarker() {
        return mapMarker;
    }

}
