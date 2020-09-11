package com.example.bohemeow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;


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

    Button walkEndBtn;

    private TMapPolyLine userRoute = null;
    private double[] lastLatitudes = new double[10];
    private double[] lastLongtitudes = new double[10];
    private int curPos = 0;
    private int lapNum = 0;

    private int polyLineCnt = 0;
    private int markerCnt = 0;

    private boolean isFirstLocation = false;

    private double userlat;
    private double userlng;
    String region = "";

    private double maxMoveLength = 10f; // 최소 10m는 이동해야 데이터가 저장됨
    private double curMoveLength = 0f; // 파이어베이스에 데이터가 저장되기까지, 현재 얼마나 걸었는가?
    private double prevLat = -1f;
    private double prevLong = -1f;
    private int moveCnt = 0;

    private int min = 60; //소요 시간
    private int speed = 60; //보행자 속도

    int num = 1 + min / 30; //선택할 스팟 수

    int[] preference = new int[3];//0:safe 1:envi 2:popularity

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

        // get intent
        Intent intent = getIntent();
        preference = intent.getIntArrayExtra("preference");

        walkEndBtn = (Button) findViewById(R.id.walkEndBtn);
        walkEndBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalkActivity.this, WalkEndActivity.class);
                startActivity(intent);
            }
        });

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
        markerItem.setPosition(0.5f, 1.0f);
        // 마커의 중심점을 중앙, 하단으로 설정
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

            userlat = point.getLatitude();
            userlng = point.getLongitude();
            getRegion(userlat, userlng);
            //makeList();
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
                    addCoordinationID(latitude, longtitude);
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

                            if(num >= 30){
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

    void getRegion(final double latitude, final double longtitude){
        //final String[] region = {""};

        new Thread() {
            public void run() {

                String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude +"," + longtitude +
                        "&language=ko&location_type=ROOFTOP&key=" + key;

                String page = "";
                try {
                    URL url = new URL(uri);
                    URLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    Log.d("line:", bufreader.toString());
                    System.out.println("\ncheckpoint");
                    String line;

                    while ((line = bufreader.readLine()) != null) {
                        Log.d("line:", line);
                        page += line;
                    }

                    JSONObject jsonObject = new JSONObject(page);
                    String results = jsonObject.getString("results");
                    JSONArray jsonArray = new JSONArray(results);

                    String address_components = jsonArray.getJSONObject(0).getString("address_components");
                    JSONArray addJsonArray = new JSONArray(address_components);

                    boolean isSuccess = false;
                    for (int i = 0; (i < addJsonArray.length()) && !isSuccess; i++) {

                        JSONObject subJsonObject = addJsonArray.getJSONObject(i);
                        String types = subJsonObject.getString("types");
                        JSONArray typJsonArray = new JSONArray(types);
                        for (int j = 0; j < typJsonArray.length(); j++) {
                            if(typJsonArray.optString(j).equals("locality")){
                                region = subJsonObject.getString("short_name");
                                setRegion(region);
                                isSuccess = true;
                                break;
                            }
                        }
                        if(!isSuccess){
                            for (int j = 0; j < typJsonArray.length(); j++) {
                                if(typJsonArray.optString(j).equals("administrative_area_level_1")){
                                    region = subJsonObject.getString("short_name");

                                    String[] Do = new String[] {"경상남도", "경상북도", "충청남도", "충청북도", "전라남도", "전라북도", "경기도", "강원도"};
                                    for (String d : Do){
                                        if(region.equals(d)) {
                                            JSONObject subJsonObject2 = addJsonArray.getJSONObject(i-1);
                                            region = subJsonObject2.getString("short_name");
                                            setRegion(region);
                                            isSuccess = true;
                                            break;
                                        }
                                    }
                                    if(!isSuccess) {
                                        setRegion(region);
                                        break;
                                    }
                                }
                            }

                        }

                    }

                    System.out.println("\nregion: " + region);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    void setRegion(final String region){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("spot_data/").child(region).getValue() == null) {
                    System.out.println("\nnew region");

                    Intent intent = new Intent(WalkActivity.this, SpotSearcher.class);
                    intent.putExtra("Region", region);
                    startActivity(intent);
                }

                makeList();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    //=========================================================================================
    private void makeList(){
        System.out.println("\nprint: makeList start");
        final int limitDis = (min * speed) / 2;


        final ArrayList<SpotDetail> spotDetails = new ArrayList<>();
        final ArrayList<Spot> spots= new ArrayList<>();


        //region = getRegion(userlat, userlng);
        //region = region + "-si";
        //System.out.println("\nRegion: " + region);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseReference.child("spot_data").child(region).child("spots");
        //DatabaseReference myRef = databaseReference.child("spot_data").child(getRegion(userlat, userlng)+"-si").child("spots");

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

                if(num > 0) chooseSpot(spots, num);
                else System.out.println("\nThere are no spot in this place!");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private int getPedestrianDistance(double lat1, double lng1, double lat2, double lng2) {

        int dis = 0;

        try {

            Thread.sleep(500);
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
            System.out.println("\nC : " + jsonObject);
            String features = jsonObject.getString("features");
            JSONArray jsonArray = new JSONArray(features);
            JSONObject subJsonObject = jsonArray.getJSONObject(0);
            String properties = subJsonObject.getString("properties");
            JSONObject subJsonObject2 = new JSONObject(properties);
            dis = Integer.parseInt(subJsonObject2.getString("totalDistance"));
            System.out.println("\nD distance : " + dis);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e){ //나중에 꼭 고치기. 원인 못찾음. 발생하는 상황도 예측 불가.
            e.printStackTrace();
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
        score += spotDetail.getSafe_score() * preference[0];
        score += spotDetail.getEnvi_score() * preference[1];
        score += spotDetail.getUser_score();
        score += spotDetail.getPopularity() * preference[2];

        return score;
    }


    //------------------------------------------------------------------------------------------


    void chooseSpot(ArrayList<Spot> spots, int num) {
        int limitDis = (min * speed / num) * (2);

        Map<Spot, Integer> temp = new HashMap<>();
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
