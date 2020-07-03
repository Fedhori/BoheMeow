package com.example.bohemeow;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Place {
    public String place_id;
    public String name;

    double lat;
    double lng;

    ArrayList<String> types = new ArrayList<String>();
    int popularity = -1;
    int safe_score = -1;
    int envi_score = -1;
    int user_score = -1;

    int visitor = -1;
    int visitor_time = -1;
    int visitor_week = -1;

    int total_score = -1;
}

public class SecondFilter {

    private Context mContext = null;

    public SecondFilter(Context context) {
        this.mContext = context;
    }


    public void FeatureCalculator(String searched){

        ArrayList<Place> Spots = new ArrayList<>();

        try{
            JSONObject jsonObject = new JSONObject(searched);
            String results = jsonObject.getString("results");
            JSONArray jsonArray = new JSONArray(results);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subJsonObject = jsonArray.getJSONObject(i);
                String place_id = subJsonObject.getString("place_id");

                Spots.add(Calculator(place_id));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SpotFilter(Spots);
    }

    private Place Calculator(String place_id){
        Place spot = new Place();
        ArrayList<String> types = new ArrayList<String>();

        String uri = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + place_id +
                "&language=ko&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";
        URL url;
        URLConnection urlConnection;
        String page = "";

        spot.place_id = place_id;

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
            //System.out.println("Done.");

            //type 배열로 저장
            JSONArray jsonArray = subJsonObject.getJSONArray("types");
            //System.out.println("before types: "+ jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                spot.types.add(jsonArray.optString(i));
            }
            //string으로 잘 들어간게 맞는지 추후 확인. ""표시가 없음.
            //System.out.println("after types: "+ spot.types);


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        spot.safe_score = safe_calculator(spot.lat, spot.lng);
        spot.envi_score = env_calculator(spot.lat, spot.lng);
        spot.total_score = spot.safe_score + spot.envi_score + spot.user_score;

        return spot;
    }

    private int safe_calculator(double lat, double lng){
        double score = 0;

        String[] positive = new String[] { "local_government_office", "police", "school", "secondary_school", "university" };
        String[] negative = new String[] { "casino", "liquor_store", "night_club" };

        int pos_num = 1;
        int neg_num = 1;

        for (String s : positive) {
            pos_num += count_spots(lat, lng, s);
        }
        for (String s : negative) {
            neg_num += count_spots(lat, lng, s);
        }

        score = (pos_num / neg_num) * 100;

        return (int)score;
    }

    private int env_calculator(double lat, double lng){
        double score = 0;
        int user_rating = 0;

        String[] best = new String[] { "tourist_attraction" };
        String[] good = new String[] { "park" };

        for (String s : best) {
            score += count_spots(lat, lng, s) * 20;
        }
        for (String s : good) {
            score += count_spots(lat, lng, s) * 10;
        }

        //추후 유저 선호도(평가) 추가
        score += user_rating;

        return (int)score;
    }

    private int count_spots(double lat, double lng, String type){
        int radius = 500;
        int num = 0;

        String uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng +
                "&radius=" + radius +
                "&type=" + type +
                //"&keyword=" + keyword +
                "&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";

        String page = "";

        try {
            URL url = new URL(uri);
            URLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());

            String line;

            while ((line = bufreader.readLine()) != null) {
                //Log.d("line:", line);
                page += line;
            }

            JSONObject jsonObject = new JSONObject(page);
            String results = jsonObject.getString("results");
            JSONArray jsonArray = new JSONArray(results);
            num = jsonArray.length();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return num;
    }


    //---------------------------------------------------------------------------------

    public void SpotFilter(ArrayList<Place> Spots){
        Place spot = new Place();
        String[] bad_type = new String[] { "casino", "liquor_store", "night_club" };
        int len;

        //삭제
        for(int i =0; i < Spots.size(); i++){
            spot = Spots.get(i);

            if(spot.safe_score < 70) {
                Spots.remove(i);
                continue;
            }
            len = spot.types.size();
            int isbad = 0;
            for(int j = 0; j < len; j++){
                for(String bad : bad_type){
                    if (spot.types.get(j).equals(bad) == true) {
                        Spots.remove(i);
                        isbad = 1;
                    }
                }
                if(isbad == 1) break;
            }
        }


        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();//배열이 필요할때
            for (int i = 0; i < Spots.size(); i++)//배열
            {
                spot = Spots.get(i);
                JSONObject subJsonObject = new JSONObject();//배열 내에 들어갈 json

                subJsonObject.put("index", i);
                subJsonObject.put("place_id", spot.place_id);
                subJsonObject.put("name", spot.name);
                subJsonObject.put("lat", spot.lat);
                subJsonObject.put("lng", spot.lng);
                subJsonObject.put("popularity", spot.popularity);
                subJsonObject.put("safe_score", spot.safe_score);
                subJsonObject.put("envi_score", spot.envi_score);
                subJsonObject.put("user_score", spot.user_score);
                subJsonObject.put("total_score", spot.total_score);
                subJsonObject.put("visitor", spot.visitor);
                subJsonObject.put("visitor_time", spot.visitor_time);
                subJsonObject.put("visitor_week", spot.visitor_week);

                JSONArray typJsonArray = new JSONArray();
                for(int j = 0; j<spot.types.size(); j++){
                    typJsonArray.put(spot.types.get(j));
                }
                subJsonObject.put("types", typJsonArray);

                jsonArray.put(subJsonObject);
            }

            SimpleDateFormat t = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");

            Calendar time = Calendar.getInstance();

            String record_time = t.format(time.getTime());

            //임시, 추후 서비스 지역을 넓힐 때 수정
            jsonObject.put("city", "Suwon-si");

            jsonObject.put("record_time", record_time);
            jsonObject.put("spots", jsonArray);

            System.out.println(jsonObject.toString());


            Writer output = null;
            String path = "C:\\Users\\LEEJIWOO\\AndroidStudioProjects\\BoheMeow\\app\\src\\main\\res\\data\\spotdata_";
            File file = new File( path + jsonObject.getString("city") + ".json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonObject.toString());
            output.close();

            System.out.println("success");

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }
}
