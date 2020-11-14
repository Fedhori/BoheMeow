package com.bohemeow.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
    ImageView iv_laptop;
    ImageView iv_onoff;
    ImageView iv_texton;
    String username;
    int catType;
    int totalSpotCount;
    String phoneNumber;
    Random rnd;

    Handler handler = new Handler();
    Runnable runnable;

    int exp;

    double lastLat, lastLng;

    int maxRankUser = 100;

    long imageChangeSpan = 4000;

    boolean isLaptopVisible = false;
    boolean isOn =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        /*
        rnd = new Random();
        int num = rnd.nextInt(2);
        if(num == 1){.
            select_btn.setBackgroundResource(R.drawable.main_cat_scaratch);
        }

         */

        UpdateBackground();

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        username = registerInfo.getString("registerUserName", "NULL");
        catType = registerInfo.getInt("userCatType", 1);
        exp = registerInfo.getInt("exp", 0);
        //Toast.makeText(this, exp + "", Toast.LENGTH_SHORT).show();
        lastLat = (double) registerInfo.getFloat("lastLat", 0);
        lastLng = (double) registerInfo.getFloat("lastLng", 0);
        totalSpotCount = registerInfo.getInt("totalSpotCount", 0);

        boolean isGrowth = registerInfo.getBoolean("isGrowth", false);
        if(isGrowth){
            SharedPreferences.Editor editor = registerInfo.edit();
            editor.putBoolean("isGrowth", false);
            editor.commit();
            Intent intent = new Intent(MainMenu.this, GrowthActivity.class);
            startActivity(intent);
        }

        //phoneNumber = registerInfo.getString("phoneNumber", "NULL");

        ImageView iv_figure1 = findViewById(R.id.iv_figure1);
        ImageView iv_figure2 = findViewById(R.id.iv_figure2);
        ImageView iv_figure3 = findViewById(R.id.iv_figure3);
        ImageView iv_figure4 = findViewById(R.id.iv_figure4);
        ImageView iv_figure5 = findViewById(R.id.iv_figure5);

        if(totalSpotCount < 25){
            iv_figure5.setVisibility(View.INVISIBLE);
        }
        if(totalSpotCount < 20){
            iv_figure4.setVisibility(View.INVISIBLE);
        }
        if(totalSpotCount < 15){
            iv_figure3.setVisibility(View.INVISIBLE);
        }
        if(totalSpotCount < 10){
            iv_figure2.setVisibility(View.INVISIBLE);
        }
        if(totalSpotCount < 5){
            iv_figure1.setVisibility(View.INVISIBLE);
        }



        iv_laptop = findViewById(R.id.iv_laptop);

        windowIV = findViewById(R.id.iv_window);
        int w = 0;

        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HH");
        df.setTimeZone(tz);
        int time = Integer.parseInt(df.format(date));

        /*

        if(time <= 5 || time >= 19){
            windowIV.setImageResource(R.drawable.main__0002_nightclear);
        }
        else if(time >= 17){
            windowIV.setImageResource(R.drawable.main__0012_sunsetclear);
        }
        else if(time <= 8){
            windowIV.setImageResource(R.drawable.main__0011_morningclear);
        }

        if(lastLat != 0)
            w = getWeather(lastLat, lastLng);

        if(w == 1){
            if(time >= 6 && time <= 18) windowIV.setImageResource(R.drawable.main__0008_dayrain);
            else windowIV.setImageResource(R.drawable.main__0010_nightrain);
        }
        else if(w == 2){
            if(time >= 6 && time <= 18) windowIV.setImageResource(R.drawable.main__0005_daysnow);
            else windowIV.setImageResource(R.drawable.main__0006_nightsnow);
        }
        else if(w == 3){
            if(time >= 6 && time <= 18) windowIV.setImageResource(R.drawable.main__0007_daythunder);
            else windowIV.setImageResource(R.drawable.main__0009_nighthunder);
        }
        else if(w == 4){
            if(time >= 6 && time <= 18) windowIV.setImageResource(R.drawable.main__0004_daycloudy);
            else windowIV.setImageResource(R.drawable.main__0003_nightcloudy);
        }

         */

        iv_texton = findViewById(R.id.iv_texton);
        //iv_texton.setVisibility(View.INVISIBLE);
        iv_onoff = findViewById(R.id.iv_onoff);
        iv_onoff.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                isOn = !isOn;
                if(isOn){
                    iv_onoff.setBackgroundResource(R.drawable.infoon);
                    iv_texton.setVisibility(View.VISIBLE);
                }
                else{
                    iv_onoff.setBackgroundResource(R.drawable.infooff);
                    iv_texton.setVisibility(View.INVISIBLE);
                }
            }
        });

        Button communityBtn = findViewById(R.id.btn_community);
        communityBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        ImageButton notice_btn = findViewById(R.id.notice_btn);
        notice_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getNoticeDataAndStartActivity();
            }
        });

        final Button selectBtn = findViewById(R.id.btn_to_select);
        selectBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, SelectActivity.class);
                startActivity(intent);
            }
        });

        int level = calculateLevel(exp);

        final ImageButton configBtn = findViewById(R.id.btn_config);
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

        int kittenSize = 3;
        int bigSize = 5;
        int[][] kittenImages = new int[9][3];
        kittenImages[1] = new int[]{R.drawable.hangangkitten1, R.drawable.hangangkitten2, R.drawable.hangangkitten3};
        kittenImages[2] = new int[]{R.drawable.bameekitten1, R.drawable.bameekitten2, R.drawable.bameekitten3};
        kittenImages[3] = new int[]{R.drawable.chachakitten1, R.drawable.chachakitten2, R.drawable.chachakitten3};
        kittenImages[4] = new int[]{R.drawable.ryonikitten1, R.drawable.ryonikitten2, R.drawable.ryonikitten3};
        kittenImages[5] = new int[]{R.drawable.moonmoonkitten1, R.drawable.moonmoonkitten2, R.drawable.moonmoonkitten3};
        kittenImages[6] = new int[]{R.drawable.popokitten1, R.drawable.popokitten2, R.drawable.popokitten3};
        kittenImages[7] = new int[]{R.drawable.taetaekitten1, R.drawable.taetaekitten2, R.drawable.taetaekitten3};
        kittenImages[8] = new int[]{R.drawable.sessakkitten1, R.drawable.sessakkitten2, R.drawable.sessakkitten3};

        int[][] bigImages = new int[9][5];
        bigImages[1] = new int[]{R.drawable.hangangbig1, R.drawable.hangangbig2, R.drawable.hangangbig3, R.drawable.hangangbig4, R.drawable.hangangbig5 };
        bigImages[2] = new int[]{R.drawable.bameebig1, R.drawable.bameebig2, R.drawable.bameebig3, R.drawable.bameebig4, R.drawable.bameebig5 };
        bigImages[3] = new int[]{R.drawable.chachabig1, R.drawable.chachabig2, R.drawable.chachabig3, R.drawable.chachabig4, R.drawable.chachabig5};
        bigImages[4] =  new int[]{R.drawable.ryonibig1, R.drawable.ryonibig2, R.drawable.ryonibig3, R.drawable.ryonibig4, R.drawable.ryonibig5 };
        bigImages[5] = new int[]{R.drawable.moonmoonbig1, R.drawable.moonmoonbig2, R.drawable.moonmoonbig3, R.drawable.moonmoonbig4, R.drawable.moonmoonbig5 };
        bigImages[6] = new int[]{R.drawable.popobig1, R.drawable.popobig2, R.drawable.popbig3, R.drawable.popobig4, R.drawable.popobig5 };
        bigImages[7] = new int[]{R.drawable.taetaebig, R.drawable.taetaebig2, R.drawable.taetaebig3, R.drawable.taetaebig4, R.drawable.taetaebig5 };
        bigImages[8] = new int[]{R.drawable.sessakbig1, R.drawable.sessakbig2, R.drawable.sessakbig3, R.drawable.sessakbig4, R.drawable.sessakbig5 };

        //catType = 6; // 임시!

        ImageView iv_cat = findViewById(R.id.iv_cat);
        if(level < 7){
            int random = new Random().nextInt(kittenSize);
            iv_cat.setBackgroundResource(kittenImages[catType][random]);
        }
        else{
            int random = new Random().nextInt(bigSize);
            iv_cat.setBackgroundResource(bigImages[catType][random]);
        }


        final ImageButton rankBtn = findViewById(R.id.btn_rank);
        rankBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getRankingAndStartRankActivity();
            }
        });


        ImageButton payBtn = findViewById(R.id.btn_pay);
        payBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, PayActivity.class);
                startActivity(intent);
            }
        });

        ImageButton infoBtn = findViewById(R.id.btn_info);
        infoBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, TutorialPopupActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                isLaptopVisible = !isLaptopVisible;
                if(isLaptopVisible){
                    iv_laptop.setVisibility(View.VISIBLE);
                }
                else{
                    iv_laptop.setVisibility(View.INVISIBLE);
                }
                handler.postDelayed(runnable, imageChangeSpan);
            }
        }, imageChangeSpan);

        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    void getNoticeDataAndStartActivity(){

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference.child("notice_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int cnt = 0;
                NoticeData[] noticeData = new NoticeData[(int)dataSnapshot.getChildrenCount()];

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    noticeData[cnt++] = postSnapshot.getValue(NoticeData.class);
                }

                Intent intent = new Intent(MainMenu.this, NoticeActivity.class);
                intent.putExtra("noticeData", noticeData);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            case 1: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0000_red));
                break;
            case 2: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0001_white));
                break;
            case 3: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0002_blue));
                break;
            case 4: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0003_black));
                break;
            case 5: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0004_rainbow));
                break;
            case 6: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0005_andro));
                break;
            case 7: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0006_purple));
                break;
            case 8: background.setBackground(ContextCompat.getDrawable(this, R.drawable._0007_green));
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

    String key = "55e6a5b4f589f421a74785f169c7abbb";
    String weather;
    int w;
    int getWeather(final double latitude, final double longtitude){
        //final String[] region = {""};
        w = 0;


        new Thread() {
            public void run() {

                String uri = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude +"&lon=" + longtitude +
                        "&appid=" + key;

                String page = "";
                try {
                    URL url = new URL(uri);
                    URLConnection urlConnection = (URLConnection) url.openConnection();
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    Log.d("line:", bufreader.toString());
                    System.out.println("\ncheckpoint");
                    String line;

                    while ((line = bufreader.readLine()) != null) {
                        Log.d("line:", line);
                        page += line;
                    }

                    JSONObject jsonObject = new JSONObject(page);
                    String result = jsonObject.getString("weather");
                    JSONArray jsonArray = new JSONArray(result);
                    weather = jsonArray.getJSONObject(0).getString("main");
                    //Toast.makeText(MainMenu.this, "날씨:" + weather[0], Toast.LENGTH_LONG).show();
                    System.out.println("날씨:" + weather);

                    if(weather.equals("Clear")){
                        System.out.println("\nweather: clear");
                    }
                    else if(weather.equals("Rain") || weather.equals("Drizzle")){
                        System.out.println("\nweather: rain or drizzle");
                        w = 1;
                    }
                    else if(weather.equals("Snow")){
                        System.out.println("\nweather: snow");
                        w = 2;
                    }
                    else if(weather.equals("Thunderstorm")){
                        System.out.println("\nweather: thunderstorm");
                        w = 3;
                    }
                    else {
                        System.out.println("\nweather: cloud");
                        w = 4;
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return w;

    }

    public void getRankingAndStartRankActivity(){
        // save user data
        final UserData[] userData = new UserData[maxRankUser + 1];
        // save rank number
        final int[] rank = new int[maxRankUser + 1];

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("user_list");
        final Query[] query = {mPostReference.orderByChild("level").limitToLast(maxRankUser)};
        query[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int size = (int)dataSnapshot.getChildrenCount();
                    int cnt = 0;

                    // get ranked users data
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        cnt++;
                        userData[size - cnt] = issue.getValue(UserData.class);
                        // array start at 0, but rank start at 1
                        rank[size - cnt] = size - cnt + 1;
                    }
                }

                // get player rank
                query[0] = mPostReference.orderByChild("level");
                query[0].addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int size = (int)dataSnapshot.getChildrenCount();
                        int cnt = 0;

                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                cnt++;
                                UserData get = issue.getValue(UserData.class);
                                if(get.nickname.equals(username)){

                                    int index;

                                    // if there are lesser than 100 users, index should be changed
                                    if(size < maxRankUser){
                                        index = size;
                                    }
                                    // else, it will located in 101th index of array
                                    else{
                                        index = maxRankUser;
                                    }

                                    userData[index] = issue.getValue(UserData.class);
                                    // array start at 0, but rank start at 1
                                    rank[index] = size - cnt + 1;
                                }
                            }
                        }

                        // put information's to intent and start ranking activity
                        Intent intent = new Intent(MainMenu.this, RankingActivity.class);
                        intent.putExtra("rank", rank);
                        intent.putExtra("userData", userData);
                        if(size < maxRankUser){
                            intent.putExtra("size", size+1);
                        }
                        else{
                            intent.putExtra("size", maxRankUser + 1);
                        }
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

}
