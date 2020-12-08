package com.here.fcws_mapmarker;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapMarker;

public class Vehicle {

    private GeoCoordinates geoCoordinates;
    private MapImage mapImage;
    private MapMarker mapMarker;
    private int velocity;
    private int heading;

    public Vehicle(GeoCoordinates geoCoordinates, MapImage mapImage, MapMarker mapMarker, int velocity, int heading) {
        this.geoCoordinates = geoCoordinates;
        this.mapImage = mapImage;
        this.mapMarker = mapMarker;
        this.velocity = velocity;
        this.heading = heading;
    }

    public Vehicle(GeoCoordinates geoCoordinates) {
        this.geoCoordinates = geoCoordinates;
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

    public GeoCoordinates getGeoCoordinates() {
        return geoCoordinates;
    }

    public void setGeoCoordinates(GeoCoordinates geoCoordinates) {
        this.geoCoordinates = geoCoordinates;
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

    @Override
    public String toString() {
        return "Vehicle{" +
                "geoCoordinates=" + geoCoordinates +
                ", velocity=" + velocity +
                ", heading=" + heading +
                '}';
    }
}
