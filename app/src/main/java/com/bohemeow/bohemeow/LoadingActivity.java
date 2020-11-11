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
    long waitingTime = 3000; // millisecond
    int readPhoneStatePermission;
    int readLocationStatePermission;
    int writeExternalStoragePermission;

    private String[] loadingTexts = {
            "예의바르게 야옹하는 중",
            "수염 닦는 중",
            "발톱 넣어두고 악수하는 중",
            "콧수염에 감탄하는 중",
            "발바닥을 혀로 핥는 중",
            "괜히 꼬리를 부풀려보는 중",
            "저녀석 그루밍 좀 하는데?",
            "왜웅 한 다음 왱 하는 중"
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
        hd.postDelayed(new splashHandler(), waitingTime); // 1초 후에 hd handler 실행  3000ms = 3초
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
