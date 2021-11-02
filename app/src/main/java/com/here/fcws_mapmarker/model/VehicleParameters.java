package com.here.fcws_mapmarker.model;

public class VehicleParameters {

    public double lat;
    public double lon;
    public double elev;
    public double heading;
    public double speed;
    // RV only
    public int rxcount;
    public double rssi;

    public VehicleParameters(double lat, double lon, double elev, double heading, double speed) {
        this.lat = lat;
        this.lon = lon;
        this.elev = elev;
        this.heading = heading;
        this.speed = speed;
    }

    public VehicleParameters(double lat, double lon, double elev, double heading, double speed, int rxcount, double rssi) {
        this.lat = lat;
        this.lon = lon;
        this.elev = elev;
        this.heading = heading;
        this.speed = speed;
        this.rxcount = rxcount;
        this.rssi = rssi;
    }
}
