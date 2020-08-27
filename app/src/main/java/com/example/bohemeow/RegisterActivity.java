package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;

    EditText idET;
    EditText passwordET;
    EditText nicknameET;
    ImageButton registerBtn;

    ImageView catFace;
    TextView catText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        idET = (EditText) findViewById(R.id.idET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        nicknameET = (EditText) findViewById(R.id.nicknameET);
        registerBtn = (ImageButton) findViewById(R.id.registerBtn);
        catFace = (ImageView) findViewById(R.id.cat_face);
        catText = (TextView) findViewById(R.id.cat_text);

        registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(nicknameET.length() * passwordET.length() * idET.length() == 0){
                    catText.setText("비어있는 칸이 있어!");
                }
                else{

                    final String new_nickname = nicknameET.getText().toString();
                    final String new_id = idET.getText().toString();
                    final String new_password = passwordET.getText().toString();

                    mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isExist = false;

                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                UserData get = postSnapshot.getValue(UserData.class);

                                if(new_nickname.equals(get.nickname)){
                                    isExist = true;
                                }
                            }

                            if(isExist){
                                catText.setText("그 이름은 이미 존재해. 다른 이름은 어때?");
                            }
                            else{
                                addNewUser(new_nickname, new_id, new_password);
                                catText.setText(new_nickname + "!! \n멋진 이름이야. \n앞으로 잘 부탁해, " + new_nickname + ".");
                                catFace.setImageResource(R.drawable.beth_0001);

                                SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = registerInfo.edit();
                                editor.putString("registerUserName", new_nickname);
                                editor.commit();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

        });
    }

    public void addNewUser(String new_nickname, String id, String password){

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        // probably this line will be changed someday.. 아마도 설문조사 화면과 연관되게 바뀌어야겠지? -> 계정 생성 후 설문조사 -> 설문조사 중간에 꺼도 아직 설문조사를 마치지 않았을 경우 처음부터 다시
        UserData data = new UserData(new_nickname, id, password, 10, 10, 10);
        postValues = data.toMap();
        childUpdates.put("/user_list/" + new_nickname + "/", postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
