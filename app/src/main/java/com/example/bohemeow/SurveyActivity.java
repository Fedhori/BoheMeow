package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


class Survey{
    String sentence;
    int type; //0:safe, 1:envi, 2:pop
    int score;

    public Survey(String sentence, int type, int score) {
        this.sentence = sentence;
        this.type = type;
        this.score = score;
    }
}

public class SurveyActivity extends AppCompatActivity{

    ImageView[] remain_surveys = new ImageView[100];

    // this string array will store surveys text
    Survey[] surveys = new Survey[9];
    /*
    String[] surveys = {
            "어둠 속에서 걷는 걸 즐긴다",
            "노래방에서 낭만고양이를 부르곤 한다",
            "가끔 광합성이 필요하다",
            "한적하고 고즈넉한 길이 좋다",
            "남들 다 가본 곳은 꼭 가봐야 직성이 풀린다",
            "SNS에서 좋아요를 많이 받고 싶다",
            "푸른 풍경을 보다보면 스트레스가 풀린다",
            "혈중 피톤치드 농도가 떨어지면 괴롭다",
            "잘하든 못하든 식물 키우는 건 즐겁다"
    };
     */
    void makeSurvey(){
        surveys[0] = new Survey("어둠 속에서 걷는 걸 즐긴다", 0, -1);
        surveys[1] = new Survey("노래방에서 낭만고양이를 부르곤 한다", 0, -1);
        surveys[2] = new Survey("가끔 광합성이 필요하다", 0, 1);
        surveys[3] = new Survey("푸른 풍경을 보다보면 스트레스가 풀린다", 1, 1);
        surveys[4] = new Survey("혈중 피톤치드 농도가 떨어지면 괴롭다", 1, 1);
        surveys[5] = new Survey("잘하든 못하든 식물 키우는 건 즐겁다", 1, 1);
        surveys[6] = new Survey("한적하고 고즈넉한 길이 좋다", 2, -1);
        surveys[7] = new Survey("남들 다 가본 곳은 꼭 가봐야 직성이 풀린다", 2, 1);
        surveys[8] = new Survey("SNS에서 좋아요를 많이 받고 싶다", 2, 1);
    }

    boolean[] answers = new boolean[100];

    ConstraintLayout constraintLayout;

    ImageView iv;
    TextView tv_survey;
    TextView tv_num;
    // number of surveys
    int num_survey = 9;
    // current survey
    int cur_survey = 0;

    float proceed_size = 52f;
    float center_x = 720f;

    int[] preference = {8, 8, 8};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        /*
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "Bonjour!");
        editor.commit();
        String user_nickname = registerInfo.getString("registerUserName", "NULL");
         */

        makeSurvey();

        constraintLayout = (ConstraintLayout) findViewById(R.id.survey_layout);

        iv = findViewById(R.id.imageView8);
        tv_survey = (TextView) findViewById(R.id.survey_text);
        tv_num = findViewById(R.id.num);
/*
        for(int i = 0;i<9;i++){
            ImageView remain_survey = new ImageView(this);
            remain_survey.setImageResource(R.drawable.survey_proceed);
            remain_survey.setX(720f + (1f / 2f - num_survey + 2 * i) * proceed_size);
            remain_survey.setY(128f);
            remain_surveys[i] = remain_survey;
            constraintLayout.addView(remain_survey);
            setContentView(constraintLayout);
        }
*/
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        tv_survey.setText(surveys[cur_survey].sentence);

        Button btn_no = findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                answers[cur_survey] = false;
                preference[surveys[cur_survey].type] -= surveys[cur_survey].score;

                cur_survey++;
                if(cur_survey == num_survey){

                    // 자동로그인이 가능하게 하기 위해 이제 로컬 데이터에 사용자의 닉네임 저장
                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = registerInfo.edit();
                    editor.putString("registerUserName", username);
                    editor.commit();

                    int catType = decideCat(preference[0], preference[1], preference[2], 8);
                    savePreference(username, preference, catType);
                    Intent intent = new Intent(SurveyActivity.this, SurveyResultActivity.class);
                    intent.putExtra("catType", catType);
                    startActivity(intent);
                }
                else{
                    tv_num.setText(Integer.toString(cur_survey+1));
                    tv_survey.setText(surveys[cur_survey].sentence);
                }
            }
        });

        Button btn_yes = findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                answers[cur_survey] = true;
                preference[surveys[cur_survey].type] += surveys[cur_survey].score;

                cur_survey++;
                if(cur_survey == num_survey){

                    // 자동로그인이 가능하게 하기 위해 이제 로컬 데이터에 사용자의 닉네임 저장
                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = registerInfo.edit();
                    editor.putString("registerUserName", username);
                    editor.commit();

                    int catType = decideCat(preference[0], preference[1], preference[2], 8);
                    savePreference(username, preference, catType);
                    Intent intent = new Intent(SurveyActivity.this, SurveyResultActivity.class);
                    intent.putExtra("catType", catType);
                    startActivity(intent);
                }
                else{
                    tv_num.setText(Integer.toString(cur_survey+1));
                    tv_survey.setText(surveys[cur_survey].sentence);
                }
            }
        });

        /*
        iv.setOnTouchListener(new OnSwipeTouchListener(SurveyActivity.this){
            public void onSwipeLeft(){
                answers[cur_survey] = false;
                preference[surveys[cur_survey].type] -= surveys[cur_survey].score;

                cur_survey++;
                if(cur_survey == num_survey){

                    // 자동로그인이 가능하게 하기 위해 이제 로컬 데이터에 사용자의 닉네임 저장
                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = registerInfo.edit();
                    editor.putString("registerUserName", username);
                    editor.commit();

                    int catType = decideCat(preference[0], preference[1], preference[2], 8);
                    savePreference(username, preference, catType);
                    Intent intent = new Intent(SurveyActivity.this, SurveyResultActivity.class);
                    intent.putExtra("catType", catType);
                    startActivity(intent);
                }
                else{
                    tv_num.setText(Integer.toString(cur_survey+1));
                    tv_survey.setText(surveys[cur_survey].sentence);
                }
            }

            public void onSwipeRight(){
                answers[cur_survey] = true;
                preference[surveys[cur_survey].type] += surveys[cur_survey].score;

                cur_survey++;
                if(cur_survey == num_survey){

                    // 자동로그인이 가능하게 하기 위해 이제 로컬 데이터에 사용자의 닉네임 저장
                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = registerInfo.edit();
                    editor.putString("registerUserName", username);
                    editor.commit();

                    int catType = decideCat(preference[0], preference[1], preference[2], 8);
                    savePreference(username, preference, catType);
                    Intent intent = new Intent(SurveyActivity.this, SurveyResultActivity.class);
                    intent.putExtra("catType", catType);
                    startActivity(intent);
                }
                else{
                    tv_num.setText(Integer.toString(cur_survey+1));
                    tv_survey.setText(surveys[cur_survey].sentence);
                }
            }



        });
         */

/*
        iv_yes.setOnTouchListener(new OnSwipeTouchListener(SurveyActivity.this){


        });


 */

    }

    void savePreference(String username, int[] preference, int catType){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user_list/" + username);
        myRef.child("safeScore").setValue(preference[0]);
        myRef.child("enviScore").setValue(preference[1]);
        myRef.child("popularity").setValue(preference[2]);
        myRef.child("catType").setValue(catType);
    }

    int decideCat(int safe, int envi, int pop, int standard){
        int catType = 0;

        boolean isSafe = false;
        boolean isEnvi = false;
        boolean isPopu = false;

        if(safe >= standard) isSafe = true;
        if(envi >= standard) isEnvi = true;
        if(pop >= standard) isPopu = true;

        if(isSafe && isEnvi && isPopu) catType = 1; //한강
        if(!isSafe && isEnvi && isPopu) catType = 2;//밤이
        if(isSafe && !isEnvi && isPopu) catType = 3;//chacha
        if(isSafe && isEnvi && !isPopu) catType = 4;//려니
        if(!isSafe && !isEnvi && isPopu) catType = 5;//문문
        if(!isSafe && isEnvi && !isPopu) catType = 6;//포포
        if(isSafe && !isEnvi && !isPopu) catType = 7;//태태
        if(!isSafe && !isEnvi && !isPopu) catType = 8;//새싹이


        return catType;

    }
}


