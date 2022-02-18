package com.example.myapplication.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Model;
import com.example.myapplication.model.User;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    TextView nameTv;
    TextView passwordTv;
    ImageView avatarImv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        String userId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();

        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                nameTv.setText(user.getUsername());
                if (user.getAvatarUrl() != null) {
                    Picasso.get().load(user.getAvatarUrl()).into(avatarImv);
                }
            }
        });

        nameTv = view.findViewById(R.id.profile_name_tv);
        avatarImv = view.findViewById(R.id.profile_avatar_img);

        Button backBtn = view.findViewById(R.id.profile_back_btn);
        backBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigateUp();
        });
        return view;
    }
}