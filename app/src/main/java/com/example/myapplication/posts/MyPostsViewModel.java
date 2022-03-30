package com.example.myapplication.posts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Model;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.User;

import java.util.List;

public class MyPostsViewModel extends ViewModel {
    LiveData<List<Post>> data;

    public MyPostsViewModel() {
        Model.instance.getLoggedInUser((user) -> {
            data = Model.instance.getAllPostsByUserEmail(user.getUsername());
        });
    }
    public LiveData<List<Post>> getData() {

        return data;
    }
}
