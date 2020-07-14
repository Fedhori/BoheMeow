package com.example.bohemeow;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.ChildEventListener;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

class Spot {
    String place_id;
    String name;
    int score = 0;
    double lat, lng;
    int case_num;

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
        ArrayList<SpotDetail> spotDetails = getSpots();
        ArrayList<Spot> spots = makeList(spotDetails); //일정 거리 안의 스팟 들만 리스트에 저장
        //System.out.println("\nprint: choose Spot start");
        //ArrayList<Spot> selectedSpots = chooseSpot(spots, num); //리스트 중에서 가중치 포함 랜덤 추출
        //ArrayList<Spot> sortedSpots = sortSpot(selectedSpots);
        //추출된 스팟들 경로 계산

    }

    ArrayList<SpotDetail> getSpots(){
        System.out.println("\nprint: getSpot start");
        final String city = "Suwon-si";

        final ArrayList<SpotDetail> spotDetails = new ArrayList<>();



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseReference.child("spot_data").child(city).child("spots");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    SpotDetail spotDetail = postSnapshot.getValue(SpotDetail.class);
                    System.out.println("get: "+spotDetail.getName());
                    //System.out.println("\nA Lat: " + spotDetail.getLat() + "\tLng: " + spotDetail.getLng());
                    spotDetails.add(spotDetail);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        System.out.println("\nprint: getSpot end");
        return spotDetails;
    }

    //=========================================================================================
    private ArrayList<Spot> makeList(ArrayList<SpotDetail> spotDetails){
        System.out.println("\nprint: makeList start");
        final int limitDis = (min * speed) / 2;

        final String city = "Suwon-si";

        //final ArrayList<SpotDetail> spotDetails = new ArrayList<>();
        final ArrayList<Spot> spots= new ArrayList<>();
        final Spot spot = new Spot();
/*
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseReference.child("spot_data").child(city).child("spots");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    SpotDetail spotDetail = postSnapshot.getValue(SpotDetail.class);
                    System.out.println(spotDetail.getName());
                    //System.out.println("\nA Lat: " + spotDetail.getLat() + "\tLng: " + spotDetail.getLng());
                    spotDetails.add(spotDetail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
        /*
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SpotDetail spotDetail = dataSnapshot.getValue(SpotDetail.class);
                System.out.println(spotDetail.getName());
                //System.out.println("\nA Lat: " + spotDetail.getLat() + "\tLng: " + spotDetail.getLng());
                spotDetails.add(spotDetail);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
*/

        int spotNum = spotDetails.size();
        for(int i = 0; i < spotNum; i++){
            if (getDistance(spotDetails.get(i).getLat(), spotDetails.get(i).getLng()) < limitDis) {
                System.out.println("B: " + spotDetails.get(i).getName());
                spots.add(simplifySpot(spotDetails.get(i)));
                System.out.println("\nspots length = " + spots.size());
                if(i == spotNum - 1) {
                    ArrayList<Spot> selectedSpots = chooseSpot(spots, num);
                }
            }
        }

        //ArrayList<Spot> selectedSpots = chooseSpot(spots, num);

/*
        if (getDistance(spots.get(0).lat, spots.get(0).lng) < limitDis) {
            System.out.println("\nprint: makeList end\nspots length = " + spots.size());
            ArrayList<Spot> selectedSpots = chooseSpot(spots, num);
        }
*/
        System.out.println("\nprint: makeList end, spots length = " + spotDetails.size());
        return spots;
    }


    private class getJsonObjectTask extends AsyncTask<URL, Void, JSONObject> {

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
        spot.case_num = caseClassifier(spot.lat, spot.lng);
        spot.score = calculateScore(spotDetail);

        //System.out.println("\tScore: " + spot.score);
        return spot;
    }

    private int caseClassifier(double lat, double lng){
        int case_num = 0;

        if(lat >= userlat && lng >= userlng) case_num = 1;
        else if(lat < userlat && lng >= userlng) case_num = 2;
        else if(lat < userlat && lng < userlng) case_num = 3;
        else if (lat >= userlat && lng < userlng) case_num = 4;

        //System.out.println("\tcase_num = " + case_num);
        return case_num;
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

    ArrayList<Spot> chooseSpot(ArrayList<Spot> spots, int num){
        ArrayList<Spot> selectedSpots = new ArrayList<>();

        Map<Spot, Integer> temp = new HashMap<Spot, Integer>();

        for (Spot s : spots){
            temp.put(s, s.score);
        }

        Random rand = new Random();

        System.out.println("\n<Selected>");

        for(int i = 0; i < num; i++){
            Spot spot = getWeightedRandom(temp, rand);
            System.out.println("\n" + spot.name);
            spots.add(spot);
            temp.remove(spot);
        }

        System.out.println("\n[" + userlat + ", " + userlng + "]");
        for(int i = 0; i < num; i++){
            System.out.println("\n[" + spots.get(i).lat + ", " + spots.get(i).lng + "]");
        }


        return selectedSpots;
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


    ArrayList<Spot> sortSpot(ArrayList<Spot> spots){
        ArrayList<Spot> sortedSpots = new ArrayList<>();




        return sortedSpots;
    }



}
