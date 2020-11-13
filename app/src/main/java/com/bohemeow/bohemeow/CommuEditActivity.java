package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CommuEditActivity extends Activity {

    String username;
    int pos;
    String time;

    post pst;
    //ImageView contentIV;

    EditText contentET;
    EditText tagET;

    ImageButton edit_btn;
    ImageView imageButton;

    CheckBox checkBox;


    private static final int SELECT_IMAGE = 776;
    Uri currentPostImage = null;
    InputStream imageStream;
    Bitmap bitmap;

    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_write);

        Intent intent = getIntent();
        pst = (post)intent.getSerializableExtra("post");
        username = intent.getStringExtra("username");
        pos = intent.getIntExtra("num", pos);



        contentET = findViewById(R.id.content);
        tagET = findViewById(R.id.tags);

        contentET.setText(pst.getContent());
        tagET.setText(pst.getTags());

        checkBox = findViewById(R.id.postType);
        if(pst.isPublic){
            checkBox.setChecked(true);
        }
        else
            checkBox.setChecked(false);


        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                }
            }
        }) ;


        String username =pst.getUsername();
        time =pst.getTime();
        String uri =pst.getUri();

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
                    imageButton.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else imageButton.setImageResource(R.drawable.commu_write_photo);



        edit_btn = findViewById(R.id.create_post_btn);
        edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(contentET.length() == 0){
                    Toast.makeText(CommuEditActivity.this, "Please input contents", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("post_list/" + pst.getTime());
                    myRef.child("content").setValue(contentET.getText().toString());
                    myRef.child("tags").setValue(splitTag(tagET.getText().toString()));
                    if(checkBox.isChecked()){
                        myRef.child("isPublic").setValue(true);
                    }
                    else{
                        myRef.child("isPublic").setValue(false);
                    }
                    if(currentPostImage!=null){
                        uploadimage(myRef, time);
                    }

                    finish();
                }
            }
        });


        imageButton = findViewById(R.id.imageBtn);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, SELECT_IMAGE);
            }
        });

    }

    int ori;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_IMAGE){
            if(data != null) {
                currentPostImage = data.getData();
                ori = getOrientation(currentPostImage);
                //boolean imageUriChecker = true;
                //imageButton.setImageURI(currentPostImage);
                try {
                    imageStream = getContentResolver().openInputStream(currentPostImage);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    bitmap = getResizedBitmap(selectedImage, 1000, 1500);

                    imageButton.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
        else if(requestCode==1){
            if(resultCode == 1){
                finish();
            }
        }
    }
    public Bitmap getResizedBitmap(Bitmap image, int WmaxSize, int HmaxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio >= 1) {
            width = WmaxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = HmaxSize;
            width = (int) (height * bitmapRatio);
        }


        image = rotateBitmap(image, ori);

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    public int getOrientation(Uri selectedImage) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = this.getContentResolver().query(selectedImage, projection, null, null, null);
        if(cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
            if(cursor.moveToFirst()) {
                orientation = cursor.isNull(orientationColumnIndex) ? 0 : cursor.getInt(orientationColumnIndex);
            }
            cursor.close();
        }
        return orientation;
    }

    public void uploadimage(final DatabaseReference myRef, String time){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        currentPostImage = Uri.parse(path);

        final String uri = currentPostImage.toString();
        StorageReference mPostRef = FirebaseStorage.getInstance().getReference("Post_images");

        StorageReference riversRef = mPostRef.child(time+".jpg");
        UploadTask uploadTask = riversRef.putFile(currentPostImage);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                myRef.child("uri").setValue(uri);
            }
        });

    }

    String splitTag(String tags){
        String tag = "";

        tags= tags.replace("#", " ");
        tags = tags.replaceAll("\\s+", " ");
        String[] ta = tags.split(" ");
        for(String t : ta){
            System.out.println(t + "+");
            tag = tag + "#" + t + " ";
        }
        tag = tag.replace("# ", "");

        return tag;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CommuEditActivity.this, WriteCheckActivity.class);
        startActivityForResult(intent, 1);
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