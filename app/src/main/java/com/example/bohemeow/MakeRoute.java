package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

class Spot {
    String place_id;
    String name;
    int score = 0;
    double lat, lng;

}

class Location{
    double lat, lng;
}

public class MakeRoute {

    //위치좌표 gps로 받아오는 기능 추후 추가

    private double userlat = 37.2939299;
    private double userlng = 126.9739263;

    private int min = 60; //소요 시간
    private int speed = 60; //보행자 속도

    int num = 1 + min / 30; //선택할 스팟 수


    private Context mContext = null;

    public MakeRoute(Context context) {
        this.mContext = context;
    }



    public void SpotSelector() {

        System.out.println("\nprint: Start");
        makeList();

    }

    public interface Callback{
        void success(DataSnapshot data);
        void fail(String errorMessage);
    }

    //=========================================================================================
    private void makeList(){
        System.out.println("\nprint: makeList start");
        final int limitDis = (min * speed) / 2;

        final String city = "Suwon-si";

        final ArrayList<SpotDetail> spotDetails = new ArrayList<>();
        final ArrayList<Spot> spots= new ArrayList<>();
        final Spot spot = new Spot();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseReference.child("spot_data").child(city).child("spots");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    SpotDetail spotDetail = postSnapshot.getValue(SpotDetail.class);
                    if (getDistance(spotDetail.getLat(), spotDetail.getLng()) < limitDis) {
                        System.out.println("spot: " + spotDetail.getName());
                        spots.add(simplifySpot(spotDetail));
                        //System.out.println("\nspots length = " + spots.size());
                    }
                }

                chooseSpot(spots, num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

/*
        final Callback callback = new Callback() {
            @Override
            public void success(DataSnapshot data) {
                for (DataSnapshot postSnapshot: data.getChildren()) {
                    ArrayList<SpotDetail> spotDetails = new ArrayList<>();
                    spotDetails.add(postSnapshot.getValue(SpotDetail.class));
                }
            }
            @Override
            public void fail(String errorMessage) {
            }
        };
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.success(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                callback.fail(error.getMessage());
            }
        });

        for(SpotDetail spotDetail: spotDetails){
            if (getDistance(spotDetail.getLat(), spotDetail.getLng()) < limitDis) {
                System.out.println("spot: " + spotDetail.getName());
                spots.add(simplifySpot(spotDetail));
            }
        }
        System.out.println("\nspots length = " + spots.size());
        chooseSpot(spots, num);
 */

    }

    static class getJsonObjectTask extends AsyncTask<URL, Void, JSONObject> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONObject doInBackground(URL... urls) {
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) urls[0].openConnection();
                int response = con.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    //System.out.println("B " + jsonObject);
                    return jsonObject;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                con.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
        }
    }

    private int getDistance(final double lat, final double lng) {

        int dis = 0;
        //sample place
        //lat = 37.296067;
        //lng = 126.982378;

        try {

            String uri = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&startName="+ "startPlace" + "&startX=" + userlng + "&startY=" + userlat +
                    "&endName=" + "endPlace" + "&endX=" + lng + "&endY=" + lat +
                    "&format=json&appkey=l7xxc4527e777ef245ef932b366ccefaa9b0";

            String page = "";
            URL url = new URL(uri);
            /*
            URLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());
            String line = null;
            while ((line = bufreader.readLine()) != null) {
                //Log.d("line:", line);
                page += line;
            }
             */

            //JSONObject jsonObject = new JSONObject(page);
            getJsonObjectTask task = new getJsonObjectTask();

            JSONObject jsonObject = task.execute(url).get();
            //System.out.println("\nC : " + jsonObject);
            String features = jsonObject.getString("features");
            JSONArray jsonArray = new JSONArray(features);
            JSONObject subJsonObject = jsonArray.getJSONObject(0);
            String properties = subJsonObject.getString("properties");
            JSONObject subJsonObject2 = new JSONObject(properties);
            dis = Integer.parseInt(subJsonObject2.getString("totalDistance"));
            //System.out.println("\nD distance : " + dis);



        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e){ //나중에 꼭 고치기. 원인 못찾음. 발생하는 상황도 예측 불가.
            //e.printStackTrace();
            dis = 10000;
        }
        return dis;
    }

    private Spot simplifySpot(SpotDetail spotDetail) {
        Spot spot = new Spot();
        //System.out.println("\nName : " + spotDetail.getName());

        spot.place_id = spotDetail.getPlace_id();
        spot.name = spotDetail.getName();
        spot.lat = spotDetail.getLat();
        spot.lng = spotDetail.getLng();
        spot.score = calculateScore(spotDetail);

        //System.out.println("\tScore: " + spot.score);
        return spot;
    }

    private int calculateScore(SpotDetail spotDetail){

        int score = 0;
        //유저 성향에 따른 가중치 계산 공식 후에 반영할것
        score += spotDetail.getEnvi_score();
        score += spotDetail.getSafe_score();
        score += spotDetail.getUser_score();
        score += spotDetail.getPopularity();

        return score;
    }

    //------------------------------------------------------------------------------------------


    void chooseSpot(ArrayList<Spot> spots, int num){

        Map<Spot, Integer> temp = new HashMap<Spot, Integer>();
        ArrayList<Location> locations = new ArrayList<>();


        for (Spot s : spots){
            temp.put(s, s.score);
        }

        Random rand = new Random();

        System.out.println("\n<Selected>");

        for(int i = 0; i < num; i++){
            Spot spot = getWeightedRandom(temp, rand);
            System.out.println("\n" + spot.name);

            Location location = new Location();
            location.lat = spot.lat;
            location.lng = spot.lng;
            locations.add(location);

            temp.remove(spot);
        }

        System.out.println("\n[" + userlat + ", " + userlng + "]");

        for(Location location : locations){
            System.out.println("\n[" + location.lat + ", " + location.lng + "]");
        }

        sortSpot(locations);

    }

    private static <Spot> Spot getWeightedRandom(Map<Spot, Integer> weights, Random random) {
        Spot result = null;
        double bestValue = Double.MAX_VALUE;

        for (Spot element : weights.keySet()) {
            double value = -Math.log(random.nextDouble()) / weights.get(element);
            if (value < bestValue) {
                bestValue = value;
                result = element;
            }
        }
        return result;
    }

    //------------------------------------------------------------------


    void sortSpot(ArrayList<Location> locations){

        Comparator<Location> comparator = new Comparator<Location>() {
            @Override
            public int compare(Location lhs, Location rhs) {
                double lhsAngle = Math.atan2(lhs.lng - userlng, lhs.lat - userlat);
                double rhsAngle = Math.atan2(rhs.lng - userlng, rhs.lat - userlat);
                // Depending on the coordinate system, you might need to reverse these two conditions
                if (lhsAngle < rhsAngle) return -1;
                if (lhsAngle > rhsAngle) return 1;
                return 0;
            }
        };
        Collections.sort(locations, comparator);

        double[][] sorted = new double[num+2][2];

        sorted[0][0] = userlat;
        sorted[0][1] = userlng;

        for(int i = 0; i < num; i++){
            sorted[i + 1][0] = locations.get(i).lat;
            sorted[i + 1][1] = locations.get(i).lng;
        }

        sorted[num+1][0] = userlat;
        sorted[num+1][1] = userlng;



    }

}
