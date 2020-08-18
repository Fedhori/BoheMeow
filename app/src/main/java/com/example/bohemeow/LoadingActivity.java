package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class LoadingActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView loadingText;

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


        // 테스트를 위해 넣어둔 코드임, 이러면 매번 접속할때마다 새로 닉네임을 지을 수 있다.
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "NULL");
        editor.commit();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progress_bar);
        loadingText = findViewById(R.id.loading_text);

        progressBar.setMax(100);

        Random random = new Random();
        loadingText.setText(loadingTexts[random.nextInt(loadingTexts.length)]);

        progressAnimation();
    }

    public void progressAnimation(){
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar, loadingText, 0f, 100f);
        anim.setDuration(8000);
        progressBar.setAnimation(anim);
    }
}
