package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

    ArrayList<RankData> rankDataList;

    UserData[] userData = new UserData[11];
    RankData[] rankData = new RankData[11];
    int[] rank = new int[11];
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Intent intent = getIntent();
        userData = (UserData[]) intent.getSerializableExtra("userData");
        rank = intent.getIntArrayExtra("rank");
        size = intent.getIntExtra("size", 11);

        ConvertUserDataToRankData();
        InitializeRankData();

        ListView listView = (ListView)findViewById(R.id.listView);
        final RankingCustomAdapter myAdapter = new RankingCustomAdapter(this,rankDataList);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                /*
                Toast.makeText(getApplicationContext(),
                        myAdapter.getItem(position).getMovieName(),
                        Toast.LENGTH_LONG).show();
                 */
            }
        });

        /*
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
         */



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

    public void ConvertUserDataToRankData(){
        for(int i = 0;i<size;i++){
            rankData[i] = new RankData(userData[i].nickname, userData[i].catType, userData[i].totalWalkLength, userData[i].totalWalkTime, userData[i].totalWalkCount,
                    userData[i].level, rank[i], userData[i].introduction);
        }
    }

    public void InitializeRankData()
    {
        rankDataList = new ArrayList<RankData>();

        for(int i = 0;i<size;i++){
            rankDataList.add(rankData[i]);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RankingActivity.this, MainMenu.class);
        startActivity(intent);
    }
}
