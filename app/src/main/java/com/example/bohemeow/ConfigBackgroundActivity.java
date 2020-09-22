package com.example.bohemeow;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

public class ConfigBackgroundActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_changeback);

        CheckBox cb1 = findViewById(R.id.checkBox2);
        CheckBox cb2 = findViewById(R.id.checkBox3);
        CheckBox cb3 = findViewById(R.id.checkBox4);
        CheckBox cb4 = findViewById(R.id.checkBox5);

        Button change_btn = findViewById(R.id.change_btn);
        change_btn.setOnClickListener(new View.OnClickListener(){
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