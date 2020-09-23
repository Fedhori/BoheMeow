package com.example.bohemeow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class ConfigBackgroundActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_changeback);

        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = userInfo.edit();
        int backgroundImageCode = userInfo.getInt("backgroundImageCode", 1);

        final CheckBox cb1 = findViewById(R.id.checkBox2);
        final CheckBox cb2 = findViewById(R.id.checkBox3);
        final CheckBox cb3 = findViewById(R.id.checkBox4);
        final CheckBox cb4 = findViewById(R.id.checkBox5);

        switch(backgroundImageCode){
            case 1: cb1.setChecked(true);
                break;
            case 2: cb2.setChecked(true);
                break;
            case 3: cb3.setChecked(true);
                break;
            case 4: cb4.setChecked(true);
                break;
            default:
                break;
        }

        Button change_btn = findViewById(R.id.change_btn);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(cb1.isChecked()){
                    editor.putInt("backgroundImageCode", 1);
                }
                else if(cb2.isChecked()){
                    editor.putInt("backgroundImageCode", 2);
                }
                else if(cb3.isChecked()){
                    editor.putInt("backgroundImageCode", 3);
                }
                else if(cb4.isChecked()){
                    editor.putInt("backgroundImageCode", 4);
                }
                editor.commit();

                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cb1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                }
            }
        }) ;

        cb2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    cb1.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                }
            }
        }) ;

        cb3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                    cb4.setChecked(false);
                }
            }
        }) ;

        cb4.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        }) ;
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