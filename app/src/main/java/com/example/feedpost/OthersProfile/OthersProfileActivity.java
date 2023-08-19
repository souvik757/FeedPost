package com.example.feedpost.OthersProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.feedpost.CustomImageAdapter.ImageAdapter;
import com.example.feedpost.CustomImageAdapter.ImageModelClass;
import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OthersProfileActivity extends AppCompatActivity {
    // widgets
    private ScrollView scrollView ;
    private RecyclerView gridView ;
    private ProgressBar profileFetching ;
    private TextView profileName ;
    private TextView userGender ;
    private TextView userBio ;
    private TextView posts ;
    private ImageView profilePic ;
    private ImageView profileBanner ;
    // resources
    private String tempUser ;
    private String tempUserGender ;
    private ArrayList<ImageModelClass> imageList ;
    private ImageAdapter adapter ;
    // Firebase
    private FirebaseStorage mStorage ;
    private StorageReference mReference ;
    private DatabaseReference mRealDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        tempUser = getIntent().getStringExtra("TappedUsersName") ;

        initializeWidgetsAndVariables() ;
        initializeDatabase() ;
        setProfileModelArrayList() ;
        setViewContents() ;
    }
    // 1 .
    private void initializeWidgetsAndVariables(){
        //
        scrollView = findViewById(R.id.scroll) ;
        scrollView.fullScroll(View.FOCUS_DOWN);
        scrollView.setSmoothScrollingEnabled(true);

        // setting actionBar resources
        getSupportActionBar().setTitle(tempUser+"'s"+" "+"Profile");

        gridView = findViewById(R.id.usersUploadGrid) ;
        profileFetching = findViewById(R.id.profileFetching) ;
        profileName = findViewById(R.id.usersProfileName) ;
        userGender = findViewById(R.id.usersProfileGender) ;
        userBio = findViewById(R.id.usersProfileBio) ;
        profilePic = findViewById(R.id.usersProfilePic) ;
        profileBanner = findViewById(R.id.backgroundBanner) ;
        posts = findViewById(R.id.usersProfilePostNumber) ;

        imageList = new ArrayList<>() ;
        adapter = new ImageAdapter(imageList , this) ;
        GridLayoutManager layoutManager = new GridLayoutManager(this , 3) ;
        gridView.setLayoutManager(layoutManager) ;
        gridView.setAdapter(adapter) ;
        gridView.setHasFixedSize(true) ;
    }
    // 2 .
    private void initializeDatabase(){
        mStorage = FirebaseStorage.getInstance() ;
        mReference = mStorage.getReference().child("userUploads") ;
        mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
    }
    // 3 .
    private void setProfileModelArrayList(){
        profileFetching.setVisibility(View.VISIBLE);
        mRealDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String name = dataSnapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    if(tempUser.equals(name)){
                        dataSnapshot.getRef().child(documentFields.realtimeFields.PostedPicture).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        String postUid = snapshot1.child("postUid").getValue(String.class);
                                        String name =    snapshot1.child("userName").getValue(String.class);
                                        String file =    snapshot1.child("fileName").getValue(String.class);
                                        if (!name.isEmpty() && !file.isEmpty()) {
                                            ImageModelClass imageModelClass = new ImageModelClass(postUid, name, file);
                                            imageList.add(imageModelClass);
                                            posts.setText(String.valueOf(gridView.getAdapter().getItemCount()));
                                            profileFetching.setVisibility(View.GONE);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                else
                                    profileFetching.setVisibility(View.GONE) ;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
    }
    // 4 .
    private void setViewContents(){
        profileName.setText(tempUser) ;
        mRealDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String name = dataSnapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;

                    if(name.equals(tempUser)){
                        String gender = dataSnapshot.child(documentFields.realtimeFields.gender).getValue(String.class) ;
                        String bio = dataSnapshot.child(documentFields.realtimeFields.bio).getValue(String.class) ;
                        boolean profilePicHas = dataSnapshot.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                        boolean profileBgHas = dataSnapshot.child(documentFields.realtimeFields.hasProfileBg).getValue(Boolean.class) ;
                        if(gender.equals("male"))
                            tempUserGender = getString(R.string.malePronounce) ;
                        else if(gender.equals("female"))
                            tempUserGender = getString(R.string.femalePronounce);
                        else
                            tempUserGender = "" ;
                        if(profilePicHas) {
                            String bgFile = dataSnapshot.child("profile").child("profilePicFile").getValue(String.class) ;
                            StorageReference ref1  = FirebaseStorage.getInstance().
                                    getReference().child("userUploads").
                                    child(name).
                                    child("ProfilePicture").
                                    child(bgFile) ;
                            SetPicture(ref1 , profilePic);
                        }
                        if(profileBgHas){
                            String bgFile = dataSnapshot.child("profile").child("profileBgFile").getValue(String.class) ;
                            StorageReference ref1  = FirebaseStorage.getInstance().
                                    getReference().child("userUploads").
                                    child(name).
                                    child("ProfileBanner").
                                    child(bgFile) ;
                            SetPicture(ref1 , profileBanner);
                        }
                        if(!bio.equals(""))
                            userBio.setText(bio);
                        userGender.setText(tempUserGender);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
    }
    //  .
    private void SetPicture(StorageReference reference , ImageView imageView){
        // Fetch the download URL for the image
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use the download URL to load the image into the ImageView
                Glide.with(OthersProfileActivity.this).load(uri).into(imageView) ;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur while fetching the image
            }
        });
    }
    //  .
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

    @Override
    public void onBackPressed() {
        finish() ;
    }
}