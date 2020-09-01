package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalkLoadingProgressBarAnimation extends Animation {
    private Context context;
    private ProgressBar progressBar;
    private float from;
    private float to;
    private int[] preference;

    public WalkLoadingProgressBarAnimation(Context context, ProgressBar progressBar, float from, float to, int[] preference){
        this.context = context;
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
        this.preference = preference;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);

        if(value == to){
            Intent intent = new Intent(context, WalkActivity.class);
            intent.putExtra("preference", preference);
            context.startActivity(intent);
        }
    }
}
