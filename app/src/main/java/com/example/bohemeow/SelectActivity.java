package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectActivity extends AppCompatActivity {

    // test

    private DatabaseReference mPostReference;

    Button btn_to_walk;

    int[] preference = new int[3];//0:safe 1:envi 2:popularity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Intent intent = getIntent();

        mPostReference = FirebaseDatabase.getInstance().getReference();

        // someday.. 언제나 bonjour! 유저의 데이터만 받아올수는 없잖아?
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "Bonjour!");
        editor.commit();

        // get user preference values
        String user_nickname = registerInfo.getString("registerUserName", "NULL");
        getUserPreferences(user_nickname);

        btn_to_walk = (Button) findViewById(R.id.btn_to_walk);

        btn_to_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectActivity.this, WalkActivity.class);
                intent.putExtra("preference", preference);
                startActivity(intent);
            }
        });
    }

    public void getUserPreferences(final String user_nickname){

        mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserData get = postSnapshot.getValue(UserData.class);

                    if(user_nickname.equals(get.nickname)){
                        preference[0] = (int)get.safeScore;
                        preference[1] = (int)get.enviScore;
                        preference[2] = (int)get.popularity;

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
