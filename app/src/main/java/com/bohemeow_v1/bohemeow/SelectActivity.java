package com.bohemeow_v1.bohemeow;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

public class SelectActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    int[] preference = new int[3];//0:safe 1:envi 2:popularity

    UserData userData;

    int time = 60;
    TextView text;
    CheckBox record1;
    CheckBox record2;
    CheckBox record3;
    CheckBox checkBox2;
    CheckBox checkBox3;
    ImageView imageView;

    boolean isExist[] = {true, true, true};

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
        time = registerInfo.getInt("walkTime", 60);
        text.setText(Integer.toString(time));

        record1 = findViewById(R.id.record_1);
        record2 = findViewById(R.id.record_2);
        record3 = findViewById(R.id.record_3);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);

        imageView = findViewById(R.id.imageView53);

        record1.setText(getRecord("R1_lats", 0));
        record2.setText(getRecord("R2_lats", 1));
        record3.setText(getRecord("R3_lats", 2));

        record1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    record1.setChecked(true);
                    record2.setChecked(false);
                    record3.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }) ;

        record2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    record1.setChecked(false);
                    record2.setChecked(true);
                    record3.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }) ;

        record3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    record1.setChecked(false);
                    record2.setChecked(false);
                    record3.setChecked(true);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }) ;

        checkBox2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    record1.setChecked(false);
                    record2.setChecked(false);
                    record3.setChecked(false);
                    checkBox2.setChecked(true);
                    checkBox3.setChecked(false);
                    imageView.setVisibility(View.GONE);
                }
            }
        }) ;

        checkBox3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    record1.setChecked(false);
                    record2.setChecked(false);
                    record3.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(true);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }) ;


        ImageButton sub_btn = findViewById(R.id.sub_btn);
        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = Integer.parseInt(text.getText().toString());
                if(time > 10 && checkBox2.isChecked()) {
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
                if(time < 180 && checkBox2.isChecked()) {
                    time += 10;
                    text.setText(Integer.toString(time));
                }
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

                if(record1.isChecked()){
                    if(isExist[0]){
                        intent.putExtra("walkType", 1);
                    }
                    else{
                        Toast.makeText(SelectActivity.this, "기록된 경로가 존재하지 않습니다.\n 자동으로 추천을 받습니다!", Toast.LENGTH_LONG).show();
                        intent.putExtra("walkType", 4);
                    }
                    startActivity(intent);
                }
                else if(record2.isChecked()){
                    if(isExist[1]){
                        intent.putExtra("walkType", 2);
                    }
                    else{
                        Toast.makeText(SelectActivity.this, "기록된 경로가 존재하지 않습니다.\n 자동으로 추천을 받습니다!", Toast.LENGTH_LONG).show();
                        intent.putExtra("walkType", 4);
                    }
                    startActivity(intent);
                }
                else if(record3.isChecked()){
                    if(isExist[2]){
                        intent.putExtra("walkType", 3);
                    }
                    else{
                        Toast.makeText(SelectActivity.this, "기록된 경로가 존재하지 않습니다.\n 자동으로 추천을 받습니다!", Toast.LENGTH_LONG).show();
                        intent.putExtra("walkType", 4);
                    }
                    startActivity(intent);
                }
                else if(checkBox2.isChecked()){
                    intent.putExtra("walkType", 4);
                    startActivity(intent);
                }
                else if(checkBox3.isChecked()){
                    intent.putExtra("walkType", 5);
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
                    userData = get;

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

    private String getRecord(String key, int num) {
        String data = "  ";

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        String json = registerInfo.getString(key, null);
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                data += a.optString(0) + " · " + a.optString(2) + "개 스팟 · " + Date(a.optString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            data += "데이터가 존재하지 않습니다.";
            isExist[num] = false;
        }
        return data;
    }

    String Date(String time){
        String t = "";

        if(time.substring(5,6).equals("0")){
            t = t + time.substring(6,7) + "월 ";
        }
        else t = t + time.substring(5,7) + "월 ";

        if(time.substring(8,9).equals("0")){
            t = t + time.substring(9,10) + "일 ";
        }
        else t = t + time.substring(8,10) + "일 ";

        return t;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectActivity.this, MainMenu.class);
        startActivity(intent);
        finish();
    }


}
