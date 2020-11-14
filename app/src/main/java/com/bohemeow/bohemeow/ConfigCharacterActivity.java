package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConfigCharacterActivity extends Activity {


    ImageView cat_Img;
    ImageView pr_Img;
    TextView name;
    TextView detail;

    CatDetail[] catDetails = new CatDetail[9];

    void makeCatDetail(){
        catDetails[0] = new CatDetail(R.drawable.cathead_null, R.drawable.parametercircle_null, "이름", "고양이 설명");
        catDetails[1] = new CatDetail(R.drawable.hanggangic, R.drawable.hangangpr, "한강", "한강공원에 사는 한강이는 소풍 온 사람들의 도시락 냄새를 좋아합니다.");
        catDetails[2] = new CatDetail(R.drawable.bameeic, R.drawable.bameepr, "밤이", "여수 바닷가에 사는 밤이는 이순신광장의 스타입니다. 언젠가 케이블카를 타보고 싶습니다.");
        catDetails[3] = new CatDetail(R.drawable.chachaic, R.drawable.chachapr, "Chacha", "이태원에 사는 chacha는 루프탑에서 남산 바라보기를 좋아합니다.");
        catDetails[4] = new CatDetail(R.drawable.ryoniic, R.drawable.ryonipr, "려니", "제주 사려니숲길에 사는 려니는 햇빛 속에 흔들리는 잎사귀를 좋아합니다. ");
        catDetails[5] = new CatDetail(R.drawable.moonmoonic, R.drawable.moonmoonpr, "문문", "해운대 달맞이길에 사는 문문이는 보름달과 산책하러 오는 사람들을 좋아합니다.");
        catDetails[6] = new CatDetail(R.drawable.popoic, R.drawable.popopr, "포포", "강릉 경포호에 사는 포포는 늦저녁 풀벌레 소리를 들으며 호숫가를 산책합니다.");
        catDetails[7] = new CatDetail(R.drawable.taetaeic, R.drawable.taetaepr, "태태", "울산대교에 사는 태태는 한낮 태화강변을 따라 산책하기를 좋아합니다.  ");
        catDetails[8] = new CatDetail(R.drawable.sessakic, R.drawable.sessackpr, "새싹", "전주 한옥마을에 사는 새싹이는 새싹비빔밥 냄새를 좋아합니다. 해가 지면 밥짓는 냄새를 맡으며 홀로 골목골목 다니곤 합니다.");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_character);

        final Intent intent = getIntent();
        int catType = intent.getIntExtra("catType", 1);

        makeCatDetail();

        cat_Img = findViewById(R.id.cat_img);
        name = findViewById(R.id.catName);
        detail = findViewById(R.id.catDetail);


        cat_Img.setImageResource(catDetails[catType].image);
        name.setText(catDetails[catType].name);
        detail.setText(catDetails[catType].detail);

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }


}