package edu.bsu.cs639.safety_alert_entry;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Brandon on 9/25/2016.
 */

public class AlertGeoCoder {
    static String TAG = "GeoCoder";

    public static GeoPoint getLocationFromAddress(String strAddress){
        double lat=0, lon=0;

        String jsonResponse = getAddressLocationResponse(strAddress);

        return new GeoPoint(lat, lon);
    }

    private static String getAddressLocationResponse(String strAddress){
        String scrubbedAddress = strAddress.replaceAll("\\s+","\\+");
        String response = "";
        try {
            URL placeUrl = new URL(
                    "http://maps.googleapis.com/maps/api/geocode/json?address="
                            + scrubbedAddress
                            + "&sensor=true_or_false"
            );

            Log.d(TAG, "URL created");

            HttpURLConnection connection = (HttpURLConnection) placeUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Log.d(TAG, "made it here");

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = null;

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                response += buffer.toString();
                Log.d(TAG, response);
            } else {
                Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
            }
        } catch (Exception e){
            Log.e(TAG, "Something went wrong with the geocoder");
            Log.e(TAG, "Exception", e);
            response = null;
        }
        return response;
    }

}

/* Code snippet that might help...
private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
   ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected String[] doInBackground(String... params) {
        String response;
        try {
            response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=mumbai&sensor=false");
            Log.d("response",""+response);
            return new String[]{response};
        } catch (Exception e) {
            return new String[]{"error"};
        }
    }

    @Override
    protected void onPostExecute(String... result) {
        try {
            JSONObject jsonObject = new JSONObject(result[0]);

            double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("latitude", "" + lat);
            Log.d("longitude", "" + lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}


public String getLatLongByURL(String requestURL) {
    URL url;
    String response = "";
    try {
        url = new URL(requestURL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        } else {
            response = "";
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return response;
}
 */
