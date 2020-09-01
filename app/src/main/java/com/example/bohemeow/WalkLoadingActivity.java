package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class WalkLoadingActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView loadingText;
    TextView timeText;

    long waitingTime = 8000;


    private DatabaseReference mPostReference;

    int[] preference = new int[3];//0:safe 1:envi 2:popularity

    private String[] loadingTexts = {
            "신발끈 동여매는 중",
            "구름의 동향을 살피는 중",
            "물 한 모금 마시는 중",
            "수염 닦아내는 중",
            "동서남북을 확인하는 중"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_loading);


        mPostReference = FirebaseDatabase.getInstance().getReference();

        // someday.. 언제나 bonjour! 유저의 데이터만 받아올수는 없잖아?
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "Bonjour!");
        editor.commit();

        // get user preference values
        String user_nickname = registerInfo.getString("registerUserName", "NULL");
        getUserPreferences(user_nickname);

        progressBar = findViewById(R.id.progress_bar);
        loadingText = findViewById(R.id.loadingText);
        timeText = findViewById(R.id.timeText);

        progressBar.setMax(100);

        Random random = new Random();
        loadingText.setText(loadingTexts[random.nextInt(loadingTexts.length)]);

        timeText.setText("잠시 후 출발!");

        progressAnimation();
    }

    public void progressAnimation(){
        WalkLoadingProgressBarAnimation anim = new WalkLoadingProgressBarAnimation(this, progressBar, 0f, 100f, preference);
        anim.setDuration(waitingTime);
        progressBar.setAnimation(anim);
    }

    public void getUserPreferences(final String user_nickname){

        mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserData get = postSnapshot.getValue(UserData.class);

                    if(user_nickname.equals(get.nickname)){
                        preference[0] = (int)get.safeScore;
                        preference[1] = (int)get.enviScore;
                        preference[2] = (int)get.popularity;

                        //Toast.makeText(WalkActivity.this, preference[0] + " " + preference[1] + " " + preference[2], Toast.LENGTH_LONG).show();

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}