package com.example.bohemeow;

public class post {
    String username;
    String content;
    String tags;

    public post(String username, String content, String tags) {
        this.username = username;
        this.content = content;
        this.tags = tags;
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
}
