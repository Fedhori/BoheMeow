package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class NotePopUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_note_pop_up);

        Intent intent = getIntent();

        String author = intent.getStringExtra("author");
        String noteContent = intent.getStringExtra("noteContent");

        TextView authorTV = findViewById(R.id.authorTV);
        authorTV.setText("작성자: " + author);

        TextView contentTV = findViewById(R.id.contentTV);
        contentTV.setText(noteContent);

        Button back_btn = findViewById(R.id.back_btn2);
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