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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.UsersList.UserListModel;
import com.example.feedpost.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
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
    private RecyclerView gridView ;
    private ProgressBar profileFetching ;
    private TextView profileName ;
    private TextView userGender ;
    private ImageView profilePic ;
    // resources
    private String tempUser ;
    private String tempUserGender ;
    private ArrayList<String> imageList ;
    private ImageAdapter adapter ;
    // Firebase
    private FirebaseStorage mStorage ;
    private StorageReference mReference ;
    private DatabaseReference mRealDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        initializeWidgetsAndVariables() ;
        initializeDatabase() ;
        setProfileModelArrayList() ;
        setViewContents() ;
    }
    // 1 .
    private void initializeWidgetsAndVariables(){
        tempUser = getIntent().getStringExtra("TappedUsersName") ;
        gridView = findViewById(R.id.usersUploadGrid) ;
        profileFetching = findViewById(R.id.profileFetching) ;
        profileName = findViewById(R.id.usersProfileName) ;
        userGender = findViewById(R.id.usersProfileGender) ;
        profilePic = findViewById(R.id.usersProfilePic) ;

        imageList = new ArrayList<>() ;
        adapter = new ImageAdapter(imageList , this) ;
        GridLayoutManager layoutManager = new GridLayoutManager(this , 3) ;
        gridView.setLayoutManager(layoutManager) ;
    }
    // 2 .
    private void initializeDatabase(){
        mStorage = FirebaseStorage.getInstance() ;
        mReference = mStorage.getReference().child("userUploads") ;
        mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
    }
    // 3 .

    private void setProfileModelArrayList(){
        profileFetching.setVisibility(View.VISIBLE) ;
        mReference.child(tempUser).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference file : listResult.getItems()) {
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //
                            profileFetching.setVisibility(View.GONE);
                            imageList.add(uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //
                            gridView.setAdapter(adapter);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showCustomToast("failed to fetch photos or there are none");
            }
        });
    }
    // 4 .
    private void setViewContents(){
        profileName.setText(tempUser) ;
        mRealDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String name = dataSnapshot.child("name").getValue(String.class) ;
                    String gender = dataSnapshot.child("gender").getValue(String.class) ;
                    if(name.equals(tempUser)){
                        if(gender.equals("male")) {
                            tempUserGender = "(he/him)";
                            profilePic.setImageResource(R.drawable.male) ;
                        }
                        else if(gender.equals("female")) {
                            tempUserGender = "(she/her)";
                            profilePic.setImageResource(R.drawable.female) ;
                        }
                        else {
                            tempUserGender = " ";
                            profilePic.setImageResource(R.drawable.skip) ;
                        }
                    }
                    userGender.setText(tempUserGender);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
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

    public void finish(View view) {
        finish() ;
    }
}