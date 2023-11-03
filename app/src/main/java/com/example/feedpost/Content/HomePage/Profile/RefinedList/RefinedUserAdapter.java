package com.example.feedpost.Content.HomePage.Profile.RefinedList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.OthersProfile.OthersProfileActivity;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RefinedUserAdapter extends RecyclerView.Adapter<RefinedUserAdapter.ViewHolder> {
    private Context context ;
    private ArrayList<RefinedModelClass> userList ;

    public RefinedUserAdapter(Context context, ArrayList<RefinedModelClass> userList) {
        this.context = context;
        this.userList = userList;
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile ;
        private TextView tvProfile ;
        private ExtendedFloatingActionButton btnToggle ; // 'tricky part'
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.idIVProfilePicture) ;
            tvProfile = itemView.findViewById(R.id.idTVProfileName) ;
            btnToggle = itemView.findViewById(R.id.idBtnFollowToggle) ;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_following_list_layout, parent, false) ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RefinedModelClass model = userList.get(position) ;
        setViews(model,holder) ;
        setOnCLick(model,holder) ;
    }

    private void setViews(RefinedModelClass model, ViewHolder holder) {
        String name = model.getName() ;
        String imgFile = model.getImgFile() ;
        holder.tvProfile.setText(String.valueOf(name)) ;
        if(!imgFile.equals("")){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(DatabaseKeys.Storage.usersUploads)
                    .child(name).child(DatabaseKeys.Storage.profilePicture).child(imgFile) ;
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(holder.imgProfile) ;
                }
            }) ;
        }
        else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(DatabaseKeys.Storage.noPicture)
                    .child(DatabaseKeys.Storage.user) ;
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(holder.imgProfile) ;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            }) ;
        }
    }
    private void setOnCLick(RefinedModelClass model, ViewHolder holder){
        new FollowEvent().followEvent(holder.btnToggle, model.getName()) ;
        holder.imgProfile.setOnClickListener(view ->{
            navigateToProfile(model.getName()) ;
        });
        holder.tvProfile.setOnClickListener(view ->{
            navigateToProfile(model.getName()) ;
        });
    }
    class FollowEvent {
        private void followEvent(ExtendedFloatingActionButton followBtn, String tempUser){
            FirebaseAuth mAuth = FirebaseAuth.getInstance() ;
            String myID = mAuth.getCurrentUser().getUid() ;
            DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
            // see if user is already present in following list
            mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot users : snapshot.getChildren()){
                            String uid = users.getKey() ;
                            String currentName = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                            if(currentName.equals(tempUser)){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child(DatabaseKeys.Realtime.users).child(myID).child(DatabaseKeys.Realtime.following).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            followBtn.setText("following");
                                            followBtn.setBackgroundColor(context.getColor(R.color.blue));
                                        }
                                        else {
                                            followBtn.setText("follow");
                                            followBtn.setBackgroundColor(context.getColor(R.color.green));
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException() ;
                }
            });
            // click event
            followBtn.setOnClickListener(view ->{
                String textOnFollowBtn = followBtn.getText().toString() ;
                if(textOnFollowBtn.equals("follow")){
                    followBtn.setText("following");
                    followBtn.setBackgroundColor(context.getColor(R.color.blue));
                    // add tempUser to my following
                    updateMyFollowing(myID,tempUser) ;
                    // add me in tempUser's follower
                    mRealDatabase.child(DatabaseKeys.Realtime.users).child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String myName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                                updateOthersFollower(myID,tempUser,myName);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            throw error.toException() ;
                        }
                    });
                }
                if(textOnFollowBtn.equals("following")) {
                    followBtn.setText("follow");
                    followBtn.setBackgroundColor(context.getColor(R.color.green));
                    // remove tempUser from my following
                    removeUser(myID,tempUser) ;
                    // remove me from tempUser's follower
                    mRealDatabase.child(DatabaseKeys.Realtime.users).child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String myName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                                removeMe(myID,tempUser) ;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            throw error.toException() ;
                        }
                    });
                }
            });
        }
        private void updateMyFollowing(String UID , String tempUser) {
            DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
            mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot users : snapshot.getChildren()){
                        String uid = users.getKey() ;
                        // confirm uid by name
                        String name = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
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
        private void updateOthersFollower(String UID,String tempUser, String myName) {
            DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
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
            });
        }
        private void removeUser(String myUID, String tempUser){
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference() ;
            ref1.child(DatabaseKeys.Realtime.users).child(myUID).child(DatabaseKeys.Realtime.following).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot followingUsers : snapshot.getChildren()){
                            String uid = followingUsers.getKey() ;
                            String currentUser = followingUsers.child("name").getValue(String.class) ;
                            if(currentUser.equals(tempUser)){
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                                ref2.child(DatabaseKeys.Realtime.users).child(myUID).child(DatabaseKeys.Realtime.following).child(uid).removeValue() ;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException() ;
                }
            });
        }
        private void removeMe(String myUID, String tempUser){
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference() ;
            ref1.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot users : snapshot.getChildren()){
                        String uid = users.getKey() ;
                        String currentUserName = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                        if(tempUser.equals(currentUserName)){
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                            ref2.child(DatabaseKeys.Realtime.users).child(uid).child(DatabaseKeys.Realtime.follower).child(myUID).removeValue() ;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException() ;
                }
            });
        }
    }
    private void navigateToProfile(String name){
        Intent i = new Intent(context , OthersProfileActivity.class) ;
        i.putExtra(documentFields.rawDataFields.currentUserName , name) ;
        context.startActivity(i) ;
    }
    @Override
    public int getItemCount() {
        return userList.size() ;
    }
}

