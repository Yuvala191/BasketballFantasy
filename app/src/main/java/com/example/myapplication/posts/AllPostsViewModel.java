package com.example.myapplication.posts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.model.Model;
import com.example.myapplication.model.Post;

import java.util.List;

public class AllPostsViewModel extends ViewModel {
    LiveData<List<Post>> data;

    public AllPostsViewModel() {
        data = Model.instance.getAllPosts();
    }
    public LiveData<List<Post>> getData() {
        return data;
    }
}
