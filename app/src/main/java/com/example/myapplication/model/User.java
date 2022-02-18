package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

@Entity
public class User {
    final public static String COLLECTION_NAME = "users";
    @PrimaryKey
    @NonNull
    String username = "";
    String password = "";
    long score = 0;
    String avatarUrl;
    ArrayList<String> players = new ArrayList<String>();;

    public User(String username, String password, long score) {
        this.username = username;
        this.password = password;
        this.score = score;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public long getScore() {
        return score;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("password",password);
        json.put("username",username);
        json.put("score",score);
        json.put("avatarUrl",avatarUrl);
        json.put("players",players);
        return json;
    }

    public static User create(Map<String, Object> json) {
        String password = (String) json.get("password");
        String username = (String) json.get("username");
        long score = (long) json.get("score");
        ArrayList<String> players = (ArrayList<String>) json.get("players");
        String avatarUrl = (String)json.get("avatarUrl");
        User user = new User(username,password,score);
        user.setPlayers(players);
        user.setAvatarUrl(avatarUrl);
        return user;
    }

    public void setAvatarUrl(String url) {
        avatarUrl = url;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setPlayers(ArrayList<String> newPlayers) {
        players = newPlayers;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }
}
