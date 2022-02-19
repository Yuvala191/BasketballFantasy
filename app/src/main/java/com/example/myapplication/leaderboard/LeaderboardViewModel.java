package com.example.myapplication.leaderboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Model;
import com.example.myapplication.model.User;

import java.util.List;

public class LeaderboardViewModel extends ViewModel {
    LiveData<List<User>> data;

    public LeaderboardViewModel() {
        data = Model.instance.getAll();
    }
    public LiveData<List<User>> getData() {
        return data;
    }
}
