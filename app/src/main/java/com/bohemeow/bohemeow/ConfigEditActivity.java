package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ConfigEditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_modify);

        Intent intent = getIntent();
        final UserData userData = (UserData) intent.getSerializableExtra("userdata");

        Button editPW_btn = findViewById(R.id.editPW_btn);
        editPW_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigEditActivity.this, ConfigEditPasswordActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        Button editWeight_btn = findViewById(R.id.editWeight_btn);
        editWeight_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigEditActivity.this, ConfigEditWeightActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        Button editIntro_btn = findViewById(R.id.editIntro_btn);
        editIntro_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigEditActivity.this, ConfigEditIntroductionActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        Button back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){

            Intent intent = new Intent();

            switch(resultCode){
                case RESULT_OK:
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                default:
                    // nothing happened
                    break;
            }
        }
    }

}