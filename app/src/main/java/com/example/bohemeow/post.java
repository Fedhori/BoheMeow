package com.example.bohemeow;

public class post {
    String username;
    String content;
    String tags;
    String time;
    int level;
    int catType;

    public post(String username, String content, String tags, String time, int level, int catType) {
        this.username = username;
        this.content = content;
        this.tags = tags;
        this.time = time;
        this.level = level;
        this.catType = catType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCatType() {
        return catType;
    }

    public void setCatType(int catType) {
        this.catType = catType;
    }
}

