package com.example.myapplication.ui.myplayers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentMyplayersBinding;

public class MyPlayersFragment extends Fragment {

    private MyPlayersViewModel myPlayersViewModel;
    private FragmentMyplayersBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myPlayersViewModel =
                new ViewModelProvider(this).get(MyPlayersViewModel.class);

        binding = FragmentMyplayersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMyPlayers;
        myPlayersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}