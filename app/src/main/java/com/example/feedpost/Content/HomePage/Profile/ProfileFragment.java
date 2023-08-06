package com.example.feedpost.Content.HomePage.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Account.EditProfile.EditProfileActivity;
import com.example.feedpost.OthersProfile.ImageAdapter;
import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    // widgets
    private Button edit ;
    private ImageView profilePicture ;
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
    private ArrayList<String> imageList ;
    private ImageAdapter adapter ;
    // Firebase
    private FirebaseAuth mAuth ;
    private FirebaseStorage mStorage ;
    private StorageReference storageReference ;
    private DocumentReference mReference ;

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
        initializeWidgets(parentHolder) ;
        initializeDatabase() ;
        setOnCLickListeners(parentHolder) ;
        setViewsAndResources(parentHolder) ;


        return parentHolder ;
    }

    // 1 .
    private void initializeWidgets(View v){
        // button
        edit = v.findViewById(R.id.editButton) ;
        // imageview
        profilePicture = v.findViewById(R.id.profileDP) ;
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
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mStorage = FirebaseStorage.getInstance() ;
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
        // set resources
        FirebaseUser user = mAuth.getCurrentUser() ;
        user.reload() ;
        if(user.isEmailVerified())
            profileVerified.setImageResource(R.drawable.verified);
        String documentPath = extract.getDocument(mAuth.getCurrentUser().getEmail()) ;
        String UID = mAuth.getCurrentUser().getUid() ;
        mReference = FirebaseFirestore.getInstance().collection(documentPath).document(UID);
        loadIndicate.setVisibility(View.VISIBLE);
        mReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.getString(documentFields.UserName) ;
                currentUserBio = documentSnapshot.getString(documentFields.ProfileBio) ;
                currentUsersGender = documentSnapshot.getString(documentFields.Gender) ;
                userName.setText(currentUser) ;
                userBio.setText(currentUserBio) ;
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
                storageReference.child(currentUser).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference file : listResult.getItems()) {
                            file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //
                                    loadIndicate.setVisibility(View.GONE);
                                    imageList.add(uri.toString());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //
                                    photoGalary.setAdapter(adapter);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showCustomToast("failed to fetch photos or there are none" , view);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showCustomToast("something went wrong" , view);
            }
        }) ;
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