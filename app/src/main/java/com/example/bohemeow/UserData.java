package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    public String nickname;
    public String id;
    public String password;
    public double totalWalkLength;
    public double totalWalkTime;
    public int totalWalkCount;
    public double realWalkTime;
    public int level;
    public boolean isItemExist;
    public double safeScore;
    public double enviScore;
    public double popularity;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserData(String nickname, String id, String password, double safeScore, double enviScore, double popularity) {
        // set by constructor
        this.nickname = nickname;
        this.id = id;
        this.password = password;
        this.safeScore = safeScore;
        this.enviScore = enviScore;
        this.popularity = popularity;

        // default value
        this.totalWalkLength = 0;
        this.totalWalkTime = 0;
        this.totalWalkCount = 0;
        this.realWalkTime = 0;
        this.level = 1;
        this.isItemExist = false;
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
        result.put("level", level);
        result.put("isItemExist", isItemExist);

        return result;
    }
}