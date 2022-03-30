package com.example.myapplication.posts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.model.Model;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.User;
import com.squareup.picasso.Picasso;


public class MyPostsFragment extends Fragment {
    MyPostsViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(MyPostsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_list,container,false);
        Model.instance.getLoggedInUser((currentUser) -> {
            swipeRefresh = view.findViewById(R.id.posts_list_swiperefresh);
            swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshPostListByEmail(currentUser.getUsername()));
            Model.instance.refreshPostListByEmail(currentUser.getUsername());
        });


        RecyclerView list = view.findViewById(R.id.posts_list_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        viewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getPostListLoadingState().getValue() == Model.PostListLoadingState.loading);
        Model.instance.getPostListLoadingState().observe(getViewLifecycleOwner(), postListLoadingState -> {
            if (postListLoadingState == Model.PostListLoadingState.loading){
                swipeRefresh.setRefreshing(true);
            }else{
                swipeRefresh.setRefreshing(false);
            }

        });

        Button backBtn = view.findViewById(R.id.posts_list_back_btn);
        backBtn.setOnClickListener((v)->{
            Navigation.findNavController(v).navigateUp();
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String postId = viewModel.getData().getValue().get(position).getId();
                Navigation.findNavController(v).navigate(MyPostsFragmentDirections.actionMyPostsFragmentToEditpostFragment(postId));
            }
        });
        return view;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView avatarImv;
        TextView titleTv;
        TextView contentTv;
        TextView userEmailTv;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.listrow_title_tv);
            contentTv = itemView.findViewById(R.id.listrow_content_tv);
            userEmailTv = itemView.findViewById(R.id.listrow_user_email_tv);
            avatarImv = itemView.findViewById(R.id.listrow_avatar_imv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v, pos);
                }
            });
        }

        void bind(Post post){
            titleTv.setText(post.getTitle());
            contentTv.setText(String.valueOf(post.getContent()));
            userEmailTv.setText("");
            avatarImv.setImageResource(R.drawable.avatar);
            if (post.getImageUrl() != null) {
                Picasso.get()
                        .load(post.getImageUrl())
                        .into(avatarImv);
            }
        }
    }

    interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyPostsFragment.MyViewHolder> {
        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyPostsFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.posts_list_row,parent,false);
            MyPostsFragment.MyViewHolder holder = new MyPostsFragment.MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyPostsFragment.MyViewHolder holder, int position) {
            Post post = viewModel.getData().getValue().get(position);
            holder.bind(post);
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