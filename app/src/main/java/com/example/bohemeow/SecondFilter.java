package com.example.bohemeow;

import android.content.Context;

import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

class SpotDetail {

    public int index = 0;
    public String place_id;
    public String name;

    public double lat;
    public double lng;

    public ArrayList<String> types = new ArrayList<>();
    public int popularity = -1;
    public int safe_score = -1;
    public int envi_score = -1;
    public int user_score = -1;

    public int visitor = -1;
    public int visitor_time = -1;
    public int visitor_week = -1;

    public int total_score = -1;
    public String record_time;


    public SpotDetail() {}

    public SpotDetail(int index, String place_id, String name, double lat, double lng, ArrayList<String> types, int popularity, int safe_score, int envi_score, int user_score, int visitor, int visitor_time, int visitor_week, int total_score, String record_time){

        this.index = index;
        this.place_id = place_id;
        this.name = name;

        this.lat = lat;
        this.lng = lng;

        this.types = types;
        this.popularity = popularity;
        this.safe_score = safe_score;
        this.envi_score = envi_score;
        this.user_score = user_score;

        this.visitor = visitor;
        this.visitor_time = visitor_time;
        this.visitor_week = visitor_week;

        this.total_score = total_score;
        this.record_time = record_time;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getSafe_score() {
        return safe_score;
    }

    public void setSafe_score(int safe_score) {
        this.safe_score = safe_score;
    }

    public int getEnvi_score() {
        return envi_score;
    }

    public void setEnvi_score(int envi_score) {
        this.envi_score = envi_score;
    }

    public int getUser_score() {
        return user_score;
    }

    public void setUser_score(int user_score) {
        this.user_score = user_score;
    }

    public int getVisitor() {
        return visitor;
    }

    public void setVisitor(int visitor) {
        this.visitor = visitor;
    }

    public int getVisitor_time() {
        return visitor_time;
    }

    public void setVisitor_time(int visitor_time) {
        this.visitor_time = visitor_time;
    }

    public int getVisitor_week() {
        return visitor_week;
    }

    public void setVisitor_week(int visitor_week) {
        this.visitor_week = visitor_week;
    }

    public int getTotal_score() {
        return total_score;
    }

    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }

    public String getRecord_time() {
        return record_time;
    }

    public void setRecord_time(String record_time) {
        this.record_time = record_time;
    }


    /*
    public String place_id;
    public String name;

    double lat;
    double lng;

    ArrayList<String> types = new ArrayList<>();
    int popularity = -1;
    int safe_score = -1;
    int envi_score = -1;
    int user_score = -1;

    int visitor = -1;
    int visitor_time = -1;
    int visitor_week = -1;

    int total_score = -1;

     */
}

public class SecondFilter {

    private Context mContext = null;

    public SecondFilter(Context context) {
        this.mContext = context;
    }


    public void FeatureCalculator(String searched){

        ArrayList<SpotDetail> Spots = new ArrayList<>();

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

    private SpotDetail Calculator(String place_id){
        SpotDetail spot = new SpotDetail();

        String uri = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + place_id +
                "&language=ko&key=AIzaSyDS_hnV0LrPuy7UTzaZf73zK5XXHWgXsdk";
        URL url;
        URLConnection urlConnection;
        String page = "";

        spot.place_id = place_id;

        try {
            url = new URL(uri);
            urlConnection = url.openConnection();
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

    private void SpotFilter(ArrayList<SpotDetail> Spots){
        SpotDetail spot = new SpotDetail();
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
                    if (spot.types.get(j).equals(bad)) {
                        Spots.remove(i);
                        isbad = 1;
                    }
                }
                if(isbad == 1) break;
            }
        }

        SimpleDateFormat t = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        String city = "Suwon-si";

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
                subJsonObject.put("record_time", t.format(Calendar.getInstance().getTime()));

                JSONArray typJsonArray = new JSONArray();
                for(int j = 0; j<spot.types.size(); j++){
                    typJsonArray.put(spot.types.get(j));
                }
                subJsonObject.put("types", typJsonArray);

                jsonArray.put(subJsonObject);
            }

            //임시, 추후 서비스 지역을 넓힐 때 수정
            jsonObject.put("last_record_time", t.format(Calendar.getInstance().getTime()));
            jsonObject.put("spots", jsonArray);

            //System.out.println(jsonObject.toString());

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            String jsonString = jsonObject.toString(); //set to json string
            Map<String, Object> jsonMap = new Gson().fromJson(jsonString, new TypeToken<HashMap<String, Object>>() {}.getType());
            myRef.child("spot_data").child(city).updateChildren(jsonMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
