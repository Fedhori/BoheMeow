package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;

public class RankActivity extends Activity {

    boolean isDailyRank = false;

    TextView[] rankViews = new TextView[2];
    ImageButton[] catImages = new ImageButton[5];
    TextView[] usernameViews = new TextView[5];
    TextView[] pointViews = new TextView[5];

    int[] rank = new int[5];
    int[] catTypes = new int[5];
    String[] usernames = new String[5];
    int[] points = new int[5];
    String[] introductions = new String[5];

    //set cat image
    int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
            R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rank);

        Arrays.fill(catTypes, 1);

        Intent intent = getIntent();

        rank = intent.getIntArrayExtra("rank");
        catTypes = intent.getIntArrayExtra("catTypes");
        usernames = intent.getStringArrayExtra("usernames");
        points = intent.getIntArrayExtra("points");
        introductions = intent.getStringArrayExtra("introductions");
        isDailyRank = intent.getBooleanExtra("isDailyRank", false);

        TextView rank_text = findViewById(R.id.rank_text);
        if(isDailyRank){
            rank_text.setText("일간 랭킹");
        }
        else{
            rank_text.setText("주간 랭킹");
        }


        rankViews[0] = findViewById(R.id.rank4);
        rankViews[1] = findViewById(R.id.rank5);
        catImages[0] = findViewById(R.id.catImage1);
        catImages[1] = findViewById(R.id.catImage2);
        catImages[2] = findViewById(R.id.catImage3);
        catImages[3] = findViewById(R.id.catImage4);
        catImages[4] = findViewById(R.id.catImage5);
        usernameViews[0] = findViewById(R.id.username1);
        usernameViews[1] = findViewById(R.id.username2);
        usernameViews[2] = findViewById(R.id.username3);
        usernameViews[3] = findViewById(R.id.username4);
        usernameViews[4] = findViewById(R.id.username5);
        pointViews[0] = findViewById(R.id.point1);
        pointViews[1] = findViewById(R.id.point2);
        pointViews[2] = findViewById(R.id.point3);
        pointViews[3] = findViewById(R.id.point4);
        pointViews[4] = findViewById(R.id.point5);

        for(int i = 0;i<5;i++){
            setRankPanel(rank[i], catTypes[i], usernames[i], points[i], i);
        }
        for(int i = 0;i<5;i++){
            final int finalI = i;
            catImages[i].setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RankActivity.this, RankUserInfoActivity.class);
                    intent.putExtra("username", usernames[finalI]);
                    intent.putExtra("introduction", introductions[finalI]);
                    intent.putExtra("catType", catTypes[finalI]);
                    startActivity(intent);
                }
            });
        }

        Button back_btn = findViewById(R.id.back_btn3);
        back_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void setRankPanel(int rank, int catType, String username, int point, int panel_num){
        if(panel_num >= 3){
            rankViews[panel_num - 3].setText(Integer.toString(rank));
        }
        catImages[panel_num].setBackgroundResource(icons[catType]);
        usernameViews[panel_num].setText(username);
        pointViews[panel_num].setText(Integer.toString(point));
    }
}