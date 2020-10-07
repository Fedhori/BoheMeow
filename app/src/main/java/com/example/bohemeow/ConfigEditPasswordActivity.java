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

public class ConfigEditPasswordActivity extends Activity {

    EditText password_check;
    EditText new_password;
    EditText new_password_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_edit_password);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");

        password_check = findViewById(R.id.password_check);
        new_password = findViewById(R.id.new_password);
        new_password_check = findViewById(R.id.new_password_check);

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button change_btn = findViewById(R.id.report_btn);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(new_password.getText().toString().equals(new_password_check.getText().toString()) && userData.password.equals(password_check.getText().toString())){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_list").child(userData.nickname);
                    ref.child("password").setValue(new_password.getText().toString());

                    Toast.makeText(ConfigEditPasswordActivity.this, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else if(!userData.password.equals(password_check.getText().toString())){
                    Toast.makeText(ConfigEditPasswordActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ConfigEditPasswordActivity.this, "재입력한 새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}