package com.example.myapplication.ui.myplayers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyPlayersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyPlayersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is myPlayers fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}