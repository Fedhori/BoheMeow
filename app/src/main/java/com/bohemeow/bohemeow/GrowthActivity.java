package com.bohemeow.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GrowthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth);

        Button confrimBtn = findViewById(R.id.confirmBtn);
        confrimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GrowthActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });
    }
}