package com.example.bohemeow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SpotSearcher_nearby extends AppCompatActivity {

    double lat = 37.2939299;
    double lng = 126.9739263;

    int radius = 2000;
    String type = "park";
    String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SecondFilter sf = new SecondFilter(this);

        new Thread() {
            public void run() {
                String result = getSpots(lat, lng, radius, type);
                jsonparser(result);
                sf.FeatureCalculator(result);
                //sf.SpotFilter();

                Bundle bun = new Bundle();
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            }
        }.start();


    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

        }
    };

    public String getSpots(double lat, double lng, int radius, String type) {

        String uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng +
                "&radius=" + radius +
                "&type=" + type +
                "&keyword=" + keyword +
                "&language=ko&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";

        String page = "";

        try {
            URL url = new URL(uri);
            URLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());

            String line = null;

            while ((line = bufreader.readLine()) != null) {
                //Log.d("line:", line);
                page += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return page;
    }

    //for test print
    public void jsonparser(String page) {

        try{
            JSONObject jsonObject = new JSONObject(page);
            String results = jsonObject.getString("results");
            JSONArray jsonArray = new JSONArray(results);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                String name = subJsonObject.getString("name");
                String place_id = subJsonObject.getString("place_id");

                /*
                String geometry = subJsonObject.getString("geometry");
                JSONObject subJsonObject2 = new JSONObject(geometry);
                String location = subJsonObject2.getString("location");
                JSONObject subJsonObject3 = new JSONObject(location);
                String sub_lat = subJsonObject3.getString("lat");
                String sub_lng = subJsonObject3.getString("lng");
                */
                //test print
                System.out.println("\nnum: " + i +
                        "\nname: " + name +
                        "\tid: " + place_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }






}
