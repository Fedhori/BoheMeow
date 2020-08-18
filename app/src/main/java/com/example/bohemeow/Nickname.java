package com.example.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class Nickname {
    public String nickname;

    public Nickname() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Nickname(String nickname) {
        this.nickname = nickname;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nickname", nickname);
        return result;
    }
}
