package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapData.TMapPathType;
import com.skt.Tmap.TMapData.FindPathDataListenerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class WalkActivity extends AppCompatActivity implements onLocationChangedCallback{

    private DatabaseReference mPostReference;

    PermissionManager permissionManager = null; // 권한요청 관리자
    TMapGpsManager gps = null;
    private TMapView tMapView = null;
    private Context context;

    TextView timeText_tv;
    TextView distText_tv;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 60000; //Delay for 60 seconds.  One second = 1000 milliseconds.
    int cur_time = 0; // minute

    Button walkEndBtn;
    Button popupBtn;

    private TMapPolyLine userRoute = null;
    private double[] lastLatitudes = new double[10];
    private double[] lastLongtitudes = new double[10];
    private int curPos = 0;
    private int lapNum = 0;

    private int polyLineCnt = 0;
    private int markerCnt = 0;

    private boolean isFirstLocation = false;

    String region = "";
    boolean isFree = false;

    private double maxMoveLength = 30f; // 최소 30m는 이동해야 데이터가 저장됨
    private double curMoveLength = 0f; // 파이어베이스에 데이터가 저장되기까지, 현재 얼마나 걸었는가?
    private double totalMoveLength = 0f; // 산책하는 동안 총 얼마나 걸었는가?
    private double prevLat = -1f;
    private double prevLong = -1f;
    private long prevTime = -1;
    private int moveCnt = 0;

    Double minMovementSpeed = 0.1d; // m/s, ex) 0.1m/s is 0.36km/h
    Double maxMovementSpeed = 10d; // 36km/h

    long totalWalkTime = 0;
    long walkStartTime = 0;
    long walkEndTime = 0;
    long realWalkTime = 0;
    long totalPoint = 0;

    long user_totalPoint = 0;
    DatabaseReference ref;
    boolean isWritten = false;

    double[] lats;
    double[] lngs;
    boolean[] isVisited = new boolean[32];
    int arr_length;

    String nickname;
    String phoneNumber;

    // how many notes one user allow to write?
    int maxNoteNumber = 3;

    // how many notes will user find in walk screen
    int maxFindNote = 3;

    // how long can user can find notes?
    double maxNoteDist = 100d; // meter

    double minWalkLengthToFindLots = 500; // meter
    double maxWalkLengthToFindLots = 1500; // meter
    double curWalkLengthToFindLots = 0;
    boolean isAlreadyFindLots = false;

    //int[] preference = new int[3];//0:safe 1:envi 2:popularity

    String key = "AIzaSyBHSgVqZUvi8EmRbrZsH9z6whHSO-R3LXo"; // google key

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionManager.setResponse(requestCode, grantResults); // 권한요청 관리자에게 결과 전달
    }

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        context = this;

        isAlreadyFindLots = checkIsAlreadyFindLots();
        // 유저는 오늘 아직 뽑기를 찾지 못했다.
        if(!isAlreadyFindLots){
            // determine the length to find lots
            curWalkLengthToFindLots = new Random().nextDouble() * (maxWalkLengthToFindLots - minWalkLengthToFindLots) + minWalkLengthToFindLots;
        }

        mPostReference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        nickname = registerInfo.getString("registerUserName", "NULL");
        phoneNumber = registerInfo.getString("phoneNumber", "NULL");

        walkStartTime = System.currentTimeMillis();

        // get intent
        Intent intent = getIntent();
        //preference = intent.getIntArrayExtra("preference");
        region = intent.getStringExtra("region");
        isFree = intent.getBooleanExtra("isFree", false);

        walkEndBtn = (Button) findViewById(R.id.walkEndBtn);
        walkEndBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                walkEndTime = System.currentTimeMillis();
                totalWalkTime = walkEndTime - walkStartTime;

                Intent intent = new Intent(WalkActivity.this, WalkEndActivity.class);
                intent.putExtra("totalWalkTime", totalWalkTime);
                intent.putExtra("realWalkTime", realWalkTime);
                intent.putExtra("totalMoveLength", totalMoveLength);
                intent.putExtra("totalPoint", totalPoint);

                handler.removeCallbacks(runnable); //stop handler when activity not visible
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        popupBtn = (Button) findViewById(R.id.popupBtn);
        popupBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //데이터 담아서 팝업(액티비티) 호출
                Intent intent = new Intent(WalkActivity.this, PopupActivity.class);
                startActivityForResult(intent, 1);
                //finish();
            }
        });

        // set t map view
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxc4527e777ef245ef932b366ccefaa9b0");
        linearLayoutTmap.addView( tMapView );

        timeText_tv = findViewById(R.id.timeText);
        distText_tv = findViewById(R.id.distText);

        lats = intent.getDoubleArrayExtra("lats");
        lngs = intent.getDoubleArrayExtra("lngs");

        int temp_length = lats.length;

        for(int i = 1;i<temp_length;i++){
            if(lats[0] == lats[i] && lngs[0] == lngs[i]){
                // 마지막 스팟은 사용자 시작 위치임. 그렇기에 이를 통해 실제 배열의 길이를 구할 수 있다.
                arr_length = i;
                break;
            }
        }
        for(int i = 0;i<arr_length;i++){
            Log.w("asd", lats[i] + " " + lats[i]);
        }


        // set screen to start position
        tMapView.setLocationPoint(lngs[0], lats[0]);
        tMapView.setCenterPoint(lngs[0], lats[0]);

        prevLat = lats[0];
        prevLong = lngs[0];

        findNotes(prevLat, prevLong, maxFindNote, maxNoteDist);

        userRoute = new TMapPolyLine();
        userRoute.setLineColor(Color.RED);
        userRoute.setOutLineColor(Color.RED);
        userRoute.setLineWidth(1);

        if(!isFree) {

            ArrayList<TMapPoint> spots = new ArrayList<>();
            for (int i = 0; lats[i] != -1; i++) {
                spots.add(new TMapPoint(lats[i], lngs[i]));
            }

            for(int i = 0; i < spots.size() - 1; i++){
                drawSpotMarker(spots.get(i));
                drawPedestrianPath(spots.get(i), spots.get(i+1));
            }
        }

        permissionManager = new PermissionManager(this); // 권한요청 관리자
        permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
            // 허가 시 GPS 화면 출력
            @Override
            public void granted() {
                turnGPSOn();

                gps = new TMapGpsManager(WalkActivity.this);
                // check every 1000ms
                gps.setMinTime(100);
                // if user moves at least 10m, call onLocationChange
                gps.setMinDistance(0.1f);
                gps.setProvider(TMapGpsManager.GPS_PROVIDER);
                gps.OpenGps();
                gps.setProvider(TMapGpsManager.NETWORK_PROVIDER);
                gps.OpenGps();

                tMapView.setIconVisibility(true);
                tMapView.setTrackingMode(true);
            }

            // 허가하지 않을 경우 토스트 메시지와 함께 메인 메뉴로 돌려보낸다
            @Override
            public void denied() {
                Toast.makeText(WalkActivity.this, "허가 없이는 진행이 불가능합니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WalkActivity.this, MainMenu.class);

                handler.removeCallbacks(runnable); //stop handler when activity not visible
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        handler.postDelayed( runnable = new Runnable() {
            public void run() {

                cur_time++;

                int hour = cur_time / 60;
                int minute = cur_time % 60;

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

                timeText_tv.setText(timeText);

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        /*
        // set center point
        tMapView.setCenterPoint(126.97406798055658, 37.29389181202027);

        // draw marker
        TMapPoint tMapPoint1 = new TMapPoint(37.29389181204, 126.97406798057);
        drawMarker(tMapPoint1);

        // draw route
        TMapPoint startPoint = new TMapPoint(37.2939299 , 126.9739263);
        TMapPoint endPoint = new TMapPoint(37.283337, 126.982062);
        drawPedestrianPath(startPoint, endPoint);
         */

    }

    public void drawPedestrianPath(TMapPoint startPoint, TMapPoint endPoint) {

        try {
            //set time in mili
            Thread.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }


        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.BLUE);
                tMapView.addTMapPolyLine(Integer.toString(polyLineCnt++), polyLine);
            }
        });
    }

    public void drawSpotMarker(TMapPoint position){

        // get bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.walk_point);
        // resize bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(bitmap); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f);
        // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(position); // 마커의 좌표 지정
        // markerItem.setName("성대");
        tMapView.addMarkerItem(Integer.toString(markerCnt++), markerItem); // 지도에 마커 추가
    }

    public void drawNoteMarker(NoteData noteData){

        // get bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_memo);
        // resize bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(bitmap); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f);
        // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(new TMapPoint(noteData.latitude, noteData.longitude)); // 마커의 좌표 지정
        markerItem.setCalloutTitle(noteData.author);
        markerItem.setCalloutSubTitle(noteData.noteContent);
        markerItem.setCanShowCallout(true);
        // markerItem.setAutoCalloutVisible(true);

        tMapView.addMarkerItem(Integer.toString(markerCnt++), markerItem); // 지도에 마커 추가
    }

    @Override
    public void onLocationChange(Location location) {

        lastLatitudes[curPos] = location.getLatitude();
        lastLongtitudes[curPos] = location.getLongitude();
        curPos++;

        if(curPos >= 10){
            curPos = 0;
            lapNum++;
        }

        if(lapNum > 1){

            double latitude = 0f;
            double longtitude = 0f;

            // 최근 GPS에 잡힌 좌표들 10개의 평균을 현재 위치로 설정한다.
            for(int i = 0;i<10;i++){
                latitude += lastLatitudes[i];
                longtitude += lastLongtitudes[i];
            }

            latitude /= 10f;
            longtitude /= 10f;

            tMapView.setLocationPoint(longtitude, latitude);
            // 이 코드를 넣으면 화면 중앙으로 고정됨
            //tMapView.setCenterPoint(longtitude, latitude);
            userRoute.addLinePoint(new TMapPoint(latitude, longtitude));
            /*
            userRoute.setLineColor(Color.RED);
            userRoute.setOutLineColor(Color.RED);
             */
            tMapView.addTMapPolyLine("Line1", userRoute);

            // get move length
            double moveLength = distFrom(latitude, longtitude, prevLat, prevLong);

            // check the speed
            long intervalTime = System.currentTimeMillis() - prevTime;
            // 만일 사용자가 최소 이동속도 (minMovementSpeed, 현재는 0.1m/s -> 0.36km/s)보다 빠른 속도로 이동했을 경우에는 실제 걸은 시간으로 책정!
            // 만일 사용자가 최대 이동속도 36km/h보다 빠르게 이동해도 제외.
            if(moveLength * 1000d > minMovementSpeed * (double)intervalTime && moveLength * 1000d < maxMovementSpeed * (double)intervalTime){

                realWalkTime += intervalTime;

                curMoveLength += moveLength;
                if(maxMoveLength <= curMoveLength){
                    addCoordinationData(latitude, longtitude);
                    addCoordinationID(latitude, longtitude);

                    checkNearSpot(latitude, longtitude);
                    // 100m당 10점이니까, 30m(maxMoveLength)당 3점
                    totalPoint+=3;

                    // 나중에 여기다가 산책 경로 데이터 저장하는 코드 넣어야겠다.

                    curMoveLength = 0f;
                }
                // 충분히 걸었고, 만일 오늘 아직 쪽지를 발견하지 않았다면 쪽지 발견 함수를 호출한다.
                if(curWalkLengthToFindLots <= curMoveLength && !isAlreadyFindLots){
                    findLots();
                }
                totalMoveLength += moveLength;
                distText_tv.setText(String.format("%.2f", totalMoveLength / 1000f));
            }

            prevLat = latitude;
            prevLong = longtitude;
            prevTime = System.currentTimeMillis();
        }
    }

    public double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    private void turnGPSOn(){

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }
    }

    public void addCoordinationData(Double latitude, Double longtitude){

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        Data data = new Data(latitude, longtitude);
        postValues = data.toMap();
        childUpdates.put("/user_data/hongkildong/" + Integer.toString(moveCnt) + "/", postValues);
        mPostReference.updateChildren(childUpdates);

        moveCnt++;
    }

    public void addCoordinationID(final Double latitude, final Double longtitude){

        new Thread() {
            public void run() {

                String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude +"," + longtitude +
                        "&language=ko&location_type=ROOFTOP&key=" + key;


                String page = "";
                String place_id = "";

                try {
                    URL url = new URL(uri);
                    URLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    //Log.d("line:", bufreader.toString());

                    String line;

                    while ((line = bufreader.readLine()) != null) {
                        //Log.d("line:", line);
                        page += line;
                    }

                    JSONObject jsonObject = new JSONObject(page);
                    String results = jsonObject.getString("results");
                    JSONArray jsonArray = new JSONArray(results);

                    JSONObject subJsonObject = jsonArray.getJSONObject(0);
                    //String name = subJsonObject.getString("name");
                    place_id = subJsonObject.getString("place_id");

                    System.out.println("\nnum: " +
                            //"\tname: " + name +
                            "\tid: " + place_id);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                //temp_list.put("TEST", 0);
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


                final String Place_id = place_id;
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                        Map<String, Object> childUpdates= new HashMap<>();
                        HashMap<String, Long> temp_list = new HashMap<>();

                        long num = 0;
                        if (dataSnapshot.child("spot_data/ID_list/").child(Place_id).getValue() != null) {
                            System.out.println("ID check line");
                            num = (long)dataSnapshot.child("spot_data/ID_list/").child(Place_id).child("visit").getValue();

                            myRef.child("spot_data/ID_list/").child(Place_id).child("visit").setValue(num+1);
                            //temp_list.put("visit", num + 1);
                            //childUpdates.put("spot_data/ID_list/" + Place_id, temp_list);
                            //myRef.updateChildren(childUpdates);

                            myRef.child("spot_data/" + region + "/spots/" + Place_id).child("visitor").setValue(num+1);
                            num = (long)dataSnapshot.child("spot_data/" + region + "/spots/" + Place_id).child("visitor_week").getValue();
                            myRef.child("spot_data/" + region + "/spots/" + Place_id).child("visitor_week").setValue(num+1);

                        }
                        else if(dataSnapshot.child("spot_data/temp_list/").child(Place_id).getValue() != null){

                            System.out.println("temp check line : " + dataSnapshot.child("spot_data/temp_list/").child(Place_id).child("count").getValue());
                            System.out.println();
                            num = (long)dataSnapshot.child("spot_data/temp_list/").child(Place_id).child("count").getValue();

                            myRef.child("spot_data/temp_list/").child(Place_id).child("count").setValue(num+1);
                            //temp_list.put("count", num + 1);
                            //childUpdates.put("spot_data/temp_list/" + Place_id, temp_list);
                            //myRef.updateChildren(childUpdates);

                            if(num >= 25){
                                System.out.println("delete");
                                myRef.child("spot_data/temp_list").child(Place_id).removeValue();

                                System.out.println("calculated");
                                ArrayList<String> spot = new ArrayList<>();
                                spot.add(Place_id);

                                SpotFilter sf = new SpotFilter(WalkActivity.this);
                                sf.FeatureCalculator(spot, region);
                            }


                        }
                        else{
                            temp_list.put("count", num);
                            childUpdates.put("spot_data/temp_list/" + Place_id, temp_list);
                            myRef.updateChildren(childUpdates);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        }.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // 팝업에 입력된 데이터 받기
                String noteContent = data.getStringExtra("result");
                // 쪽지를 Firebase에 저장하기
                addNewNote(nickname, prevLat, prevLong, noteContent);

                SharedPreferences countInfo = getSharedPreferences("countInfo", Context.MODE_PRIVATE);
                int lastDate = countInfo.getInt("lastDate", -1); // 가장 마지막으로 쪽지를 남긴 날짜
                int todayCount = countInfo.getInt("todayCount", 0); // 오늘 작성한 쪽지의 갯수

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                int todayDate = Integer.parseInt(dayFormat.format(currentTime)); // 실제 오늘 날짜

                // 새로운 날에 쪽지 작성시, todayCount 초기화
                if(todayDate != lastDate){
                    lastDate = todayDate;
                    todayCount = 0;
                }

                todayCount++;

                // 하루에는 최대 3번만 쪽지로 점수를 벌 수 있다.
                if(todayCount <= 3){
                    Toast.makeText(WalkActivity.this, "쪽지 작성! +10포인트", Toast.LENGTH_LONG).show();
                    totalPoint += 10;
                }

                // 로컬 데이터에 다시 업데이트
                SharedPreferences.Editor editor = countInfo.edit();
                editor.putInt("lastDate", lastDate);
                editor.putInt("todayCount", todayCount);
                editor.commit();
            }
        }
    }

    public void addNewNote(String nickname, double latitude, double longitude, String noteContent){

        SharedPreferences noteNumberInfo = getSharedPreferences("currentNoteNumber", Context.MODE_PRIVATE);
        int currentNoteNumber = noteNumberInfo.getInt("currentNoteNumber", 0);

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        NoteData data = new NoteData(latitude, longitude, noteContent, nickname);
        postValues = data.toMap();
        childUpdates.put("/note_list/" + nickname + "_" + currentNoteNumber + "/", postValues);
        mPostReference.updateChildren(childUpdates);

        currentNoteNumber++;
        if(currentNoteNumber >= maxNoteNumber){
            currentNoteNumber = 0;
        }
        SharedPreferences.Editor editor = noteNumberInfo.edit();
        editor.putInt("currentNoteNumber", currentNoteNumber);
        editor.commit();
    }

    public void findNotes(final double user_latitude, final double user_longitude, final int maxFindNote, final double maxNoteDist){
        mPostReference.child("note_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int curFindNote = 0;

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    NoteData get = postSnapshot.getValue(NoteData.class);

                    // if note is located inside of maxNoteDist's range
                    if(distFrom(get.latitude, get.longitude, user_latitude, user_longitude) < maxNoteDist){

                        drawNoteMarker(get);

                        curFindNote++;
                        if(curFindNote >= maxFindNote){
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void checkNearSpot(double user_lat, double user_lng){
        // except first spot (which is start point)
        for(int i = 1;i<arr_length;i++){
            // if user is nearer than 50m at the point & not visited yet
            if(distFrom(user_lat, user_lng, lats[i], lngs[i]) < 50d && !isVisited[i]){
                isVisited[i] = true;
                // add 100 point!
                totalPoint += 100;

                Toast.makeText(WalkActivity.this, "스팟 도달! +100경험치", Toast.LENGTH_LONG).show();
            }
        }
    }

    // when you need to update the point, use this method
    void updatePoint(String username, final long totalPoint){

        ref = FirebaseDatabase.getInstance().getReference("user_list").child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!isWritten) {
                    user_totalPoint = (long) dataSnapshot.child("level").getValue();
                    ref.child("realWalkTime").setValue(user_totalPoint + totalPoint);
                    isWritten = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    boolean checkIsAlreadyFindLots(){
        SharedPreferences countInfo = getSharedPreferences("countInfo", Context.MODE_PRIVATE);
        // 로컬 데이터에 저장된 가장 마지막으로 쪽지를 찾은 날짜를 가져온다.
        int lastDate = countInfo.getInt("lastLotsFoundDate", -1);

        // 실제 오늘 날짜를 구한다.
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        int todayDate = Integer.parseInt(dayFormat.format(currentTime));

        Toast.makeText(this, todayDate + " " + lastDate, Toast.LENGTH_LONG).show();

        // 새로운 날에 쪽지 작성시, 오늘은 아직 쪽지를 찾지 못했으므로 false를 반환
        if(todayDate != lastDate){
            return false;
        }
        // 이미 오늘 작성한 기록이 로컬 데이터에 남아 있으므로 true를 반환
        else{
            return true;
        }
    }

    void findLots(){

        Toast.makeText(WalkActivity.this, "된다!", Toast.LENGTH_LONG).show();


        // 파이어베이스에 Primary key값으로 저장할 시간을 구한다.
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(date);

        // 파이어베이스에 유저가 뽑기를 찾았음을 기록한다.
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        lotsData data = new lotsData(phoneNumber);
        postValues = data.toMap();
        childUpdates.put("/lots_list/" + time + "/", postValues);
        mPostReference.updateChildren(childUpdates);

        // 실제 오늘 날짜를 구한다.
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        int todayDate = Integer.parseInt(dayFormat.format(currentTime));
        // 뽑기를 찾은 마지막 날짜를 갱신한다.
        SharedPreferences countInfo = getSharedPreferences("countInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = countInfo.edit();
        editor.putInt("lastLotsFoundDate", todayDate);
        editor.commit();

        // 이제 유저는 뽑기를 찾았다.
        isAlreadyFindLots = true;
    }
/*
    void sortSpot(ArrayList<Spot> selected){

        Comparator<Spot> comparator = new Comparator<Spot>() {
            @Override
            public int compare(Spot lhs, Spot rhs) {
                double lhsAngle = Math.atan2(lhs.lng - userlng, lhs.lat - userlat);
                double rhsAngle = Math.atan2(rhs.lng - userlng, rhs.lat - userlat);
                // Depending on the coordinate system, you might need to reverse these two conditions
                if (lhsAngle < rhsAngle) return -1;
                if (lhsAngle > rhsAngle) return 1;
                return 0;
            }
        };

        Collections.sort(selected, comparator);

        ArrayList<TMapPoint> sorted = new ArrayList<>();

        sorted.add(new TMapPoint(userlat, userlng));
        for(Spot s : selected){
            sorted.add(new TMapPoint(s.lat, s.lng));
        }
        sorted.add(new TMapPoint(userlat, userlng));

        for(int i = 0; i<sorted.size() - 1; i++){
            drawMarker(sorted.get(i));
            drawPedestrianPath(sorted.get(i), sorted.get(i+1));
        }

    }

 */


}
