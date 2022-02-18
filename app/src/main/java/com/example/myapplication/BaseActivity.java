package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.ui.NavigationUI;

public class BaseActivity extends AppCompatActivity {
    NavController navCtl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        NavHost navHost = (NavHost) getSupportFragmentManager().findFragmentById(R.id.base_navhost);
        navCtl = navHost.getNavController();
        String userId = getIntent().getStringExtra("userId");
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        navCtl.setGraph(R.navigation.nav_graph, bundle);
    }
}