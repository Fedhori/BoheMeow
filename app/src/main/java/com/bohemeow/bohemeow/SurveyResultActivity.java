package com.bohemeow.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class SurveyResultActivity extends AppCompatActivity{


    ImageView cat_Img;
    ImageView pr_Img;
    TextView name;
    TextView detail;

    TypeData[] TypeDatas = new TypeData[9];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_result);

        Intent intent = getIntent();
        int catType = intent.getIntExtra("catType", 0);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putInt("userCatType", catType);
        editor.commit();

        TypeDatas = TypeData.makeTypeData();

        cat_Img = findViewById(R.id.cat_Img);
        pr_Img = findViewById(R.id.parameter);
        name = findViewById(R.id.user_name);
        detail = findViewById(R.id.detail);


        cat_Img.setImageResource(TypeDatas[catType].image);
        pr_Img.setImageResource(TypeDatas[catType].primage);
        name.setText(TypeDatas[catType].name);
        detail.setText(TypeDatas[catType].detail);


        Button next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SurveyResultActivity.this, TutorialActivity.class);
                SurveyResultActivity.this.finish();
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(CommunityActivity.this, MainMenu.class);
        //startActivity(intent);
    }


}


