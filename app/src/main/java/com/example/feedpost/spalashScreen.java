package com.example.feedpost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.badge.BadgeUtils;

public class spalashScreen extends AppCompatActivity {
    // widgets
    private ProgressBar progressBar ;
    private Button consentBtn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalash_screen);

        initializeWidgets() ;
        finishSplash() ;
    }
    // 1 .
    private void initializeWidgets(){
        progressBar = findViewById(R.id.progressBar) ;
        consentBtn = findViewById(R.id.consentButton) ;
    }
    // 2 .
    private void finishSplash(){
        progressBar.setVisibility(View.VISIBLE) ;
        consentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.GONE);
                finish() ;
            }
        });
    }
}