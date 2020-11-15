package com.bohemeow.bohemeow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class TutorialActivity extends AppCompatActivity {

    int count = 0;
    int size;

    ImageView tutorialIV;
    int[] tutorialimages = {R.drawable.tutorial_main1, R.drawable.tutorial_main2, R.drawable.tutorial_main3, R.drawable.tutorial_main4, R.drawable.tutorial_main5, R.drawable.tutorial_main6,
            R.drawable.tutorial_main7, R.drawable.tutorial_main8, R.drawable.tutorial_main9, R.drawable.tutorial_main10,
            R.drawable.tutorial_startwalk1, R.drawable.tutorial_startwalk2, R.drawable.tutorial_startwalk3, R.drawable.tutorial_startwalk4, R.drawable.tutorial_startwalk5,
            R.drawable.tutorial_walking1, R.drawable.tutorial_walking2, R.drawable.tutorial_walking3, R.drawable.tutorial_walking4, R.drawable.tutorial_walking5, R.drawable.tutorial_walking6,
            R.drawable.tutorial_walking7, R.drawable.tutorial_walking8, R.drawable.tutorial_walking9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialIV = findViewById(R.id.imageView39);
        size = tutorialimages.length;

        ImageButton skip_btn =  findViewById(R.id.skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialActivity.this, TutorialSkipActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                count++;
                if(count < size){
                    tutorialIV.setBackgroundResource(tutorialimages[count]);
                }
                else{
                    Intent intent = new Intent(TutorialActivity.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if(requestCode==1){
            if(resultCode == 1){
                Intent intent = new Intent(TutorialActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private long backKeyPressedTime = 0;
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            Intent intent = new Intent(TutorialActivity.this, TutorialSkipActivity.class);
            startActivityForResult(intent, 1);
        }
    }

}
