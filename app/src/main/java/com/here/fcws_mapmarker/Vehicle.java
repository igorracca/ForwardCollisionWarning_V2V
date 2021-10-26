package com.here.fcws_mapmarker;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;

public class Vehicle {

    private int id;
    private double latitude;
    private double longitude;
    private int velocity;
    private int heading;
    private MapImage mapImage;
    private MapMarker mapMarker;

    public void updateCoordinates(GeoCoordinates geoCoordinates){
        this.latitude = geoCoordinates.latitude;
        this.longitude = geoCoordinates.longitude;
        this.mapMarker.setCoordinates(geoCoordinates);
    }

    public Vehicle(int id, double latitude, double longitude, int velocity, int heading, MapImage mapImage, MapMarker mapMarker) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.velocity = velocity;
        this.heading = heading;
        this.mapImage = mapImage;
        this.mapMarker = mapMarker;
        this.mapMarker.addImage(this.mapImage, new MapMarkerImageStyle());
    }

    public Vehicle(double latitude, double longitude, MapMarker mapMarker) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.velocity = velocity;
        this.heading = heading;
        this.mapMarker = mapMarker;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public MapImage getMapImage() {
        return mapImage;
    }

    public void setMapImage(MapImage mapImage) {
        this.mapImage = mapImage;
    }

    public MapMarker getMapMarker() {
        return mapMarker;
    }

    public void setMapMarker(MapMarker mapMarker) {
        this.mapMarker = mapMarker;
    }
}
