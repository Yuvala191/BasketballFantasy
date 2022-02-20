package com.example.myapplication.posts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.model.Model;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.User;

public class NewPostFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    User loggedUser;
    EditText titleEt;
    EditText contentEt;
    Button createBtn;
    Button backBtn;
    Bitmap imageBitmap;
    ImageView imageImv;
    ImageButton camBtn;
    ImageButton galleryBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);

        String userId = NewPostFragmentArgs.fromBundle(getArguments()).getUserId();

        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                loggedUser = user;
            }
        });

        titleEt = view.findViewById(R.id.new_post_title);
        contentEt = view.findViewById(R.id.new_post_content);
        imageImv = view.findViewById(R.id.new_post_avatar_imv);
        camBtn = view.findViewById(R.id.new_post_cam_btn);
        galleryBtn = view.findViewById(R.id.new_post_gallery_btn);

        createBtn = view.findViewById(R.id.new_post_create_btn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create(userId);
            }
        });

        backBtn = view.findViewById(R.id.new_post_back_btn);
        backBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigateUp();
        });
        camBtn.setOnClickListener(v -> {
            openCam();
        });
        return view;
    }

    private void openCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageImv.setImageBitmap(imageBitmap);

            }
        }
    }

    private void create(String userId) {
        createBtn.setEnabled(false);
        backBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();
        Post post = new Post(title, content);

        if (title.isEmpty() || content.isEmpty()) return;

        if (imageBitmap == null){
            loggedUser.addPost(post);
            Model.instance.updateUser(userId, loggedUser, ()->{
                Navigation.findNavController(titleEt).navigateUp();
            });
        }else{
            Model.instance.saveImage(imageBitmap, title + ".jpg", url -> {
                post.setImageUrl(url);
                loggedUser.addPost(post);
                Model.instance.updateUser(userId, loggedUser ,()->{
                    Navigation.findNavController(titleEt).navigateUp();
                });
            });
        }
    }
}