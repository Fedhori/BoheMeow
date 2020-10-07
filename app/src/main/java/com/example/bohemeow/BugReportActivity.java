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

import java.util.HashMap;
import java.util.Map;

public class BugReportActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bug_report);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText bugreport_et = findViewById(R.id.bugreport_et);

        Button report_btn = findViewById(R.id.report_btn);
        report_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                int length = bugreport_et.getText().toString().length();

                if(length != 0){
                    // 버그 리포트를 데이터베이스에 저장
                    DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = null;
                    bugReportData data = new bugReportData(bugreport_et.getText().toString(), userData.nickname);
                    postValues = data.toMap();
                    if(length < 5){
                        childUpdates.put("/bug_report/" + bugreport_et.getText().toString().substring(0, length) + "/", postValues);
                    }
                    else{
                        childUpdates.put("/bug_report/" + bugreport_et.getText().toString().substring(0, 5) + "/", postValues);
                    }
                    mPostReference.updateChildren(childUpdates);
                    Toast.makeText(BugReportActivity.this, "버그 제보가 완료되었습니다. 감사합니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else{
                    Toast.makeText(BugReportActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}