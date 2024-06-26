package com.example.feedpost.Content.HomePage.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.feedpost.Account.EditAccount.EditProfileActivity;
import com.example.feedpost.Content.HomePage.Profile.RefinedList.RefinedModelClass;
import com.example.feedpost.Content.HomePage.Profile.RefinedList.RefinedUserAdapter;
import com.example.feedpost.CustomImageAdapter.ImageAdapter;
import com.example.feedpost.CustomImageAdapter.ImageModelClass;
import com.example.feedpost.ImageActivity.ImageUploader.ImageUploadBackGround;
import com.example.feedpost.ImageActivity.ImageUploader.ImageUploadForProfilePic;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    View parentHolder ;
    // layouts
    private SwipeRefreshLayout swipeRefreshLayout ;
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
    private TextView nullProfilePic ;
    private TextView nullProfileBg ;
    private RecyclerView photoGalary ;
    private ProgressBar loadIndicate ;
    private LinearLayout followerLL ;
    private LinearLayout followingLL ;

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
        init() ;

        swipeRefreshLayout = parentHolder.findViewById(R.id.pullToRefresh) ;
        swipeRefreshLayout.setOnRefreshListener(this) ;
        return parentHolder ;
    }

    @Override
    public void onRefresh() {
        init() ;
        swipeRefreshLayout.setRefreshing(false);
    }

    // 0 .
    private void init(){
        initializeWidgets(parentHolder) ;
        initializeDatabase() ;
        setOnCLickListeners() ;
        setViewsAndResources(parentHolder) ;
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
        nullProfilePic = v.findViewById(R.id.idTvNoProfilePicture) ;
        nullProfileBg = v.findViewById(R.id.idTvNoProfileBg) ;
        // layout
        followerLL = v.findViewById(R.id.idFollowerLayout) ;
        followingLL = v.findViewById(R.id.idFollowingLayout) ;
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
    private void setOnCLickListeners(){
        // 1 .
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext() , EditProfileActivity.class)) ;
            }
        });
        // 2 .
        profilePicture.setOnClickListener(view ->{
            getActivity().startActivity(new Intent(getContext(), ImageUploadForProfilePic.class));
        });
        // 3 .
        profileBanner.setOnClickListener(view ->{
            getActivity().startActivity(new Intent(getContext(), ImageUploadBackGround.class));
        });
        // on click 'followerLL' & 'followingLL' pops up a "BottomDialogLayout" which shows follower's & following's of current user
        // 4 .
        followerLL.setOnClickListener(view ->{
            initBottomDialog("follower");
        });
        // 5 .
        followingLL.setOnClickListener(view ->{
            initBottomDialog("following");
        });
    }
    // 4 .
    private void setViewsAndResources(View view){
        loadIndicate.setVisibility(View.VISIBLE);
        FirebaseUser user = mAuth.getCurrentUser() ;
        user.reload() ;
        if(user.isEmailVerified()) {
            profileVerified.setImageResource(R.drawable.verified);
        }
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        // set resources
        setPosts(UID) ;
        setFollowers(UID) ;
        setFollowings(UID) ;
        setBioGenderName(UID) ;
        setProfileImageViews(UID);
        setImageGridViews(UID);
    }

    private void setPosts(String UID) {
        realTimeRef.child(DatabaseKeys.Realtime.users).child(UID).child(documentFields.realtimeFields.PostedPicture).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount() ;
                userPost.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        }) ;
    }

    private void setFollowers(String UID){
        realTimeRef.child(DatabaseKeys.Realtime.users).child(UID).child(DatabaseKeys.Realtime.follower).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount() ;
                userFollowers.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }
    private void setFollowings(String UID){
        realTimeRef.child(DatabaseKeys.Realtime.users).child(UID).child(DatabaseKeys.Realtime.following).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = (int) snapshot.getChildrenCount() ;
                        userFollowings.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException() ;
                    }
                });
    }
    private void setBioGenderName(String Id){
        realTimeRef.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()){
                    String currId = user.getKey() ;
                    if(Id.equals(currId)) {
                        String bio, name, gender;
                        bio = user.child(documentFields.realtimeFields.bio).getValue(String.class);
                        name = user.child(documentFields.realtimeFields.fullName).getValue(String.class);
                        gender = user.child(documentFields.realtimeFields.gender).getValue(String.class);

                        userBio.setText(bio);
                        userName.setText(name);
                        userGender.setText(gender);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw  error.toException() ;
            }
        });
    }
    private void setProfileImageViews(String Id){
        realTimeRef.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String currId = users.getKey() ;
                    if(Id.equals(currId)){
                        currentUser = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                        boolean hasPicFile , hasBgFile  ;
                        hasPicFile = users.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                        hasBgFile = users.child(documentFields.realtimeFields.hasProfileBg).getValue(Boolean.class) ;
                        if(hasPicFile){
                            nullProfilePic.setVisibility(View.GONE) ;
                            String profilePicFile = users.child(DatabaseKeys.Realtime.profile).child(DatabaseKeys.Realtime._profile_.profilePicFile).getValue(String.class) ;
                            StorageReference ref1 = FirebaseStorage.getInstance().getReference().child(DatabaseKeys.Storage.usersUploads)
                                    .child(currentUser).child(DatabaseKeys.Storage.profilePicture).child(profilePicFile) ;
                            SetPicture(ref1 , profilePicture) ;
                        }
                        else
                            nullProfilePic.setVisibility(View.VISIBLE) ;

                        if(hasBgFile){
                            nullProfileBg.setVisibility(View.GONE) ;
                            String profileBgFile  = users.child(DatabaseKeys.Realtime.profile).child(DatabaseKeys.Realtime._profile_.profileBgFile).getValue(String.class) ;
                            StorageReference ref1 = FirebaseStorage.getInstance().getReference().child(DatabaseKeys.Storage.usersUploads)
                                    .child(currentUser).child(DatabaseKeys.Storage.profileBanner).child(profileBgFile) ;
                            SetPicture(ref1 , profileBanner) ;
                        }
                        else
                            nullProfileBg.setVisibility(View.VISIBLE) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }
    private void setImageGridViews(String Id){
        realTimeRef.child(DatabaseKeys.Realtime.users).child(Id).
                child(documentFields.realtimeFields.PostedPicture).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int count = (int) snapshot.getChildrenCount() ;
                    userPost.setText(String.valueOf(count));
                    imageList.clear() ;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String postUid = dataSnapshot.child("postUid").getValue(String.class);
                        String name = dataSnapshot.child("userName").getValue(String.class);
                        String file = dataSnapshot.child("fileName").getValue(String.class);
                        ImageModelClass imageModelClass = new ImageModelClass(postUid, name, file);
                        imageList.add(imageModelClass);
                    }
                    loadIndicate.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                else
                    loadIndicate.setVisibility(View.GONE) ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
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
                e.printStackTrace() ;
            }
        });
    }
    // 5 .
    private void initBottomDialog(String parameter){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext()) ;
        bottomSheetDialog.setContentView(R.layout.refined_list_bottom_dialog_layout) ;
        // widgets
        TextView txtParam = bottomSheetDialog.findViewById(R.id.txtParameter) ;
        RecyclerView userListRV = bottomSheetDialog.findViewById(R.id.idRVRefinedList) ;
        // resources for rv
        ArrayList<RefinedModelClass> userArrayList = new ArrayList<>() ;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext() , 2) ;
        RefinedUserAdapter refinedUserAdapter = new RefinedUserAdapter(getContext(), userArrayList) ;
        userListRV.setLayoutManager(layoutManager) ;
        userListRV.setAdapter(refinedUserAdapter) ;
        // setting views
        setResources(parameter,txtParam, userArrayList,refinedUserAdapter) ;
        // on click & dismiss
        setOnCLick(bottomSheetDialog) ;
        // show the layout
        bottomSheetDialog.show() ;
    }

    private void setResources(String parameter, TextView txtParam, ArrayList<RefinedModelClass> userArrayList, RefinedUserAdapter refinedUserAdapter) {
        txtParam.setText(String.valueOf(parameter)) ;
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference() ;
        ref.child(DatabaseKeys.Realtime.users).child(UID).child(parameter).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        String uid = users.getKey() ;
                        String name = users.child("name").getValue(String.class) ;
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                        ref1.child(DatabaseKeys.Realtime.users).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    boolean hasProfilePic = snapshot.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                                    if(hasProfilePic){
                                        String profilePicFile = snapshot
                                                .child(DatabaseKeys.Realtime.profile)
                                                .child(DatabaseKeys.Realtime._profile_.profilePicFile).getValue(String.class) ;
                                        RefinedModelClass model = new RefinedModelClass(parameter, name, profilePicFile) ;
                                        userArrayList.add(model) ;
                                    }
                                    else {
                                        RefinedModelClass model = new RefinedModelClass(parameter, name, "") ;
                                        userArrayList.add(model) ;
                                    }
                                    refinedUserAdapter.notifyDataSetChanged() ;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                throw error.toException() ;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }

    private void setOnCLick(BottomSheetDialog bottomSheetDialog) {
        bottomSheetDialog.setOnDismissListener(view ->{

        });
    }
}