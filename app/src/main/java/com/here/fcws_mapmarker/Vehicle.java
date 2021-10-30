package com.here.fcws_mapmarker;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;

import java.io.Serializable;

public class Vehicle implements Serializable {
    /*
    rv_lat = adr_data['RV'][0]['Lat']
    rv_lon = adr_data['RV'][0]['Lon']
    rv_elev = adr_data['RV'][0]['Elev']
    rv_heading = adr_data['RV'][0]['Heading']
    rv_speed = adr_data['RV'][0]['Speed']
    rv_speed_conv = float(rv_speed)*speed_conv
    rv_rxcount = adr_data['RV'][0]['RxCnt']
    rv_rssi = adr_data['RV'][0]['RSSI']
     */

    private enum vehicleType {
        HV, //host vehicle
        RM  //remote vehicle
    }

    private int id;
    private MapMarker mapMarker; // contains lat, lon and alt
    private double heading;
    private double speed;
    private int rxcount;
    private double rssi;
    private MapImage mapImage;

    public Vehicle() {}

    public boolean hasCoordinates() {
        return getMapMarker() != null;
    }

    public void setCoordinates(MapMarker mapMarker){
        this.mapMarker = mapMarker;
    }

    // Calculates metric distance from GPS latlon
    public double gpsToDist(double lat2, double lon2) {
        // Approximate radius of earth in km
        double R = 6373.0;

        double lat1 = this.getLatitude();
        double lon1 = this.getLongitude();
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
    public double ttc(double speed2, double lat2, double lon2) {
        double speed1 = this.getSpeed();
        double v_delta = Math.abs(speed1 - speed2);
        double distancevv = gpsToDist(lat2, lon2);

        double ttc = 0;
        if(v_delta != 0) {
            ttc = distancevv / v_delta;
        } else {
            ttc = distancevv;
        }
        return ttc;
        // ?? distvv = floor(distanceVV / 10)
    }

    public double getLatitude() {
        return mapMarker.getCoordinates().latitude;
    }

    public double getLongitude() {
        return mapMarker.getCoordinates().longitude;
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

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getRxcount() {
        return rxcount;
    }

    public void setRxcount(int rxcount) {
        this.rxcount = rxcount;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public MapImage getMapImage() {
        return mapImage;
    }

    public void setMapImage(MapImage mapImage) {
        this.mapImage = mapImage;
    }


}