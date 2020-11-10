package com.bohemeow.bohemeow;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class MainConfigActivity extends AppCompatActivity  {

    ImageButton cat_imgbtn;

    TextView user_id;
    TextView user_name;
    TextView user_level;
    TextView total_count;
    TextView total_dis;
    TextView total_time;

    Button back_btn;
    Button edit_btn;
    Button man_btn;
    Button logout_btn;
    Button delacc_btn;
    Button bugreport_btn;
    Button FAQ_btn;

    ProgressBar progressBar;

    TabLayout tabLayout;
    ViewPager2 viewPager;

    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config_popup);

        Intent intent = getIntent();
        userData = (UserData) intent.getSerializableExtra("userdata");


        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position == 0){
                            tab.setText("기록");
                        }
                        else if(position == 1){
                            tab.setText("캐릭터");
                        }
                    }
                }).attach();



        cat_imgbtn = findViewById(R.id.cat_img);
        user_id = findViewById(R.id.user_id);
        user_name = findViewById(R.id.user_name);
        user_level = findViewById(R.id.user_level);
        total_count = findViewById(R.id.total_count);
        total_dis = findViewById(R.id.total_dis);
        total_time = findViewById(R.id.total_time);

        //set cat image
        int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
                R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};
        cat_imgbtn.setImageResource(icons[userData.catType]);
        cat_imgbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigCharacterActivity.class);
                intent.putExtra("catType", userData.catType);
                startActivity(intent);
            }
        });

        //set detail data
        user_id.setText(userData.id);
        user_name.setText(userData.nickname);
        user_level.setText(Integer.toString(calculateLevel(userData.level)));
        total_count.setText(Integer.toString(userData.totalWalkCount) + "번");

        int distance = (int) userData.totalWalkLength;
        total_dis.setText(Integer.toString(distance) + "m");

        long totalTime = userData.realWalkTime; // ms
        long hour;
        long minute;
        long second;

        hour = totalTime / 3600000;
        totalTime %= 3600000;
        minute = totalTime / 60000;
        totalTime %= 60000;
        second = totalTime / 1000;

        String timeText = "";
        if(hour >= 10){
            timeText += String.valueOf(hour);
        }
        else{
            timeText += "0" + hour;
        }
        timeText += ":";
        if(minute >= 10){
            timeText += String.valueOf(minute);
        }
        else{
            timeText += "0" + minute;
        }
        timeText += ":";
        if(second >= 10){
            timeText += String.valueOf(second);
        }
        else{
            timeText += "0" + second;
        }
        total_time.setText(timeText);




        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edit_btn = findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigEditActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        man_btn = findViewById(R.id.man_btn);
        man_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigBackgroundActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigLogoutActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        delacc_btn = findViewById(R.id.delacc_btn);
        delacc_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, ConfigDelActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        bugreport_btn = findViewById(R.id.bugreport_btn);
        bugreport_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, BugReportActivity.class);
                intent.putExtra("userdata", userData);
                startActivityForResult(intent, 1);
            }
        });

        final FAQDAta[] faqData = getFaqData();
        FAQ_btn = findViewById(R.id.FAQ_btn);
        FAQ_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainConfigActivity.this, FAQActivity.class);
                intent.putExtra("faqData", faqData);
                startActivityForResult(intent, 1);
            }
        });

        Button notice_btn = findViewById(R.id.notice_btn);
        notice_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getNoticeDataAndStartActivity();
            }
        });

        progressBar = findViewById(R.id.progressbar);

        double currentLevel = 0;

        if(userData.level >= 10000){
            currentLevel = ((double) ((userData.level - 10000) % 1500)) / 15d;
        }
        else{
            currentLevel = ((double) userData.level % 1000) / 10d;
        }

        progressBar.setProgress((int)currentLevel);

    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        private static final int CARD_ITEM_SIZE = 2;
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        @NonNull @Override
        public Fragment createFragment(int position) {
            if(position == 0){
                return fragmentWalkdetail.newInstance(position);
            }
            else return fragmentCatdetail.newInstance(position);
        }
        @Override
        public int getItemCount() {
            return CARD_ITEM_SIZE;
        }
    }

    void getNoticeDataAndStartActivity(){

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
        mPostReference.child("notice_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int cnt = 0;
                NoticeData[] noticeData = new NoticeData[(int)dataSnapshot.getChildrenCount()];

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    noticeData[cnt++] = postSnapshot.getValue(NoticeData.class);
                }

                Intent intent = new Intent(MainConfigActivity.this, NoticeActivity.class);
                intent.putExtra("noticeData", noticeData);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    FAQDAta[] getFaqData(){
        return new FAQDAta[]{
                new FAQDAta("나는 강을 지키고 있는 진기다. 누군지 이름을 밝혀라!", "관우"),
                new FAQDAta("어디로 가는 길이오?", "하북"),
                new FAQDAta("통행증은 갖고 있겠지?", "그런 건 없다")
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){

            Intent intent = new Intent();

            switch(resultCode){
                case RESULT_OK: // result_ok -> nothing happened
                    break;
                case 0: // background case
                    setResult(0, intent);
                    finish();
                    break;
                case 1: // logout & delete account case
                    setResult(1, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
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
}
