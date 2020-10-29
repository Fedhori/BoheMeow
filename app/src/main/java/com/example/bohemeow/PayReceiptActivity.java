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


        TextView storeNameTV = findViewById(R.id.storeName);
        TextView timeTV = findViewById(R.id.textView35);
        TextView point_useTV = findViewById(R.id.textView36);
        TextView point_remainTV = findViewById(R.id.textView37);

        final Intent intent = getIntent();


        String store = intent.getStringExtra("storeName");
        if(store.equals("NU")) store = "Cafe NU:";
        else if (store.equals("Pandorothy")) store = "팬도로시";

        String time = intent.getStringExtra("time");

        storeNameTV.setText(store);
        timeTV.setText(time.substring(0, 10) + "\n" + time.substring(11,19));
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