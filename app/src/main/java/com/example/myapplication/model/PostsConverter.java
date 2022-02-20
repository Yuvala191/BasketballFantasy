package com.example.myapplication.model;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class PostsConverter {
    @TypeConverter
    public Posts storedStringToPosts(String value) {
        Type listType = new TypeToken<List<Post>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public String fromArrayList(Posts list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
