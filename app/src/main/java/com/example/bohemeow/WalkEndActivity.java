package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class WalkEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_end);

        TextView time = findViewById(R.id.time_view);
        TextView distance = findViewById(R.id.dis_view);
        TextView pace = findViewById(R.id.pace_view);
        TextView comment = findViewById(R.id.comment);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WalkEndActivity.this, MainMenu.class);
        startActivity(intent);
    }

}