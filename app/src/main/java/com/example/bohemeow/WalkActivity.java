package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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


class Spot {
    String place_id;
    String name;
    int score = 0;
    double lat, lng;

}



public class WalkActivity extends AppCompatActivity implements onLocationChangedCallback {

    PermissionManager permissionManager = null; // 권한요청 관리자
    TMapGpsManager gps = null;
    private TMapView tMapView = null;
    private Context context;

    private TMapPolyLine userRoute = null;
    private double[] lastLatitudes = new double[10];
    private double[] lastLongtitudes = new double[10];
    private int curPos = 0;
    private int lapNum = 0;

    private int polyLineCnt = 0;
    private int markerCnt = 0;

    private boolean isFirstLocation = false;

    private double userlat = 37.2939299;
    private double userlng = 126.9739263;

    private double maxMoveLength = 10f; // 최소 10m는 이동해야 데이터가 저장됨
    private double curMoveLength = 0f; // 파이어베이스에 데이터가 저장되기까지, 현재 얼마나 걸었는가?
    private double prevLat = -1f;
    private double prevLong = -1f;

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

        userRoute = new TMapPolyLine();
        userRoute.setLineColor(Color.RED);
        userRoute.setLineWidth(1);

        /*
        SharedPreferences pref = getSharedPreferences("isGranted", MODE_PRIVATE);
        if(pref.getString("isGranted", "empty").equals("empty")){
            isGranted = false;
        }
        else{
            isGranted = true;
        }
         */

        permissionManager = new PermissionManager(this); // 권한요청 관리자
        permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
            // 허가 시 GPS 화면 출력
            @Override
            public void granted() {
                turnGPSOn();

                gps = new TMapGpsManager(WalkActivity.this);
                gps.setMinTime(100);
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
                startActivity(intent);
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

        makeList();
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

    public void drawMarker(TMapPoint position){

        // get bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.walk_point);
        // resize bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);

        TMapMarkerItem markerItem = new TMapMarkerItem();
        markerItem.setIcon(bitmap); // 마커 아이콘 지정
        markerItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem.setTMapPoint(position); // 마커의 좌표 지정
        markerItem.setName("성대");
        tMapView.addMarkerItem(Integer.toString(markerCnt++), markerItem); // 지도에 마커 추가
    }

    @Override
    public void onLocationChange(Location location) {

        if(!isFirstLocation){
            isFirstLocation = true;
            TMapPoint point = gps.getLocation();
            tMapView.setLocationPoint(point.getLongitude(), point.getLatitude());
            tMapView.setCenterPoint(point.getLongitude(), point.getLatitude());
            Toast.makeText(WalkActivity.this, "Works", Toast.LENGTH_LONG).show();
        }

        lastLatitudes[curPos] = location.getLatitude();
        lastLongtitudes[curPos] = location.getLongitude();

        curPos++;
        if(curPos >= 10){
            curPos = 0;
            lapNum++;
        }

        if(lapNum > 2){

            double latitude = 0f;
            double longtitude = 0f;

            for(int i = 0;i<10;i++){
                latitude += lastLatitudes[i];
                longtitude += lastLongtitudes[i];
            }

            latitude /= 10f;
            longtitude /= 10f;

            tMapView.setLocationPoint(longtitude, latitude);
            tMapView.setCenterPoint(longtitude, latitude);
            userRoute.addLinePoint(new TMapPoint(latitude, longtitude));
            tMapView.addTMapPolyLine("Line1", userRoute);

            if(prevLat != -1f){
                curMoveLength += distFrom(latitude, longtitude, prevLat, prevLong);
                if(maxMoveLength <= curMoveLength){
                    addCoordinationData(latitude, longtitude);
                    curMoveLength = 0f;
                }
            }


            prevLat = latitude;
            prevLong = longtitude;
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
        childUpdates.put("/user_data/hongkildong/", postValues);
        mPostReference.updateChildren(childUpdates);
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
                    //보행자 거리를 사용하는 경우
                    //if (getPedestrianDistance(spotDetail.getLat(), spotDetail.getLng()) < limitDis) {
                    if (distFrom(userlat, userlng, spotDetail.getLat(), spotDetail.getLng()) < limitDis) {
                        System.out.println("spot: " + spotDetail.getName());
                        spots.add(simplifySpot(spotDetail));
                        //System.out.println("\nspots length = " + spots.size());
                    }
                }

                if(spots.size() < num) num = spots.size(); //최종 후보가 원래 뽑으려던 스팟 수보다 적을경우 뽑으려던 수 변경

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


    private int getPedestrianDistance(double lat1, double lng1, double lat2, double lng2) {

        int dis = 0;
        //sample place
        //lat = 37.296067;
        //lng = 126.982378;

        try {

            String uri = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&startName="+ "startPlace" + "&startX=" + lng1 + "&startY=" + lat1 +
                    "&endName=" + "endPlace" + "&endX=" + lng2 + "&endY=" + lat2 +
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
            getJsonObjectTask task = new getJsonObjectTask();

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

    static class getJsonObjectTask extends AsyncTask<URL, Void, JSONObject> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONObject doInBackground(URL... urls) {
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) urls[0].openConnection();
                int response = con.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    //System.out.println("B " + jsonObject);
                    return jsonObject;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                con.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
        }
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


    void chooseSpot(ArrayList<Spot> spots, int num) {
        int limitDis = (min * speed / num) * (2);

        Map<Spot, Integer> temp = new HashMap<Spot, Integer>();
        ArrayList<Spot> selected = new ArrayList<>();

        for (Spot s : spots) {
            temp.put(s, s.score);
        }

        Random rand = new Random();

        System.out.println("\n<Selected>");

        for (int i = 0; i < num; i++) {

            Spot spot = getWeightedRandom(temp, rand);
            System.out.println("\n" + spot.name);

            temp.remove(spot);
            if(getPedestrianDistance(userlat, userlng, spot.lat, spot.lng) > limitDis) {
                i -= 1;
            }
            else selected.add(spot);
            if(temp.size() == 0) break;

        }

            /*
            ArrayList<Integer> dis = new ArrayList<>();
            dis.add(getPedestrianDistance(userlat, userlng, locations.get(0).lat, locations.get(0).lng));
            for (int i = 0; i < num - 1; i++) {
                dis.add(getPedestrianDistance(locations.get(i).lat, locations.get(i).lng, locations.get(i + 1).lat, locations.get(i + 1).lng));
            }
            dis.add(getPedestrianDistance(locations.get(num - 1).lat, locations.get(num - 1).lng, userlat, userlng));

            int totalDis = 0;
            for(int d: dis) totalDis += d;
            if(totalDis <= limitDis) break;
             */


        System.out.println("\n[" + userlat + ", " + userlng + "]");

        for (Spot s : selected) {
            System.out.println("\n[" + s.lat + ", " + s.lng + "]");
        }

        sortSpot(selected);
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
        for(Spot s : selected){
            sorted.add(new TMapPoint(s.lat, s.lng));
        }
        sorted.add(new TMapPoint(userlat, userlng));

        for(int i = 0; i<sorted.size() - 1; i++){
            drawMarker(sorted.get(i));
            drawPedestrianPath(sorted.get(i), sorted.get(i+1));
        }

    }
}
