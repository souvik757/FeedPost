package com.example.feedpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class spalashScreen extends AppCompatActivity {
    // widgets
    private Button consentBtn ;
    private ImageView img ;
    private TextView txt ;
    private ProgressBar progressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalash_screen);

        initializeWidgets() ;
        setAnimation() ;
        finishSplash() ;
    }
    // 1 .
    private void initializeWidgets(){
        img = findViewById(R.id.iconLogo) ;
        txt = findViewById(R.id.earlyVersionMessege) ;
        consentBtn = findViewById(R.id.consentButton) ;
        progressBar = findViewById(R.id.progressBar) ;
    }
    // 2 .
    private void setAnimation(){
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.scale_in_animation) ;
        img.setAnimation(anim);
        new Handler().postDelayed(()->{
            Animation anim1 = AnimationUtils.loadAnimation(getApplicationContext() , android.R.anim.fade_out) ;
            txt.setAnimation(anim1);
            txt.setVisibility(View.GONE);
        } , 1700) ;
    }
    // 3 .
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