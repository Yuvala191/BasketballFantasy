package com.example.myapplication.model;

public class Post {
    String title = "";
    String content = "";
    String imageUrl;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setImageUrl(String url) {
        imageUrl = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
