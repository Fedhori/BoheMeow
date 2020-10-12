package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class dailyRankData {
    public String username;
    public int point;

    public dailyRankData(){

    }

    public dailyRankData(String username, int point) {
        this.username = username;
        this.point = point;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("point", point);
        return result;
    }
}
