package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class lotsData {
    public String phoneNumber;

    public lotsData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public lotsData(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("phoneNumber", phoneNumber);
        return result;
    }
}
