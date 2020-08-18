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

    EditText nicknameET;
    ImageButton registerBtn;

    ImageView catFace;
    TextView catText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        nicknameET = (EditText) findViewById(R.id.nickname_ET);
        registerBtn = (ImageButton) findViewById(R.id.register_btn);
        catFace = (ImageView) findViewById(R.id.cat_face);
        catText = (TextView) findViewById(R.id.cat_text);

        registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(nicknameET.length() == 0){
                    catText.setText("이름이 빈칸일수는 없지 않을까?");
                }
                else{

                    final String new_nickname = nicknameET.getText().toString();

                    mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isExist = false;

                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Nickname get = postSnapshot.getValue(Nickname.class);

                                if(new_nickname.equals(get.nickname)){
                                    isExist = true;
                                }
                            }

                            if(isExist){
                                catText.setText("그 이름은 이미 존재해. 다른 이름은 어때?");
                            }
                            else{
                                addNewUser(new_nickname);
                                catText.setText(new_nickname + "!! \n멋진 이름이야. \n앞으로 잘 부탁해, " + new_nickname + ".");
                                catFace.setImageResource(R.drawable.beth_0001);

                                SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = registerInfo.edit();
                                editor.putString("registerUserName", new_nickname);
                                editor.commit();

                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
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

    public void addNewUser(String new_nickname){

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        Nickname data = new Nickname(new_nickname);
        postValues = data.toMap();
        childUpdates.put("/user_list/" + new_nickname + "/", postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
