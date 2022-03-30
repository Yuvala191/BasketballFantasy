package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@TypeConverters({PlayersConverter.class })
public class User {
    final public static String COLLECTION_NAME = "users";
    @PrimaryKey
    @NonNull
    String username = "";
    String password = "";
    long score = 0;
    String avatarUrl;
    Long updateDate = new Long(0);
    Players players = new Players();

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
        json.put("updateDate", FieldValue.serverTimestamp());
        json.put("players",players.getPlayers());
        return json;
    }

    public static User create(Map<String, Object> json) {
        String password = (String) json.get("password");
        String username = (String) json.get("username");
        long score = (long) json.get("score");
        List<String> players = (ArrayList<String>) json.get("players");

        String avatarUrl = (String)json.get("avatarUrl");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();
        User user = new User(username,password,score);
        user.setPlayers(players);
        user.setAvatarUrl(avatarUrl);
        user.setUpdateDate(updateDate);
        return user;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
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

    public void setPlayers(List<String> newPlayers) {
        players.setPlayers(newPlayers);
    }

    public List<String> getPlayers() {
        return players.getPlayers();
    }
}

class Players {
    private List<String> players;

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
