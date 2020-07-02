package com.example.bohemeow;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

class place {
    public String place_id;
    public String name;

    public double lat;
    public double lng;

    ArrayList<String> types = new ArrayList<String>();
    public int popularity = -1;
    public int safe_score = -1;
    public int envi_score = -1;
    public int user_score = -1;

    public int visitor = -1;
    public int visitor_time = -1;
    public int visitor_week = -1;

    public int total_score = -1;
}

public class SecondFilter {

    private Context mContext = null;

    public SecondFilter(Context context) {
        this.mContext = context;
    }

    public void FeatureCalculator(String searched){
        //make id list
        ArrayList<String> arr = new ArrayList<String>();
        ArrayList<place> Spots = new ArrayList<place>();

        try{
            JSONObject jsonObject = new JSONObject(searched);
            String results = jsonObject.getString("results");
            JSONArray jsonArray = new JSONArray(results);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                //String name = subJsonObject.getString("name");
                String id = subJsonObject.getString("id");

                arr.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private place Calculator(String id){
        place spot = new place();
        ArrayList<String> types = new ArrayList<String>();

        String uri = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + id +
                "&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";
        URL url = null;
        URLConnection urlConnection = null;
        BufferedInputStream buf = null;

        String page = "";

        try {
            url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());

            String line = null;

            while ((line = bufreader.readLine()) != null) {
                //Log.d("line:", line);
                page += line;
            }

            JSONObject jsonObject = new JSONObject(page);
            String result = jsonObject.getString("result");
            JSONObject subJsonObject = new JSONObject(result);
            spot.name = subJsonObject.getString("name");

            String geometry = subJsonObject.getString("geometry");
            JSONObject subJsonObject2 = new JSONObject(geometry);
            String location = subJsonObject2.getString("location");
            JSONObject subJsonObject3 = new JSONObject(location);
            spot.lat = Double.parseDouble(subJsonObject3.getString("lat"));
            spot.lng = Double.parseDouble(subJsonObject3.getString("lng"));

            //type 배열로 저장
            String tps = jsonObject.getString("types");
            JSONArray jsonArray = new JSONArray(tps);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        spot.safe_score = safe_calculator(spot.lat, spot.lng);
        spot.envi_score = envi_calculator(spot.lat, spot.lng);
        spot.total_score = spot.safe_score + spot.envi_score + spot.user_score;

        return spot;
    }

    int safe_calculator(double lat, double lng){
        double score = 0;

        return (int)score;
    }

    int envi_calculator(double lat, double lng){
        double score = 0;

        return (int)score;
    }


    //---------------------------------------------------------------------------------
    public void SpotFilter(){


    }
}
