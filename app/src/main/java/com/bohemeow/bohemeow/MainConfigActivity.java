package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainConfigActivity extends Activity {

    ImageView catFace;

    TextView user_name;
    TextView user_level;
    TextView total_count;
    TextView total_dis;
    TextView total_time;
    TextView total_spot;
    TextView user_introduction;

    Button edit_btn;
    Button tutorial_btn;
    Button logout_btn;
    Button delacc_btn;
    Button bugreport_btn;
    Button FAQ_btn;

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
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config_popup);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");
        typeNum = userData.catType;

        catFace = findViewById(R.id.cat_img);
        user_name = findViewById(R.id.user_name);
        user_level = findViewById(R.id.user_level);
        total_count = findViewById(R.id.total_count);
        total_dis = findViewById(R.id.total_dis);
        total_time = findViewById(R.id.total_time);
        total_spot = findViewById(R.id.total_spot);
        user_introduction = findViewById(R.id.tv_introduction);
        type_name = findViewById(R.id.typename2);
        type_detail = findViewById(R.id.typedetail2);
        type_parameter = findViewById(R.id.imageView38);
        prtext = findViewById(R.id.imageView41);
        box = findViewById(R.id.imageView35);


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

        walkdataBtn = findViewById(R.id.button4);
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

        typedataBtn = findViewById(R.id.button5);
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

        edit_btn = findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigEditActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        tutorial_btn = findViewById(R.id.tutorial_btn);
        tutorial_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, TutorialActivity.class);
                intent.putExtra("userdata", userData);
                startActivity(intent);
                finish();
            }
        });

        logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigLogoutActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        delacc_btn = findViewById(R.id.delacc_btn);
        delacc_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigDelActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        bugreport_btn = findViewById(R.id.bugreport_btn);
        bugreport_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, BugReportActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        final FAQDAta[] faqData = getFaqData();
        FAQ_btn = findViewById(R.id.FAQ_btn);
        FAQ_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, FAQActivity.class);
                intent.putExtra("faqData", faqData);
                startActivityForResult(intent, 1);
            }
        });

        double currentLevel = 0;

        if(userData.level >= 10000){
            currentLevel = ((double) ((userData.level - 10000) % 1500)) / 15d;
        }
        else{
            currentLevel = ((double) userData.level % 1000) / 10d;
        }
    }

    FAQDAta[] getFaqData(){
        return new FAQDAta[]{
                new FAQDAta("나는 강을 지키고 있는 진기다. 누군지 이름을 밝혀라!", "관우"),
                new FAQDAta("어디로 가는 길이오?", "하북"),
                new FAQDAta("통행증은 갖고 있겠지?", "그런 건 없다")
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){

            Intent intent = new Intent();

            switch(resultCode){
                case RESULT_OK: // result_ok -> nothing happened
                    break;
                case 0: // background case
                    setResult(0, intent);
                    finish();
                    break;
                case 1: // logout & delete account case
                    setResult(1, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
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
