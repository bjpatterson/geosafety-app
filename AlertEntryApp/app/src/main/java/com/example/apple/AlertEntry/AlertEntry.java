package com.example.apple.AlertEntry;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.apple.maptesting2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertEntry extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private static final String BASE_URL = "http://10.230.120.9";
    private static final String PULL_URL = BASE_URL + "/alertPull.php";
    private static final String REGISTER_URL = BASE_URL + "/volleyPost.php";
    private static final LatLng BALL_STATE = new LatLng(40, -85);
    private List<Marker> markers;
    Circle circle;
    Marker marker;
    Geocoder geocoder;

    //    EditText locationTextField = (EditText)findViewById(R.id.address);
//    String location = locationTextField.getText().toString();
//    EditText locationradius = (EditText)findViewById(R.id.alertRadius);
//    EditText crimeTypeField = (EditText)findViewById(R.id.alertType);
//    String type = crimeTypeField.getText().toString();
//    double setradius = Double.parseDouble(locationradius.getText().toString());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markers = new ArrayList<>();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        geocoder = new Geocoder(this);

        this.mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        this.zoomToLocation(BALL_STATE, 15);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            EditText locationTextField = (EditText) findViewById(R.id.address);

            @Override
            public void onMapLongClick(LatLng latLng) {

                List<Address> addresses = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                android.location.Address address = addresses.get(0);


                if (address != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append("\n");
                    }
                    Toast.makeText(AlertEntry.this, sb.toString(), Toast.LENGTH_LONG).show();
                    locationTextField.setText(sb.toString());
                }

                //remove previously placed Marker
                if (marker != null) {
                    marker.remove();
                }

                //place marker where user just clicked

                lableMarkers(latLng);

            }

        });

    }

    private void zoomToLocation(LatLng latLng, int degree) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, degree));
    }

    public void onSubmit(View view) {
        Log.d("MAP", "pressed submit button!");
        EditText locationTextField = (EditText) findViewById(R.id.address);
        EditText locationRadiusField = (EditText) findViewById(R.id.alertRadius);
        EditText crimeTypeField = (EditText) findViewById(R.id.alertType);
        EditText alertLevelField = (EditText) findViewById(R.id.alertlevel);

        String location = locationTextField.getText().toString();
        final String title = crimeTypeField.getText().toString();
        final String radius = locationRadiusField.getText().toString();
        final String alertLevel = alertLevelField.getText().toString();

        if (!location.equals("") && !title.equals("")) {
            List<Address> locationArray = null;
            Geocoder coder = new Geocoder(this);
            try {
                locationArray = coder.getFromLocationName(location, 3);
            } catch (IOException e) {
                Log.e("MAP", "something went wrong", e);
            }
            if (locationArray != null && !locationArray.isEmpty()) {
                // database should add this event!
                final String currentTime = DateFormat.getDateTimeInstance().format(new Date());
                final Address address = locationArray.get(0);
                final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                this.addMarker(latLng, title, "Happened at " + currentTime);

                this.zoomToLocation(latLng, 12);
                //setMarkers(latLng,setradius,type);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(AlertEntry.this, response, Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(getApplicationContext(), "Communication Error!", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ServerError) {
                                    Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof NetworkError) {
                                    Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                                } else if (error instanceof ParseError) {
                                    Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("DescribeEvents", title);
                        params.put("Latitude", Double.toString(address.getLatitude()));
                        params.put("Longtitude", Double.toString(address.getLongitude()));
                        params.put("CurrentTimeEvents", currentTime);
                        params.put("Level", alertLevel);
                        params.put("Radius", radius);
                        System.out.println("This map param: " + params);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                System.out.println(stringRequest + " string request");
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        }
    }

    private void addMarker(LatLng latLng, String title, String snippet) {
        Marker marker = this.mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(snippet));
        System.out.println("Marker is here " + marker);
        System.out.println("Markers are here " + markers);
        this.markers.add(marker);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        String selectedMarkerID = marker.getId();
        System.out.println(selectedMarkerID + " Marker is Selected");
        marker.showInfoWindow();
        return true;
    }

    public void onSearch(View view) {
        Log.d("MAP", "pressed search button!");
        EditText locationTextField = (EditText) findViewById(R.id.address);
        EditText locationRadiusField = (EditText) findViewById(R.id.alertRadius);
        EditText crimeTypeField = (EditText) findViewById(R.id.alertType);
        EditText alertlevelField = (EditText) findViewById(R.id.alertlevel);

        String location = locationTextField.getText().toString();
        String title = crimeTypeField.getText().toString();
        double radius = Double.parseDouble(locationRadiusField.getText().toString());
        double alertLevel = Double.parseDouble(alertlevelField.getText().toString());

        if (!location.equals("")) {
            List<Address> locationArray = null;
            Geocoder coder = new Geocoder(this);
            try {
                locationArray = coder.getFromLocationName(location, 3);
            } catch (IOException e) {
                Log.e("MAP", "something went wrong", e);
            }
            if (locationArray != null && !locationArray.isEmpty()) {
                final Address address = locationArray.get(0);
                final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                this.zoomToLocation(latLng, 12);
                setMarkers(latLng, radius, title, alertLevel);
            }
        }
    }

    public void setMarkers(LatLng latLng, double radius, String title, double levelnumber) {
        if (marker != null) {
            marker.remove();
            marker = null;

        }
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
        if (circle != null) {
            circle.remove();
        }
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
                .radius(radius*1000) // convert km to m
                .fillColor(color)
                .strokeColor(Color.GRAY)
                .strokeWidth(3);
        return mMap.addCircle(options);
    }
}
