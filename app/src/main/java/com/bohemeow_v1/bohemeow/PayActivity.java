package com.bohemeow_v1.bohemeow;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


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

class Codelist implements Serializable {
    int num;
    String code;

    public Codelist(){

    }

    public Codelist(int num, String code){
        this.num = num;
        this.code = code;
    }
}

public class PayActivity extends Activity {

    String username;
    int point_remain;
    int point_use;
    String code;

    int num = 0;
    int storeNum;
    String time;

    TextView point_remainTV;
    TextView storename1;
    TextView storename2;
    TextView storename3;
    CheckBox store1;
    CheckBox store2;
    CheckBox store3;
    EditText point_useET;
    EditText codeET;
    ImageView imageView;

    boolean isYul = true;
    String[] MYnames = {"[중앙학술정보관]\n카페테리아", "[경영관]\n사랑방", ""};
    String[] YJnames = {"[산학협력센터]\n팬도로시", "[공학관]\nCafe:NU", "[의관]\n카페나무"};

    String[] stores = {"카페테리아", "사랑방", "팬도로시", "NU", "카페나무"};
    String[] codes = new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay);

        Intent intent = getIntent();

        point_remainTV = findViewById(R.id.point_remain);
        storename1 = findViewById(R.id.storename1);
        storename2 = findViewById(R.id.storename2);
        storename3 = findViewById(R.id.storename3);
        store1 = findViewById(R.id.store1);
        store2 = findViewById(R.id.store2);
        store3 = findViewById(R.id.store3);
        point_useET = findViewById(R.id.point_use);
        codeET = findViewById(R.id.code);
        imageView = findViewById(R.id.imageView10);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        // get user preference values
        username = registerInfo.getString("registerUserName", "NULL");
        isYul = registerInfo.getBoolean("isYul", true);
        getUserData(username);

        if(!isYul){
            imageView.setVisibility(View.INVISIBLE);
            storename1.setText(MYnames[0]);
            storename2.setText(MYnames[1]);
            storename3.setText(MYnames[2]);
            store1.setChecked(true);
            store3.setChecked(false);
            store3.setVisibility(View.GONE);
            num = 0;
        }

        store1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    store1.setChecked(true);
                    store2.setChecked(false);
                    store3.setChecked(false);
                    num = 0;
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
                    num = 1;
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
                    num = 2;
                }
            }
        }) ;



        Button btn = findViewById(R.id.button6);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(isYul) {
                    imageView.setVisibility(View.INVISIBLE);
                    storename1.setText(MYnames[0]);
                    storename2.setText(MYnames[1]);
                    storename3.setText(MYnames[2]);

                    store1.setChecked(true);
                    store2.setChecked(false);
                    store3.setChecked(false);
                    store3.setVisibility(View.GONE);
                    num = 0;

                    isYul = false;
                }
            }
        });

        Button btn2 = findViewById(R.id.button7);
        btn2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(!isYul) {
                    imageView.setVisibility(View.VISIBLE);
                    storename1.setText(YJnames[0]);
                    storename2.setText(YJnames[1]);
                    storename3.setText(YJnames[2]);

                    store1.setChecked(true);
                    store2.setChecked(false);
                    store3.setChecked(false);
                    store3.setVisibility(View.VISIBLE);
                    num = 0;

                    isYul = true;
                }
            }
        });

        ImageButton pay_btn =findViewById(R.id.pay_btn);
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
                    code = codeET.getText().toString();

                    if(isYul)
                        storeNum = num + 2;
                    else
                        storeNum = num;

                    DatabaseReference mPostReference =  FirebaseDatabase.getInstance().getReference();
                    mPostReference.child("store_list").child(stores[storeNum]).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String storeCode = dataSnapshot.child("code").getValue(String.class);

                            if(code.equals(storeCode)){
                                SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = registerInfo.edit();
                                editor.putBoolean("isYul", isYul);
                                editor.commit();

                                payment(storeNum, point_use, dataSnapshot.child("totalPoint").getValue(Integer.class));

                                Intent intent = new Intent(PayActivity.this, PayReceiptActivity.class);
                                intent.putExtra("storeNum", storeNum);
                                intent.putExtra("time", time);
                                intent.putExtra("point_use", Integer.toString(point_use));
                                intent.putExtra("point_remain", Integer.toString(point_remain));
                                startActivityForResult(intent, 1);

                            }
                            else{
                                System.out.println("inputcode : " + code);
                                System.out.println("storeCode : " + storeCode);
                                System.out.println("store info : " + num + stores[storeNum]);
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

    public void payment(int num, int point, int total_point){
        point_remain -= point;
        total_point += point;

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("user_list/" + username);
        mPostReference.child("point").setValue(point_remain);

        mPostReference = FirebaseDatabase.getInstance().getReference("store_list/" + stores[num]);
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
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
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
