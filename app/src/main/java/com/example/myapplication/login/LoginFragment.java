package com.example.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.BaseActivity;
import com.example.myapplication.model.Model;
import com.example.myapplication.model.User;
import com.example.myapplication.myplayers.MyPlayersFragmentDirections;

import java.util.List;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginBtn = view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(v -> {
            String userId = ((TextView) view.findViewById(R.id.login_username)).getText().toString();
            String password = ((TextView) view.findViewById(R.id.login_password_et)).getText().toString();
            Model.instance.signIn(userId, password, (user, error) -> {
                    if (user != null) {
                        toMyPlayersActivity(userId);
                    }
                });
            });

        Button signupBtn = view.findViewById(R.id.login_signup_btn);
        signupBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment());
        });
        return view;
    }

    private void toMyPlayersActivity(String userId) {
        Intent intent = new Intent(getContext(), BaseActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        getActivity().finish();
    }
}