package com.bohemeow_v1.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfigEditWeightActivity extends Activity {

    EditText newWeight;
    int weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_edit_weight);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");

        getUserData(userData.nickname);

        newWeight = findViewById(R.id.newWeight);
        //newWeight.setText(""+weight);



        Button change_btn = findViewById(R.id.yes_btn);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                weight = Integer.parseInt(newWeight.getText().toString());
                if(weight > 25 && weight < 200){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_list").child(userData.nickname);
                    ref.child("weight").setValue(Integer.valueOf(weight));

                    Toast.makeText(ConfigEditWeightActivity.this, "체중 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else{
                    Toast.makeText(ConfigEditWeightActivity.this, "실제 체중을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private DatabaseReference mPostReference;
    public void getUserData(final String user_nickname){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserData get = postSnapshot.getValue(UserData.class);

                    if(user_nickname.equals(get.nickname)){
                        weight = get.weight;
                        newWeight.setText(""+weight);
                        //Toast.makeText(WalkActivity.this, preference[0] + " " + preference[1] + " " + preference[2], Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}