package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RankPopUpActivity extends Activity {

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rank_pop_up);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        username = registerInfo.getString("registerUserName", "NULL");

        ImageButton total_rank_btn = findViewById(R.id.total_btn);
        total_rank_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getRankAndStartActivity();
            }
        });

        ImageButton daily_rank_btn = findViewById(R.id.daily_btn);
        daily_rank_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getDailyRankAndStartActivity();
            }
        });

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getDailyRankAndStartActivity(){
        final int[] rank = new int[5];
        final int[] catTypes = {1, 1, 1, 1 ,1};
        final String[] usernames = new String[5];
        final int[] points = new int[5];
        final String[] introductions = new String[5];

        // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        String time = df.format(date);

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("daily_rank").child(time);
        final Query[] query = {mPostReference.orderByChild("point").limitToLast(3)};
        query[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long size = dataSnapshot.getChildrenCount();
                    long cnt = 0;

                    // get 1st, 2nd, 3rd user's username
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        cnt++;
                        dailyRankData get = issue.getValue(dailyRankData.class);
                        rank[(int)(size - cnt)] = (int)(size - cnt) + 1;
                        usernames[(int)(size - cnt)] = get.username;
                        points[(int)(size - cnt)] = get.point;
                    }
                }

                // get player rank
                query[0] = mPostReference.orderByChild("point");
                query[0].addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long size = dataSnapshot.getChildrenCount();
                        int cnt = 0;
                        int user_rank = 0;

                        boolean isArrived = false;
                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                dailyRankData get = issue.getValue(dailyRankData.class);
                                if(get.username.equals(username)){

                                    isArrived = true;

                                    rank[4] = (int) (size - cnt);
                                    usernames[4] = get.username;
                                    points[4] = get.point;

                                    user_rank = rank[4];

                                    // 플레이어가 1위면 위에 아무도 없어야함!
                                    if(user_rank == 1){
                                        rank[3] = 0;
                                        usernames[3] = "당신이 1위입니다!";
                                        points[3] = 0;

                                        break;
                                    }
                                }
                                // 플레이어 바로 위의 유저 정보
                                else if(isArrived){
                                    rank[3] = user_rank -1;
                                    usernames[3] = get.username;
                                    points[3] = get.point;

                                    break;
                                }
                                cnt++;
                            }
                        }

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_list");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(int i = 0;i<5;i++){
                                    // if user exist
                                    if(rank[i] != 0){
                                        UserData get = dataSnapshot.child(usernames[i]).getValue(UserData.class);
                                        if(get != null){
                                            catTypes[i] = get.catType;
                                            introductions[i] = get.introduction;
                                            Log.d("asdf", introductions[i]);
                                        }
                                    }
                                }

                                // put information's to intent
                                Intent intent = new Intent(RankPopUpActivity.this, RankActivity.class);
                                intent.putExtra("rank", rank);
                                intent.putExtra("catTypes", catTypes);
                                intent.putExtra("points", points);
                                intent.putExtra("usernames", usernames);
                                intent.putExtra("introductions", introductions);
                                intent.putExtra("isDailyRank", true);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getRankAndStartActivity(){
        final int[] rank = new int[5];
        final int[] catTypes = new int[5];
        final String[] usernames = new String[5];
        final int[] points = new int[5];
        final String[] introductions = new String[5];

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("user_list");
        final Query[] query = {mPostReference.orderByChild("level").limitToLast(3)};
        query[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int cnt = 0;

                    // get 1st, 2nd, 3rd user's data
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        UserData get = issue.getValue(UserData.class);

                        rank[2 - cnt] = 3 - cnt;
                        catTypes[2 - cnt] = get.catType;
                        usernames[2 - cnt] = get.nickname;
                        points[2 - cnt] = get.level;
                        introductions[2 - cnt] = get.introduction;

                        cnt++;
                    }
                }

                // get player rank
                query[0] = mPostReference.orderByChild("level");
                query[0].addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long size = dataSnapshot.getChildrenCount();
                        int cnt = 0;
                        int user_rank = 0;

                        boolean isArrived = false;
                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                UserData get = issue.getValue(UserData.class);
                                if(get.nickname.equals(username)){

                                    isArrived = true;

                                    rank[4] = (int) (size - cnt);
                                    catTypes[4] = get.catType;
                                    usernames[4] = get.nickname;
                                    points[4] = get.level;
                                    introductions[4] = get.introduction;

                                    user_rank = rank[4];

                                    // 플레이어가 1위면 위에 아무도 없어야함!
                                    if(user_rank == 1){
                                        rank[3] = 0;
                                        catTypes[3] = 1;
                                        usernames[3] = "당신이 1위입니다!";
                                        points[3] = 0;
                                        introductions[3] = get.introduction;

                                        break;
                                    }
                                }
                                // 플레이어 바로 위의 유저 정보
                                else if(isArrived){
                                    rank[3] = user_rank -1;
                                    catTypes[3] = get.catType;
                                    usernames[3] = get.nickname;
                                    points[3] = get.level;
                                    introductions[3] = get.introduction;

                                    break;
                                }
                                cnt++;
                            }
                        }

                        // put information's to intent
                        Intent intent = new Intent(RankPopUpActivity.this, RankActivity.class);
                        intent.putExtra("rank", rank);
                        intent.putExtra("catTypes", catTypes);
                        intent.putExtra("points", points);
                        intent.putExtra("usernames", usernames);
                        intent.putExtra("introductions", introductions);
                        intent.putExtra("isDailyRank", false);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}