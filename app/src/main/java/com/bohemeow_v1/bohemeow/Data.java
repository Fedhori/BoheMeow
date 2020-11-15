package com.bohemeow_v1.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class Data {
    public Double latitude;
    public Double longtitude;

    public Data() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Data(Double latitude, Double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude);
        result.put("longtitude", longtitude);
        return result;
    }
}
