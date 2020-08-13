package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {

    private Context context;
    private ProgressBar progressBar;
    private TextView loadingText;
    private float from;
    private float to;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView loadingText, float from, float to){
        this.context = context;
        this.progressBar = progressBar;
        this.loadingText = loadingText;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);

        if(value == to){

            /*
            SharedPreferences registerInfo = context.getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
            // user hasn't registered yet
            if(registerInfo.getString("registerUserName", "NULL").equals("NULL")){
                Intent intent = new Intent(context, RegisterActivity.class);
                context.startActivity(intent);
            }
            // user already registered
            else{
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
             */
        }
    }
}
