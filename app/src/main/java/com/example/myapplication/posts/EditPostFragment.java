package com.example.myapplication.posts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.model.Model;
import com.example.myapplication.model.Post;
import com.example.myapplication.model.User;

import java.util.UUID;

public class EditPostFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    String postId;
    EditText titleEt;
    EditText contentEt;
    Button createBtn;
    Button backBtn;
    Bitmap imageBitmap;
    ImageView imageImv;
    ImageButton camBtn;
    ImageButton galleryBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        titleEt = view.findViewById(R.id.edit_post_title);
        contentEt = view.findViewById(R.id.edit_post_content);

        postId = EditPostFragmentArgs.fromBundle(getArguments()).getPostId();
        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {
                titleEt.setText(post.getTitle());
                titleEt.requestFocus();
                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imgr.showSoftInput(titleEt, 0);
                imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                titleEt.setSelection(titleEt.getText().length());

                contentEt.setText(post.getContent());
            }
        });

        Button saveBtn = view.findViewById(R.id.edit_post_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                update(v);
            }
        });

        Button deleteBtn = view.findViewById(R.id.edit_post_delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                delete(v);
            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    private void update(View v) {
        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();

        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {
                post.setTitle(title);
                post.setContent(content);

                Model.instance.updatePostById(post.getId(), post, () -> {
                    Toast.makeText(getContext(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigateUp();
                });
            }
        });
    }

    private void delete(View v) {
        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {
                post.setIsDeleted(true);

                Model.instance.updatePostById(post.getId(), post, () -> {
                    Toast.makeText(getContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(titleEt).navigateUp();
                });
            }
        });
    }
}