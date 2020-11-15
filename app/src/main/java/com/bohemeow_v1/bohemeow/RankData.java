package com.bohemeow_v1.bohemeow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RankData implements Serializable {
    public String nickname;
    public double totalWalkLength;
    public long realWalkTime;
    public int totalWalkCount;
    public int totalSpotCount;
    public int level;
    public int catType;
    public int rank;
    public String introduction;
    public boolean isUser;

    public RankData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public RankData(String nickname, int catType, double totalWalkLength, long realWalkTime, int totalWalkCount, int totalSpotCount, int level, int rank, String introduction) {
        // set by constructor
        this.nickname = nickname;
        this.catType = catType;
        this.totalWalkLength = totalWalkLength;
        this.realWalkTime = realWalkTime;
        this.totalWalkCount = totalWalkCount;
        this.totalSpotCount = totalSpotCount;
        this.level = level;
        this.rank =rank;
        this.introduction = introduction;
        this.isUser = false;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("nickname", nickname);
        result.put("totalWalkLength", totalWalkLength);
        result.put("realWalkTime", realWalkTime);
        result.put("totalWalkCount", totalWalkCount);
        result.put("totalSpotCount", totalSpotCount);
        result.put("level", level);
        result.put("catType", catType);
        result.put("rank", rank);
        result.put("introduction", introduction);
        result.put("isUser", isUser);

        return result;
    }
}
