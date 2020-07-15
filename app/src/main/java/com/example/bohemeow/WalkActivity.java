package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogManager;

public class WalkActivity extends AppCompatActivity implements onLocationChangedCallback {

    PermissionManager permissionManager = null; // 권한요청 관리자
    TMapGpsManager gps = null;
    private TMapView tMapView = null;
    private Context context;
    private boolean isGranted = false;

    private double userlat = 37.2939299;
    private double userlng = 126.9739263;

    private int min = 60; //소요 시간
    private int speed = 60; //보행자 속도

    int num = 1 + min / 30; //선택할 스팟 수

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

        makeList();
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

    //=================================================================================




    public void SpotSelector() {

        System.out.println("\nprint: Start");
        makeList();

    }

    public interface Callback{
        void success(DataSnapshot data);
        void fail(String errorMessage);
    }

    //=========================================================================================
    private void makeList(){
        System.out.println("\nprint: makeList start");
        final int limitDis = (min * speed) / 2;

        final String city = "Suwon-si";

        final ArrayList<SpotDetail> spotDetails = new ArrayList<>();
        final ArrayList<Spot> spots= new ArrayList<>();
        final Spot spot = new Spot();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseReference.child("spot_data").child(city).child("spots");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    SpotDetail spotDetail = postSnapshot.getValue(SpotDetail.class);
                    if (getDistance(spotDetail.getLat(), spotDetail.getLng()) < limitDis) {
                        System.out.println("spot: " + spotDetail.getName());
                        spots.add(simplifySpot(spotDetail));
                        //System.out.println("\nspots length = " + spots.size());
                    }
                }

                chooseSpot(spots, num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

/*
        final Callback callback = new Callback() {
            @Override
            public void success(DataSnapshot data) {
                for (DataSnapshot postSnapshot: data.getChildren()) {
                    ArrayList<SpotDetail> spotDetails = new ArrayList<>();
                    spotDetails.add(postSnapshot.getValue(SpotDetail.class));
                }
            }
            @Override
            public void fail(String errorMessage) {
            }
        };
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.success(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                callback.fail(error.getMessage());
            }
        });

        for(SpotDetail spotDetail: spotDetails){
            if (getDistance(spotDetail.getLat(), spotDetail.getLng()) < limitDis) {
                System.out.println("spot: " + spotDetail.getName());
                spots.add(simplifySpot(spotDetail));
            }
        }
        System.out.println("\nspots length = " + spots.size());
        chooseSpot(spots, num);
 */

    }


    private int getDistance(final double lat, final double lng) {

        int dis = 0;
        //sample place
        //lat = 37.296067;
        //lng = 126.982378;

        try {

            String uri = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&startName="+ "startPlace" + "&startX=" + userlng + "&startY=" + userlat +
                    "&endName=" + "endPlace" + "&endX=" + lng + "&endY=" + lat +
                    "&format=json&appkey=l7xxc4527e777ef245ef932b366ccefaa9b0";

            String page = "";
            URL url = new URL(uri);
            /*
            URLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            //Log.d("line:", bufreader.toString());
            String line = null;
            while ((line = bufreader.readLine()) != null) {
                //Log.d("line:", line);
                page += line;
            }
             */

            //JSONObject jsonObject = new JSONObject(page);
            MakeRoute.getJsonObjectTask task = new MakeRoute.getJsonObjectTask();

            JSONObject jsonObject = task.execute(url).get();
            //System.out.println("\nC : " + jsonObject);
            String features = jsonObject.getString("features");
            JSONArray jsonArray = new JSONArray(features);
            JSONObject subJsonObject = jsonArray.getJSONObject(0);
            String properties = subJsonObject.getString("properties");
            JSONObject subJsonObject2 = new JSONObject(properties);
            dis = Integer.parseInt(subJsonObject2.getString("totalDistance"));
            //System.out.println("\nD distance : " + dis);



        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e){ //나중에 꼭 고치기. 원인 못찾음. 발생하는 상황도 예측 불가.
            //e.printStackTrace();
            dis = 10000;
        }
        return dis;
    }

    private Spot simplifySpot(SpotDetail spotDetail) {
        Spot spot = new Spot();
        //System.out.println("\nName : " + spotDetail.getName());

        spot.place_id = spotDetail.getPlace_id();
        spot.name = spotDetail.getName();
        spot.lat = spotDetail.getLat();
        spot.lng = spotDetail.getLng();
        spot.score = calculateScore(spotDetail);

        //System.out.println("\tScore: " + spot.score);
        return spot;
    }

    private int calculateScore(SpotDetail spotDetail){

        int score = 0;
        //유저 성향에 따른 가중치 계산 공식 후에 반영할것
        score += spotDetail.getEnvi_score();
        score += spotDetail.getSafe_score();
        score += spotDetail.getUser_score();
        score += spotDetail.getPopularity();

        return score;
    }

    //------------------------------------------------------------------------------------------


    void chooseSpot(ArrayList<Spot> spots, int num){

        Map<Spot, Integer> temp = new HashMap<Spot, Integer>();
        ArrayList<com.example.bohemeow.Location> locations = new ArrayList<>();


        for (Spot s : spots){
            temp.put(s, s.score);
        }

        Random rand = new Random();

        System.out.println("\n<Selected>");

        for(int i = 0; i < num; i++){
            Spot spot = getWeightedRandom(temp, rand);
            System.out.println("\n" + spot.name);

            com.example.bohemeow.Location location = new com.example.bohemeow.Location();
            location.lat = spot.lat;
            location.lng = spot.lng;
            locations.add(location);

            temp.remove(spot);
        }

        System.out.println("\n[" + userlat + ", " + userlng + "]");

        for(com.example.bohemeow.Location location : locations){
            System.out.println("\n[" + location.lat + ", " + location.lng + "]");
        }

        sortSpot(locations);

    }

    private static <Spot> Spot getWeightedRandom(Map<Spot, Integer> weights, Random random) {
        Spot result = null;
        double bestValue = Double.MAX_VALUE;

        for (Spot element : weights.keySet()) {
            double value = -Math.log(random.nextDouble()) / weights.get(element);
            if (value < bestValue) {
                bestValue = value;
                result = element;
            }
        }
        return result;
    }

    //------------------------------------------------------------------


    void sortSpot(ArrayList<com.example.bohemeow.Location> locations){

        Comparator<com.example.bohemeow.Location> comparator = new Comparator<com.example.bohemeow.Location>() {
            @Override
            public int compare(com.example.bohemeow.Location lhs, com.example.bohemeow.Location rhs) {
                double lhsAngle = Math.atan2(lhs.lng - userlng, lhs.lat - userlat);
                double rhsAngle = Math.atan2(rhs.lng - userlng, rhs.lat - userlat);
                // Depending on the coordinate system, you might need to reverse these two conditions
                if (lhsAngle < rhsAngle) return -1;
                if (lhsAngle > rhsAngle) return 1;
                return 0;
            }
        };
        Collections.sort(locations, comparator);
/*
        double[][] sorted = new double[num+2][2];


        sorted[0][0] = userlat;
        sorted[0][1] = userlng;

        for(int i = 0; i < num; i++){
            sorted[i + 1][0] = locations.get(i).lat;
            sorted[i + 1][1] = locations.get(i).lng;
        }

        sorted[num+1][0] = userlat;
        sorted[num+1][1] = userlng;


 */

        ArrayList<TMapPoint> sorted = new ArrayList<>();

        sorted.add(new TMapPoint(userlat, userlng));
        for(com.example.bohemeow.Location loc : locations){
            sorted.add(new TMapPoint(loc.lat, loc.lng));
        }
        sorted.add(new TMapPoint(userlat, userlng));

        for(int i = 0; i<sorted.size() - 1; i++){
            drawMarker(sorted.get(i));
            drawPedestrianPath(sorted.get(i), sorted.get(i+1));
        }




    }
}
