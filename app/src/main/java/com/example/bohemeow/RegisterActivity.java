package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
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
    EditText passwordET2;
    EditText weightET;
    EditText nicknameET;
    ImageButton registerBtn;

    ImageView catFace;
    TextView catText;

    String phoneNumber;

    private static final int MY_PERMISSION_REQUEST_CODE_PHONE_STATE = 1;
    private void askPermissionAndGetPhoneNumbers() {

        // With Android Level >= 23, you have to ask the user
        // for permission to get Phone Number.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23

            // Check if we have READ_PHONE_STATE permission
            int readPhoneStatePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);

            if ( readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSION_REQUEST_CODE_PHONE_STATE
                );
                return;
            }
        }

        this.getPhoneNumbers();
    }

    // Need to ask user for permission: android.permission.READ_PHONE_STATE
    @SuppressLint("MissingPermission")
    private void getPhoneNumbers() {
        try {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = manager.getLine1Number();

            SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = registerInfo.edit();
            editor.putString("phoneNumber", phoneNumber);
            editor.commit();

        } catch (Exception ex) {
            /*
            Log.e( LOG_TAG,"Error: ", ex);
            Toast.makeText(this,"Error: " + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
             */
        }
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE_PHONE_STATE: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (SEND_SMS).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.getPhoneNumbers();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(RegisterActivity.this, "허가 없이는 계정 생성이 불가합니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // get phone number
        askPermissionAndGetPhoneNumbers();

        mPostReference = FirebaseDatabase.getInstance().getReference();

        idET = (EditText) findViewById(R.id.idET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        passwordET2 = (EditText) findViewById(R.id.passwordET2);
        weightET = (EditText) findViewById(R.id.weightET);
        nicknameET = (EditText) findViewById(R.id.nicknameET);

        registerBtn = (ImageButton) findViewById(R.id.registerBtn);
        catFace = (ImageView) findViewById(R.id.cat_face);
        catText = (TextView) findViewById(R.id.cat_text);

        registerBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(nicknameET.length() * passwordET.length() * passwordET2.length() * weightET.length() * idET.length() == 0){
                    catText.setText("비어있는 칸이 있어!");
                }
                else if(!passwordET.getText().toString().equals(passwordET2.getText().toString())){
                    catText.setText("비밀번호가 다른걸? 다시 한번 확인해봐!");
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
                                catText.setText("그 이름은 이미 존재해. \n다른 이름은 어때?");
                            }
                            else if(isIDExist){
                                catText.setText("그 아이디는 이미 존재해. \n다른 아이디는 어때?");
                            }
                            else{
                                addNewUser(new_nickname, new_id, new_password,  Integer.parseInt(weight), phoneNumber);
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

    public void addNewUser(String new_nickname, String id, String password, int weight, String phoneNumber){

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        // default value of userdata is -1,-1,-1 which means, user hadn't complete the survey yet!
        UserData data = new UserData(new_nickname, id, password, weight, phoneNumber);
        postValues = data.toMap();
        childUpdates.put("/user_list/" + new_nickname + "/", postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
