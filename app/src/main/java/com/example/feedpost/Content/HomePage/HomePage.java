package com.example.feedpost.Content.HomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.HomePage.Profile.ProfileFragment;
import com.example.feedpost.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

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
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId() ;
        if(itemID == R.id.feedPost) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, new feedPostFragment()).commit();
            return true;
        }else if(itemID == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, new HomeFragment()).commit();
            return true;
        }
        else if(itemID == R.id.profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, new ProfileFragment()).commit();
            return true;
        }

        return false;
    }
    private void showCustomToast(String message){
        LayoutInflater inflater = getLayoutInflater() ;
        View layout = inflater.inflate(R.layout.custom_toast_layout , (ViewGroup) findViewById(R.id.containerToast)) ;
        ImageView img = layout.findViewById(R.id.imageViewToast) ;
        img.setImageResource(R.drawable.warning);
        TextView txt = layout.findViewById(R.id.textViewToast) ;
        txt.setText(message);
        Toast toast = new Toast(getApplicationContext()) ;
        toast.setDuration(Toast.LENGTH_LONG) ;
        toast.setView(layout);
        toast.show() ;
    }
}