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

class CatDetail{

    int image;
    String name;
    String detail;

    public CatDetail(int image, String name, String detail) {
        this.image = image;
        this.name = name;
        this.detail = detail;
    }
}


public class SurveyResultActivity extends AppCompatActivity{


    ImageView cat_Img;
    TextView name;
    TextView detail;

    CatDetail[] catDetails = new CatDetail[9];

    void makeCatDetail(){
        catDetails[0] = new CatDetail(R.drawable.beth_0000, "이름", "고양이 설명");
        catDetails[1] = new CatDetail(R.drawable.full_0001,"한강", "한강공원에 사는 한강이는 소풍 온 사람들의 도시락 냄새를 좋아합니다.");
        catDetails[2] = new CatDetail(R.drawable.full_0002,"밤이", "여수 바닷가에 사는 밤이는 이순신광장의 스타입니다. 언젠가 케이블카를 타보고 싶습니다.");
        catDetails[3] = new CatDetail(R.drawable.full_0003,"Chacha", "이태원에 사는 chacha는 루프탑에서 남산 바라보기를 좋아합니다.");
        catDetails[4] = new CatDetail(R.drawable.full_0004,"려니", "제주 사려니숲길에 사는 려니는 햇빛 속에 흔들리는 잎사귀를 좋아합니다. ");
        catDetails[5] = new CatDetail(R.drawable.full_0005,"문문", "해운대 달맞이길에 사는 문문이는 보름달과 산책하러 오는 사람들을 좋아합니다.");
        catDetails[6] = new CatDetail(R.drawable.full_0006,"포포", "강릉 경포호에 사는 포포는 늦저녁 풀벌레 소리를 들으며 호숫가를 산책합니다.");
        catDetails[7] = new CatDetail(R.drawable.full_0007,"태태", "울산대교에 사는 태태는 한낮 태화강변을 따라 산책하기를 좋아합니다.  ");
        catDetails[8] = new CatDetail(R.drawable.full_0008,"새싹", "전주 한옥마을에 사는 새싹이는 새싹비빔밥 냄새를 좋아합니다. 해가 지면 밥짓는 냄새를 맡으며 홀로 골목골목 다니곤 합니다.");
    }

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

        makeCatDetail();

        cat_Img = findViewById(R.id.cat_Img);
        name = findViewById(R.id.user_name);
        detail = findViewById(R.id.detail);


        cat_Img.setImageResource(catDetails[catType].image);
        name.setText(catDetails[catType].name);
        detail.setText(catDetails[catType].detail);


        Button next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SurveyResultActivity.this, MainMenu.class);
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


