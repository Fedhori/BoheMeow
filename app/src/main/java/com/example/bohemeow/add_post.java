package com.example.bohemeow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class add_post extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout2;

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
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imageButton = findViewById(R.id.imageBtn);
        contentET = findViewById(R.id.content);
        tagET = findViewById(R.id.tag);

        mStorageRef = FirebaseStorage.getInstance().getReference("User_icons");
        mPostReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //name = intent.getStringExtra("name");

        Button create_post_btn = (Button) findViewById(R.id.create_post_btn);

        // add toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, SELECT_IMAGE);
            }
        });



        create_post_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(contentET.length() == 0){
                    Toast.makeText(add_post.this, "Please input contents", Toast.LENGTH_SHORT).show();
                }
                else{
                    postFirebaseDatabase(true);

                    Intent intent = new Intent(add_post.this, CommunityActivity.class);
                    intent.putExtra("username", username);
                    //intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            currentImageUri = data.getData();
            boolean imageUriChecker = true;
            user_icon.setImageURI(currentImageUri);
            StorageReference riversRef = mStorageRef.child(username+".jpg");
            UploadTask uploadTask = riversRef.putFile(currentImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                }
            });
        }
        else if(requestCode == SELECT_IMAGE){
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
        String postType = "";

        if(currentPostImage!=null){
            uri = currentPostImage.toString();
        }

        content = contentET.getText().toString();

        if(tagET.getText() != null){
            tags = tagET.getText().toString();
        }

        postType = "Public";


        childUpdates = new HashMap<>();
        postValues = null;
        if(add){
            postData data = new postData(username, uri, content, tags, postType);
            postValues = data.toMap();
        }
        childUpdates.put("/post_list/" + content, postValues);


        // image exist
        if(uri != ""){

            Log.w(this.getClass().getName(), "WHAT?");

            StorageReference mPostRef = FirebaseStorage.getInstance().getReference("Post_images");;

            StorageReference riversRef = mPostRef.child(content+".jpg");
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
