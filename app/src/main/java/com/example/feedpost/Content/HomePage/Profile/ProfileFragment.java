package com.example.feedpost.Content.HomePage.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.feedpost.Account.EditAccount.EditProfileActivity;
import com.example.feedpost.CustomImageAdapter.ImageAdapter;
import com.example.feedpost.CustomImageAdapter.ImageModelClass;
import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    View parentHolder ;
    private ScrollView scrollview ;
    // widgets
    private FloatingActionButton edit ;
    private ImageView profilePicture ;
    private ImageView profileBanner ;
    private ImageView profileVerified ;
    private TextView userName ;
    private TextView userGender ;
    private TextView userBio ;
    private TextView userPost ;
    private TextView userFollowers ;
    private TextView userFollowings ;
    private RecyclerView photoGalary ;
    private ProgressBar loadIndicate ;
    // resources
    private String currentUser ;
    private String currentUserBio ;
    private String currentUsersGender ;
    private ArrayList<ImageModelClass> imageList ;
    private ImageAdapter adapter ;
    // Firebase
    private FirebaseAuth mAuth ;
    private FirebaseStorage mStorage ;
    private StorageReference storageReference ;
    private DocumentReference mReference ;
    private DatabaseReference realTimeRef ;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentHolder = inflater.inflate(R.layout.fragment_profile, container, false);
        // Action bar activities
        ((AppCompatActivity)getActivity()).getSupportActionBar().setIcon(R.drawable.feedpost) ;

        initializeWidgets(parentHolder) ;
        initializeDatabase() ;
        setOnCLickListeners(parentHolder) ;
        setViewsAndResources(parentHolder) ;

        return parentHolder ;
    }

    // 1 .
    private void initializeWidgets(View v){
        // scrollview
        scrollview = v.findViewById(R.id.ScrollView) ;
        scrollview.fullScroll(View.FOCUS_DOWN);
        scrollview.setSmoothScrollingEnabled(true);
        // button
        edit = v.findViewById(R.id.editButton) ;
        // imageview
        profilePicture = v.findViewById(R.id.profileDP) ;
        profileBanner = v.findViewById(R.id.profileBanner) ;
        profileVerified = v.findViewById(R.id.verified) ;
        // textview
        userName = v.findViewById(R.id.userName) ;
        userGender = v.findViewById(R.id.userPronounce) ;
        userBio = v.findViewById(R.id.userBio) ;
        userPost = v.findViewById(R.id.postNumber) ;
        userFollowers = v.findViewById(R.id.followerNumber) ;
        userFollowings = v.findViewById(R.id.followingNumber) ;
        // recycler view
        photoGalary = v.findViewById(R.id.usersPhotoGalary) ;
        // loading bar
        loadIndicate = v.findViewById(R.id.photoFetching) ;
        // resources
        imageList = new ArrayList<>() ;
        adapter = new ImageAdapter(imageList , getContext()) ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext() , 3) ;
        photoGalary.setLayoutManager(gridLayoutManager) ;
        photoGalary.setAdapter(adapter);
        photoGalary.setHasFixedSize(true);
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mStorage = FirebaseStorage.getInstance() ;
        realTimeRef = FirebaseDatabase.getInstance().getReference() ;
        storageReference = mStorage.getReference().child("userUploads") ;
    }
    // 3 .
    private void setOnCLickListeners(View view){
        // 1 .
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext() , EditProfileActivity.class)) ;
            }
        });
    }
    // 4 .
    private void setViewsAndResources(View view){
        loadIndicate.setVisibility(View.VISIBLE);
        // dummy views
        userFollowers.setText("0");
        userFollowings.setText("0");

        photoGalary.setNestedScrollingEnabled(false) ;
        // set resources
        FirebaseUser user = mAuth.getCurrentUser() ;
        user.reload() ;
        if(user.isEmailVerified()) {
            profileVerified.setImageResource(R.drawable.verified);
        }
        String documentPath = extract.getDocument(mAuth.getCurrentUser().getEmail()) ;
        String UID = mAuth.getCurrentUser().getUid() ;
        mReference = FirebaseFirestore.getInstance().collection(documentPath).document(UID);
        mReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.getString(documentFields.UserName) ;
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currentUser+"'s"+" "+"Profile");
                currentUserBio = documentSnapshot.getString(documentFields.ProfileBio) ;
                currentUsersGender = documentSnapshot.getString(documentFields.Gender) ;
                userName.setText(currentUser) ;
                userBio.setText(currentUserBio) ;
                String profilePicFile = documentSnapshot.getString(documentFields.ProfilePic) ;
                String profileBgFile = documentSnapshot.getString(documentFields.ProfileBG) ;
                if(profilePicFile.equals("")){
                    if(currentUsersGender.equals("male")) {
                        userGender.setText(getResources().getString(R.string.malePronounce));
                        profilePicture.setImageResource(R.drawable.male) ;
                    }
                    else if(currentUsersGender.equals("female")) {
                        userGender.setText(getResources().getString(R.string.femalePronounce));
                        profilePicture.setImageResource(R.drawable.female) ;
                    }
                    else {
                        userGender.setText(" ");
                        profilePicture.setImageResource(R.drawable.skip) ;
                    }
                }
                else {
                    if(currentUsersGender.equals("male")) {
                        userGender.setText("(he/him)");
                    }
                    else if(currentUsersGender.equals("female")) {
                        userGender.setText("(she/her)");
                    }
                    else {
                        userGender.setText(" ");
                    }
                    StorageReference ref  = FirebaseStorage.getInstance().
                            getReference().child("userUploads").
                            child(currentUser).
                            child("ProfilePicture").
                            child(profilePicFile) ;
                    SetPicture(ref,profilePicture) ;
                }
                if(!profileBgFile.equals("")){
                    StorageReference ref  = FirebaseStorage.getInstance().
                            getReference().child("userUploads").
                            child(currentUser).
                            child("ProfileBanner").
                            child(profileBgFile) ;
                    SetPicture(ref,profileBanner) ;
                }
                setImageGridViews(UID , view);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showCustomToast("something went wrong" , view);
            }
        }) ;
    }
    private void setImageGridViews(String Id ,View view){
        realTimeRef.child("users").child(Id).
                child(documentFields.realtimeFields.PostedPicture).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String postUid = dataSnapshot.child("postUid").getValue(String.class);
                        String name = dataSnapshot.child("userName").getValue(String.class);
                        String file = dataSnapshot.child("fileName").getValue(String.class);
                        ImageModelClass imageModelClass = new ImageModelClass(postUid, name, file);
                        imageList.add(imageModelClass);
                        userPost.setText(String.valueOf(photoGalary.getAdapter().getItemCount()));
                        loadIndicate.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
                else
                    loadIndicate.setVisibility(View.GONE) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SetPicture(StorageReference reference , ImageView imageView){
        // Fetch the download URL for the image
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use the download URL to load the image into the ImageView
                Glide.with(parentHolder).load(uri).into(imageView) ;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur while fetching the image
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //  .
    private void showCustomToast(String message , View v){
        LayoutInflater inflater = getLayoutInflater() ;
        View layout = inflater.inflate(R.layout.custom_toast_layout , v.findViewById(R.id.containerToast)) ;
        ImageView img = layout.findViewById(R.id.imageViewToast) ;
        img.setImageResource(R.drawable.warning);
        TextView txt = layout.findViewById(R.id.textViewToast) ;
        txt.setText(message);
        Toast toast = new Toast(getContext()) ;
        toast.setDuration(Toast.LENGTH_LONG) ;
        toast.setView(layout);
        toast.show() ;
    }
}