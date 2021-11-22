package com.here.fcws_mapmarker.model;

import java.io.Serializable;
import java.util.Date;

public class Parameters implements Serializable {

    public int timestamp;
    public String time;

    public double HV_Lat;
    public double HV_Lon;
    public double HV_Elev;
    public double HV_Heading;
    public double HV_Speed;

    public int RV_Id;
    public int RV_SeqNbr;
    public double RV_Lat;
    public double RV_Lon;
    public double RV_Elev;
    public double RV_Heading;
    public double RV_Speed;
    public int RV_RSSI;
    public int RV_RxCnt;

    public double Dist;
    public double ttc;

    public Parameters(){}

    public Parameters(int ts, String time) {
        this.timestamp = ts;
        this.time = time;
    }

    public void setHVParameters(double HV_lat, double HV_lon, double HV_elev, double HV_heading, double HV_speed) {
        this.HV_Lat = HV_lat;
        this.HV_Lon = HV_lon;
        this.HV_Elev = HV_elev;
        this.HV_Heading = HV_heading;
        this.HV_Speed = HV_speed;
    }

    public void setRVParameters(int RV_Id, int RV_SeqNbr, double RV_Lat, double RV_Lon, double RV_Elev, double RV_Heading, double RV_Speed, int RV_RSSI, int RV_RxCnt) {
        this.RV_Id = RV_Id;
        this.RV_SeqNbr = RV_SeqNbr;
        this.RV_Lat = RV_Lat;
        this.RV_Lon = RV_Lon;
        this.RV_Elev = RV_Elev;
        this.RV_Heading = RV_Heading;
        this.RV_Speed = RV_Speed;
        this.RV_RSSI = RV_RSSI;
        this.RV_RxCnt = RV_RxCnt;
    }

    public void setTtc(double dist, double ttc) {
        this.Dist = dist;
        this.ttc = ttc;
    }
}
