package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class User {
    final public static String COLLECTION_NAME = "users";
    @PrimaryKey
    @NonNull
    String id = "";
    String name = "";
    int score = 0;
    Long updateDate = new Long(0);
    String avatarUrl;

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public User(){}
    public User(String name, String id, int score) {
        this.name = name;
        this.id = id;
        this.score = score;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id",id);
        json.put("name",name);
        json.put("flag",score);
        json.put("avatarUrl",avatarUrl);
        return json;
    }

    public static User create(Map<String, Object> json) {
        String id = (String) json.get("id");
        String name = (String) json.get("name");
        int score = (int) json.get("score");
        String avatarUrl = (String)json.get("avatarUrl");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        User user = new User(name,id,score);
        user.setUpdateDate(updateDate);
        user.setAvatarUrl(avatarUrl);
        return user;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setAvatarUrl(String url) {
        avatarUrl = url;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
