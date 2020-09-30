package com.example.bohemeow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapData.FindPathDataListenerCallback;
import com.skt.Tmap.TMapData.TMapPathType;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AddSpotActivity extends AppCompatActivity  {

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

    int num;
    double[] lats;
    double[] lngs;
    double startLat, startLng;

    int newnum = 0;
    double newlat, newlng;

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

        walkStart_btn = (Button) findViewById(R.id.walkStart_btn);
        walkStart_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                sortSpot();

                double[] lats2 = {startLat, -1, -1, -1, -1, -1, -1};
                double[] lngs2 = {startLng, -1, -1, -1, -1, -1, -1};
                int i = 1;
                for(location l : locs){
                    lats2[i] = l.lat;
                    lngs2[i] = l.lng;
                    i++;
                }
                lats2[i] = startLat;
                lngs2[i] = startLng;



                Intent intent = new Intent(AddSpotActivity.this, WalkActivity.class);

                intent.putExtra("region", region);
                intent.putExtra("isFree", false);
                intent.putExtra("lats", lats2);
                intent.putExtra("lngs", lngs2);

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


        lats = intent.getDoubleArrayExtra("lats");
        lngs = intent.getDoubleArrayExtra("lngs");

        startLat = lats[0];
        startLng = lngs[0];

        // set screen to start position
        tMapView.setLocationPoint(startLng, startLat);
        tMapView.setCenterPoint(startLng, startLat);

        spots.add(new TMapPoint(startLat, startLng));
        for (int i = 1; lats[i] != -1; i++) {
            spots.add(new TMapPoint(lats[i], lngs[i]));
            locs.add(new location(lats[i], lngs[i]));
        }
        spots.add(new TMapPoint(startLat, startLng));
        num = locs.size();

        for(int i = 0; i < spots.size() - 1; i++){
            drawSpotMarker(spots.get(i));
            drawPedestrianPath(spots.get(i), spots.get(i+1));
        }




        //===========================================================



        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList markerlist,ArrayList poilist, TMapPoint point) {

                newlat = point.getLatitude();
                newlng = point.getLongitude();

                if(newnum >= 3){
                    Toast.makeText(AddSpotActivity.this, "새로운 스팟을 3개 이상 추가할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                else if(num >= 5){
                    Toast.makeText(AddSpotActivity.this, "스팟을 5개 이상 설정할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                else if (distFrom(newlat, newlng, startLat, startLng) < 500) {
                    Toast.makeText(AddSpotActivity.this, "시작 지점에서 너무 가까운 지점입니다.", Toast.LENGTH_LONG).show();
                }
                else if(isNear(newlat, newlng)){
                    Toast.makeText(AddSpotActivity.this, "다른 스팟과 너무 가까운 지점입니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(AddSpotActivity.this, AddSpotPopupActivity.class);
                    intent.putExtra("lat", newlat);
                    intent.putExtra("lng", newlng);
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

                gps = new TMapGpsManager(AddSpotActivity.this);
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
                Toast.makeText(AddSpotActivity.this, "허가 없이는 진행이 불가능합니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddSpotActivity.this, MainMenu.class);

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
                    locs.add(new location(newlat, newlng));
                    drawSpotMarker(new TMapPoint(newlat, newlng));
                    break;
                default:
                    break;
            }
        }
    }


    boolean isNear(double lat, double lng){

        for(location l : locs){
            if(distFrom(lat, lng, l.lat, l.lng) < 300){
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


}
