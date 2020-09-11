package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class postData {
    public String username;
    public String uri;
    public String content;
    public String tags;
    public String time;


    public postData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public postData(String username, String uri, String content, String tags, String time) {
        this.username = username;
        this.uri = uri;
        this.content = content;
        this.tags = tags;
        this.time = time;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("uri", uri);
        result.put("content", content);
        result.put("tags", tags);
        result.put("time", time);
        return result;
    }
}
