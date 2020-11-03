package com.example.bohemeow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RankData implements Serializable {
    public String nickname;
    public double totalWalkLength;
    public long totalWalkTime;
    public int totalWalkCount;
    public int level;
    public int catType;
    public int rank;
    public String introduction;
    public boolean isUser;

    public RankData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RankData(String nickname, int catType, double totalWalkLength, long totalWalkTime, int totalWalkCount, int level, int rank, String introduction) {
        // set by constructor
        this.nickname = nickname;
        this.catType = catType;
        this.totalWalkLength = totalWalkLength;
        this.totalWalkTime = totalWalkTime;
        this.totalWalkCount = totalWalkCount;
        this.level = level;
        this.rank =rank;
        this.introduction = introduction;
        this.isUser = false;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("nickname", nickname);
        result.put("totalWalkLength", totalWalkLength);
        result.put("totalWalkTime", totalWalkTime);
        result.put("totalWalkCount", totalWalkCount);
        result.put("level", level);
        result.put("catType", catType);
        result.put("rank", rank);
        result.put("introduction", introduction);
        result.put("isUser", isUser);

        return result;
    }
}
