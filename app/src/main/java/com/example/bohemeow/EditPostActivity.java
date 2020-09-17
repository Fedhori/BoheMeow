package com.example.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EditPostActivity extends Activity {

    String username;
    int pos;

    post pst;
    ImageView iconIV;
    ImageView contentIV;

    TextView usernameTV;
    TextView timeTV;
    TextView levelTV;

    EditText contentET;
    EditText tagET;

    Button back_btn;
    Button edit_btn;
    Button del_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_community_edit);

        Intent intent = getIntent();
        pst = (post)intent.getSerializableExtra("post");
        username = intent.getStringExtra("username");
        pos = intent.getIntExtra("num", pos);



        contentET = findViewById(R.id.content);
        tagET = findViewById(R.id.tags);

        contentET.setText(pst.getContent());
        tagET.setText(pst.getTags());



        back_btn = (Button) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(contentET.length() == 0){
                    Toast.makeText(EditPostActivity.this, "Please input contents", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("post_list/" + pst.getTime());
                    myRef.child("content").setValue(contentET.getText().toString());
                    myRef.child("tags").setValue(tagET.getText().toString());
                    finish();
                }
            }
        });

        /*
        del_btn = (Button) findViewById(R.id.del_btn);
        del_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(pst.getUsername().equals(username)){
                    Intent intent1 = new Intent(EditPostActivity.this, EditPostActivity.class);
                    intent1.putExtra("post", pst);
                    startActivity(intent1);
                }
                else {
                    Toast.makeText(EditPostActivity.this, "삭제 권한이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

         */


        iconIV = (ImageView)findViewById(R.id.user_icon);
        contentIV = (ImageView)findViewById(R.id.content_image);
        usernameTV = (TextView)findViewById(R.id.user_name);
        timeTV = (TextView)findViewById(R.id.time);
        levelTV = (TextView)findViewById(R.id.user_level);

        String username =pst.getUsername();
        String time =pst.getTime();
        String uri =pst.getUri();
        int level =pst.getLevel();
        int catType =pst.getCatType();

        int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
                R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};

        StorageReference mStorageRef;
        StorageReference islandRef;
        final long ONE_MEGABYTE = 2048 * 2048;

        if (!uri.equals("")){
            mStorageRef = FirebaseStorage.getInstance().getReference("Post_images");
            islandRef = mStorageRef.child(time + ".jpg");

            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    contentIV.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        usernameTV.setText(username);
        timeTV.setText(Date(time));
        levelTV.setText("Lv." + Integer.toString(level));
        iconIV.setImageResource(icons[catType]);


    }

    String Date(String time){
        String t = time.substring(0,4) + "년";

        if(time.substring(5,6).equals("0")){
            t = t + time.substring(6,7) + "월";
        }
        else t = t + time.substring(5,7) + "월";

        if(time.substring(8,9).equals("0")){
            t = t + time.substring(9,10) + "일 ";
        }
        else t = t + time.substring(8,10) + "일 ";

        t = t + time.substring(11,16);

        return t;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}