package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();

        Button communityBtn = (Button) findViewById(R.id.btn_community);
        communityBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        Button selectBtn = (Button) findViewById(R.id.btn_to_select);
        selectBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, SelectActivity.class);
                startActivity(intent);
            }
        });

        /*
        Button tempBtn = (Button) findViewById(R.id.btn_furniture);
        tempBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, SurveyActivity.class);
                startActivity(intent);
            }
        });

         */
    }
}
