package com.example.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PayReceiptActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_receipt);


        TextView storeNameTV = findViewById(R.id.textView30);
        TextView timeTV = findViewById(R.id.textView31);
        TextView point_useTV = findViewById(R.id.textView25);
        TextView point_remainTV = findViewById(R.id.textView29);

        final Intent intent = getIntent();

        storeNameTV.setText("매장명 :  "  + intent.getStringExtra("storeName"));
        timeTV.setText("거래 일시 :  " + intent.getStringExtra("time"));
        point_useTV.setText("결제 금액 :  " + intent.getStringExtra("point_use"));
        point_remainTV.setText("잔여 포인트 :  " + intent.getStringExtra("point_remain"));


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