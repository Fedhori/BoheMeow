package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SurveyResultActivity extends AppCompatActivity{

    ConstraintLayout constraintLayout;
    ImageView iv_ball;
    TextView tv_survey;
    // number of surveys
    int num_survey = 9;
    // current survey
    int cur_survey = 0;

    float proceed_size = 52f;
    float center_x = 720f;

    int[] preference = {5, 5, 5};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_result);


    }


}


