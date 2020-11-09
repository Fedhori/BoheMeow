package com.bohemeow.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class CommunityActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    Uri currentImageUri;


    private DatabaseReference mPostReference;
    String username;
    int level;
    int catType;

    TabLayout tabLayout;
    ViewPager2 viewPager;

    ImageView user_icon;

    private ArrayList<post> mArrayList;
    private CustomAdapter mAdapter;

    boolean isWritable = true;
    //private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        // get user preference values
        username = registerInfo.getString("registerUserName", "NULL");
        getUserData(username);


        // get user icon
        //mStorageRef = FirebaseStorage.getInstance().getReference("User_icons");


        // view pager
        // view pager
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position == 0){
                            tab.setText("전체");


                        }
                        else if(position == 1){
                            tab.setText("내 게시글");
                        }
                        else if(position == 2){
                            tab.setText("검색");
                        }
                    }
                }).attach();



        mArrayList = new ArrayList<>();
        final ArrayList<post> publicArrayList = new ArrayList<>();
        // get data

        final ValueEventListener postListener = new ValueEventListener(){

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mArrayList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    postData get = postSnapshot.getValue(postData.class);
                    post data = new post(get.username, get.content, get.tags, get.time, get.uri, get.level, get.catType, get.isPublic);
                    mArrayList.add(data);
                    Log.d("asdf", data.isPublic + "a");
                    if(data.isPublic()){
                        Log.d("asdf", data.isPublic + "b");
                        publicArrayList.add(data);
                    }
                }

                if(publicArrayList.size() >= 3){
                    String[] lastWriters = {publicArrayList.get(0).getUsername(), publicArrayList.get(1).getUsername(), publicArrayList.get(2).getUsername()};
                    String lastDate = publicArrayList.get(2).getTime();

                    isWritable = checkWritable(lastWriters, lastDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        String time = df.format(date);

        mPostReference = FirebaseDatabase.getInstance().getReference();
        //mPostReference.child("post_list").limitToLast(3).addValueEventListener(postListener);
        mPostReference.child("post_list").orderByKey().startAt(time).addValueEventListener(postListener);





        // button
        Button add_post_btn = (Button) findViewById(R.id.add_post_btn);
        // add post button
        add_post_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(isWritable) {
                    Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("level", level);
                    intent.putExtra("catType", catType);
                    //intent.putExtra("name", name);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(CommunityActivity.this, "연속해서 네개 이상의 게시물을 작성할수 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });



    }




    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        private static final int CARD_ITEM_SIZE = 3;
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        @NonNull @Override
        public Fragment createFragment(int position) {
            if(position <= 1){
                return fragmentGeneral.newInstance(position);
            }
            else return fragmentSearch.newInstance(position);
        }
        @Override
        public int getItemCount() {
            return CARD_ITEM_SIZE;
        }
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CommunityActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void getUserData(final String user_nickname){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserData get = postSnapshot.getValue(UserData.class);

                    if(user_nickname.equals(get.nickname)){
                        level = get.level;
                        catType = get.catType;
                        //Toast.makeText(WalkActivity.this, preference[0] + " " + preference[1] + " " + preference[2], Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //도배 방지, 하로에 연속해서 세개의 게시글을 올리면 isWritable = false
    boolean checkWritable(String[] lastWriters, String lastDate){
        int duplicated = 0;

        for(String w:lastWriters){
            if(username.equals(w)){
                duplicated++;
            }
        }

        if (duplicated >= 3){
            TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(tz);
            String time = df.format(date);

            if(time.equals(lastDate.substring(0,10))){
                return false;
            }
        }

        return true;
    }
}
