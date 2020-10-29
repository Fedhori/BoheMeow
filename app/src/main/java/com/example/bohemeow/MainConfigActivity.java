package com.example.bohemeow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainConfigActivity extends Activity {

    ImageButton cat_imgbtn;

    TextView user_id;
    TextView user_name;
    TextView user_level;
    TextView total_count;
    TextView total_dis;
    TextView total_time;

    Button back_btn;
    Button edit_btn;
    Button man_btn;
    Button logout_btn;
    Button delacc_btn;
    Button bugreport_btn;

    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config_popup);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");

        cat_imgbtn = findViewById(R.id.cat_img);
        user_id = findViewById(R.id.user_id);
        user_name = findViewById(R.id.user_name);
        user_level = findViewById(R.id.user_level);
        total_count = findViewById(R.id.total_count);
        total_dis = findViewById(R.id.total_dis);
        total_time = findViewById(R.id.total_time);

        //set cat image
        int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
                R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};
        cat_imgbtn.setImageResource(icons[userData.catType]);
        cat_imgbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigCharacterActivity.class);
                intent.putExtra("catType", userData.catType);
                startActivity(intent);
            }
        });

        //set detail data
        user_id.setText(userData.id);
        user_name.setText(userData.nickname);
        user_level.setText("Lv." + Integer.toString(calculateLevel(userData.level)));
        total_count.setText(Integer.toString(userData.totalWalkCount) + "번");

        int distance = (int) userData.totalWalkLength;
        total_dis.setText(Integer.toString(distance) + "m");

        long totalTime = userData.totalWalkTime; // ms
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




        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
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

        man_btn = findViewById(R.id.man_btn);
        man_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigBackgroundActivity.class);
                startActivityForResult(intent, 1);
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



        progressBar = findViewById(R.id.progressbar);

        double currentLevel = 0;

        if(userData.level >= 10000){
            currentLevel = ((double) ((userData.level - 10000) % 1500)) / 15d;
        }
        else{
            currentLevel = ((double) userData.level % 1000) / 10d;
        }

        progressBar.setProgress((int)currentLevel);

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
