package com.bohemeow.bohemeow;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("place_id", place_id);
        //IDJsonArray.put(spot.place_id);
        result.put("name", name);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("popularity", popularity);
        result.put("safe_score", safe_score);
        result.put("envi_score", envi_score);
        result.put("user_score", user_score);
        result.put("total_score", total_score);
        result.put("visitor", visitor);
        result.put("visitor_time", visitor_time);
        result.put("visitor_week", visitor_week);
        result.put("types", types);

        SimpleDateFormat t = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
        result.put("record_time", t.format(Calendar.getInstance().getTime()));


        return result;
    }


    public String getPlace_id() {
        return place_id;
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

    public double getLng() {
        return lng;
    }

    public int getPopularity() {
        return popularity;
    }

    public int getSafe_score() {
        return safe_score;
    }

    public int getEnvi_score() {
        return envi_score;
    }

    public int getUser_score() {
        return user_score;
    }

    public int getTotal_score(){
        return total_score;
    }

    public int getVisitor() {
        return visitor;
    }

    public int getVisitor_week() {
        return visitor_week;
    }
}

public class SpotFilter {

    String key = "AIzaSyAmpwbsSqfD51IznC5uqa15XqXuAnoyHtk";
    private Context mContext = null;


    public SpotFilter(Context context) {
        this.mContext = context;
    }


    public void FeatureCalculator(final ArrayList<String> searched, final String region, final int visit_num){

        final ArrayList<SpotDetail> Spots = new ArrayList<>();
        new Thread() {
            public void run() {
                for(String place_id:searched){
                    Spots.add(Calculator(place_id, visit_num));
                }

                SpotFilter(Spots, region);
            }
        }.start();

    }


    private SpotDetail Calculator(String place_id, int visit_num){
        SpotDetail spot = new SpotDetail();

        String uri = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + place_id +
                "&language=ko&key=" + key;
        URL url;
        URLConnection urlConnection;
        String page = "";

        spot.place_id = place_id;

        try {
            url = new URL(uri);
            urlConnection = url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());

            String line;

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
        spot.popularity = visit_num;
        spot.total_score = spot.safe_score + spot.envi_score + spot.popularity + spot.user_score;

        return spot;
    }

    private int safe_calculator(double lat, double lng){
        double score = 0;

        String[] safePlace = new String[] {"police"};
        String[] positive = new String[] { "primary_school", "church"};
        String[] negative = new String[] { "night_club" , "bar"};

        int safe_num = 1;
        int pos_num = 1;
        int neg_num = 1;

        for (String s :safePlace) {
            safe_num += count_spots(lat, lng, s);
        }
        for (String s : positive) {
            pos_num += count_spots(lat, lng, s);
        }
        for (String s : negative) {
            neg_num += count_spots(lat, lng, s);
        }

        score = 100 + safe_num * 10 + pos_num * 5 - neg_num * 5;

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
        int radius = 350;
        int num = 0;

        String uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng +
                "&radius=" + radius +
                "&type=" + type +
                //"&keyword=" + keyword +
                "&key=" + key;

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

    public double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }



    private void SpotFilter(ArrayList<SpotDetail> Spots, String region){
        SpotDetail spot;
        String[] bad_type = new String[] { "casino", "liquor_store", "night_club", "bar" };

        System.out.println("Before: " + Spots);
        int len;

        //삭제
        for(int i =0; i < Spots.size(); i++){
            spot = Spots.get(i);

            //for test
            if(spot.place_id.equals("ChIJxbv7OkldezURodvg7lNFi5w")){
                Spots.remove(i);
                continue;
            }
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

        System.out.println("After: " + Spots);
        System.out.println("\nNum: " + Spots.size());
        //String region = "Suwon-si";

        if (Spots.size() == 1){
            spotUpload(Spots.get(0), region, true);
        }
        else {
            for (int i = 0; i < Spots.size(); i++)//배열
            {
                spotUpload(Spots.get(i), region, false);
            }
        }
    }

    void spotUpload(final SpotDetail spot, final String region, final boolean isSingle){
        final boolean[] isGood = {true};

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference MyRef = databaseReference.child("spot_data").child(region).child("spots");

        MyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<SpotDetail> del_list = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    SpotDetail spotDetail = postSnapshot.getValue(SpotDetail.class);
                    int limitDis = 900;

                    if (distFrom(spot.lat, spot.lng, spotDetail.getLat(), spotDetail.getLng()) < limitDis) {
                        int score = spotDetail.getTotal_score() + spotDetail.getVisitor() + spotDetail.getVisitor_week()*2;
                        if(spot.total_score > score) {
                            del_list.add(spotDetail);
                        }
                        else{
                            isGood[0] = false;
                            System.out.println("\nIt's too near. " + spot.name);

                            Map<String, Object> childUpdates= new HashMap<>();
                            HashMap<String, Long> temp_list = new HashMap<>();
                            temp_list.put("count", (long)spot.popularity/40);
                            temp_list.put("visit", (long)0);
                            childUpdates.put("/spot_data/temp_list/" + spot.place_id, temp_list);

                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                            myRef.updateChildren(childUpdates);

                            break;
                        }
                    }
                }
                if (isGood[0]){
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

                    for(SpotDetail s:del_list){
                        myRef.child("spot_data").child(region).child("spots").child(s.getPlace_id()).removeValue();
                        myRef.child("spot_data").child("ID_list").child(s.getPlace_id()).removeValue();
                    }

                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> value = spot.toMap();
                    childUpdates.put("/spot_data/" + region + "/spots/" + spot.place_id, value);
                    myRef.updateChildren(childUpdates);

                    HashMap<String, Integer> ID = new HashMap<>();
                    ID.put("visit", 0);

                    Map<String, Object> childUpdate = new HashMap<>();
                    childUpdate.put("spot_data/ID_list/" + spot.place_id, ID);
                    myRef.updateChildren(childUpdate);

                    SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    myRef.child("spot_data").child(region).child("last_record_time").setValue(t.format(Calendar.getInstance().getTime()));

                    if(isSingle){
                        myRef.child("spot_data/temp_list").child(spot.place_id).removeValue();
                    }

                    System.out.println("uploaded");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
