package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class postData {
    public String username;
    public String uri;
    public String content;
    public String tags;
    public String postType;

    public postData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public postData(String username, String uri, String content, String tags, String postType) {
        this.username = username;
        this.uri = uri;
        this.content = content;
        this.tags = tags;
        this.postType = postType;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("uri", uri);
        result.put("content", content);
        result.put("tags", tags);
        result.put("postType", postType);
        return result;
    }
}
