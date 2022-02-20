package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.myplayers.MyPlayersFragmentDirections;

public class BaseActivity extends AppCompatActivity {
    NavController navCtl;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        NavHost navHost = (NavHost) getSupportFragmentManager().findFragmentById(R.id.base_navhost);
        navCtl = navHost.getNavController();
        userId = getIntent().getStringExtra("userId");
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        navCtl.setGraph(R.navigation.nav_graph, bundle);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!super.onOptionsItemSelected(item)){
            switch (item.getItemId()){
                case android.R.id.home:
                    navCtl.navigateUp();
                    return true;

                case R.id.newPostFragment:
                    navCtl.navigate(MyPlayersFragmentDirections.actionMyPlayersFragmentToNewPostFragment(userId));
                default:
                    NavigationUI.onNavDestinationSelected(item,navCtl);
            }
        }else{
            return true;
        }
        return false;
    }
}