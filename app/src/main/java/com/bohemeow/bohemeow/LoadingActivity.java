package com.bohemeow.bohemeow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Random;

public class LoadingActivity extends AppCompatActivity {

    TextView loadingText;
    long waitingTime = 2000; // millisecond
    int readPhoneStatePermission;
    int readLocationStatePermission;
    int writeExternalStoragePermission;

    private String[] loadingTexts = {
            "전동킥보드나 자전거를 이용하는 경우 정상적으로 기록되지 않습니다.",
            "자전거 모드는 아직 제공되지 않습니다.",
            "적당한 페이스의 조깅과 달리기는 정상 기록됩니다.",
            "쪽지를 남기고 발견하면서 동네 고양이들을 알아가보세요.",
            "열심히 걷다보면 보물을 발견할 수도 있습니다.",
            "추천 스팟을 많이 방문하다보면 선반에 인형이 하나둘 모입니다.",
            "매일매일 커뮤니티에 글을 남기고 포인트를 모아보세요.",
            "커뮤니티의 비공개 기능을 이용해 나만의 산책일지를 남겨보세요.",
            "총 12가지의 풍경이 준비되어 있습니다. 보헤미양의 첫눈을 기다려보세요.",
            "현실 시각에 맞추어 바뀌는 고양이 방 풍경을 구경해보세요.",
            "성장한 고양이는 다섯가지 다른 모습을 보여줍니다.",
            "특정 레벨에 도달하면 성묘로 성장합니다."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        /*
        // 테스트를 위해 넣어둔 코드임, 이러면 매번 접속할때마다 새로 로그인할 수 있다. (테스트를 위한 자동 로그인 방지용!)
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "NULL");
        editor.commit();
         */

        // Check if we have READ_PHONE_STATE permission
        readPhoneStatePermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        readLocationStatePermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        writeExternalStoragePermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);


        loadingText = findViewById(R.id.loading_text);
        Random random = new Random();
        loadingText.setText(loadingTexts[random.nextInt(loadingTexts.length)]);

        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), waitingTime); // 3초 후에 hd handler 실행  3000ms = 3초
    }

    private class splashHandler implements Runnable{
        public void run(){
            SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
            // user hasn't registered yet
            if(registerInfo.getString("registerUserName", "NULL").equals("NULL")){

                // permission not granted yey
                if ( readPhoneStatePermission != PackageManager.PERMISSION_GRANTED || readLocationStatePermission != PackageManager.PERMISSION_GRANTED || writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(LoadingActivity.this, CheckPermissionActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
            // user already registered
            else{
                Intent intent = new Intent(LoadingActivity.this, MainMenu.class);
                startActivity(intent);
            }
            LoadingActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}
