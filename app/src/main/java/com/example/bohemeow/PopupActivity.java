package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PopupActivity extends Activity {

    EditText noteET;
    Button noteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        noteET = (EditText) findViewById(R.id.noteET);

        noteBtn = (Button) findViewById(R.id.noteBtn);
        noteBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(noteET.length() != 0){
                    Intent intent = new Intent();
                    intent.putExtra("result", noteET.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else{
                    Toast.makeText(PopupActivity.this, "내용을 입력해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}