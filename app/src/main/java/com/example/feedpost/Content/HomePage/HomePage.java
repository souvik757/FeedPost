package com.example.feedpost.Content.HomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.HomePage.HomeContents.HomeFragment;
import com.example.feedpost.Content.HomePage.Profile.ProfileFragment;
import com.example.feedpost.Content.UsersList.chooseUserActivity;
import com.example.feedpost.ImageActivity.ImageUploader.PostActivity.ImageUploadForPost;
import com.example.feedpost.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // widgets
    private BottomNavigationView bottomNavigationView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        initializeWidgets() ;
        setBottomNavigationView() ;
    }
    // 1 .
    private void initializeWidgets(){
        bottomNavigationView = findViewById(R.id.bottomNavView) ;
    }
    // 3 .
    private void setBottomNavigationView(){
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId() ;
        if(itemID == R.id.feedPost) {
            startActivity(new Intent(this , ImageUploadForPost.class));
            return true;
        }
        else if(itemID == R.id.searchPeople) {
            startActivity(new Intent(this , chooseUserActivity.class));
            return true ;
        }
        else if(itemID == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, new HomeFragment()).commit();
            return true;
        }
        else if(itemID == R.id.profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, new ProfileFragment()).commit();
            return true;
        }

        return false;
    }
}