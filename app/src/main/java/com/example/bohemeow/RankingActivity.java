package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class RankingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    Uri currentImageUri;


    private DatabaseReference mPostReference;
    String username; // 보고있는 유저의 정보
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
        getUserData(username); // 레벨과 타입을 가져옴. 아래 함수 참고


        // get user icon
        mStorageRef = FirebaseStorage.getInstance().getReference("User_icons");


        // view pager  // 탭 관리. 탭을 늘리려면 porition == 2 ...
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position == 0){
                            tab.setText("일간랭킹");
                        }
                        else if(position == 1){
                            tab.setText("주간랭킹");
                        }
                    }
                }).attach();



        mArrayList = new ArrayList<>();
        // get data



        /*  버튼을 사용할것이라면 추가

        // button
        Button add_post_btn = (Button) findViewById(R.id.add_post_btn);
        // add post button
        add_post_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(RankingActivity.this, WritePostActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("level", level);
                    intent.putExtra("catType", catType);
                    //intent.putExtra("name", name);
                    startActivity(intent);

            }
        });

         */



    }




    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        private static final int CARD_ITEM_SIZE = 2; // 탭의 수
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        @NonNull @Override
        public Fragment createFragment(int position) {  // fragment 호출. 여기서 모든 데이터를 가져오고 보여줌.
            return fragmentGeneral.newInstance(position);

        }
        @Override
        public int getItemCount() {
            return CARD_ITEM_SIZE;
        }
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RankingActivity.this, MainMenu.class);
        startActivity(intent);
    }

    public void getUserData(final String user_nickname){ // 서버에서 유저의 정보를 가져오는 함수, 이 함수에서는 레벨과 타입만을 가져옴.
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UserData get = postSnapshot.getValue(UserData.class);

                    if(user_nickname.equals(get.nickname)){
                        level = get.level;
                        catType = get.catType;
                        //다른 정보가 필요하면 추가
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
