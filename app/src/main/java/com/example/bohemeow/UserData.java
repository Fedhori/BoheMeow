package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    public String nickname;
    public String id;
    public String password;
    public int weight;
    public double totalWalkLength;
    public double totalWalkTime;
    public int totalWalkCount;
    public double realWalkTime;
    public int level;
    public int catType;
    public boolean isItemExist;
    public double safeScore;
    public double enviScore;
    public double popularity;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserData(String nickname, String id, String password, int weight) {
        // set by constructor
        this.nickname = nickname;
        this.id = id;
        this.password = password;
        this.weight = weight;

        // default value
        this.safeScore = -1;
        this.enviScore = -1;
        this.popularity = -1;
        this.catType = 0;
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
        result.put("weight", weight);
        result.put("level", level);
        result.put("catType", catType);
        result.put("isItemExist", isItemExist);

        return result;
    }
}
