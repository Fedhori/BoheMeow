package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;

    EditText idET;
    EditText passwordET;

    ImageButton registerBtn;
    ImageButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        idET = (EditText) findViewById(R.id.idET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        registerBtn = (ImageButton) findViewById(R.id.registerBtn);
        loginBtn = (ImageButton) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(passwordET.length() * idET.length() == 0){
                    Toast.makeText(LoginActivity.this, "아이디나 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else{

                    final String id = idET.getText().toString();
                    final String password = passwordET.getText().toString();

                    mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isLoginSuccess = false;
                            boolean isSurveyComplete = false;

                            String username = "";

                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                UserData get = postSnapshot.getValue(UserData.class);

                                if(id.equals(get.id) && password.equals(get.password)){
                                    isLoginSuccess = true;
                                    if(get.popularity == -1){
                                        isSurveyComplete = false;
                                    }
                                    else{
                                        isSurveyComplete = true;
                                    }
                                    username = get.nickname;
                                }
                            }

                            // someday, you need to add sharedpreference stuff
                            if(isLoginSuccess){
                                // go to main menu
                                if(isSurveyComplete){
                                    Intent intent = new Intent(LoginActivity.this, MainMenu.class);
                                    startActivity(intent);
                                }
                                // go to survey screen
                                else{
                                    Intent intent = new Intent(LoginActivity.this, SurveyActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "잘못된 아이디나 비밀번호입니다.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

        });

        registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}