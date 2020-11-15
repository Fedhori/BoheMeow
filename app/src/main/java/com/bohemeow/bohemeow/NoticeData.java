package com.bohemeow.bohemeow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NoticeData implements Serializable {
    public String time;
    public String content;
    public String title;
    public String author;
    public int catType;

    public NoticeData() {

    }

    public NoticeData(String time, String title, String content, String author, int catType) {
        // set by constructor
        this.time = time;
        this.title = title;
        this.content = content;
        this.author = author;
        this.catType = catType;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();

        result.put("time", time);
        result.put("title", title);
        result.put("content", content);
        result.put("author", author);
        result.put("catType", catType);

        return result;
    }
}
