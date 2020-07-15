package com.example.bohemeow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapData.TMapPathType;
import com.skt.Tmap.TMapData.FindPathDataListenerCallback;

import java.util.logging.LogManager;

public class WalkActivity extends AppCompatActivity implements onLocationChangedCallback {

    PermissionManager permissionManager = null; // 권한요청 관리자
    TMapGpsManager gps = null;
    private TMapView tMapView = null;
    private Context context;
    private boolean isGranted = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionManager.setResponse(requestCode, grantResults); // 권한요청 관리자에게 결과 전달
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        context = this;

        // get intent
        Intent intent = getIntent();

        // set t map view
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxc4527e777ef245ef932b366ccefaa9b0");
        linearLayoutTmap.addView( tMapView );
        tMapView.setIconVisibility(true);
        tMapView.setTrackingMode(true);

        // 사용자가 위치 정보 허가 여부를 이전에 허가했는지 안했는지 확인
        SharedPreferences pref = getSharedPreferences("isGranted", MODE_PRIVATE);
        if(pref.getString("isGranted", "empty") == "empty"){
            isGranted = false;
        }
        else{
            isGranted = true;
        }

        // 아직 허가 안했을 경우
        if(!isGranted){
            permissionManager = new PermissionManager(this); // 권한요청 관리자
            permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
                // 허가 시 GPS 화면 출력
                @Override
                public void granted() {
                    gps = new TMapGpsManager(WalkActivity.this);
                    gps.setMinTime(1000);
                    gps.setMinDistance(5);
                    gps.setProvider(gps.GPS_PROVIDER);
                    gps.OpenGps();
                    gps.setProvider(gps.NETWORK_PROVIDER);
                    gps.OpenGps();

                    // 위치 정보 제공을 허가했음을 로컬 데이터에 저장
                    SharedPreferences pref = getSharedPreferences("isGranted",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("isGranted", "isGranted");
                    editor.apply();
                }

                // 허가하지 않을 경우 토스트 메시지와 함께 메인 메뉴로 돌려보낸다
                @Override
                public void denied() {
                    Toast.makeText(WalkActivity.this, "허가 없이는 화면을 출력할 수 없습니다.", Toast.LENGTH_LONG);
                    Intent intent = new Intent(WalkActivity.this, MainMenu.class);
                    startActivity(intent);
                }
            });
        }
        // 이미 허가했을 경우
        else{
            gps = new TMapGpsManager(WalkActivity.this);
            gps.setMinTime(1000);
            gps.setMinDistance(5);
            gps.setProvider(gps.GPS_PROVIDER);
            gps.OpenGps();
            gps.setProvider(gps.NETWORK_PROVIDER);
            gps.OpenGps();
        }

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

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, startPoint, endPoint, new FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.BLUE);
                tMapView.addTMapPath(polyLine);
            }
        });
    }

    public void drawMarker(TMapPoint position){
        // get bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.main_cat_scaratch);
        // resize bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);

        TMapMarkerItem markerItem1 = new TMapMarkerItem();
        markerItem1.setIcon(bitmap); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint(position); // 마커의 좌표 지정
        markerItem1.setName("성대");
        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
    }

    @Override
    public void onLocationChange(Location location) {
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
    }
}
