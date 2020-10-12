package com.example.bohemeow;

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

public class ConfigEditWeightActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_edit_weight);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText newWeight = findViewById(R.id.newWeight);

        Button change_btn = findViewById(R.id.yes_btn);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_list").child(userData.nickname);
                ref.child("weight").setValue(Integer.valueOf(newWeight.getText().toString()));

                Toast.makeText(ConfigEditWeightActivity.this, "체중 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}