package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RankUserInfoActivity extends Activity {

    //set cat image
    int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
            R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_user_info);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String introduction = intent.getStringExtra("introduction");
        int catType = intent.getIntExtra("catType", 1);

        TextView username_tv = findViewById(R.id.bugreport_tv);
        username_tv.setText("유저 " + username + "의 자기소개");

        TextView introduction_tv = findViewById(R.id.introduction_tv);
        introduction_tv.setText(introduction);

        ImageView catImage = findViewById(R.id.catImage);
        catImage.setBackgroundResource(icons[catType]);

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}