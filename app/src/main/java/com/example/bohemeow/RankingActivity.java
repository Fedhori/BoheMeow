package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
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

import java.io.Serializable;
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

        Button questionBtn = findViewById(R.id.questionBtn);
        questionBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RankingActivity.this, RankingExplanationActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        userData = (UserData[]) intent.getSerializableExtra("userData");
        rank = intent.getIntArrayExtra("rank");
        size = intent.getIntExtra("size", 11);

        ConvertUserDataToRankData();
        InitializeRankData();

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setDivider(null);
        final RankingCustomAdapter myAdapter = new RankingCustomAdapter(this,rankDataList);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){

                RankData rankData = myAdapter.getItem(position);
                Intent intent = new Intent(RankingActivity.this, RankPopUpActivity.class);
                intent.putExtra("rankData", rankData);
                startActivity(intent);
            }
        });
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

        // last array is user data
        rankDataList.add(rankData[size - 1]);
        // add 1st~10th user's data
        for(int i = 0;i<size - 1;i++){
            rankDataList.add(rankData[i]);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RankingActivity.this, MainMenu.class);
        startActivity(intent);
    }
}
