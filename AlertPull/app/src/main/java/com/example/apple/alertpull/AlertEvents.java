package com.example.apple.alertpull;

import android.location.Location;

/**
 * Created by apple on 9/29/16.
 */

public class AlertEvents {
    private int id;
    private String datetime;
    private double lat;
    private double lng;
    private String type;
    private double level;
    private double radius;
    public AlertEvents(){
        id=0;
        datetime="";
        lat=0.0;
        lng=0.0;
        type="";
        level=0.0;
        radius =0.0;


    }
    /**
     *
     * @param id
     * @param lat
     * @param lng
     * @param type
     * @param datetime
     */
    public AlertEvents(int id, double lat, double lng,String type, String datetime,double level,double radius){
        this.id=id;
        this.lat=lat;
        this.lng=lng;
        this.type=type;
        this.datetime=datetime;
        this.radius = radius;
        this.level = level;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDatetime() {
        return datetime;
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public double getLevel() {
        return level;
    }
    public void setLevel(double level) {
        this.level = level;
    }
    public double getRadius() {
        return radius;
    }
    public void setRadius(double lng) {
        this.radius = radius;
    }

    @Override
    public String toString(){
        return "Lat "+this.lat+" Long "+this.lng + " Type "+this.type + " Level "+this.level+" Radius "+this.radius;
    }

    public float distanceToUser(Location userLocation) {
        Location eventLocation = new Location("");
        eventLocation.setLatitude(this.lat);
        eventLocation.setLongitude(this.lng);
        float distance = userLocation.distanceTo(eventLocation);
        return distance;
    }
}