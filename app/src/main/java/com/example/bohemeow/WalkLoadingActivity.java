package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.Random;

public class WalkLoadingActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    PermissionManager permissionManager = null; // 권한요청 관리자

    private TMapView tMapView = null;
    TMapGpsManager gps = null;
    private double userlat;
    private double userlng;
    private boolean isFirstLocation = false;

    TextView loadingText;

    long waitingTime = 3000;

    int[] preference = new int[3];//0:safe 1:envi 2:popularity

    private String[] loadingTexts = {
            "신발끈 동여매는 중",
            "구름의 동향을 살피는 중",
            "물 한 모금 마시는 중",
            "수염 닦아내는 중",
            "동서남북을 확인하는 중"
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionManager.setResponse(requestCode, grantResults); // 권한요청 관리자에게 결과 전달
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_loading);

        // get intent
        Intent intent = getIntent();
        preference = intent.getIntArrayExtra("preference");

        // choose loading text randomly
        loadingText = findViewById(R.id.loadingText);
        Random random = new Random();
        loadingText.setText(loadingTexts[random.nextInt(loadingTexts.length)]);

        permissionManager = new PermissionManager(this); // 권한요청 관리자
        permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
            // 허가 시 GPS 화면 출력
            @Override
            public void granted() {

                turnGPSOn();
                gps = new TMapGpsManager(WalkLoadingActivity.this);
                gps.setMinTime(100);
                gps.setMinDistance(0.1f);
                gps.setProvider(TMapGpsManager.GPS_PROVIDER);
                gps.OpenGps();
                gps.setProvider(TMapGpsManager.NETWORK_PROVIDER);
                gps.OpenGps();

                /*
                tMapView.setIconVisibility(true);
                tMapView.setTrackingMode(true);
                */
            }

            // 허가하지 않을 경우 토스트 메시지와 함께 메인 메뉴로 돌려보낸다
            @Override
            public void denied() {
                Toast.makeText(WalkLoadingActivity.this, "허가 없이는 진행이 불가능합니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WalkLoadingActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        // change text every X second
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                Random random = new Random();
                loadingText.setText(loadingTexts[random.nextInt(loadingTexts.length)]);
                handler.postDelayed(this, delay);
            }
        }, delay);
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

    public void onLocationChange(Location location) {

        if(!isFirstLocation){
            isFirstLocation = true;
            TMapPoint point = gps.getLocation();
            userlat = point.getLatitude();
            userlng = point.getLongitude();
            //Toast.makeText(WalkLoadingActivity.this, userlat + " " + userlng, Toast.LENGTH_LONG).show();

            Handler hd = new Handler();
            hd.postDelayed(new WalkLoadingActivity.splashHandler(), waitingTime); // 3초후 splashHandler 호출됨
        }
    }

    private class splashHandler implements Runnable{
        public void run(){
            Intent intent = new Intent(WalkLoadingActivity.this, WalkActivity.class);
            // 일단은 이 부분을 넣지 않으면 WalkActivity에서 초기화되지 않은 preference를 참조하면서 crash가 발생함. 이를 방지하고자 이 코드를 넣었음.
            intent.putExtra("preference", preference);
            startActivity(intent);
            WalkLoadingActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}