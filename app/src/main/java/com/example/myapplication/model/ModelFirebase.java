package com.example.myapplication.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User loggedInUser;

    public ModelFirebase(){
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
    }

    public interface GetAllUsersListener{
        void onComplete(List<User> list);
    }

    public void getAllUsers(Long lastUpdateDate, GetAllUsersListener listener) {
        db.collection(User.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<User> list = new LinkedList<User>();
                    Collections.sort(list, new Comparator<User>() {
                        @Override
                        public int compare(User u1, User u2) {
                            return (int) (u1.getScore() - u2.getScore());
                        }
                    });
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            User user = User.create(doc.getData());
                            if (user != null){
                                list.add(user);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public interface GetAllPostsListener{
        void onComplete(List<Post> list);
    }

    public void getAllPosts(Long lastUpdateDate, GetAllPostsListener listener) {
        db.collection(Post.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> list = new LinkedList<Post>();
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            Post post = Post.create(doc.getData());
                            if (post != null){
                                list.add(post);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void createPost(Post post, Model.CreatePostListener listener) {
        Map<String, Object> json = post.toJson();
        db.collection(Post.COLLECTION_NAME)
                .document(post.getId())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void updateUser(String username, User user, Model.UpdateUserListener listener) {
        Map<String, Object> json = user.toJson();
        db.collection(User.COLLECTION_NAME)
                .document(username)
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
        loggedInUser = user;
    }

    public void getUserById(String userId, Model.GetUserById listener) {
        db.collection(User.COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = null;
                        if (task.isSuccessful() & task.getResult().getData() != null) {
                            user = User.create(task.getResult().getData());
                        }
                        listener.onComplete(user);
                    }
                });

    }

    public void getPostById(String postId, Model.GetPostById listener) {
        db.collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Post post = null;
                        if (task.isSuccessful() & task.getResult().getData() != null) {
                            post = Post.create(task.getResult().getData());
                        }
                        listener.onComplete(post);
                    }
                });

    }

    public void updatePostById(String postId, Post note, Model.UpdatePostListener listener) {
        Map<String, Object> json = note.toJson();
        db.collection(Post.COLLECTION_NAME)
                .document(postId)
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void getUserByUsernameAndPassword(String userId, String password, Model.GetUserByUsernameAndPassword listener) {
        db.collection(User.COLLECTION_NAME)
                .whereEqualTo("username", userId)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = null;
                                if (document.getData() != null) {
                                    user = User.create(document.getData());
                                    listener.onComplete(user);
                                }
                            }
                        }

                    }
                });

    }

    FirebaseStorage storage = FirebaseStorage.getInstance();
    public void saveImage(Bitmap imageBitmap, String imageName, Model.SaveImageListener listener) {
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("user_avatars/" + imageName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Uri downloadUrl = uri;
                    listener.onComplete(downloadUrl.toString());
                });
            }
        });
    }

    /**
     * Authentication
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean isSignedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null);
    }

    public void signIn(@NonNull String userId, @NonNull String password, Model.SignInListener listener) {
        mAuth.signInWithEmailAndPassword(userId, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                getUserById(userId, (currentUser) -> {
                    loggedInUser = currentUser;
                });
                listener.onComplete(user, null);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithEmail:failure", task.getException());
                listener.onComplete(null, task.getException());
            }
        });
    }

    public void getLoggedInUser(Model.GetLoggedUserListener listener) {
        if (this.loggedInUser != null)
            listener.onComplete( this.loggedInUser);

        FirebaseUser user = mAuth.getCurrentUser();
        getUserById(user.getEmail(), (currentUser) -> {
            this.loggedInUser = currentUser;
            listener.onComplete( this.loggedInUser);
        });
    };

    public void createUser(User user, Model.RegisterListener listener) {
        mAuth.createUserWithEmailAndPassword(user.getUsername(), user.getPassword()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "createUserWithEmail:success");
                FirebaseUser currentUser = mAuth.getCurrentUser();
                Map<String, Object> json = user.toJson();
                db.collection(User.COLLECTION_NAME)
                        .document(user.getUsername())
                        .set(json)
                        .addOnSuccessListener(unused -> listener.onComplete(currentUser, null))
                        .addOnFailureListener(e -> listener.onComplete(null, task.getException()));
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                listener.onComplete(null, task.getException());
            }
        });
    }

    public void logout() {
        mAuth.signOut();
    }
}
