package com.example.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DelPopupActivity extends Activity {

    int pos;
    post pst;

    Button cancel_btn;
    Button del_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_community_delpopup);

        final Intent intent = getIntent();
        pst = (post)intent.getSerializableExtra("post");
        pos = intent.getIntExtra("num", 0);

        cancel_btn = findViewById(R.id.can_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        del_btn = findViewById(R.id.del_btn);
        del_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("post_list/" + pst.getTime());
                myRef.removeValue();

                Intent intent = new Intent();
                intent.putExtra("result", "Close Popup");
                setResult(RESULT_OK, intent);

                finish();
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }


}