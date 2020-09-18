package com.example.bohemeow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    int count = 0;

    ImageView iv1;
    TextView tv1;
    ImageView iv2;
    TextView tv2;
    ImageView iv3;
    TextView tv3;
    ImageView iv4;
    TextView tv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        iv1 = findViewById(R.id.iv1);
        tv1 = findViewById(R.id.tv1);
        iv2 = findViewById(R.id.iv2);
        tv2 = findViewById(R.id.tv2);
        iv3 = findViewById(R.id.iv3);
        tv3 = findViewById(R.id.tv3);
        iv4 = findViewById(R.id.iv4);
        tv4 = findViewById(R.id.tv4);

        Button skip_btn = (Button) findViewById(R.id.skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialActivity.this, MainMenu.class);
                finish();
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                count++;
                if(count == 1){
                    iv1.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.VISIBLE);
                }
                else if(count == 2){
                    iv2.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                }
                else if(count == 3){
                    iv3.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.VISIBLE);
                }
                else if(count == 4){
                    iv4.setVisibility(View.VISIBLE);
                    tv4.setVisibility(View.VISIBLE);
                }
                else{
                    Intent intent = new Intent(TutorialActivity.this, MainMenu.class);
                    finish();
                    startActivity(intent);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(CommunityActivity.this, MainMenu.class);
        //startActivity(intent);
    }

}
