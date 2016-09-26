package edu.bsu.cs639.safety_alert_entry;

/**
 * Created by Brandon on 9/26/2016.
 */

public class GeoPoint {
    double lat, lon;

    public GeoPoint(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }
}
