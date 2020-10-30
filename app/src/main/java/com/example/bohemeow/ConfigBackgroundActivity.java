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

public class ConfigBackgroundActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_changeback);

        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = userInfo.edit();

        Button red = findViewById(R.id.red);
        red.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 1);
                editor.commit();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button white = findViewById(R.id.white);
        white.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 2);
                editor.commit();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button blue = findViewById(R.id.blue);
        blue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 3);
                editor.commit();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button black = findViewById(R.id.black);
        black.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 4);
                editor.commit();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button rainbow = findViewById(R.id.rainbow);
        rainbow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 5);
                editor.commit();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button andro = findViewById(R.id.andro);
        andro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 6);
                editor.commit();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button purple = findViewById(R.id.purple);
        purple.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 7);
                editor.commit();
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });

        Button green = findViewById(R.id.green);
        green.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putInt("backgroundImageCode", 8);
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