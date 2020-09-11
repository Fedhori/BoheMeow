package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    int[] preference = new int[3];//0:safe 1:envi 2:popularity

    int time = 30;
    TextView text;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_2);
        Intent intent = getIntent();

        mPostReference = FirebaseDatabase.getInstance().getReference();

        // 현재는 유저 닉네임이 BonJour! 인 유저만의 정보를 받아올 수 있다. 추후 수정 예정
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "Bonjour!");
        editor.commit();

        // get user preference values
        String user_nickname = registerInfo.getString("registerUserName", "NULL");
        getUserPreferences(user_nickname);

        text = findViewById(R.id.time_view);
        checkBox = findViewById(R.id.checkBox);



        ImageButton sub_btn = findViewById(R.id.sub_btn);
        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time > 10) {
                    time -= 10;
                    text.setText(Integer.toString(time) + "분");
                }
            }
        });

        ImageButton add_btn = findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time < 180) {
                    time += 10;
                    text.setText(Integer.toString(time) + "분");
                }
            }
        });

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        ImageButton start_btn = findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, WalkLoadingActivity.class);
                intent.putExtra("time", time);
                intent.putExtra("preference", preference);

                if(checkBox.isChecked()){
                    intent.putExtra("isFree", true);
                }
                else intent.putExtra("isFree", false);

                startActivity(intent);
            }
        });

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
