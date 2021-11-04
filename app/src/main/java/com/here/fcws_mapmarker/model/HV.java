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
    public void updateParameters(VehiclesParameters vp) {
        super.updateParameters(vp);
        super.setHeading(vp.HV_Heading);
        super.setSpeed(vp.HV_Speed);
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

}
