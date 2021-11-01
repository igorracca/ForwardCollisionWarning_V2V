package com.here.fcws_mapmarker.model;

public class RV extends Vehicle {

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

    private int rxcount;
    private double rssi;

    public RV() {
        super();
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
}
