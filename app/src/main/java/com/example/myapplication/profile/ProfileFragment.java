package com.example.myapplication.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.myapplication.R;
import com.example.myapplication.model.Model;
import com.example.myapplication.model.User;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    User loggedUser;
    EditText usernameEt;
    EditText passwordEt;
    Button saveBtn;
    Button backBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;
    ImageView avatarImv;
    ImageButton camBtn;
    ImageButton galleryBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        String userId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();

        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                loggedUser = user;
                usernameEt.setText(user.getUsername());
                if (user.getAvatarUrl() != null) {
                    Picasso.get().load(user.getAvatarUrl()).into(avatarImv);
                }
            }
        });

        usernameEt = view.findViewById(R.id.profile_username_et);
        passwordEt = view.findViewById(R.id.profile_password_et);
        progressBar = view.findViewById(R.id.profile_progressbar);
        progressBar.setVisibility(View.GONE);
        avatarImv = view.findViewById(R.id.profile_avatar_imv);
        camBtn = view.findViewById(R.id.profile_cam_btn);
        galleryBtn = view.findViewById(R.id.profile_gallery_btn);

        saveBtn = view.findViewById(R.id.profile_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        backBtn = view.findViewById(R.id.profile_back_btn);
        backBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigateUp();
        });
        camBtn.setOnClickListener(v -> {
            openCam();
        });

        galleryBtn.setOnClickListener(v -> {
            openGallery();
        });
        return view;
    }

    private void openGallery() {
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
                avatarImv.setImageBitmap(imageBitmap);

            }
        }
    }

    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        backBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();
        Log.d("TAG","updated username:" + username);
        String oldUsername = loggedUser.getUsername();
        if (!username.isEmpty()) loggedUser.setUsername(username);
        if (!password.isEmpty()) loggedUser.setPassword(password);
        if (imageBitmap == null){
            Model.instance.updateUser(oldUsername, loggedUser,()->{
                Navigation.findNavController(usernameEt).navigateUp();
            });
        }else{
            Model.instance.saveImage(imageBitmap, username + ".jpg", url -> {
                loggedUser.setAvatarUrl(url);
                Model.instance.updateUser(oldUsername, loggedUser,()->{
                    Navigation.findNavController(usernameEt).navigateUp();
                });
            });
        }
    }
}