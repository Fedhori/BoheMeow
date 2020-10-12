package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class WritePostActivity extends AppCompatActivity{

    Map<String, Object> childUpdates;
    Map<String, Object> postValues;

    private static final int SELECT_IMAGE = 776;
    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    private DatabaseReference mPostReference;
    Uri currentImageUri;
    Uri currentPostImage = null;

    ImageView user_icon;

    ImageButton imageButton;
    EditText contentET;
    EditText tagET;
    CheckBox checkBox;

    String username;
    int level;
    int catType;
    String date;

    long user_totalPoint = 0;
    DatabaseReference ref;

    boolean isUpdated = false;

    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        phoneNumber = registerInfo.getString("phoneNumber", "NULL");

        imageButton = findViewById(R.id.imageBtn);
        contentET = findViewById(R.id.content);
        tagET = findViewById(R.id.tags);
        checkBox = findViewById(R.id.postType);

        mStorageRef = FirebaseStorage.getInstance().getReference("User_icons");
        mPostReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        level = intent.getIntExtra("level", 1);
        catType = intent.getIntExtra("catType", 0);



        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, SELECT_IMAGE);
            }
        });



        ImageButton cancel_btn = findViewById(R.id.can_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WritePostActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });


        ImageButton create_post_btn = findViewById(R.id.create_post_btn);
        create_post_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(contentET.length() == 0){
                    Toast.makeText(WritePostActivity.this, "Please input contents", Toast.LENGTH_SHORT).show();
                }
                else{
                    postFirebaseDatabase(true);
                    updatePoint(username, 50);

                    Intent intent = new Intent(WritePostActivity.this, CommunityActivity.class);
                    startActivity(intent);
                }
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE){
            if(data.getData() != null) {
                currentPostImage = data.getData();
                boolean imageUriChecker = true;
                imageButton.setImageURI(currentPostImage);
            }
        }
    }

    public void postFirebaseDatabase(boolean add){

        String uri = "";
        String content = "";
        String tags = "";
        boolean isPublic = true;

        if(checkBox.isChecked()){
            isPublic = true;
        }
        else{
            isPublic = false;
        }

        if(currentPostImage!=null){
            uri = currentPostImage.toString();
        }

        content = contentET.getText().toString();

        if(tagET.getText() != null){
            tags = tagET.getText().toString();
        }

        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(d);
        date = time.substring(0,10);


        childUpdates = new HashMap<>();
        postValues = null;
        if(add){
            postData data = new postData(username, uri, content, tags, time, level, catType, isPublic);
            postValues = data.toMap();
        }
        childUpdates.put("/post_list/" + time, postValues);


        // image exist
        if(uri != ""){

            Log.w(this.getClass().getName(), "WHAT?");

            StorageReference mPostRef = FirebaseStorage.getInstance().getReference("Post_images");;

            StorageReference riversRef = mPostRef.child(time+".jpg");
            UploadTask uploadTask = riversRef.putFile(currentPostImage);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mPostReference.updateChildren(childUpdates);
                }
            });
        }
        else{
            mPostReference.updateChildren(childUpdates);
        }

    }


    void updatePoint(String username, final long totalPoint){

        ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isUpdated) {
                    String lastDate = dataSnapshot.child("lastPost").child("date").getValue().toString().substring(0, 10);
                    if (date.equals(lastDate)) {
                        long num = (long) dataSnapshot.child("lastPost").child("num").getValue();
                        if (num < 3) {
                            user_totalPoint = (long) dataSnapshot.child("level").getValue();
                            ref.child("level").setValue(user_totalPoint + totalPoint);

                            checkAndUpdateLevelReward((int)user_totalPoint, (int) (user_totalPoint + totalPoint));
                            updateDailyPoint(totalPoint);
                        }
                        ref.child("lastPost").child("num").setValue(num + 1);
                    }
                    else {
                        user_totalPoint = (long) dataSnapshot.child("level").getValue();
                        ref.child("level").setValue(user_totalPoint + totalPoint);
                        ref.child("lastPost").child("date").setValue(date);
                        ref.child("lastPost").child("num").setValue(0);

                        checkAndUpdateLevelReward((int)user_totalPoint, (int) (user_totalPoint + totalPoint));
                        updateDailyPoint(totalPoint);
                    }
                    isUpdated = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int calculateLevel(int score){
        int level;
        if(score >= 10000){
            score -= 10000;
            level = (score / 1500) + 11;
        }
        else{
            level = score/1000 + 1;
        }
        return level;
    }

    void updateDailyPoint(final long totalPoint){
        // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        String time = df.format(date);

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("daily_rank").child(time).child(username);
        mPostReference.addValueEventListener(new ValueEventListener() {

            boolean isWritten = false;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!isWritten) {

                    long user_daily_totalPoint = 0;

                    if(dataSnapshot.exists()){
                        user_daily_totalPoint = (long) dataSnapshot.child("point").getValue();
                    }
                    else{
                        // not generated yet
                        mPostReference.child("username").setValue(username);
                    }

                    mPostReference.child("point").setValue(user_daily_totalPoint + totalPoint);

                    isWritten = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void checkAndUpdateLevelReward(int user_totalPoint, int new_totalPoint){

        int prev_level = calculateLevel(user_totalPoint);
        int cur_level = calculateLevel(new_totalPoint);

        if(prev_level < cur_level){
            // this value must be synchronized with WalkEndActivity's rewardLevelList array
            int[] rewardLevelList = {2, 5, 10, 20, 30, 40};
            int length = rewardLevelList.length;
            for(int i = 0;i<length;i++) {
                if (prev_level < rewardLevelList[i] && cur_level >= rewardLevelList[i]) {

                    // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
                    TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(tz);
                    String time = df.format(date);

                    // 파이어베이스에 유저가 특정 레벨에 도달헀음을 저장한다.
                    DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = null;
                    lotsData data = new lotsData(phoneNumber);
                    postValues = data.toMap();
                    childUpdates.put("/level_reward/" + i + "/" + time + "/", postValues);
                    mPostReference.updateChildren(childUpdates);

                    break;
                }
            }
        }
    }
}
