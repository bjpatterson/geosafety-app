package com.example.apple.alertpull;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class AlertPullevens extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,LocationListener {

    private GoogleMap mMap;
    private static final String BASE_URL = "http://10.64.83.126";
    private static final String PULL_URL = BASE_URL + "/alertPull.php";
    private static final LatLng BALL_STATE = new LatLng(40.2011, -85.4070);


    private List<AlertEvents> eventList;
    private Location mLocation;
    private String provider;

    Circle circle;
    Marker marker;
    Geocoder geocoder;
    public static final String Type = "DescribeEvents";
    public static final String Lng = "Longitude";
    public static final String Lat = "Latitude";
    public static final String ITEM_TIME = "CurrentTimeEvents";
    public static final String ITEM_LEVEL = "Level";
    public static final String ITEM_RADIUS = "Radius";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alert_pull);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.eventList = new ArrayList<>();
        this.mLocation = new Location("");
        this.mLocation.setLatitude(40.2011);
        this.mLocation.setLongitude(-85.4070);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        geocoder = new Geocoder(this);
        this.mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        initLocation();
        this.zoomToLocation(BALL_STATE, 15);
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

    }
    private void zoomToLocation(LatLng latLng, int degree) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, degree));
    }

    public void setMarkers(LatLng latLng, double radius, String title, double levelnumber) {
//        if (marker != null) {
//            marker.remove();
//            marker = null;
//
//        }
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        marker = mMap.addMarker(options);
        circle = drawCirle(latLng, radius, levelnumber);

    }

    public void lableMarkers(LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng);
        marker = mMap.addMarker(options);
    }

    public Circle drawCirle(LatLng latLng, double radius, double levelNumber) {
//        if (circle != null) {
//            circle.remove();
//        }
        int color = 0;
        if (levelNumber == 1) {
            color = getResources().getColor(R.color.colorAlertLevel1);
        }
        if (levelNumber == 2) {
            color = getResources().getColor(R.color.colorAlertLevel2);
        }
        if (levelNumber == 3) {
            color = getResources().getColor(R.color.colorAlertLevel3);
        }
        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(radius*100) // convert km to m
                .fillColor(color)
                .strokeColor(Color.GRAY)
                .strokeWidth(3);
        return mMap.addCircle(options);
    }
    public void onPull(View view){
        StringRequest stringRequest = new StringRequest(PULL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showJSON(response);
                System.out.println("xxx is here"+response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AlertPullevens.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        try{
            this.eventList.clear();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("orders");
            System.out.println("jsonArray is here "+ jsonArray);
            for(int i=0; i<jsonArray.length();i++){
                double lat = Double.valueOf(jsonArray.getJSONObject(i).get(Lat).toString());
                double lng = Double.valueOf(jsonArray.getJSONObject(i).get(Lng).toString());
                double radius = Double.valueOf(jsonArray.getJSONObject(i).get(ITEM_RADIUS).toString());
                double level = Double.valueOf(jsonArray.getJSONObject(i).get(ITEM_LEVEL).toString());
                String date = (String)jsonArray.getJSONObject(i).get(ITEM_TIME);
                String event = (String)jsonArray.getJSONObject(i).get(Type);
                int id = Integer.valueOf(jsonArray.getJSONObject(i).get("id").toString());
                AlertEvents bsuEvent = new AlertEvents(id, lat, lng, event, date,level,radius);
                this.eventList.add(bsuEvent);
                System.out.println("evenlist is here "+ eventList);
                System.out.println("bsuevent: "+bsuEvent);
                LatLng latLng = new LatLng(lat,lng);
                setMarkers(latLng,radius,event,level);

            }
            mMap.clear();


            addMarkers(mLocation);

        }catch(JSONException e){

        }
    }
    private void addMarkers(Location userLocation) {
        for(AlertEvents e : eventList){
//            float distance=e.distanceToUser(userLocation);
            Location eventLocation = new Location("");
            eventLocation.setLatitude(e.getLat());
            eventLocation.setLongitude(e.getLng());
            double levelnumber = e.getLevel();
            double radius = e.getRadius();
//            System.out.println("Distance "+distance);
//            if(distance<=100){
//                e.setType("Level 4  "+e.getType());
//            }
//            else if(distance<=200){
//                e.setType("Level 3 "+e.getType());
//            }
//            else if(distance<=500){
//                e.setType("Level 2 "+e.getType());
//            }
//            else{
//                e.setType("Level 1 "+e.getType());
//            }
            LatLng event=new LatLng(e.getLat(),e.getLng());

            mMap.addMarker(new MarkerOptions().title(e.getType()).position(event).snippet(e.getDatetime()));
            circle = drawCirle(event, radius, levelnumber);
        }
        LatLng latLng = new LatLng(userLocation.getLatitude(),userLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

    }

    private void initLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(AlertPullevens.this, "no provider is currently available", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        try{
            //this.mLocation = locationManager.getLastKnownLocation(provider);
            System.out.println(mLocation+"My location");
            locationManager.requestLocationUpdates(provider,5000,10,this);
            //locationManager.requestLocationUpdates(provider, 5000, 10, this);
        }
        catch (SecurityException e){
            System.out.println("Security Exception "+ e);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        this.mLocation = location;
        System.out.println("My location!!!!"+mLocation);
        this.onPull(null);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 15));
    }
    @Override
    public boolean onMarkerClick(final Marker marker){
//        Location markerLocation = new Location(" ");
//        markerLocation.setLongitude(marker.getPosition().longitude);
//        markerLocation.setLatitude(marker.getPosition().latitude);
//        float radius = this.mLocation.distanceTo(markerLocation);
//        int color = 0;
//        if(radius<=100){
//            color = Color.RED;
//        }
//        else if(radius<=200){
//            color = Color.CYAN;
//        }
//        else if(radius<=500){
//            color = Color.BLUE;
//        }
//        else{
//            color = Color.LTGRAY;
//        }
//        if (circle != null){
//            circle.remove();
//        }
//        circle = mMap.addCircle(new CircleOptions().center(marker.getPosition()).
//                radius(radius).fillColor(color).strokeWidth(1));
//        marker.showInfoWindow();
        return true;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

