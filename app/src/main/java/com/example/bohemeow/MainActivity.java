package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.loadingLayout);

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for test
                postFirebaseDatabase(true);

                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        //hello world

        final MakeRoute mr = new MakeRoute(this);

        Button button= findViewById(R.id.testBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, SpotSearcher_nearby.class);
                //startActivity(intent);
                mr.SpotSelector();
            }
        });
    }

    public void postFirebaseDatabase(boolean add){

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            Data data = new Data("IT WORKS!!!!!");
            postValues = data.toMap();
        }
        childUpdates.put("/user_list/" + "Test", postValues);
        mPostReference.updateChildren(childUpdates);

        Log.e("MyActivity", "OK!");
    }
}
