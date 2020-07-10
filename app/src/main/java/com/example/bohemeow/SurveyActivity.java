package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SurveyActivity extends AppCompatActivity{

    ImageView[] remain_surveys = new ImageView[100];

    // this string array will store surveys text
    String[] surveys = {
            "느긋하게 걷는 것 보단\n조금 숨차게 운동하듯 걷는 게 좋다",
            "솔직히 민트초코는 맛있다.",
            "나는 지금 피곤하다",
            "나는 개발중이다"
    };

    boolean[] answers = new boolean[100];

    ConstraintLayout constraintLayout;
    ImageView iv_ball;
    TextView tv_survey;
    // number of surveys
    int num_survey = 4;
    // current survey
    int cur_survey = 0;

    float proceed_size = 52f;
    float center_x = 720f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        constraintLayout = (ConstraintLayout) findViewById(R.id.survey_layout);
        iv_ball = (ImageView) findViewById(R.id.ball);
        tv_survey = (TextView) findViewById(R.id.survey_text);

        for(int i = 0;i<4;i++){
            ImageView remain_survey = new ImageView(this);
            remain_survey.setImageResource(R.drawable.survey_proceed);
            remain_survey.setX(720f + (1f / 2f - num_survey + 2 * i) * proceed_size);
            remain_survey.setY(128f);
            remain_surveys[i] = remain_survey;
            constraintLayout.addView(remain_survey);
            setContentView(constraintLayout);
        }

        Intent intent = getIntent();

        tv_survey.setText(surveys[cur_survey]);

        iv_ball.setOnTouchListener(new OnSwipeTouchListener(SurveyActivity.this){
            public void onSwipeLeft(){
                answers[cur_survey] = false;

                cur_survey++;
                remain_surveys[num_survey - cur_survey].setVisibility(View.INVISIBLE);
                if(cur_survey == num_survey){
                    Intent intent = new Intent(SurveyActivity.this, MainMenu.class);
                    startActivity(intent);
                }
                else{
                    tv_survey.setText(surveys[cur_survey]);
                }
            }
            public void onSwipeRight(){
                answers[cur_survey] = true;

                cur_survey++;
                remain_surveys[num_survey - cur_survey].setVisibility(View.INVISIBLE);
                if(cur_survey == num_survey){
                    Intent intent = new Intent(SurveyActivity.this, MainMenu.class);
                    startActivity(intent);
                }
                else{
                    tv_survey.setText(surveys[cur_survey]);
                }
            }
        });
    }
}


