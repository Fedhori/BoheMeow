package com.bohemeow.bohemeow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

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
            public void onItemClick(AdapterView parent, View v, int position, long id){

                RankData rankData = myUserAdapter.getItem(position);
                Intent intent = new Intent(RankingActivity.this, RankPopUpActivity.class);
                intent.putExtra("rankData", rankData);
                startActivity(intent);
            }
        });

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
