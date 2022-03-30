package com.example.myapplication.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.MyApplication;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
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

    public enum PostListLoadingState {
        loading,
        loaded
    }

    interface Filter<T>{
        List<T> filter(List<T> list);
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    MutableLiveData<UserListLoadingState> userListLoadingState = new MutableLiveData<UserListLoadingState>();

    private Model() {
        userListLoadingState.setValue(UserListLoadingState.loaded);
        postListLoadingState.setValue(PostListLoadingState.loaded);
    }

    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();

    public LiveData<UserListLoadingState> getUserListLoadingState() {
        return userListLoadingState;
    }

    MutableLiveData<List<User>> usersList = new MutableLiveData<List<User>>();

    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();

    public LiveData<List<User>> getAll() {
        if (usersList.getValue() == null) {
            refreshUserList();
        }
        ;
        return usersList;
    }

    public LiveData<List<Post>> getAllPosts() {
        if (postsList.getValue() == null) {
            refreshPostList(null    );
        }
        ;
        return postsList;
    }

    public LiveData<List<Post>> getAllPostsByUserEmail(String email) {
        refreshPostListByEmail(email);
        return postsList;
    }

    public void refreshPostListByEmail(String email) {
        ArrayList<Post> postsToBePresented = new ArrayList<>();
        Filter<Post> postsFilter = list -> {
            for (Post post : list) {
                if (post.getCreatedBy().equals(email)) {
                    postsToBePresented.add(post);
                }
            }
            return postsToBePresented;
        };
        refreshPostList(postsFilter);
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

    public void refreshPostList(Filter<Post> filterList) {
        postListLoadingState.setValue(PostListLoadingState.loading);

        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("UsersLastUpdateDate", 0);

        executor.execute(() -> {
            List<Post> postList = AppLocalDb.db.postDao().getAll();
            postsList.postValue(postList);
        });

        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Post post : list) {
                            AppLocalDb.db.postDao().insertAll(post);
                            if (lud < post.getUpdateDate()) {
                                lud = post.getUpdateDate();
                            }

                        }
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostsLastUpdateDate", lud)
                                .commit();

                        List<Post> postList = AppLocalDb.db.postDao().getAll();
                        for (Post post: postList) {
                            if (post.getIsDeleted() == true) {
                                AppLocalDb.db.postDao().delete(post);
                            }
                        }

                        if (filterList != null){
                            postList = filterList.filter(AppLocalDb.db.postDao().getAll());
                        }
                        else{
                            postList = AppLocalDb.db.postDao().getAll();
                        }
                        postsList.postValue(postList);
                        postListLoadingState.postValue(PostListLoadingState.loaded);
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

    public interface GetPostById {
        void onComplete(Post post);
    }

    public User getPostById(String postId, GetPostById listener) {
        modelFirebase.getPostById(postId, listener);
        return null;
    }

    public interface UpdatePostListener {
        void onComplete();
    }

    public void updatePostById(String postId, Post post, UpdatePostListener listener) {
        modelFirebase.updatePostById(postId, post, () -> {
            listener.onComplete();
            refreshPostList(null);
        });
    }

    public interface GetUserByUsernameAndPassword {
        void onComplete(User user);
    }

    public User getUserByUsernameAndPassword(String userId, String password, GetUserByUsernameAndPassword listener) {
        modelFirebase.getUserByUsernameAndPassword(userId, password, listener);
        return null;
    }

    public interface CreateUserListener {
        void onComplete();
    }

    public interface CreatePostListener {
        void onComplete();
    }

    public void createPost(Post post, CreatePostListener listener) {
        modelFirebase.createPost(post, () -> {
            listener.onComplete();
            refreshPostList(null);
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

    public interface SignInListener {
        void onComplete(FirebaseUser user, Exception error);
    }

    public void signIn(@NonNull String userId, @NonNull String password, SignInListener listener) {
        modelFirebase.signIn(userId, password, listener);
    }

    public interface RegisterListener {
        void onComplete(FirebaseUser user, Exception error);
    }

    public void createUser(User user, RegisterListener listener) {
        modelFirebase.createUser(user, listener);
    }

    public void logout() {
        modelFirebase.logout();
    }

    public void getLoggedInUser(GetLoggedUserListener listener) {
        modelFirebase.getLoggedInUser(listener);
    }

    public interface GetLoggedUserListener {
        void onComplete(User user);
    }

}