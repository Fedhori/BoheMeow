package com.bohemeow_v1.bohemeow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CommuWriteActivity extends Activity {

    Map<String, Object> childUpdates;
    Map<String, Object> postValues;

    private static final int SELECT_IMAGE = 776;
    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    private DatabaseReference mPostReference;
    Uri currentImageUri;
    Uri currentPostImage = null;
    InputStream imageStream;
    Bitmap bitmap;

    ImageView user_icon;

    ImageView imageButton;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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





        ImageButton create_post_btn = findViewById(R.id.create_post_btn);
        create_post_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(contentET.length() == 0){
                    Toast.makeText(CommuWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    postFirebaseDatabase(true);
                    updatePoint(username, 100);

                    Intent intent = new Intent(CommuWriteActivity.this, CommunityActivity.class);
                    startActivity(intent);
                }
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
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
            currentPostImage = Uri.parse(path);
            uri = currentPostImage.toString();

        }

        content = contentET.getText().toString();

        if(tagET.getText() != null){
            tags = splitTag(tagET.getText().toString());
        }

        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(d);
        date = time.substring(0,10);




        //byte[] reviewImage = stream.toByteArray();
        //String simage = byteArrayToBinaryString(reviewImage);

        //System.out.println(simage);


        childUpdates = new HashMap<>();
        postValues = null;
        if(add){
            postData data = new postData(username, uri, content, tags, time, level, catType, isPublic);

            //postData data = new postData(username, simage, content, tags, time, level, catType, isPublic);
            postValues = data.toMap();

        }
        childUpdates.put("/post_list/" + time, postValues);

        //mPostReference.updateChildren(childUpdates);

        // image exist

        if(uri != ""){

            Log.w(this.getClass().getName(), "WHAT?");

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
                            Toast.makeText(CommuWriteActivity.this, "포스트 작성 완료 +100경험치", Toast.LENGTH_LONG).show();

                            TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                            Date date = new Date();
                            DateFormat df = new SimpleDateFormat("MMdd");
                            df.setTimeZone(tz);
                            int time = Integer.parseInt(df.format(date));
                            if(time < 1123){
                                Log.d("time", time + "");
                                long point = (long) dataSnapshot.child("point").getValue();
                                ref.child("point").setValue(point + totalPoint);
                            }
                        }
                        ref.child("lastPost").child("num").setValue(num + 1);
                    }
                    else {
                        user_totalPoint = (long) dataSnapshot.child("level").getValue();

                        ref.child("level").setValue(user_totalPoint + totalPoint);
                        ref.child("lastPost").child("date").setValue(date);
                        ref.child("lastPost").child("num").setValue(0);
                        Toast.makeText(CommuWriteActivity.this, "포스트 작성 완료 +100경험치", Toast.LENGTH_LONG).show();

                        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                        Date date = new Date();
                        DateFormat df = new SimpleDateFormat("MMdd");
                        df.setTimeZone(tz);
                        int time = Integer.parseInt(df.format(date));
                        if(time < 1123){
                            Log.d("time", time + "");
                            long point = (long) dataSnapshot.child("point").getValue();
                            ref.child("point").setValue(point + totalPoint);
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
        Intent intent = new Intent(CommuWriteActivity.this, WriteCheckActivity.class);
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
