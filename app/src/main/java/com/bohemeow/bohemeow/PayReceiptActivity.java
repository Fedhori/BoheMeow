package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PayReceiptActivity extends Activity {

    String[] MYnames = {"[중앙학술정보관]\n카페테리아", "[경영관]\n사랑방", "[600주년기념관]\n지하 1층 카페", "[경영관]\nttt"};
    String[] YJnames = {"[산학협력센터]\n팬도로시", "[공학관]\nCafe:NU", "[의관]\n카페나무", "[공학관]\n매점"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_receipt);


        TextView storeNameTV = findViewById(R.id.storeName);
        TextView timeTV = findViewById(R.id.textView35);
        TextView point_useTV = findViewById(R.id.textView36);
        TextView point_remainTV = findViewById(R.id.textView37);

        final Intent intent = getIntent();


        int num = intent.getIntExtra("storeNum", 0);
        String time = intent.getStringExtra("time");

        if(num < 4)
            storeNameTV.setText(MYnames[num]);
        else
            storeNameTV.setText(YJnames[num-4]);


        timeTV.setText(time.substring(0, 10) + " " + time.substring(11,19));
        point_useTV.setText(intent.getStringExtra("point_use"));
        point_remainTV.setText(intent.getStringExtra("point_remain"));


        Button btn = findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener(){
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