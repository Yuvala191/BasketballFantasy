package com.example.myapplication.myplayers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Model;
import com.example.myapplication.model.User;

import java.util.List;

public class MyPlayersFragment extends Fragment {
    TextView team_name;
    TextView score;
    TextView player_one;
    TextView player_two;
    TextView player_three;
    TextView player_four;
    TextView player_five;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myplayers, container, false);

        String userId = getArguments().getString("userId");

        Model.instance.getUserById(userId, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                List<String> userPlayers = user.getPlayers();
                team_name.setText(user.getUsername() + "'s Team");
                score.setText(String.valueOf(user.getScore()));
                player_one.setText(userPlayers.get(0));
                player_two.setText(userPlayers.get(1));
                player_three.setText(userPlayers.get(2));
                player_four.setText(userPlayers.get(3));
                player_five.setText(userPlayers.get(4));
            }
        });

        team_name = view.findViewById(R.id.team_name_label);
        score = view.findViewById(R.id.user_score);
        player_one = view.findViewById(R.id.player_one_name);
        player_two = view.findViewById(R.id.player_two_name);
        player_three = view.findViewById(R.id.player_three_name);
        player_four = view.findViewById(R.id.player_four_name);
        player_five = view.findViewById(R.id.player_five_name);

        Button profileBtn = view.findViewById(R.id.myplayers_to_profile_btn);
        profileBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigate(MyPlayersFragmentDirections.actionMyPlayersFragmentToProfileFragment(userId));
        });
        return view;
    }
}