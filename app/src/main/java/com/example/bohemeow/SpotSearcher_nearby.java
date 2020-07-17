package com.example.bohemeow;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SpotSearcher_nearby extends AppCompatActivity {

    double lat = 37.2939299;
    double lng = 126.9739263;
    int radius = 2000;

    String region = "Jangan-gu, Suwon-si";
    String type = "park";

    public String page_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[][] placeIDs = new String[3][20];

        final SecondFilter sf = new SecondFilter(this);

        new Thread() {
            public void run() {


                String result = getSpots(region, type, false);
                placeIDs[0] = jsonparser(result);

                for(int i = 1; i < 3; i++) {
                    //System.out.println("\npage_token = " + page_token);
                    if(page_token.equals("")) break;
                    result = getSpots(region, type, true);
                    placeIDs[i] = jsonparser(result);
                }
                sf.FeatureCalculator(placeIDs);
            }
        }.start();


    }

    public String getSpots(String region, String type, boolean nextPage) {
/*
        String uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng +
                "&radius=" + radius +
                "&type=" + type +
                "&keyword=" + keyword +
                "&language=ko&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";
 */
        String uri = new String();

        if(nextPage){
            uri = "https://maps.googleapis.com/maps/api/place/textsearch/json?pagetoken=" + page_token + "&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";
        }else{
            uri = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + type + "+in+" + region +
                    "&region=kr&language=ko&type="+ type +
                    "&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";
        }
        System.out.println("\nURI = " + uri);
        String page = "";

        try {
            Thread.sleep(1000);
            URL url = new URL(uri);
            URLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());

            String line = null;

            while ((line = bufreader.readLine()) != null) {
                Log.d("line:", line);
                page += line;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return page;
    }

    //for test print
    public String[] jsonparser(String page) {
        String[] placeIDs = new String[20];

        try{
            JSONObject jsonObject = new JSONObject(page);
            page_token = jsonObject.optString("next_page_token");
            String results = jsonObject.getString("results");
            JSONArray jsonArray = new JSONArray(results);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                String name = subJsonObject.getString("name");
                String place_id = subJsonObject.getString("place_id");
                placeIDs[i] = place_id;

                //test print
                System.out.println("\nnum: " + i +
                        "\tname: " + name +
                        "\tid: " + place_id);


            }
        } catch (JSONException e) {
            e.printStackTrace();
            //page_token = "";
            return placeIDs;
        }

        return placeIDs;
    }






}
