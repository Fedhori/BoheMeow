package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class Data {
    public String username;

    public Data() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Data(String username) {
        this.username = username;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        return result;
    }
}
