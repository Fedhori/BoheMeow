package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class WalkEndActivity extends AppCompatActivity {

    DatabaseReference ref;

    String username;
    String phoneNumber;

    double totalMoveLength = 0f; // 산책하는 동안 총 얼마나 걸었는가? 단위: m
    long totalWalkTime = 0; // 얼마나 오래 산책했는가? 단위: ms
    long realWalkTime = 0; // 얼마나 오래 실제로 걸었는가? 단위: ms
    long totalPoint = 0;

    long user_realWalkTime;
    long user_totalWalkTime;
    long user_totalWalkCount;
    double user_totalMoveLength;
    long user_totalPoint;
    long user_totalRealPoint;

    boolean isWritten = false;
    boolean isBackToSelect = false;

    TextView callory;

    private String[] normalTexts = {
            "이 구역 탐험가는 나야! 오늘도 좋은 모험이었어.",
            "한층 건강해진 느낌이다옹. 오늘도 즐거웠어.",
            "고양이는 말야. 한걸음 한걸음 허투루 걷는 법이 없지.",
            "천리길도 한 걸음부터다옹. 이제 999리 남았나?",
            "영차영차 해봅시다. 영! 영! 아니, 차 안해주냐옹.",
            "늘 조금씩 나아지는 탐험가가 되자. 오늘도 수고했다옹."
    };

    private String[] levelUpTexts = {
            "뭔가 강해진 기분이다옹..!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_end);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        username = registerInfo.getString("registerUserName", "NULL");
        phoneNumber = registerInfo.getString("phoneNumber", "NULL");

        // get intent
        Intent intent = getIntent();

        isBackToSelect = intent.getBooleanExtra("isBackToSelect", false);

        totalMoveLength = intent.getDoubleExtra("totalMoveLength", -1);
        totalWalkTime = intent.getLongExtra("totalWalkTime", -1);
        realWalkTime = intent.getLongExtra("realWalkTime", -1);
        totalPoint = intent.getLongExtra("totalPoint", -1);

        TextView time = findViewById(R.id.time_view);
        TextView distance = findViewById(R.id.dis_view);
        TextView pace = findViewById(R.id.pace_view);
        TextView comment = findViewById(R.id.comment);
        callory = findViewById(R.id.cal_view);
        TextView score = findViewById(R.id.score_view);

        long totalTime = totalWalkTime; // ms
        long hour;
        long minute;
        long second;

        hour = totalTime / 3600000;
        totalTime %= 3600000;
        minute = totalTime / 60000;
        totalTime %= 60000;
        second = totalTime / 1000;

        /*
        String timeText = "";
        if(hour >= 10){
            timeText += String.valueOf(hour);
        }
        else{
            timeText += "0" + hour;
        }
        timeText += "";
        if(minute >= 10){
            timeText += String.valueOf(minute);
        }
        else{
            timeText += "0" + minute;
        }
        timeText += ":";
        if(second >= 10){
            timeText += String.valueOf(second);
        }
        else{
            timeText += "0" + second;
        }
        time.setText(timeText);

         */
        String timeText = hour + "시간 " + minute + "분 " + second + "초";
        time.setText(timeText);


        ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double cal;
                long weight =  (long) dataSnapshot.child("weight").getValue();
                cal = 0.9 * weight * (realWalkTime/60000) / 15;
                callory.setText(Double.toString(Double.parseDouble(String.format("%.2f",cal))) + "kcal");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        distance.setText(String.format("%.2f", totalMoveLength / 1000d) + "km");

        if(totalMoveLength != 0){
            long paceTime = realWalkTime / (long)totalMoveLength; // second

            minute = paceTime / 60;
            paceTime %= 60;
            second = paceTime;

            String paceText = "";
            if(minute >= 10){
                paceText += String.valueOf(minute);
            }
            else{
                paceText += "0" + minute;
            }
            paceText += "'";
            if(second >= 10){
                paceText += String.valueOf(second);
            }
            else{
                paceText += "0" + second;
            }
            paceText += "\"";
            pace.setText(paceText);
        }
        else{
            pace.setText("데이터가 부족합니다.");
        }

        score.setText(String.valueOf(totalPoint));

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalkEndActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        // update value to firebase
        updateUserData(username, realWalkTime, totalWalkTime, totalMoveLength, totalPoint);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WalkEndActivity.this, MainMenu.class);
        startActivity(intent);
    }

    void updateUserData(String username, final long realWalkTime, final long totalWalkTime, final double totalMoveLength, final long totalPoint){

        ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!isWritten) {
                    user_realWalkTime = (long) dataSnapshot.child("realWalkTime").getValue();
                    user_totalWalkTime = (long) dataSnapshot.child("totalWalkTime").getValue();
                    user_totalWalkCount = (long) dataSnapshot.child("totalWalkCount").getValue();
                    // Double.class를 한 이유는, Firebase에서 0을 가져오면 그냥 Long으로 취급해버림. 그래서 타입 캐스팅 오류가 발생하므로 이를 방지하고자 Double형으로 받아오도록 명시해줘야 함
                    user_totalMoveLength = dataSnapshot.child("totalWalkLength").getValue(Double.class);
                    user_totalPoint = (long) dataSnapshot.child("level").getValue();
                    user_totalRealPoint = (long) dataSnapshot.child("point").getValue();


                    ref.child("realWalkTime").setValue(user_realWalkTime + realWalkTime);
                    ref.child("totalWalkTime").setValue(user_totalWalkTime + totalWalkTime);
                    ref.child("totalWalkCount").setValue(user_totalWalkCount + 1);
                    ref.child("totalWalkLength").setValue(user_totalMoveLength + totalMoveLength);
                    ref.child("level").setValue(user_totalPoint + totalPoint);
                    ref.child("point").setValue(user_totalRealPoint + totalPoint);

                    TextView totalTime_tv = findViewById(R.id.total_time_view);
                    TextView totalDist_tv = findViewById(R.id.total_dis_view);
                    TextView totalCount_tv = findViewById(R.id.total_walk_count);

                    long totalTime = user_totalWalkTime + totalWalkTime; // ms
                    long hour;
                    long minute;
                    long second;

                    hour = totalTime / 3600000;
                    totalTime %= 3600000;
                    minute = totalTime / 60000;
                    totalTime %= 60000;
                    second = totalTime / 1000;

                    /*
                    String timeText = "";
                    if(hour >= 10){
                        timeText += String.valueOf(hour);
                    }
                    else{
                        timeText += "0" + hour;
                    }
                    timeText += ":";
                    if(minute >= 10){
                        timeText += String.valueOf(minute);
                    }
                    else{
                        timeText += "0" + minute;
                    }
                    timeText += ":";
                    if(second >= 10){
                        timeText += String.valueOf(second);
                    }
                    else{
                        timeText += "0" + second;
                    }
                    totalTime_tv.setText(timeText);

                     */
                    String timeText = hour + "시간 " + minute + "분 " + second + "초";
                    totalTime_tv.setText(timeText);
                    totalDist_tv.setText(String.format("%.2f", (user_totalMoveLength + totalMoveLength) / 1000d) + "km");
                    totalCount_tv.setText(user_totalWalkCount + "");

                    TextView comment = findViewById(R.id.comment);

                    checkAndUpdateLevelReward((int)user_totalPoint, (int)(user_totalPoint + totalPoint));
                    updateDailyPoint(totalPoint);

                    int prev_level = calculateLevel((int)user_totalPoint);
                    int cur_level = calculateLevel((int) (user_totalPoint + totalPoint));

                    // level up!
                    if(prev_level < cur_level){
                        Random random = new Random();
                        comment.setText(levelUpTexts[random.nextInt(levelUpTexts.length)]);
                    }
                    else{
                        Random random = new Random();
                        comment.setText(normalTexts[random.nextInt(normalTexts.length)]);
                    }

                    isWritten = true;
                    if(isBackToSelect){
                        Intent intent = new Intent(WalkEndActivity.this, SelectActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    int calculateLevel(int score){
        int level;
        if(score >= 10000){
            score -= 10000;
            level = (score / 1500) + 11;
        }
        else{
            level = score/1000 + 1;
        }
        return level;
    }

    void updateDailyPoint(final long totalPoint){
        // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        String time = df.format(date);

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("daily_rank").child(time).child(username);
        mPostReference.addValueEventListener(new ValueEventListener() {

            boolean isWritten = false;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!isWritten) {

                    long user_daily_totalPoint = 0;

                    if(dataSnapshot.exists()){
                        user_daily_totalPoint = (long) dataSnapshot.child("point").getValue();
                    }
                    else{
                        // not generated yet
                        mPostReference.child("username").setValue(username);
                    }

                    mPostReference.child("point").setValue(user_daily_totalPoint + totalPoint);

                    isWritten = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void checkAndUpdateLevelReward(int user_totalPoint, int new_totalPoint){

        int prev_level = calculateLevel(user_totalPoint);
        int cur_level = calculateLevel(new_totalPoint);

        if(prev_level < cur_level){
            // this value must be synchronized with WalkEndActivity's rewardLevelList array
            int[] rewardLevelList = {2, 5, 10, 20, 30, 40};
            int length = rewardLevelList.length;
            for(int i = 0;i<length;i++) {
                if (prev_level < rewardLevelList[i] && cur_level >= rewardLevelList[i]) {

                    // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
                    TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(tz);
                    String time = df.format(date);

                    // 파이어베이스에 유저가 특정 레벨에 도달헀음을 저장한다.
                    DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = null;
                    lotsData data = new lotsData(phoneNumber);
                    postValues = data.toMap();
                    childUpdates.put("/level_reward/" + i + "/" + time + "/", postValues);
                    mPostReference.updateChildren(childUpdates);

                    break;
                }
            }
        }
    }
}