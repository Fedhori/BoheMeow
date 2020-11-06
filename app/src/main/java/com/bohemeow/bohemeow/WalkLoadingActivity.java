package com.bohemeow.bohemeow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

class Spot {
    String place_id;
    String name;
    int score = 0;
    double lat, lng;
}

public class WalkLoadingActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    PermissionManager permissionManager = null; // 권한요청 관리자

    private TMapView tMapView = null;
    TMapGpsManager gps = null;
    private double userlat;
    private double userlng;
    private boolean isFirstLocation = false;

    LocationManager locationManager;

    TextView loadingText;

    long textChangeSpan = 2000; // ms
    Handler handler = new Handler();
    Runnable runnable;

    String region = "";
    int num; //선택할 스팟 수
    private int min; //소요 시간
    private int speed = 50; //보행자 속도

    int walkType;

    int[] preference = new int[3];//0:safe 1:envi 2:popularity

    String key = "AIzaSyBHSgVqZUvi8EmRbrZsH9z6whHSO-R3LXo"; // google key


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
        min = intent.getIntExtra("time", 60);
        preference = intent.getIntArrayExtra("preference");
        walkType = intent.getIntExtra("walkType", 3);

        num = 1 + min / 30;
        if(num > 5) num = 5;
        setNum();

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
    }

    void setNum(){

        if(min <= 40) num = 1;
        else if(min <= 70) num = 2;
        else if(min <= 120) num = 3;
        else if (min < 160) num = 4;
        else num = 5;

    }

    void setDistance(){

    }

    private void turnGPSOn(){

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

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

            SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = registerInfo.edit();
            editor.putFloat("lastLat", (float)userlat);
            editor.putFloat("lastLng", (float)userlng);
            editor.commit();

            getRegion(userlat, userlng);
        }
    }


    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                Random random = new Random();
                loadingText.setText(loadingTexts[random.nextInt(loadingTexts.length)]);

                if(locationManager != null){
                    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        Toast.makeText(WalkLoadingActivity.this, "허가 없이는 진행이 불가능합니다.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(WalkLoadingActivity.this, MainMenu.class);
                        startActivity(intent);
                    }
                }
                else{
                    Log.w("qwr", "YEAH!");
                }
                handler.postDelayed(runnable, textChangeSpan);
            }
        }, textChangeSpan);

        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    String sub_region;
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
                            if(typJsonArray.optString(j).equals("sublocality_level_2")){
                                sub_region = subJsonObject.getString("short_name");
                            }
                            if(typJsonArray.optString(j).equals("locality")){
                                region = subJsonObject.getString("short_name");
                                setRegion(region, sub_region);
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
                                            setRegion(region, sub_region);
                                            isSuccess = true;
                                            break;
                                        }
                                    }
                                    if(!isSuccess) {
                                        setRegion(region, sub_region);
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

    boolean isExist = false;
    boolean isDone = false;

    void setRegion(final String region, String sub_region){

        if(walkType <= 3){
            System.out.println("\ntype:1");
            String key = "R" + walkType;
            double[] lats = getArray(key + "_lats");
            double[] lngs = getArray(key + "_lngs");
            if(lats[0] != -1){
                Intent intent = new Intent(WalkLoadingActivity.this, WalkActivity.class);
                intent.putExtra("region", region);
                intent.putExtra("lats", lats);
                intent.putExtra("lngs", lngs);
                startActivity(intent);
                WalkLoadingActivity.this.finish();
                isDone = true;
            }
        }
        else if(walkType == 5){
            System.out.println("\ntype:3");
            Intent intent = new Intent(WalkLoadingActivity.this, SelectSpotActivity.class);
            intent.putExtra("lat", userlat);
            intent.putExtra("lng", userlng);
            intent.putExtra("region", region);
            intent.putExtra("sub_region", sub_region);
            startActivity(intent);
            WalkLoadingActivity.this.finish();
            isDone = true;
        }



        if(!isDone) {
            System.out.println("\ntype:2");
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                    if (dataSnapshot.child("spot_data/").child(region).getValue() == null) {
                        System.out.println("\n\n================\nnew region");

                        SpotSearcher searcher = new SpotSearcher(WalkLoadingActivity.this);
                        searcher.Search(region);
                        //isExist = true;
                        //SpotSearcher.Search(region);
                    } else {
                        isDone = true;
                        makeList();
                    }

                    if (!isDone) {
                        final Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isExist) {
                                    checkDB(region);
                                    System.out.println("\n\n===============\ncheck region\n");
                                    handler2.postDelayed(this, 10000);
                                } else {
                                    System.out.println("\n\n===============");
                                    makeList();
                                }

                            }
                        }, 120000);
                    }

                    //makeList();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


    }



    void checkDB(final String region){

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                //boolean isExist = false;
                if (dataSnapshot.child("spot_data/").child(region).getValue() != null) {
                    isExist = true;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    //=========================================================================================

    int limitDis;

    private void makeList(){
        System.out.println("\nprint: makeList start");

        limitDis = (min * speed) / 3 + 100;
        if (limitDis > 5000) limitDis = 5000;


        final ArrayList<SpotDetail> spotDetails = new ArrayList<>();
        final ArrayList<Spot> spots= new ArrayList<>();


        //region = getRegion(userlat, userlng);
        //region = region + "-si";
        //System.out.println("\nRegion: " + region);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = databaseReference.child("spot_data").child(region).child("spots");
        //DatabaseReference myRef = databaseReference.child("spot_data").child(getRegion(userlat, userlng)+"-si").child("spots");


        System.out.println("\nprint: makeList continue...");

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

                if(spots.size() < num) {
                    num = spots.size(); //최종 후보가 원래 뽑으려던 스팟 수보다 적을경우 뽑으려던 수 변경
                    System.out.println("\nchanged num : " + num);
                }

                if(num > 0) chooseSpot(spots, num);
                else {
                    System.out.println("\nThere are no spot in this place!");
                    Toast.makeText(WalkLoadingActivity.this, "가능한 스팟이 존재하지 않습니다.\n 원하시는 장소를 선택하세요!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(WalkLoadingActivity.this, SelectSpotActivity.class);
                    // 일단은 이 부분을 넣지 않으면 WalkActivity에서 초기화되지 않은 preference를 참조하면서 crash가 발생함. 이를 방지하고자 이 코드를 넣었음.
                    //intent.putExtra("preference", preference);
                    intent.putExtra("region", region);
                    intent.putExtra("lat", userlat);
                    intent.putExtra("lng", userlng);
                    //intent.putExtra("spots", sorted);
                    startActivity(intent);
                    WalkLoadingActivity.this.finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
        //score += spotDetail.getPopularity() * preference[2];
        score += spotDetail.getVisitor() * preference[2];

        return score;
    }


    //------------------------------------------------------------------------------------------


    void chooseSpot(ArrayList<Spot> spots, int num) {
        int limit = (min * speed / num * 3) * 2;

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
            if(getPedestrianDistance(userlat, userlng, spot.lat, spot.lng) > limit) {
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

        double[] lats = {userlat, -1, -1, -1, -1, -1, -1};
        double[] lngs = {userlng, -1, -1, -1, -1, -1, -1};
        int i = 1;
        for(Spot s : selected){
            lats[i] = s.lat;
            lngs[i] = s.lng;
            i++;
        }
        lats[i] = userlat;
        lngs[i] = userlng;

        ArrayList<Double> lastLats = new ArrayList<>();
        ArrayList<Double> lastLngs = new ArrayList<>();
        for(int j = 0; lats[j] != -1 && j <7; j++){
            lastLats.add(lats[j]);
            lastLngs.add(lngs[j]);
        }

        String key = checkRecord();
        recordArray(key + "lats", sub_region, lastLats);
        recordArray(key + "lngs", sub_region, lastLngs);
/*
        ArrayList<location> sorted = new ArrayList<>();

        location loc = new location(userlat, userlng);
        sorted.add(loc);
        for(Spot s : selected){
            loc = new location(s.lat, s.lng);
            sorted.add(loc);
        }
        loc = new location(userlat, userlng);
        sorted.add(loc);
*/
        Intent intent = new Intent(WalkLoadingActivity.this, WalkActivity.class);
        intent.putExtra("region", region);
        intent.putExtra("lats", lats);
        intent.putExtra("lngs", lngs);

        startActivity(intent);
        WalkLoadingActivity.this.finish(); // 로딩페이지 Activity stack에서 제거

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

    private double[] getArray(String key) {
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        String json = registerInfo.getString(key, null);
        double[] urls = {-1, -1, -1, -1, -1, -1, -1};
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 3; i < a.length(); i++) {
                    double url = Double.parseDouble(a.optString(i));
                    urls[i - 3] = url;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
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