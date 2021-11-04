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

    private int Id;
    private int SeqNbr;
    private int RSSI;
    private int RxCnt;

    public RV() {
        super();
    }

    @Override
    public void updateParameters(VehiclesParameters vp) {
        super.updateParameters(vp);
        this.Id = vp.RV_Id;
        this.SeqNbr = vp.RV_SeqNbr;
        super.setHeading(vp.RV_Heading);
        super.setSpeed(vp.RV_Speed);
        this.RSSI = vp.RV_RSSI;
        this.RxCnt = vp.RV_RxCnt;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getSeqNbr() {
        return SeqNbr;
    }

    public void setSeqNbr(int seqNbr) {
        SeqNbr = seqNbr;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public int getRxCnt() {
        return RxCnt;
    }

    public void setRxCnt(int rxCnt) {
        RxCnt = rxCnt;
    }
}
