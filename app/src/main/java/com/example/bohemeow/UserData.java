package com.example.bohemeow;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserData implements Serializable {
    public String nickname;
    public String id;
    public String password;
    public int weight;
    public double totalWalkLength;
    public long totalWalkTime;
    public int totalWalkCount;
    public long realWalkTime;
    public int level;
    public int catType;
    public boolean isItemExist;
    public double safeScore;
    public double enviScore;
    public double popularity;
    public String phoneNumber;

    public String date;
    public int num;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserData(String nickname, String id, String password, int weight, String phoneNumber) {
        // set by constructor
        this.nickname = nickname;
        this.id = id;
        this.password = password;
        this.weight = weight;
        this.phoneNumber = phoneNumber;

        // default value
        this.safeScore = -1;
        this.enviScore = -1;
        this.popularity = -1;
        this.catType = 0;
        this.totalWalkLength = 0;
        this.totalWalkTime = 0;
        this.totalWalkCount = 0;
        this.realWalkTime = 0;
        this.level = 0;
        this.isItemExist = false;

        this.num = 0;
        this.date = "0000-00-00 ";
    }


    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("nickname", nickname);
        result.put("id", id);
        result.put("password", password);
        result.put("safeScore", safeScore);
        result.put("enviScore", enviScore);
        result.put("popularity", popularity);
        result.put("totalWalkLength", totalWalkLength);
        result.put("totalWalkTime", totalWalkTime);
        result.put("totalWalkCount", totalWalkCount);
        result.put("realWalkTime", realWalkTime);
        result.put("weight", weight);
        result.put("level", level);
        result.put("catType", catType);
        result.put("isItemExist", isItemExist);

        HashMap<String, Object> lastPost = new HashMap<>();
        lastPost.put("date", date);
        lastPost.put("num", num);

        result.put("lastPost", lastPost);

        result.put("phoneNumber", phoneNumber);

        return result;
    }



}
