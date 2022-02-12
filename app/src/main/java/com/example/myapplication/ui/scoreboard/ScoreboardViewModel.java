package com.example.myapplication.ui.scoreboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScoreboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ScoreboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is scoreboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}