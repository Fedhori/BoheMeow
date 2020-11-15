package com.bohemeow_v1.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfoActivity extends Activity {

    ImageView catFace;

    TextView user_name;
    TextView user_level;
    TextView total_count;
    TextView total_dis;
    TextView total_time;
    TextView total_spot;
    TextView user_introduction;

    Button walkdataBtn;
    Button typedataBtn;

    TextView type_name;
    TextView type_detail;
    ImageView type_parameter;
    ImageView prtext;
    ImageView box;

    TypeData[] TypeDatas = new TypeData[9];

    boolean isWalk = true;
    int typeNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");
        typeNum = userData.catType;

        catFace = findViewById(R.id.catFace);
        user_name = findViewById(R.id.username);
        user_level = findViewById(R.id.userLevel);
        total_count = findViewById(R.id.walkCount);
        total_dis = findViewById(R.id.walkLength);
        total_time = findViewById(R.id.walkTime);
        total_spot = findViewById(R.id.totalSpotCount);
        user_introduction = findViewById(R.id.introduction);
        type_name = findViewById(R.id.typename);
        type_detail = findViewById(R.id.typedetail);
        type_parameter = findViewById(R.id.imageView31);
        prtext = findViewById(R.id.imageView32);
        box = findViewById(R.id.imageView28);

        total_spot.setText(userData.totalSpotCount + " 번");
        user_introduction.setText(userData.introduction);

        //set cat image
        TypeDatas = TypeData.makeTypeData();
        catFace.setImageResource(TypeDatas[typeNum].getImage());

        //set detail data
        user_name.setText(userData.nickname + "의 정보");
        user_level.setText("Lv. " + Integer.toString(calculateLevel(userData.level)));
        total_count.setText(Integer.toString(userData.totalWalkCount) + " 번");

        float distance = (float) userData.totalWalkLength / 1000f;
        String strNumber = String.format("%.2f", distance);
        total_dis.setText(strNumber + " km");

        long totalTime = userData.realWalkTime; // ms
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
        total_time.setText(timeText);

        walkdataBtn = findViewById(R.id.button8);
        walkdataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isWalk) {
                    walkdataBtn.setBackgroundResource(R.drawable.main_config_tap_on);
                    typedataBtn.setBackgroundResource(R.drawable.main_config_tap_off);
                    type_name.setText("");
                    type_detail.setText("");
                    box.setVisibility(View.INVISIBLE);
                    type_parameter.setVisibility(View.INVISIBLE);
                    prtext.setVisibility(View.INVISIBLE);
                    isWalk = true;
                }
            }
        });

        typedataBtn = findViewById(R.id.button9);
        typedataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWalk) {
                    walkdataBtn.setBackgroundResource(R.drawable.main_config_tap_off);
                    typedataBtn.setBackgroundResource(R.drawable.main_config_tap_on);
                    String text = TypeDatas[typeNum].getName();
                    if(typeNum == 1 || typeNum == 5) text += "은?";
                    else text += "는?";
                    type_name.setText(text);
                    type_detail.setText(TypeDatas[typeNum].getDetail());
                    box.setVisibility(View.VISIBLE);
                    type_parameter.setVisibility(View.VISIBLE);
                    type_parameter.setImageResource(TypeDatas[typeNum].getPrimage());
                    prtext.setVisibility(View.VISIBLE);
                    isWalk = false;
                }
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