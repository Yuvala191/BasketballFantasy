package com.example.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.BaseActivity;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginBtn = view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(v -> {
            //TODO - connect to model login function
            if (true) {
                String userId = ((TextView) view.findViewById(R.id.login_username)).getText().toString();
                toMyPlayersActivity(userId);
            }
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