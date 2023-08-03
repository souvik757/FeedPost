package com.example.feedpost.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.feedpost.R;
import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends AppCompatActivity {
    // Firebase
    private FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeDatabase() ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
    }
    public void back(View view) {
        finish() ;
    }

    public void signIn(View view) {
        startActivity(new Intent(EditProfileActivity.this , MainActivity.class));
        finish() ;
    }

    public void signOut(View view) {
        mAuth.signOut() ;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(EditProfileActivity.this , MainActivity.class));
                finish() ;
            }
        } , 200) ;
    }
}