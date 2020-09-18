package com.example.bohemeow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    String username;
    int level;
    int catType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        imageButton = findViewById(R.id.imageBtn);
        contentET = findViewById(R.id.content);
        tagET = findViewById(R.id.tags);

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

        if(currentPostImage!=null){
            uri = currentPostImage.toString();
        }

        content = contentET.getText().toString();

        if(tagET.getText() != null){
            tags = tagET.getText().toString();
        }

        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(date);



        childUpdates = new HashMap<>();
        postValues = null;
        if(add){
            postData data = new postData(username, uri, content, tags, time, level, catType);
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
}
