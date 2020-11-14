package com.bohemeow.bohemeow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RankingActivity extends AppCompatActivity {
// last array is user data

    ArrayList<RankData> rankDataList;
    ArrayList<RankData> userDataList;

    // !!!this value must synchronize with MainMenu's maxRankUser variable!!!
    int maxRankUser = 100;

    UserData[] userData = new UserData[maxRankUser + 1];
    RankData[] rankData = new RankData[maxRankUser + 1];
    int[] rank = new int[maxRankUser + 1];
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
        size = intent.getIntExtra("size", maxRankUser + 1);

        ConvertUserDataToRankData();
        InitializeRankData();

        ListView userListView = (ListView)findViewById(R.id.user_listView);
        userListView.setDivider(null);
        final RankingCustomAdapter myUserAdapter = new RankingCustomAdapter(this,userDataList);
        userListView.setAdapter(myUserAdapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, final int position, long id){

                DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData get = dataSnapshot.child(myUserAdapter.getItem(position).nickname).getValue(UserData.class);
                        System.out.println(get);
                        Intent intent = new Intent(RankingActivity.this, MainConfigActivity.class);
                        intent.putExtra("userdata", get);
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setDivider(null);
        final RankingCustomAdapter myAdapter = new RankingCustomAdapter(this,rankDataList);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, final int position, long id){

                DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                mPostReference.child("user_list").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData get = dataSnapshot.child(myAdapter.getItem(position).nickname).getValue(UserData.class);
                        System.out.println(get);
                        Intent intent = new Intent(RankingActivity.this, MainConfigActivity.class);
                        intent.putExtra("userdata", get);
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void ConvertUserDataToRankData(){
        for(int i = 0;i<size;i++){
            rankData[i] = new RankData(userData[i].nickname, userData[i].catType, userData[i].totalWalkLength, userData[i].realWalkTime, userData[i].totalWalkCount, userData[i].totalSpotCount,
                    userData[i].level, rank[i], userData[i].introduction);
        }
    }

    public void InitializeRankData()
    {
        // put user data
        userDataList = new ArrayList<RankData>();
        rankData[size - 1].isUser = true;
        userDataList.add(rankData[size - 1]);

        // add 1st~10th user's data
        rankDataList = new ArrayList<RankData>();
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
