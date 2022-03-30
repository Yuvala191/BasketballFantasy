package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Post {
    final public static String COLLECTION_NAME = "posts";
    @PrimaryKey
    @NonNull
    String id;
    String title = "";
    String content = "";
    String imageUrl;
    String createdBy;
    Boolean isDeleted;
    Long updateDate = new Long(0);

    public Post(String id, String createdBy, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isDeleted = false;
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
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

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id", id);
        json.put("title", title);
        json.put("content", content);
        json.put("isDeleted", isDeleted);
        json.put("createdBy", createdBy);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static Post create(Map<String, Object> json) {
        String id = (String) json.get("id");
        String title = (String) json.get("title");
        String content = (String) json.get("content");
        Boolean isDeleted = (Boolean) json.get("isDeleted");
        String createdBy = (String) json.get("createdBy");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        Post post = new Post(id, createdBy, title, content);
        post.setIsDeleted(isDeleted);
        post.setUpdateDate(updateDate);
        return post;
    }
}
