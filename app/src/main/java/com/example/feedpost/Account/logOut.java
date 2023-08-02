package com.example.feedpost.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.feedpost.R;
import com.google.firebase.auth.FirebaseAuth;

public class logOut extends AppCompatActivity {
    // Firebase
    private FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);
        initializeDatabase() ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
    }

    public void signOut(View view) {
        mAuth.signOut() ;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(logOut.this , MainActivity.class));
                finish() ;
            }
        } , 200) ;
    }
}