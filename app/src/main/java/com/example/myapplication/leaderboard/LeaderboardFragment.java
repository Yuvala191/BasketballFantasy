package com.example.myapplication.leaderboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
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

public class LeaderboardFragment extends Fragment {
    LeaderboardViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard,container,false);

        swipeRefresh = view.findViewById(R.id.leaderboard_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshUserList());

        RecyclerView list = view.findViewById(R.id.leaderboard_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        viewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getUserListLoadingState().getValue() == Model.UserListLoadingState.loading);
        Model.instance.getUserListLoadingState().observe(getViewLifecycleOwner(), userListLoadingState -> {
            if (userListLoadingState == Model.UserListLoadingState.loading){
                swipeRefresh.setRefreshing(true);
            }else{
                swipeRefresh.setRefreshing(false);
            }

        });

        Button backBtn = view.findViewById(R.id.leaderboard_back_btn);
        backBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigateUp();
        });
        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView avatarImv;
        TextView nameTv;
        TextView scoreTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.listrow_name_tv);
            scoreTv = itemView.findViewById(R.id.listrow_score_tv);
            avatarImv = itemView.findViewById(R.id.listrow_avatar_imv);
        }

        void bind(User user){
            nameTv.setText(user.getUsername());
            scoreTv.setText(String.valueOf(user.getScore()));
            avatarImv.setImageResource(R.drawable.avatar);
            if (user.getAvatarUrl() != null) {
                Picasso.get()
                        .load(user.getAvatarUrl())
                        .into(avatarImv);
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.leaderboard_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            User user = viewModel.getData().getValue().get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            if(viewModel.getData().getValue() == null){
                return 0;
            }
            return viewModel.getData().getValue().size();
        }
    }
}