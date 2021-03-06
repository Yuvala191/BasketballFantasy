package com.example.myapplication.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.MyApplication;

@Database(entities = {User.class, Post.class}, version = 6)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PostDao postDao();
}

public class AppLocalDb{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName2.db")
                    .fallbackToDestructiveMigration()
                    .build();
}

