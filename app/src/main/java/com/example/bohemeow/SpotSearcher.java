package com.example.bohemeow;

import android.content.Context;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class SpotSearcher {

    //private static String region;
    //좌표를 조회해서 현재 시, 구 정보 가져올 수 있도록 추후 수정
    String region = "Jangan-gu, Suwon-si";
    String region_limit = "장안구";
    String type = "park";

    public String page_token = "";
    String key = "AIzaSyBHSgVqZUvi8EmRbrZsH9z6whHSO-R3LXo";

    private Context mContext = null;

    public SpotSearcher(Context context) {
        this.mContext = context;
    }




    void Search(String reg){

        region = reg;

        final ArrayList<String> placeIDs = new ArrayList<>();


        new Thread() {
            public void run() {

                String result = getSpots(region, type, false);
                placeIDs.addAll(jsonparser(result));

                for(int i = 1; i < 3; i++) {
                    //System.out.println("\npage_token = " + page_token);
                    if(page_token.equals("")) break;
                    result = getSpots(region, type, true);
                    placeIDs.addAll(jsonparser(result));
                }
                SpotFilter sf = new SpotFilter(mContext);
                sf.FeatureCalculator(placeIDs, region, 25);
            }
        }.start();


    }

    public String getSpots(String region, String type, boolean nextPage) {

        String uri;

        if(nextPage){
            uri = "https://maps.googleapis.com/maps/api/place/textsearch/json?pagetoken=" + page_token + "&key=" + key;
        }else{
            uri = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + type + "+in+" + region +
                    "&region=kr&language=ko&type="+ type +
                    "&key=" + key;
        }
        System.out.println("\nURI = " + uri);
        String page = "";

        try {
            Thread.sleep(1500);
            URL url = new URL(uri);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());

            String line;

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
    public ArrayList<String> jsonparser(String page) {
        ArrayList<String> placeIDs = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(page);
            page_token = jsonObject.optString("next_page_token");
            String results = jsonObject.getString("results");
            JSONArray jsonArray = new JSONArray(results);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                //String formatted_address = subJsonObject.getString("formatted_address");
                //구단위 필터링을 원하면 아래 주석 사용
                //if(formatted_address.contains(region_limit)) {
                    String name = subJsonObject.getString("name");
                    String place_id = subJsonObject.getString("place_id");
                    placeIDs.add(place_id);

                    //test print
                    System.out.println("\nnum: " + i +
                            "\tname: " + name +
                            "\tid: " + place_id);
                //}
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //page_token = "";
            return placeIDs;
        }

        return placeIDs;
    }

}
