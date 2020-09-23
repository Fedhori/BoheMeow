package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String username;
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

        if(time <= 5 || time >= 20){
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


        UpdateBackground();

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        username = registerInfo.getString("registerUserName", "NULL");

        Intent intent = getIntent();

        Button communityBtn = (Button) findViewById(R.id.btn_community);
        communityBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
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

        Button configBtn = (Button) findViewById(R.id.btn_itemboard);
        configBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData get = dataSnapshot.child(username).getValue(UserData.class);
                        System.out.println(get);
                        Intent intent = new Intent(MainMenu.this, MainConfigActivity.class);
                        intent.putExtra("userdata", get);
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();

            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();

            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    public void UpdateBackground(){
        ConstraintLayout background = findViewById(R.id.mainmenu_background);
        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        int backgroundImageCode = userInfo.getInt("backgroundImageCode", 1);
        switch(backgroundImageCode){
            case 1: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0001_blue));
                break;
            case 2: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0000_yellow));
                break;
            case 3: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0002_green));
                break;
            case 4: background.setBackground(ContextCompat.getDrawable(this, R.drawable.mainwall_0003_red));
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            switch(resultCode){
                case 0: // background case
                    UpdateBackground();
                    break;
                case 1: // logout case
                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = registerInfo.edit();
                    editor.putString("registerUserName", "NULL");
                    editor.commit();

                    Intent intent = new Intent(MainMenu.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }

}
