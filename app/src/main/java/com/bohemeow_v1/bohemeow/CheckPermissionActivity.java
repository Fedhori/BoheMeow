package com.bohemeow_v1.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CheckPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_permission);

        Button moveToLoginBtn = findViewById(R.id.moveToLoginBtn);

        moveToLoginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckPermissionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}