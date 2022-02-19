package com.example.myapplication.model;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListConverter {
    @TypeConverter
    public Players storedStringToPlayers(String value) {
        List<String> players = Arrays.asList(value.split("\\s*,\\s*"));
        Players pl = new Players();
        pl.setPlayers(players);
        return pl;
    }

    @TypeConverter
    public String playersToString(Players players) {
        String value = "";

        for (String lang : players.getPlayers())
            value += lang + ",";

        return value;
    }
}
