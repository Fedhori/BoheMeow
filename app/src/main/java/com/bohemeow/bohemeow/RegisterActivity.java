package com.bohemeow.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    EditText passwordET2;
    EditText weightET;
    EditText nicknameET;
    ImageButton registerBtn;

    String phoneNumber;

    int reservation_reward = 1000;

    boolean isAgree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        phoneNumber = registerInfo.getString("phoneNumber", "NULL");

        mPostReference = FirebaseDatabase.getInstance().getReference();

        idET = (EditText) findViewById(R.id.idET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        passwordET2 = (EditText) findViewById(R.id.passwordET2);
        weightET = (EditText) findViewById(R.id.weightET);
        nicknameET = (EditText) findViewById(R.id.nicknameET);

        final Button btn_showAgree = findViewById(R.id.btn_showAgree);
        btn_showAgree.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Dialog settingsDialog = new Dialog(RegisterActivity.this);
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.agreement
                        , null));
                settingsDialog.show();
            }
        });

        final ImageButton btn_isAgree = findViewById(R.id.btn_isAgree);
        btn_isAgree.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                isAgree = !isAgree;

                if(isAgree){
                    btn_isAgree.setBackgroundResource(R.drawable.npay_checkbox_checked);
                }
                else{
                    btn_isAgree.setBackgroundResource(R.drawable.npay_checkbox_none);
                }
            }
        });

        registerBtn = (ImageButton) findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(nicknameET.length() * passwordET.length() * passwordET2.length() * weightET.length() * idET.length() == 0){
                    Toast.makeText(RegisterActivity.this, "비어있는 칸이 있습니다.", Toast.LENGTH_LONG).show();
                }
                else if(!passwordET.getText().toString().equals(passwordET2.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
                }
                else if(idET.length() < 4){
                    Toast.makeText(RegisterActivity.this, "아이디가 너무 짧습니다.", Toast.LENGTH_LONG).show();
                }
                else if(passwordET.length() < 4){
                    Toast.makeText(RegisterActivity.this, "비밀번호가 너무 짧습니다.", Toast.LENGTH_LONG).show();
                }
                else if(!isAgree){
                    Toast.makeText(RegisterActivity.this, "개인정보 수집 및 이용 약관에 동의해주셔야 합니다.", Toast.LENGTH_LONG).show();
                }
                else{

                    final String new_nickname = nicknameET.getText().toString();
                    final String new_id = idET.getText().toString();
                    final String new_password = passwordET.getText().toString();
                    final String weight = weightET.getText().toString();

                    mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isNicknameExist = false;
                            boolean isIDExist = false;

                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                UserData get = postSnapshot.getValue(UserData.class);

                                if(new_nickname.equals(get.nickname)){
                                    isNicknameExist = true;
                                }
                                else if(new_id.equals(get.id)){
                                    isIDExist = true;
                                }
                            }

                            if(isNicknameExist){
                                Toast.makeText(RegisterActivity.this, "그 닉네임은 이미 존재합니다.", Toast.LENGTH_LONG).show();
                            }
                            else if(isIDExist){
                                Toast.makeText(RegisterActivity.this, "그 아이디는 이미 존재합니다.", Toast.LENGTH_LONG).show();
                            }
                            else{
                                addNewUser(new_nickname, new_id, new_password,  Integer.parseInt(weight), phoneNumber);

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

    public void addNewUser(final String new_nickname, final String id, final String password, final int weight, final String phoneNumber){

        DatabaseReference ref;

        ref = FirebaseDatabase.getInstance().getReference("reservation_list");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isReserved = false;

                for (DataSnapshot get : dataSnapshot.getChildren()) {
                    if(get.getValue().toString().equals(phoneNumber)) {
                        DatabaseReference reservation_user = FirebaseDatabase.getInstance().getReference("reservation_list").child(phoneNumber);
                        reservation_user.removeValue();
                        isReserved = true;
                        break;
                    }
                }

                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Object> postValues = null;
                // default value of userdata is -1,-1,-1 which means, user hadn't complete the survey yet!
                UserData data = new UserData(new_nickname, id, password, weight, phoneNumber);
                if(isReserved){
                    data.level = reservation_reward;
                    data.point = reservation_reward;
                }
                postValues = data.toMap();
                childUpdates.put("/user_list/" + new_nickname + "/", postValues);
                mPostReference.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
