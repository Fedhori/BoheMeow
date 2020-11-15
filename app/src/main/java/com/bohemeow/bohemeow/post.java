package com.bohemeow.bohemeow;

import java.io.Serializable;

public class post implements Serializable {
    String username;
    String content;
    String tags;
    String time;
    String uri;
    int level;
    int catType;
    boolean isPublic;

    public post(String username, String content, String tags, String time, String uri, int level, int catType, boolean isPublic) {
        this.username = username;
        this.content = content;
        this.tags = tags;
        this.time = time;
        this.uri = uri;
        this.level = level;
        this.catType = catType;
        this.isPublic = isPublic;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}

