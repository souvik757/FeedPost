package com.example.feedpost.Account.EditAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.feedpost.ImageActivity.ImageUploader.ImageUploadBackGround;
import com.example.feedpost.ImageActivity.ImageUploader.ImageUploadForProfilePic;
import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class personalizeProfile extends AppCompatActivity {
    // widgets
    private LinearLayout genderLayout ;
    private TextInputLayout bioLayout ;
    private AppCompatSpinner spinner ;
    private AppCompatButton saveBTN ;
    private EditText userBio ;
    // Firebase
    private FirebaseAuth mAuth ;
    private DatabaseReference mRealTimeDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_profile);

        initializeWidgets() ;
        setSpinnerItems() ;
        initializeDatabase() ;
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
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mRealTimeDatabase = FirebaseDatabase.getInstance().getReference() ;
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
                String ProfileGender = String.valueOf(spinner.getSelectedItem()).trim() ;
                String ProfileBio = String.valueOf(userBio.getText()).trim() ;
                if(!TextUtils.isEmpty(userBio.getText()) && !ProfileGender.equals("skip")) {
                    mRealTimeDatabase.child("users").child(mAuth.getCurrentUser().getUid()).
                            child(documentFields.realtimeFields.bio).setValue(ProfileBio) ;
                    mRealTimeDatabase.child("users").child(mAuth.getCurrentUser().getUid()).
                            child(documentFields.realtimeFields.gender).setValue(ProfileGender) ;
                }
                if(TextUtils.isEmpty(userBio.getText()) && !ProfileGender.equals("skip")) {
                    mRealTimeDatabase.child("users").child(mAuth.getCurrentUser().getUid()).
                            child(documentFields.realtimeFields.gender).setValue(ProfileGender) ;
                }
                if(!TextUtils.isEmpty(userBio.getText()) && ProfileGender.equals("skip")) {
                    mRealTimeDatabase.child("users").child(mAuth.getCurrentUser().getUid()).
                            child(documentFields.realtimeFields.bio).setValue(ProfileBio) ;
                }
                finish() ;
            }
        }) ;
    }
    public void editUserPronounce(View view) {
        genderLayout.setVisibility(View.VISIBLE) ;
        saveBTN.setVisibility(View.VISIBLE);
    }

    public void editUserProfileBio(View view) {
        bioLayout.setVisibility(View.VISIBLE) ;
        saveBTN.setVisibility(View.VISIBLE);
    }

    public void editProfilePic(View view) {
        startActivity(new Intent(personalizeProfile.this , ImageUploadForProfilePic.class));
    }

    public void editProfileBackground(View view) {
        startActivity(new Intent(personalizeProfile.this , ImageUploadBackGround.class));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}