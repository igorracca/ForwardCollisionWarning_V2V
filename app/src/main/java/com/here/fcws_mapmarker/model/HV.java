package com.here.fcws_mapmarker.model;

public class HV extends Vehicle {

    /*
    hv_lat = adr_data['RV'][0]['Lat']
    hv_lon = adr_data['RV'][0]['Lon']
    hv_elev = adr_data['RV'][0]['Elev']
    hv_heading = adr_data['RV'][0]['Heading']
    hv_speed = adr_data['RV'][0]['Speed']
    hv_speed_conv = float(rv_speed)*speed_conv
    */

    public HV (){
        super();
    }

    @Override
    public void updateParameters(Parameters vp) {
        super.updateParameters(vp);
        super.setHeading(vp.HV_Heading);
        super.setSpeed(vp.HV_Speed);
    }

}
