package com.example.feedpost.Account.EditProfile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.feedpost.ImageActivity.ImageUploader.ImageUploadForPost;
import com.example.feedpost.R;

public class personalizeProfile extends AppCompatActivity {
    // widgets
    private LinearLayout genderLayout ;
    private LinearLayout bioLayout ;
    private AppCompatSpinner spinner ;
    private AppCompatButton saveBTN ;
    private EditText userBio ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_profile);

        initializeWidgets() ;
        setSpinnerItems() ;
        setOnCLickListeners() ;
    }
    // 1 .
    private void initializeWidgets(){
        genderLayout = findViewById(R.id.genderLayout) ;
        spinner = findViewById(R.id.pronounceSpinner) ;
        saveBTN = findViewById(R.id.saveChanges) ;
        bioLayout = findViewById(R.id.bioLayout) ;
        userBio = findViewById(R.id.userBio) ;
    }

    private void setSpinnerItems(){
        final String[] contents = new String[]{
                "skip" ,
                "male" ,
                "female"
        } ;
        ArrayAdapter add = new ArrayAdapter(personalizeProfile.this ,
                android.R.layout.simple_spinner_dropdown_item , contents) ;
        add.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(add) ;
    }

    private void setOnCLickListeners(){
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish() ;
            }
        }) ;
    }

    public void backToPrev(View view) {
        finish() ;
    }

    public void editProfilePic(View view) {
    }

    public void editProfileBackground(View view) {

    }

    public void editUserPronounce(View view) {
        genderLayout.setVisibility(View.VISIBLE) ;
        saveBTN.setVisibility(View.VISIBLE);
    }

    public void editUserProfileBio(View view) {
        bioLayout.setVisibility(View.VISIBLE) ;
        saveBTN.setVisibility(View.VISIBLE);
    }
}