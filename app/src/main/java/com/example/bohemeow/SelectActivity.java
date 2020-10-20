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
import android.widget.Toast;

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
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Intent intent = getIntent();

        mPostReference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        // get user preference values
        String user_nickname = registerInfo.getString("registerUserName", "NULL");
        getUserPreferences(user_nickname);

        text = findViewById(R.id.time_view);
        time = registerInfo.getInt("walkTime", 30);
        text.setText(Integer.toString(time));

        checkBox1 = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);

        checkBox1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    checkBox1.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                }
            }
        }) ;

        checkBox2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(false);
                }
            }
        }) ;

        checkBox3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(true);
                }
            }
        }) ;


        ImageButton sub_btn = findViewById(R.id.sub_btn);
        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = Integer.parseInt(text.getText().toString());
                if(time > 10) {
                    time -= 10;
                    text.setText(Integer.toString(time));
                }
            }
        });

        ImageButton add_btn = findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = Integer.parseInt(text.getText().toString());
                if(time < 180) {
                    time += 10;
                    text.setText(Integer.toString(time));
                }
            }
        });

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        Button start_btn = findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                time = Integer.parseInt(text.getText().toString());

                SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = registerInfo.edit();
                editor.putInt("walkTime", time);
                editor.commit();

                Intent intent = new Intent(SelectActivity.this, WalkLoadingActivity.class);
                intent.putExtra("time", time);
                intent.putExtra("preference", preference);

                if(checkBox1.isChecked()){
                    intent.putExtra("walkType", 1);
                    startActivity(intent);
                }
                else if(checkBox2.isChecked()){
                    intent.putExtra("walkType", 2);
                    startActivity(intent);
                }
                else if(checkBox3.isChecked()){
                    intent.putExtra("walkType", 3);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SelectActivity.this, "경로 추천 방식을 선택해주세요!", Toast.LENGTH_LONG).show();
                }



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
