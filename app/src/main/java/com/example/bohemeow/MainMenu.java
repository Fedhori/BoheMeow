package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class MainMenu extends AppCompatActivity {

    //뒤로가기 두번 시 종료되도록 구현 예정
    private long backKeyPressedTime = 0;
    private Toast toast;
    ImageView windowIV;
    Random rnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        windowIV = findViewById(R.id.iv_window);


        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH");
        df.setTimeZone(tz);
        int time = Integer.parseInt(df.format(date));

        if(time <= 5 || time >= 21){
            windowIV.setImageResource(R.drawable.main_0005_windownight);
        }
        else if(time >= 18){
            windowIV.setImageResource(R.drawable.windowsunset);
        }
        else if(time <= 8){
            windowIV.setImageResource(R.drawable.windowmorning);
        }

        /*
        rnd = new Random();
        int num = rnd.nextInt(2);
        if(num == 1){
            select_btn.setBackgroundResource(R.drawable.main_cat_scaratch);
        }

         */


        Intent intent = getIntent();

        Button communityBtn = (Button) findViewById(R.id.btn_community);
        communityBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainMenu.this, CommunityActivity.class);
                Intent intent = new Intent(MainMenu.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        Button selectBtn = (Button) findViewById(R.id.btn_to_select);
        selectBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, SelectActivity.class);
                startActivity(intent);
            }
        });

        Button logoutBtn = (Button) findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = registerInfo.edit();
                editor.putString("registerUserName", "NULL");
                editor.commit();

                Intent intent = new Intent(MainMenu.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /*
        Button tempBtn = (Button) findViewById(R.id.btn_furniture);
        tempBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, SurveyActivity.class);
                startActivity(intent);
            }
        });

         */
    }

    /*
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }

     */



}
