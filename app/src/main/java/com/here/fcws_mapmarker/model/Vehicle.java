package com.here.fcws_mapmarker.model;

import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapMarker;

import java.io.Serializable;

public class Vehicle implements Serializable {

    private int id;
    private MapMarker mapMarker; // contains lat, lon and alt
    private double heading;
    private double speed;
    private double speed_conv;
    private MapImage mapImage;

    public Vehicle() {}

    public boolean hasCoordinates() {
        return getMapMarker() != null;
    }

    public void setCoordinates(MapMarker mapMarker){
        this.mapMarker = mapMarker;
    }

    public double getLatitude() {
        return mapMarker.getCoordinates().latitude;
    }

    public double getLongitude() {
        return mapMarker.getCoordinates().longitude;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        this.speed_conv = speed * 3.6; // m/s to km/h
    }

    public void updateParameters(VehicleParameters vp) {
        setHeading(vp.heading);
        setSpeed(vp.speed);
    }

    // default Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MapMarker getMapMarker() {
        return mapMarker;
    }

    public void setMapMarker(MapMarker mapMarker) {
        this.mapMarker = mapMarker;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public double getSpeed_conv() {
        return speed_conv;
    }

    public MapImage getMapImage() {
        return mapImage;
    }

    public void setMapImage(MapImage mapImage) {
        this.mapImage = mapImage;
    }

}