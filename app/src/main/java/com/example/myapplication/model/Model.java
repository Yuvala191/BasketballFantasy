package com.example.myapplication.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.MyApplication;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {
    public static final Model instance = new Model();
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    public enum UserListLoadingState {
        loading,
        loaded
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    MutableLiveData<UserListLoadingState> userListLoadingState = new MutableLiveData<UserListLoadingState>();

    private Model() {
        userListLoadingState.setValue(UserListLoadingState.loaded);
    }

    public LiveData<UserListLoadingState> getUserListLoadingState() {
        return userListLoadingState;
    }

    MutableLiveData<List<User>> usersList = new MutableLiveData<List<User>>();

    public LiveData<List<User>> getAll() {
        if (usersList.getValue() == null) {
            refreshUserList();
        }
        ;
        return usersList;
    }

    public void refreshUserList() {
        userListLoadingState.setValue(UserListLoadingState.loading);

        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("UsersLastUpdateDate", 0);

        executor.execute(() -> {
            List<User> userList = AppLocalDb.db.userDao().getAll();
            usersList.postValue(userList);
        });

        modelFirebase.getAllUsers(lastUpdateDate, new ModelFirebase.GetAllUsersListener() {
            @Override
            public void onComplete(List<User> list) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (User user : list) {
                            AppLocalDb.db.userDao().insertAll(user);
                            if (lud < user.getUpdateDate()) {
                                lud = user.getUpdateDate();
                            }
                        }
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("UsersLastUpdateDate", lud)
                                .commit();

                        List<User> userList = AppLocalDb.db.userDao().getAll();
                        usersList.postValue(userList);
                        userListLoadingState.postValue(UserListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public interface GetUserById {
        void onComplete(User user);
    }

    public User getUserById(String userId, GetUserById listener) {
        modelFirebase.getUserById(userId, listener);
        return null;
    }

    public interface CreateUserListener {
        void onComplete();
    }

    public void createUser(User user, CreateUserListener listener) {
        modelFirebase.createUser(user, () -> {
            listener.onComplete();
        });
    }

    public interface UpdateUserListener {
        void onComplete();
    }

    public void updateUser(String username, User user, UpdateUserListener listener) {
        modelFirebase.updateUser(username, user, () -> {
            listener.onComplete();
        });
    }

    public interface SaveImageListener {
        void onComplete(String url);
    }

    public void saveImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, imageName, listener);
    }

    public boolean isSignedIn() {
        return modelFirebase.isSignedIn();
    }
}