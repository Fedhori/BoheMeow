package com.example.bohemeow;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

class Spot {
    String place_id;
    int score;
    double lat, lng;
    int case_num;
}

public class MakeRoute {

    //위치좌표 gps로 받아오는 기능 추가
    //소요 시간 선택 가능하도록
    //이용자 걸음속도 고려?
    double userlat = 37.2939299;
    double userlng = 126.9739263;


    private Context mContext = null;

    public MakeRoute(Context context) {
        this.mContext = context;
    }


    public void SpotSelector(){

        new Thread() {
            public void run() {
                System.out.println("print: Done");

                getDistance(userlat, userlng);

                ArrayList<Spot> spots = makeList(userlat, userlng); //일정 거리 안의 스팟 들만 리스트에 저장
                //리스트 중에서 가중치 포함 랜덤 추출
                //추출된 스팟들 경로 계산
            }
        }.start();

    }

    private ArrayList<Spot> makeList(final double lat, final double lng){


        final String city = "Suwon-si";

        ArrayList<Spot> spots= new ArrayList<>();
        Spot spot = new Spot();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseReference.child("spot_data").child(city).child("spots");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                SpotDetail spotDetail = dataSnapshot.getValue(SpotDetail.class);
                System.out.println("\nIndex: " + spotDetail.getIndex() + "\tName: " + spotDetail.getName());

                //거리 비교(for){
                    //계산
                    //배열에 추가
                //}

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

        return spots;

    };

    int getDistance(double lat, double lng) {

        int dis = 0;
        //sample place
        lat = 37.296067;
        lng = 126.982378;

        //Tmap api를 이용해 도보 거리 계산

        String uri = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&startName="+ "startPlace" + "&startX=" + userlng + "&startY=" + userlat +
                "&endName=" + "endPlace" + "&endX=" + lng + "&endY=" + lat +
                "&format=json&appkey=l7xxc4527e777ef245ef932b366ccefaa9b0";

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

            JSONObject jsonObject = new JSONObject(page);
            String features = jsonObject.getString("features");
            JSONArray jsonArray = new JSONArray(features);
            JSONObject subJsonObject = jsonArray.getJSONObject(0);
            String properties = subJsonObject.getString("properties");
            JSONObject subJsonObject2 = new JSONObject(properties);
            dis = Integer.parseInt(subJsonObject2.getString("totalDistance"));

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        System.out.println("\ndistance : " + dis);

        return dis;
    }






}
