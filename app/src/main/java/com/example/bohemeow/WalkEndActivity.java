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

public class WalkEndActivity extends AppCompatActivity {

    DatabaseReference ref;

    double totalMoveLength = 0f; // 산책하는 동안 총 얼마나 걸었는가? 단위: m
    long totalWalkTime = 0; // 얼마나 오래 산책했는가? 단위: ms
    long realWalkTime = 0; // 얼마나 오래 실제로 걸었는가? 단위: ms

    long user_realWalkTime;
    long user_totalWalkTime;
    long user_totalWalkCount;
    double user_totalMoveLength;

    boolean isWritten = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_end);

        // get intent
        Intent intent = getIntent();

        totalMoveLength = intent.getDoubleExtra("totalMoveLength", -1);
        totalWalkTime = intent.getLongExtra("totalWalkTime", -1);
        realWalkTime = intent.getLongExtra("realWalkTime", -1);

        TextView time = findViewById(R.id.time_view);
        TextView distance = findViewById(R.id.dis_view);
        TextView pace = findViewById(R.id.pace_view);
        TextView comment = findViewById(R.id.comment);
        TextView callory = findViewById(R.id.cal_view);

        long totalTime = totalWalkTime; // ms
        long hour;
        long minute;
        long second;

        hour = totalTime / 3600000;
        totalTime %= 3600000;
        minute = totalTime / 60000;
        totalTime %= 60000;
        second = totalTime / 1000;

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
        time.setText(timeText);

        distance.setText(String.format("%.2f", totalMoveLength / 1000d));

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


        double cal;
        int weight = 60;//임의의 무게
        cal = 0.9 * weight * (hour * 60 + minute) / 15;
        callory.setText(Double.toString(cal) + "kcal");


        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalkEndActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        // update value to firebase
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        String username = registerInfo.getString("registerUserName", "NULL");
        updateUserData(username, realWalkTime, totalWalkTime, totalMoveLength);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WalkEndActivity.this, MainMenu.class);
        startActivity(intent);
    }

    void updateUserData(String username, final long realWalkTime, final long totalWalkTime, final double totalMoveLength){

        ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!isWritten) {
                    user_realWalkTime = (long) dataSnapshot.child("realWalkTime").getValue();
                    user_totalWalkTime = (long) dataSnapshot.child("totalWalkTime").getValue();
                    user_totalWalkCount = (long) dataSnapshot.child("totalWalkCount").getValue();
                    user_totalMoveLength = (long) dataSnapshot.child("totalWalkLength").getValue();

                    ref.child("realWalkTime").setValue(user_realWalkTime + realWalkTime);
                    ref.child("totalWalkTime").setValue(user_totalWalkTime + totalWalkTime);
                    ref.child("totalWalkCount").setValue(user_totalWalkCount + 1);
                    ref.child("totalWalkLength").setValue(user_totalMoveLength + totalMoveLength);

                    isWritten = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}