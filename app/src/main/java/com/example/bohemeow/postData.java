package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class postData {
    public String username;
    public String uri;
    public String content;
    public String tags;
    public String time;
    public int level;
    public int catType;
    public boolean isPublic;


    public postData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public postData(String username, String uri, String content, String tags, String time, int level, int catType, boolean isPublic) {
        this.username = username;
        this.uri = uri;
        this.content = content;
        this.tags = tags;
        this.time = time;
        this.level = level;
        this.catType = catType;
        this.isPublic = isPublic;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("uri", uri);
        result.put("content", content);
        result.put("tags", tags);
        result.put("time", time);
        result.put("level", level);
        result.put("catType", catType);
        result.put("isPublic", isPublic);
        return result;
    }
}
