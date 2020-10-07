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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

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

    void updateScore(long totalPoint){
        SharedPreferences countInfo = getSharedPreferences("countInfo", Context.MODE_PRIVATE);
        int lastDate = countInfo.getInt("lastDate", -1); // 가장 마지막으로 쪽지를 남긴 날짜
        int todayCount = countInfo.getInt("todayCount", 0); // 오늘 작성한 쪽지의 갯수

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        int todayDate = Integer.parseInt(dayFormat.format(currentTime)); // 실제 오늘 날짜

        // 새로운 날에 쪽지 작성시, todayCount 초기화
        if(todayDate != lastDate){
            lastDate = todayDate;
            todayCount = 0;
        }

        todayCount++;

        // 하루에는 최대 3번만 쪽지로 점수를 벌 수 있다.
        if(todayCount <= 3){
            totalPoint += 10;
        }

        // 로컬 데이터에 다시 업데이트
        SharedPreferences.Editor editor = countInfo.edit();
        editor.putInt("lastDate", lastDate);
        editor.putInt("todayCount", todayCount);
        editor.commit();
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

                            int prev_level = calculateLevel((int)user_totalPoint);
                            int cur_level = calculateLevel((int) (user_totalPoint + totalPoint));

                            // level up!
                            if(prev_level < cur_level){
                                // this value must be synchronized with WalkEndActivity's rewardLevelList array
                                int[] rewardLevelList = {2, 5, 10, 20, 30, 40};
                                int length = rewardLevelList.length;
                                for(int i = 0;i<length;i++){
                                    if(cur_level == rewardLevelList[i]){
                                        // now do something!
                                        break;
                                    }
                                }
                            }
                        }
                        ref.child("lastPost").child("num").setValue(num + 1);
                    } else {
                        user_totalPoint = (long) dataSnapshot.child("level").getValue();
                        ref.child("level").setValue(user_totalPoint + totalPoint);
                        ref.child("lastPost").child("date").setValue(date);
                        ref.child("lastPost").child("num").setValue(0);

                        int prev_level = calculateLevel((int)user_totalPoint);
                        int cur_level = calculateLevel((int) (user_totalPoint + totalPoint));

                        // level up!
                        if(prev_level < cur_level){
                            // this value must be synchronized with WalkEndActivity's rewardLevelList array
                            int[] rewardLevelList = {2, 5, 10, 20, 30, 40};
                            int length = rewardLevelList.length;
                            for(int i = 0;i<length;i++){
                                if(cur_level == rewardLevelList[i]){
                                    // now do something!
                                    break;
                                }
                            }
                        }
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

}
