package com.example.myapplication.login;

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

import java.util.ArrayList;

public class SignupFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    EditText usernameEt;
    EditText passwordEt;
    EditText playerOneEt;
    EditText playerTwoEt;
    EditText playerThreeEt;
    EditText playerFourEt;
    EditText playerFiveEt;
    Button createBtn;
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        usernameEt = view.findViewById(R.id.signup_username_et);
        passwordEt = view.findViewById(R.id.signup_password_et);
        playerOneEt = view.findViewById(R.id.signup_player_one_et);
        playerTwoEt = view.findViewById(R.id.signup_player_two_et);
        playerThreeEt = view.findViewById(R.id.signup_player_three_et);
        playerFourEt = view.findViewById(R.id.signup_player_four_et);
        playerFiveEt = view.findViewById(R.id.signup_player_five_et);
        progressBar = view.findViewById(R.id.signup_progressbar);
        progressBar.setVisibility(View.GONE);
        avatarImv = view.findViewById(R.id.signup_avatar_imv);
        camBtn = view.findViewById(R.id.signup_cam_btn);
        galleryBtn = view.findViewById(R.id.signup_gallery_btn);

        createBtn = view.findViewById(R.id.signup_create_btn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        backBtn = view.findViewById(R.id.signup_back_btn);
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

    private void createUser() {
        progressBar.setVisibility(View.VISIBLE);
        createBtn.setEnabled(false);
        backBtn.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();
        ArrayList<String> players = new ArrayList<String>();
        players.add(playerOneEt.getText().toString());
        players.add(playerTwoEt.getText().toString());
        players.add(playerThreeEt.getText().toString());
        players.add(playerFourEt.getText().toString());
        players.add(playerFiveEt.getText().toString());
        if (username.isEmpty() || password.isEmpty()) return;
        User user = new User(username, password, 0);
        user.setPlayers(players);
        Log.d("TAG","new user:" + username);
        if (imageBitmap == null){
            Model.instance.createUser(user, (a, error)->{
                Navigation.findNavController(usernameEt).navigateUp();
            });
        }else{
            Model.instance.saveImage(imageBitmap, username + ".jpg", url -> {
                user.setAvatarUrl(url);
                Model.instance.createUser(user, (a, error)->{
                    Navigation.findNavController(usernameEt).navigateUp();
                });
            });
        }
    }
}