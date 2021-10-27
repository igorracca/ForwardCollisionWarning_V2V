package com.here.fcws_mapmarker;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;

public class Vehicle {

    private int id;
    private int velocity;
    private int heading;
    private MapImage mapImage;
    private MapMarker mapMarker;

    public Vehicle(double latitude, double longitude, MapMarker mapMarker) {
        this.velocity = velocity;
        this.heading = heading;
        this.mapMarker = mapMarker;
    }

    public void setCoordinates(double lat, double lon){
        GeoCoordinates geoCoordinates = new GeoCoordinates(lat, lon);
        this.mapMarker.setCoordinates(geoCoordinates);
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
