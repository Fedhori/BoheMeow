package com.bohemeow.bohemeow;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RankPopUpActivity extends Activity {

    RankData rankData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rank_pop_up);

        //set cat image
        int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
                R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};

        Intent intent = getIntent();
        rankData = (RankData) intent.getSerializableExtra("rankData");

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView catFace = findViewById(R.id.catFace);
        catFace.setBackgroundResource(icons[rankData.catType]);

        TextView usernameTV = findViewById(R.id.username);
        usernameTV.setText(rankData.nickname + "의 산책정보");

        TextView introductionTV = findViewById(R.id.introduction);
        introductionTV.setText(rankData.introduction);

        int level = calculateLevel(rankData.level);
        TextView levelTV = findViewById(R.id.level);
        levelTV.setText("레벨: " + level);

        double distance = rankData.totalWalkLength / 1000f;
        String strNumber = String.format("%.2f", distance);
        TextView walkLengthTV = findViewById(R.id.walkLength);
        walkLengthTV.setText("누적거리: " + strNumber + "km");

        TextView walkCountTV = findViewById(R.id.walkCount);
        walkCountTV.setText("산책횟수: " + rankData.totalWalkCount + "회");

        long totalTime = rankData.totalWalkTime; // ms
        long hour;
        long minute;
        long second;
        hour = totalTime / 3600000;
        totalTime %= 3600000;
        minute = totalTime / 60000;
        totalTime %= 60000;
        second = totalTime / 1000;
        String timeText = "";
        if(hour >= 10){
            timeText += String.valueOf(hour);
        }
        else{
            timeText += "0" + hour;
        }
        timeText += ":";
        if(minute >= 10){
            timeText += String.valueOf(minute);
        }
        else{
            timeText += "0" + minute;
        }
        timeText += ":";
        if(second >= 10){
            timeText += String.valueOf(second);
        }
        else{
            timeText += "0" + second;
        }
        TextView walkTimeTV = findViewById(R.id.walkTime);
        walkTimeTV.setText("산책시간: " + timeText);
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