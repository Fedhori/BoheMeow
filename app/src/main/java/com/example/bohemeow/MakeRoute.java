package com.example.bohemeow;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

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
                ArrayList<Spot> spots = makeList(userlat, userlng);
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

                //거리 비교
                //계산
                //배열에 추가

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

    int getDistance(double lat, double lng){

        int dis = 0;

        //Tmap api를 이용해 도보 거리 계산


        return dis;
    }






}
