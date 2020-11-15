package com.bohemeow_v1.bohemeow;

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

    PermissionManager permissionManager = null; // 권한요청 관리자

    //뒤로가기 두번 시 종료되도록 구현 예정
    private long backKeyPressedTime = 0;

    String phoneNumber;
    private static final int MY_PERMISSION_REQUEST_CODE_PHONE_STATE = 1;
    private static final int MY_PERMISSION_REQUEST_CODE_FINE_LOCATION = 2;
    private void askPhoneNumberPermission() {

        // With Android Level >= 23, you have to ask the user
        // for permission to get Phone Number.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23

            // Check if we have READ_PHONE_STATE permission
            int readPhoneStatePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);
            int readLocationStatePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int writeExternalStoragePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readBackgroundLocationStatePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            if ( readPhoneStatePermission != PackageManager.PERMISSION_GRANTED || readLocationStatePermission != PackageManager.PERMISSION_GRANTED
                    || writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED || readBackgroundLocationStatePermission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
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
            if (phoneNumber.startsWith("+82")) {
                phoneNumber = phoneNumber.replace("+82", "0"); // +8210xxxxyyyy 로 시작되는 번호
            }

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
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    this.getPhoneNumbers();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(LoginActivity.this, "허가 없이는 진행이 불가합니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        askPhoneNumberPermission();

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
                                int catType = 1;
                                int exp = 0;
                                String phoneNumber = "";

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
                                        catType = get.catType;
                                        exp = get.level;
                                        phoneNumber = get.phoneNumber;
                                    }
                                }

                                // someday, you need to add sharedpreference stuff
                                if(isLoginSuccess){

                                    // store user's phone number at local data
                                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = registerInfo.edit();
                                    editor.putString("phoneNumber", phoneNumber);
                                    editor.commit();

                                    // go to main menu
                                    if(isSurveyComplete){
                                        // 자동로그인이 가능하게 하기 위해 이제 로컬 데이터에 사용자의 닉네임 저장
                                        editor.putString("registerUserName", username);
                                        editor.putInt("userCatType", catType);
                                        editor.putInt("exp", exp);
                                        editor.commit();

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

        /*
        permissionManager = new PermissionManager(this); // 권한요청 관리자
        permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
            // 허가 시 GPS 화면 출력
            @Override
            public void granted() {

            }

            // 허가하지 않을 경우 토스트 메시지와 함께 메인 메뉴로 돌려보낸다
            @Override
            public void denied() {
                Toast.makeText(LoginActivity.this, "허가 없이는 진행이 불가능합니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
         */
    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();

            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }
}