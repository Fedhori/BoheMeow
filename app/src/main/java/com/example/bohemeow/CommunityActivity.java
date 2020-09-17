package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class CommunityActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    Uri currentImageUri;


    private DatabaseReference mPostReference;
    String username;
    int level;
    int catType;

    ViewPager2 viewPager;

    ImageView user_icon;

    private ArrayList<post> mArrayList;
    private CustomAdapter mAdapter;

    //private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);



        // someday.. 언제나 bonjour! 유저의 데이터만 받아올수는 없잖아?
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "Bonjour!");
        editor.commit();

        // get user preference values
        username = registerInfo.getString("registerUserName", "NULL");
        getUserData(username);


        // get user icon
        mStorageRef = FirebaseStorage.getInstance().getReference("User_icons");


        // view pager
        //viewPager = findViewById(R.id.view_pager);
        //viewPager.setAdapter(createCardAdapter());



        // button
        Button add_post_btn = (Button) findViewById(R.id.add_post_btn);
        // add post button
        add_post_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, WritePostActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("level", level);
                intent.putExtra("catType", catType);
                //intent.putExtra("name", name);
                startActivity(intent);
            }
        });






        final TextView textViewCounter = findViewById(R.id.textViewFrag);

        textViewCounter.setText("No post");
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(CommunityActivity.this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();

        mAdapter = new CustomAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View v, int pos)
            {
                Intent intent = new Intent(CommunityActivity.this, PostPopupActivity.class);

                startActivityForResult(intent, 1);
            }
        });


        // get data

        final ValueEventListener postListener = new ValueEventListener(){

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mArrayList.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    postData get = postSnapshot.getValue(postData.class);
                    post data = new post(get.username, get.content, get.tags, get.time, get.uri, get.level, get.catType);
                    mArrayList.add(data);
                    textViewCounter.setText("");
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference.child("post_list").addValueEventListener(postListener);





    }



/*
    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        private static final int CARD_ITEM_SIZE = 1;
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        @NonNull @Override
        public Fragment createFragment(int position) {
            return fragment.newInstance(position);
        }
        @Override
        public int getItemCount() {
            return CARD_ITEM_SIZE;
        }
    }


 */

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
}
