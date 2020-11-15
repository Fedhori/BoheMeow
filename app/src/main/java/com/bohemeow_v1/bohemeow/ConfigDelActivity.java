package com.bohemeow_v1.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigDelActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config_delete);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");

        final EditText passwordCheck = findViewById(R.id.passwordCheck);

        Button yes_btn = findViewById(R.id.yes_btn);
        yes_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(passwordCheck.getText().toString().equals(userData.password)){
                    Toast.makeText(ConfigDelActivity.this, "계정이 삭제되었습니다.", Toast.LENGTH_LONG).show();
                    deleteUser(userData.nickname);
                    // 이 부분은 로그아웃과 같음
                    Intent intent = new Intent();
                    setResult(1, intent);
                    finish();
                }
                else{
                    Toast.makeText(ConfigDelActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
                //double check

            }
        });

        Button no_btn = findViewById(R.id.no_btn);
        no_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
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

    public void deleteUser(String username){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);
        ref.removeValue();
    }


}