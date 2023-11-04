package com.example.feedpost.OthersProfile;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.feedpost.CustomImageAdapter.ImageAdapter;
import com.example.feedpost.CustomImageAdapter.ImageModelClass;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Have to change totally ....
 */
public class OthersProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    // layouts
    private SwipeRefreshLayout swipeRefreshLayout ;
    // widgets
    private ScrollView scrollView ;
    private RecyclerView gridView ;
    private ProgressBar profileFetching ;
    private TextView profileName ;
    private TextView userGender ;
    private TextView userFollower ;
    private TextView userFollowing ;
    private TextView userBio ;
    private TextView posts ;
    private ImageView profilePic ;
    private ImageView profileBanner ;
    private ExtendedFloatingActionButton followBtn ;
    // resources
    private String tempUser ;
    private String tempUserGender ;
    private ArrayList<ImageModelClass> imageList ;
    private ImageAdapter adapter ;
    // Firebase
    private FirebaseStorage mStorage ;
    private FirebaseAuth mAuth ;
    private StorageReference mReference ;
    private DocumentReference storeReference ;
    private DatabaseReference mRealDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        tempUser = getIntent().getStringExtra("TappedUsersName") ;
        init() ;

        swipeRefreshLayout = findViewById(R.id.slideDownToRefresh) ;
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        init() ;
        swipeRefreshLayout.setRefreshing(false);
    }

    private void init(){
        initializeWidgetsAndVariables() ;
        initializeDatabase() ;
        setProfileModelArrayList() ;
        setViewContents() ;
        followEvent() ;
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
        userFollower = findViewById(R.id.usersProfileFollowerNumber) ;
        userFollowing = findViewById(R.id.usersProfileFollowingNumber) ;
        profilePic = findViewById(R.id.usersProfilePic) ;
        profileBanner = findViewById(R.id.backgroundBanner) ;
        posts = findViewById(R.id.usersProfilePostNumber) ;
        followBtn = findViewById(R.id.followUser) ;

        imageList = new ArrayList<>() ;
        adapter = new ImageAdapter(imageList , this) ;
        GridLayoutManager layoutManager = new GridLayoutManager(this , 3) ;
        gridView.setLayoutManager(layoutManager) ;
        gridView.setAdapter(adapter) ;
        gridView.setHasFixedSize(true) ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
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
                for (DataSnapshot userSnap : snapshot.getChildren()){
                    String name = userSnap.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    if(tempUser.equals(name)){
                        // 👇 this part needs improvement
                        userSnap.getRef().child(documentFields.realtimeFields.PostedPicture).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    for (DataSnapshot userPostSnap : snapshot.getChildren()) {
                                        String postUid = userPostSnap.child("postUid").getValue(String.class);
                                        String name =    userPostSnap.child("userName").getValue(String.class);
                                        String file =    userPostSnap.child("fileName").getValue(String.class);
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
                        // 👆 - - - - - - needs modification
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
    }
    // 4 .
    private void followEvent(){
        String myID = mAuth.getCurrentUser().getUid() ;

        // check if already following
        mRealDatabase.child(DatabaseKeys.Realtime.users).child(myID).child(DatabaseKeys.Realtime.following).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot usersSnapShots : snapshot.getChildren()) {
                        String name = usersSnapShots.child("name").getValue(String.class) ;
                        if(name.equals(tempUser)){
                            followBtn.setBackgroundColor(getResources().getColor(R.color.blue_dark));
                            followBtn.setText("following");
                            followBtn.setClickable(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
        // progress if not follow
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update following of current User
                updateMyFollowing(myID) ;
                // update follower of clicked user
                String documentPath = extract.getDocument(mAuth.getCurrentUser().getEmail()) ;
                String UID = mAuth.getCurrentUser().getUid() ;
                storeReference = FirebaseFirestore.getInstance().collection(documentPath).document(UID);
                storeReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString(documentFields.UserName) ;
                        updateOthersFollower(myID , name) ;
                        followBtn.setText("following");
                        followBtn.setBackgroundColor(getApplicationContext().getColor(R.color.blue_dark)) ;
                        followBtn.setTextColor(getApplicationContext().getColor(R.color.white)) ;
                        followBtn.setClickable(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace() ;
                    }
                }) ;
            }
        });
    }

    /**
     * updating my followings in realtime db
     */
    private void updateMyFollowing(String UID) {
        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String uid = users.getKey() ;
                    // confirm uid by name
                    String name= users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    if(tempUser.equals(name)){
                        // add to my following
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference() ;
                        ref.child(DatabaseKeys.Realtime.users).child(UID).child(DatabaseKeys.Realtime.following).child(uid)
                                .child("name").setValue(name) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        }); ;
    }

    /**
     * update others follower
     */
    private void updateOthersFollower(String UID,String myName) {
        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String uid = users.getKey() ;
                    String name = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    if(tempUser.equals(name)){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference() ;
                        ref.child(DatabaseKeys.Realtime.users).child(uid).child(DatabaseKeys.Realtime.follower).child(UID).
                                child("name").setValue(myName) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); ;
    }

    // 5 .
    private void setViewContents(){
        profileName.setText(tempUser) ;
        setFollower() ;
        setFollowing() ;
        mRealDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
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
        });
    }

    private void setFollowing() {
        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String name = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    if(tempUser.equals(name)){
                        users.getRef().child(DatabaseKeys.Realtime.following).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userFollowing.setText(String.valueOf((int)snapshot.getChildrenCount())) ;
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
        });
    }

    private void setFollower() {
        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String name = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    if(tempUser.equals(name)){
                        users.getRef().child(DatabaseKeys.Realtime.follower).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userFollower.setText(String.valueOf((int)snapshot.getChildrenCount())) ;
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
        });
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

    @Override
    public void onBackPressed() {
        finish() ;
    }
}