package com.example.bohemeow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapData.FindPathDataListenerCallback;
import com.skt.Tmap.TMapData.TMapPathType;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;


public class SelectSpotActivity extends AppCompatActivity  {

    private DatabaseReference mPostReference;

    PermissionManager permissionManager = null; // 권한요청 관리자
    TMapGpsManager gps = null;
    private TMapView tMapView = null;
    private Context context;

    Button walkStart_btn;
    Button popupBtn;

    private int polyLineCnt = 0;
    private int markerCnt = 0;

    String region = "";
    String sub_region;

    double startLat, startLng;
    int num = 0;
    int newnum = 0;
    double curLat, curLng;
    String markerID;

    boolean isJumped = false;

    ArrayList<TMapPoint> spots = new ArrayList<>();
    ArrayList<location> locs = new ArrayList<>();

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
        setContentView(R.layout.activity_walk_addspot);
        context = this;

        mPostReference = FirebaseDatabase.getInstance().getReference();

        // get intent
        Intent intent = getIntent();
        //preference = intent.getIntArrayExtra("preference");
        region = intent.getStringExtra("region");
        sub_region = intent.getStringExtra("sub_region");

        walkStart_btn = (Button) findViewById(R.id.walkStart_btn);
        walkStart_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                sortSpot();

                double[] lats = {-1, -1, -1, -1, -1, -1, -1};
                double[] lngs = {-1, -1, -1, -1, -1, -1, -1};
                if(isJumped){ //출발지가 삭제된 경우 남은 스팟들로만 계산
                    int i = 0;
                    for(location l : locs){
                        lats[i] = l.lat;
                        lngs[i] = l.lng;
                        i++;
                    }
                    lats[i] = lats[0];
                    lngs[i] = lngs[0];
                }
                else{ //출발지가 보존된 경우 원래대로 경로 계산
                    lats[0] = startLat;
                    lngs[0] = startLng;
                    int i = 1;
                    for(location l : locs){
                        lats[i] = l.lat;
                        lngs[i] = l.lng;
                        i++;
                    }
                    lats[i] = startLat;
                    lngs[i] = startLng;
                }

                ArrayList<Double> lastLats = new ArrayList<>();
                ArrayList<Double> lastLngs = new ArrayList<>();
                for(int j = 0; lats[j] != -1 && j <7; j++){
                    lastLats.add(lats[j]);
                    lastLngs.add(lngs[j]);
                }

                String key = checkRecord();
                recordArray(key + "lats", sub_region, lastLats);
                recordArray(key + "lngs", sub_region, lastLngs);

                Intent intent = new Intent(SelectSpotActivity.this, WalkActivity.class);
                intent.putExtra("lats", lats);
                intent.putExtra("lngs", lngs);
                startActivity(intent);
                finish();
            }
        });

        /*
        popupBtn = (Button) findViewById(R.id.popupBtn);
        popupBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
            }
        });

         */

        // set t map view
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxc4527e777ef245ef932b366ccefaa9b0");
        linearLayoutTmap.addView( tMapView );

        startLat = intent.getDoubleExtra("lat", 0);
        startLng = intent.getDoubleExtra("lng", 0);

        // set screen to start position
        tMapView.setLocationPoint(startLng, startLat);
        tMapView.setCenterPoint(startLng, startLat);

        TMapPoint startPoint = new TMapPoint(startLat, startLng);

        drawSpotMarker(startPoint,  R.drawable.point_start);
        drawCircle(startPoint);


        //===========================================================

        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList markerlist,ArrayList poilist, TMapPoint point, PointF pointf) {
                return false;
            }

            @Override
            public boolean onPressEvent(ArrayList markerlist, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF) {
                if(!markerlist.isEmpty()){
                    //Log.e("MARKER ID : ", ""+ markerlist.get(0));
                    TMapMarkerItem markerItem = (TMapMarkerItem) markerlist.get(0);
                    markerID = markerItem.getID();
                    TMapPoint point = markerItem.getTMapPoint();
                    curLat = point.getLatitude();
                    curLng = point.getLongitude();

                    Intent intent = new Intent(SelectSpotActivity.this, DelSpotPopupActivity.class);
                    intent.putExtra("lat", curLat);
                    intent.putExtra("lng", curLng);
                    startActivityForResult(intent, 1);
                    //System.out.println("\nID: " + MarkerID + ", loc = " + p.getLongitude() + p.getLatitude());


                }
                return false;
            }
        });


        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList markerlist,ArrayList poilist, TMapPoint point) {

                curLat = point.getLatitude();
                curLng = point.getLongitude();
/*
                if(newnum >= 3){
                    Toast.makeText(SelectSpotActivity.this, "새로운 스팟을 3개 이상 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                else if(num >= 5){
                    Toast.makeText(SelectSpotActivity.this, "스팟을 5개 이상 설정할 수 없습니다.", Toast.LENGTH_LONG).show();
                }

 */
                if(num >= 4){
                    Toast.makeText(SelectSpotActivity.this, "스팟을 4개 이상 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                else if (distFrom(curLat, curLng, startLat, startLng) < 400 && !isJumped) {
                    Toast.makeText(SelectSpotActivity.this, "시작 지점에서 너무 가까운 지점입니다.", Toast.LENGTH_LONG).show();
                }
                else if(isNear(curLat, curLng)){
                    Toast.makeText(SelectSpotActivity.this, "다른 스팟과 너무 가까운 지점입니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(SelectSpotActivity.this, SelectSpotPopupActivity.class);
                    intent.putExtra("lat", curLat);
                    intent.putExtra("lng", curLng);
                    startActivityForResult(intent, 1);
                }



            }
        });


        permissionManager = new PermissionManager(this); // 권한요청 관리자
        permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
            // 허가 시 GPS 화면 출력
            @Override
            public void granted() {
                turnGPSOn();

                gps = new TMapGpsManager(SelectSpotActivity.this);
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
                Toast.makeText(SelectSpotActivity.this, "허가 없이는 진행이 불가능합니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectSpotActivity.this, MainMenu.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

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


    public void drawSpotMarker(TMapPoint position, int marker){

        // get bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), marker);
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

    public void drawCircle(TMapPoint position){

        TMapCircle tMapCircle = new TMapCircle();
        tMapCircle.setCenterPoint(position);
        tMapCircle.setRadius(400);
        tMapCircle.setCircleWidth(2);
        tMapCircle.setLineColor(Color.BLUE);
        tMapCircle.setAreaColor(Color.GRAY);
        tMapCircle.setAreaAlpha(100);
        tMapView.addTMapCircle("circle1", tMapCircle);

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






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            switch(resultCode){
                case RESULT_OK: // result_ok -> nothing happened
                    break;
                case 1:
                    newnum += 1;
                    num += 1;
                    locs.add(new location(curLat, curLng));
                    drawSpotMarker(new TMapPoint(curLat, curLng), R.drawable.walk_point);
                    break;
                case 2:
                    num -= 1;
                    if(markerID.equals("0")) {
                        isJumped = true;
                    }
                    else {
                        for(location l:locs){
                            if(l.lat == curLat && l.lng == curLng){
                                locs.remove(l);
                                System.out.println("\nDeleted");
                                break;
                            }
                        }
                    }
                    tMapView.removeMarkerItem(markerID);
                    break;
                default:
                    break;
            }
        }
    }


    boolean isNear(double lat, double lng){

        for(location l : locs){
            if(distFrom(lat, lng, l.lat, l.lng) < 400){
                return true;
            }
        }

        return false;
    }



    void sortSpot(){
        Comparator<location> comparator = new Comparator<location>() {
            @Override
            public int compare(location lhs, location rhs) {
                double lhsAngle = Math.atan2(lhs.lng - startLng, lhs.lat - startLat);
                double rhsAngle = Math.atan2(rhs.lng - startLng, rhs.lat - startLat);
                // Depending on the coordinate system, you might need to reverse these two conditions
                if (lhsAngle < rhsAngle) return -1;
                if (lhsAngle > rhsAngle) return 1;
                return 0;
            }
        };

        Collections.sort(locs, comparator);

    }

    private void recordArray(String key, String sub_region, ArrayList<Double> values) {
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        JSONArray a = new JSONArray();
        a.put(sub_region);

        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Date d = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(tz);
        String date = df.format(d);

        a.put(date);
        a.put(Integer.toString(values.size() - 2));
        for (int i = 0; i < values.size(); i++) {
            a.put(Double.toString(values.get(i)));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }

    public String checkRecord(){
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        if(registerInfo.getString("R1_lats", null) == null){
            return "R1_";
        }
        else if(registerInfo.getString("R2_lats", null) == null){
            return "R2_";
        }
        else if(registerInfo.getString("R3_lats", null) == null){
            return "R3_";
        }
        else{
            return "R1_";
        }
    }

}
