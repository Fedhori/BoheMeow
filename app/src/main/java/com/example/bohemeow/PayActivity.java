package com.example.bohemeow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


class Log{
    int point;
    String time;
    String userName;

    public Log(int point, String time, String userName) {
        this.point = point;
        this.time = time;
        this.userName = userName;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("point", point);
        result.put("time", time);
        result.put("userName", userName);
        return result;
    }
}

public class PayActivity extends Activity {

    private DatabaseReference mPostReference;
    String username;
    String storeName;
    int point_remain;
    int point_use;
    int code;

    String time;

    TextView point_remainTV;
    CheckBox store1;
    CheckBox store2;
    CheckBox store3;
    EditText point_useET;
    EditText codeET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay);

        Intent intent = getIntent();

        point_remainTV = findViewById(R.id.point_remain);
        store1 = findViewById(R.id.store1);
        store2 = findViewById(R.id.store2);
        store3 = findViewById(R.id.store3);
        point_useET = findViewById(R.id.point_use);
        codeET = findViewById(R.id.code);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        // get user preference values
        username = registerInfo.getString("registerUserName", "NULL");
        getUserData(username);

        store1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    store1.setChecked(true);
                    store2.setChecked(false);
                    store3.setChecked(false);
                }
            }
        }) ;

        store2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    store1.setChecked(false);
                    store2.setChecked(true);
                    store3.setChecked(false);
                }
            }
        }) ;

        store3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    store1.setChecked(false);
                    store2.setChecked(false);
                    store3.setChecked(true);
                }
            }
        }) ;


        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(PayActivity.this, MainMenu.class);
                //startActivity(intent);
                finish();
            }
        });

        Button pay_btn =findViewById(R.id.pay_btn);
        pay_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if((store1.isChecked()==false)&&(store2.isChecked()==false)&&(store3.isChecked()==false)){
                    Toast.makeText(PayActivity.this, "가게를 선택해주세요.", Toast.LENGTH_LONG).show();
                }
                else if(point_useET.length() * codeET.length() == 0){
                    Toast.makeText(PayActivity.this, "빈 칸을 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else if(point_remain < Integer.parseInt(point_useET.getText().toString())){
                    Toast.makeText(PayActivity.this, "잔액이 부족합니다.", Toast.LENGTH_LONG).show();
                }
                else {

                    point_use = Integer.parseInt(point_useET.getText().toString());
                    code = Integer.parseInt(codeET.getText().toString());

                    if(store1.isChecked()){
                        storeName = "NU";
                    }
                    else if(store2.isChecked()){
                        storeName = "Pandorothy";
                    }
                    else if(store3.isChecked()){
                        storeName = "NU";
                    }


                    mPostReference.child("store_list").child(storeName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(code == dataSnapshot.child("code").getValue(Integer.class)){
                                payment(storeName, point_use, dataSnapshot.child("totalPoint").getValue(Integer.class));

                                Intent intent = new Intent(PayActivity.this, PayReceiptActivity.class);
                                intent.putExtra("storeName", storeName);
                                intent.putExtra("time", time);
                                intent.putExtra("point_use", Integer.toString(point_use));
                                intent.putExtra("point_remain", Integer.toString(point_remain));
                                startActivityForResult(intent, 1);

                            }
                            else{
                                Toast.makeText(PayActivity.this, "잘못된 코드입니다.", Toast.LENGTH_LONG).show();
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

    public void payment(String storename, int point, int total_point){
        point_remain -= point;
        total_point += point;

        mPostReference = FirebaseDatabase.getInstance().getReference("user_list/" + username);
        mPostReference.child("point").setValue(point_remain);

        mPostReference = FirebaseDatabase.getInstance().getReference("store_list/" + storename);
        mPostReference.child("totalPoint").setValue(total_point);

        SimpleDateFormat t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = t.format(Calendar.getInstance().getTime());

        Log log = new Log(point, time, username);

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> logValues = log.toMap();
        childUpdates.put("/log/" + time, logValues);
        mPostReference.updateChildren(childUpdates);

        Toast.makeText(PayActivity.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
        point_remainTV.setText(Integer.toString(point_remain));
        point_useET.setText("");
        codeET.setText("");


    }


    public void getUserData(final String user_nickname){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference.child("user_list").child(user_nickname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                point_remain = dataSnapshot.child("point").getValue(Integer.class);
                point_remainTV.setText(Integer.toString(point_remain));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
