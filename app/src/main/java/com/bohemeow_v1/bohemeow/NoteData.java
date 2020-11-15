package com.bohemeow_v1.bohemeow;

import java.util.HashMap;
import java.util.Map;

public class NoteData {
    public double latitude;
    public double longitude;
    public String noteContent;
    public String author;

    public NoteData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public NoteData(double latitude, double longitude, String noteContent, String author) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.noteContent = noteContent;
        this.author = author;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("noteContent", noteContent);
        result.put("author", author);

        return result;
    }
}
