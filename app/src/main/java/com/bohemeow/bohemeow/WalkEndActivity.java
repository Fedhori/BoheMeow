package com.bohemeow.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class WalkEndActivity extends AppCompatActivity {

    DatabaseReference ref;

    String username;
    String phoneNumber;

    TMapView tMapView;

    int treasureCnt = 0;
    int markerCnt = 0;

    boolean isFindTreasure = false;
    double[] treasureLats = new double[10];
    double[] treasureLngs = new double[10];
    int numOfTreasure = 0;

    double totalMoveLength = 0f; // 산책하는 동안 총 얼마나 걸었는가? 단위: m
    long totalWalkTime = 0; // 얼마나 오래 산책했는가? 단위: ms
    long realWalkTime = 0; // 얼마나 오래 실제로 걸었는가? 단위: ms
    long totalPoint = 0;
    long notePoint = 0;
    long walkPoint = 0;
    long spotPoint = 0;
    long spotCount = 0;
    long treasureValue = 0;

    long user_realWalkTime;
    long user_totalWalkTime;
    long user_totalWalkCount;
    long user_totalSpotCount;
    double user_totalMoveLength;
    long user_totalPoint;
    long user_totalRealPoint;
    long user_catType;

    boolean isWritten = false;
    boolean isBackToSelect = false;

    // 치팅을 했다고 판정되는 경우 true가 됨
    boolean isCheating = false;
    // 유저가 산책을 제대로 진행하지 않았을 경우 true가 됨
    boolean isTooShort = false;

    String skKey = "l7xxc4527e777ef245ef932b366ccefaa9b0";

    //set cat image
    int[] icons = {R.drawable.cathead_null, R.drawable.hanggangic, R.drawable.bameeic, R.drawable.chachaic,
            R.drawable.ryoniic, R.drawable.moonmoonic, R.drawable.popoic,R.drawable.taetaeic, R.drawable.sessakic};

    TextView callory;

    private String[] shortWalkTexts = {
      "모험이라기엔 너무 조금 움직였구만!",
      "준비운동은 끝난거냐옹?",
      "음…. 내일은 집밖으로 나가보자."
    };

    private String[] cheatingTexts = {
      "평범한 고양이의 움직임이 아닌데. 기록하기는 어렵겠어..",
      "모험자라면 정정당당하게 승부한다옹. 이번엔 잘 걸어보자.",
      "보헤미양은 걷기운동만을 위한 앱인데, 알고있지?"
    };

    private String[] normalTexts = {
            "이 구역 탐험가는 나야! 오늘도 좋은 모험이었어.",
            "한층 건강해진 느낌이다옹. 오늘도 즐거웠어.",
            "고양이는 말야. 한걸음 한걸음 허투루 걷는 법이 없지.",
            "천리길도 한 걸음부터다옹. 이제 999리 남았나?",
            "영차영차 해봅시다. 영! 영! 아니, 차 안해주냐옹.",
            "늘 조금씩 나아지는 탐험가가 되자. 오늘도 수고했다옹."
    };

    private String[] levelUpTexts = {
            "뭔가 강해진 기분이다옹..!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_end);

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        username = registerInfo.getString("registerUserName", "NULL");
        phoneNumber = registerInfo.getString("phoneNumber", "NULL");

        // get intent
        Intent intent = getIntent();

        isBackToSelect = intent.getBooleanExtra("isBackToSelect", false);

        totalMoveLength = intent.getDoubleExtra("totalMoveLength", -1);
        totalWalkTime = intent.getLongExtra("totalWalkTime", -1);
        realWalkTime = intent.getLongExtra("realWalkTime", -1);
        totalPoint = intent.getLongExtra("totalPoint", -1);
        spotCount = intent.getLongExtra("spotCount", 0);

        notePoint = intent.getLongExtra("notePoint", -1);
        walkPoint = intent.getLongExtra("walkPoint", -1);
        spotPoint = intent.getLongExtra("spotPoint", -1);
        isFindTreasure = intent.getBooleanExtra("isFindTreasure", false);
        treasureValue = intent.getLongExtra("treasureValue", treasureValue);
        /*
        treasurePoint = intent.getLongExtra("treasurePoint", -1);
        if(isFindTreasure){
            numOfTreasure = intent.getIntExtra("numOfTreasure", 0);
            treasureLats = intent.getDoubleArrayExtra("treasureLats");
            treasureLngs = intent.getDoubleArrayExtra("treasureLngs");

            for(int i = 0;i<numOfTreasure;i++){
                drawTreasureMarker(new TMapPoint( treasureLats[i], treasureLngs[i]));
            }
        }
         */

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.tmapLinearLayout);

        String strObj = getIntent().getStringExtra("tMapView");
        Gson gson = new Gson();
        TMapPolyLine userRoute = gson.fromJson(strObj, TMapPolyLine.class);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey(skKey);
        linearLayoutTmap.addView( tMapView );

        tMapView.addTMapPolyLine("Line1", userRoute);

        double centerLat = intent.getDoubleExtra("centerLat", 0);
        double centerLng = intent.getDoubleExtra("centerLng", 0);

        tMapView.setCenterPoint(centerLng, centerLat);
        tMapView.setZoomLevel(14);

        int visitedSize = intent.getIntExtra("visitedSize", 0);
        double[] visitedLats = intent.getDoubleArrayExtra("visitedLats");
        double[] visitedLngs = intent.getDoubleArrayExtra("visitedLngs");

        TMapPoint startPoint = new TMapPoint(centerLat, centerLng);
        drawSpotMarker(startPoint, R.drawable.walking_marker_startpoint);
        for(int i = 0;i<visitedSize;i++){
            drawSpotMarker(new TMapPoint(visitedLats[i], visitedLngs[i]), R.drawable.walking_marker_visited);
        }

        TextView time = findViewById(R.id.time_view);
        TextView distance = findViewById(R.id.dis_view);
        TextView pace = findViewById(R.id.pace_view);
        callory = findViewById(R.id.cal_view);
        TextView score = findViewById(R.id.score_view);
        TextView spot = findViewById(R.id.count_view);
        TextView treasure = findViewById(R.id.treasure_view);

        spot.setText(spotCount + "번");
        treasure.setText(treasureValue + "");

        long totalTime = realWalkTime; // ms
        long hour;
        long minute;
        long second;

        hour = totalTime / 3600000;
        totalTime %= 3600000;
        minute = totalTime / 60000;
        totalTime %= 60000;
        second = totalTime / 1000;

        /*
        String timeText = "";
        if(hour >= 10){
            timeText += String.valueOf(hour);
        }
        else{
            timeText += "0" + hour;
        }
        timeText += "";
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
        time.setText(timeText);

         */
        String timeText = hour + "시간 " + minute + "분 " + second + "초";
        time.setText(timeText);


        ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double cal;
                long weight =  (long) dataSnapshot.child("weight").getValue();
                cal = 0.9 * weight * (realWalkTime/60000) / 15;
                callory.setText(Double.toString(Double.parseDouble(String.format("%.2f",cal))) + "kcal");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        int catType = registerInfo.getInt("userCatType", 1);
        ImageView catFace = findViewById(R.id.catFace);
        catFace.setImageResource(icons[catType]);

        distance.setText(String.format("%.2f", totalMoveLength / 1000d) + "km");

        if(totalMoveLength >= 1d){
            long paceTime = realWalkTime / (long)totalMoveLength; // second

            // 100m도 안 움직였어?!!
            if(totalMoveLength <= 100d){
                isTooShort = true;
            }
            // 1km를 평균 4분만에 완주하는 페이스 -> 치팅!
            if(paceTime < 240){
                isCheating = true;
            }

            minute = paceTime / 60;
            paceTime %= 60;
            second = paceTime;

            String paceText = "";
            if(minute >= 10){
                paceText += String.valueOf(minute);
            }
            else{
                paceText += "0" + minute;
            }
            paceText += "'";
            if(second >= 10){
                paceText += String.valueOf(second);
            }
            else{
                paceText += "0" + second;
            }
            paceText += "\"";
            pace.setText(paceText);
        }
        else{
            isTooShort = true;
        }

        score.setText(String.valueOf(totalPoint));

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalkEndActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        TextView comment = findViewById(R.id.comment);
        Random random = new Random();
        comment.setText(normalTexts[random.nextInt(normalTexts.length)]);

        if(isFindTreasure){
            comment.setText("이번 산책에서 보물을 발견해서 추가 포인트를 받았어!");
        }

        // update value to firebase
        updateUserData(username, realWalkTime, totalWalkTime, totalMoveLength, totalPoint);

        if(isCheating || isTooShort){

            if(isCheating){
                Toast.makeText(WalkEndActivity.this, "산책이 정상적으로 진행이 되지 않았음이 감지되어 점수에 반영되지 않습니다.", Toast.LENGTH_LONG).show();
                comment.setText(cheatingTexts[random.nextInt(cheatingTexts.length)]);
            }
            else {
                Toast.makeText(WalkEndActivity.this, "산책을 너무 짧게 진행하여 점수에 반영되지 않습니다.", Toast.LENGTH_LONG).show();
                comment.setText(shortWalkTexts[random.nextInt(shortWalkTexts.length)]);
            }

            if(isBackToSelect){
                intent = new Intent(WalkEndActivity.this, SelectActivity.class);
                startActivity(intent);
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WalkEndActivity.this, MainMenu.class);
        startActivity(intent);
    }

    void updateUserData(String username, final long realWalkTime, final long totalWalkTime, final double totalMoveLength, final long totalPoint){

        ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!isWritten) {

                    user_realWalkTime = (long) dataSnapshot.child("realWalkTime").getValue();
                    user_totalWalkTime = (long) dataSnapshot.child("totalWalkTime").getValue();
                    user_totalWalkCount = (long) dataSnapshot.child("totalWalkCount").getValue();
                    user_totalSpotCount = (long) dataSnapshot.child("totalSpotCount").getValue();
                    // Double.class를 한 이유는, Firebase에서 0을 가져오면 그냥 Long으로 취급해버림. 그래서 타입 캐스팅 오류가 발생하므로 이를 방지하고자 Double형으로 받아오도록 명시해줘야 함
                    user_totalMoveLength = dataSnapshot.child("totalWalkLength").getValue(Double.class);
                    user_totalPoint = (long) dataSnapshot.child("level").getValue();
                    user_totalRealPoint = (long) dataSnapshot.child("point").getValue();
                    user_catType = (long) dataSnapshot.child("catType").getValue();

                    SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = registerInfo.edit();

                    // 치팅이 아닐때만 데이터가 수정됨
                    if(!isCheating && !isTooShort){
                        ref.child("realWalkTime").setValue(user_realWalkTime + realWalkTime);
                        ref.child("totalWalkTime").setValue(user_totalWalkTime + totalWalkTime);
                        ref.child("totalWalkCount").setValue(user_totalWalkCount + 1);
                        ref.child("totalSpotCount").setValue(user_totalSpotCount + spotCount);
                        ref.child("totalWalkLength").setValue(user_totalMoveLength + totalMoveLength);
                        ref.child("level").setValue(user_totalPoint + totalPoint);

                        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                        Date date = new Date();
                        DateFormat df = new SimpleDateFormat("MMdd");
                        df.setTimeZone(tz);
                        int time = Integer.parseInt(df.format(date));
                        if(time < 1123){
                            ref.child("point").setValue(user_totalRealPoint + totalPoint);
                        }

                        editor.putInt("exp", (int)(user_totalPoint + totalPoint));
                        editor.putInt("totalSpotCount", (int)(user_totalSpotCount + spotCount));
                    }

                    TextView totalTime_tv = findViewById(R.id.total_time_view);
                    TextView totalDist_tv = findViewById(R.id.total_dis_view);
                    TextView totalCount_tv = findViewById(R.id.total_walk_count);

                    long totalTime = user_realWalkTime + realWalkTime; // ms
                    long hour;
                    long minute;
                    long second;

                    hour = totalTime / 3600000;
                    totalTime %= 3600000;
                    minute = totalTime / 60000;
                    totalTime %= 60000;
                    second = totalTime / 1000;

                    String timeText = hour + "시간 " + minute + "분 " + second + "초";
                    totalTime_tv.setText(timeText);
                    totalDist_tv.setText(String.format("%.2f", (user_totalMoveLength + totalMoveLength) / 1000d) + "km");
                    user_totalSpotCount += spotCount;
                    totalCount_tv.setText(user_totalSpotCount + "번");

                    TextView comment = findViewById(R.id.comment);

                    int prev_level = 0;
                    int cur_level = 0;

                    if(!isCheating && !isTooShort){
                        prev_level = calculateLevel((int)user_totalPoint);
                        cur_level = calculateLevel((int) (user_totalPoint + totalPoint));
                    }

                    // level up!
                    if(prev_level < cur_level){
                        Random random = new Random();
                        comment.setText(levelUpTexts[random.nextInt(levelUpTexts.length)]);

                        if(cur_level == 7){
                            editor.putBoolean("isGrowth", true);
                            editor.commit();
                        }
                    }

                    isWritten = true;
                    if(isBackToSelect){
                        Intent intent = new Intent(WalkEndActivity.this, SelectActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    void updateDailyPoint(final long totalPoint){
        // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        String time = df.format(date);

        final DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference().child("daily_rank").child(time).child(username);
        mPostReference.addValueEventListener(new ValueEventListener() {

            boolean isWritten = false;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!isWritten) {

                    long user_daily_totalPoint = 0;

                    if(dataSnapshot.exists()){
                        user_daily_totalPoint = (long) dataSnapshot.child("point").getValue();
                    }
                    else{
                        // not generated yet
                        mPostReference.child("username").setValue(username);
                    }

                    mPostReference.child("point").setValue(user_daily_totalPoint + totalPoint);

                    isWritten = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void checkAndUpdateLevelReward(int user_totalPoint, int new_totalPoint){

        int prev_level = calculateLevel(user_totalPoint);
        int cur_level = calculateLevel(new_totalPoint);

        if(prev_level < cur_level){
            // this value must be synchronized with WalkEndActivity's rewardLevelList array
            int[] rewardLevelList = {2, 5, 10, 20, 30, 40};
            int length = rewardLevelList.length;
            for(int i = 0;i<length;i++) {
                if (prev_level < rewardLevelList[i] && cur_level >= rewardLevelList[i]) {

                    // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
                    TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(tz);
                    String time = df.format(date);

                    // 파이어베이스에 유저가 특정 레벨에 도달헀음을 저장한다.
                    DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> childUpdates = new HashMap<>();
                    Map<String, Object> postValues = null;
                    lotsData data = new lotsData(phoneNumber);
                    postValues = data.toMap();
                    childUpdates.put("/level_reward/" + i + "/" + time + "/", postValues);
                    mPostReference.updateChildren(childUpdates);

                    break;
                }
            }
        }
    }


    public void drawSpotMarker(TMapPoint position, int marker){

        // get bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), marker);
        // resize bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, 75, 135, false);

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(bitmap); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f);
        // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(position); // 마커의 좌표 지정
        tMapView.addMarkerItem("Spot" + markerCnt++, markerItem); // 지도에 마커 추가
    }

    public void drawTreasureMarker(TMapPoint position){
        int marker = R.drawable.walking_marker_treasure;
        // get bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), marker);
        // resize bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, false);

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(bitmap); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f);
        // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(position); // 마커의 좌표 지정
        tMapView.addMarkerItem("treasure" + treasureCnt++, markerItem);
    }
}