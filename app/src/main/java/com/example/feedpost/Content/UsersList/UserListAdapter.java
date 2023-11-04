package com.example.feedpost.Content.UsersList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.OthersProfile.OthersProfileActivity;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private Context context ;
    private ArrayList<UserListModel> dataLists ;
    public UserListAdapter(Context context, ArrayList<UserListModel> dataLists) {
        this.context = context;
        this.dataLists = dataLists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView profileImageView ;
        private MaterialTextView profileNameView ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileIMG) ;
            profileNameView  = itemView.findViewById(R.id.profileUserName) ;
        }
    }
    public void filterList(ArrayList<UserListModel> filterList) {
        dataLists = filterList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.custom_card_layout , parent , false) ;

        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setResources(holder,position) ;
        setOnCLick(holder,position) ;
        setAnimation(holder.itemView) ;
    }
    private void setResources(ViewHolder holder, int position){
        UserListModel dataModel = dataLists.get(position) ;
        holder.profileNameView.setText(dataModel.getProfileName());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference() ;
        if(dataModel.getProfilePic().equals(DatabaseKeys.Storage.user)){
            storageReference.child(DatabaseKeys.Storage.noPicture)
                    .child(dataModel.getProfilePic())
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(context).load(uri).into(holder.profileImageView);
                    });
        }
        else {
            storageReference.child(DatabaseKeys.Storage.usersUploads).child(dataModel.getProfileName()).child(DatabaseKeys.Storage.profilePicture)
                    .child(dataModel.getProfilePic())
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(context).load(uri).into(holder.profileImageView);
                    });
        }
    }
    private void setOnCLick(ViewHolder holder, int position){
        UserListModel dataModel = dataLists.get(position) ;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show users uploaded images and activities on realtime data channel
                String userName = dataLists.get(holder.getAdapterPosition()).getProfileName() ;
                Intent tappedUser = new Intent(context , OthersProfileActivity.class) ;
                tappedUser.putExtra(documentFields.rawDataFields.currentUserName , userName) ;
                context.startActivity(tappedUser) ;
            }
        });
    }
    private void setAnimation(View viewToAnimate){
        viewToAnimate.setAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_in_animation)) ;
    }
    @Override
    public int getItemCount() {
        return dataLists.size() ;
    }
}
