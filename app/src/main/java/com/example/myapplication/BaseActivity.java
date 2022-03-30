package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.login.LoginActivity;
import com.example.myapplication.model.Model;
import com.example.myapplication.myplayers.MyPlayersFragmentDirections;

public class BaseActivity extends AppCompatActivity {
    NavController navCtl;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Model.instance.getLoggedInUser((user) -> {
            userId = user.getUsername();
            NavHost navHost = (NavHost) getSupportFragmentManager().findFragmentById(R.id.base_navhost);
            navCtl = navHost.getNavController();
            Bundle bundle = new Bundle();
            navCtl.setGraph(R.navigation.nav_graph, bundle);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.base_menu,menu);
        return true;
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
                    break;
                case R.id.logoutFragment:
                    Model.instance.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    this.finish();
                    break;

                default:
                    NavigationUI.onNavDestinationSelected(item,navCtl);
            }
        }else{
            return true;
        }
        return false;
    }
}