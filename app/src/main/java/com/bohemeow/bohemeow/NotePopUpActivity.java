package com.bohemeow.bohemeow;

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
        authorTV.setText("from. " + author);

        TextView contentTV = findViewById(R.id.contentTV);
        contentTV.setText(noteContent);
    }
}