package com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.PictureFragment.ModelAndAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>{
    private Context context ;
    private ArrayList<PictureModel> pictureModelList ;

    public PictureAdapter(Context context, ArrayList<PictureModel> pictureModelList) {
        this.context = context;
        this.pictureModelList = pictureModelList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iVProfileHeader ;
        private TextView tVProfileHeader ;
        private Button bTNThreeDots ;
        private ImageView iVPost ;
        private Button bTNLikePost ;
        private TextView tVLikeCount ;
        private Button bTNCommentPost ;
        private TextView tVCommentCount ;
        private LinearLayout lLCommentLayout ;
        private ImageView iVProfileComment ;
        private TextView tVUserComment ;
        private ProgressBar progressBar ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iVProfileHeader = itemView.findViewById(R.id.idIVHeaderProfilePic) ;
            tVProfileHeader = itemView.findViewById(R.id.idTVHeaderProfileName) ;
            bTNThreeDots = itemView.findViewById(R.id.idBTNHeaderThreeDots) ;
            iVPost = itemView.findViewById(R.id.idIVPost) ;
            bTNLikePost = itemView.findViewById(R.id.idLikeForPost) ;
            tVLikeCount = itemView.findViewById(R.id.idLikeCount) ;
            bTNCommentPost = itemView.findViewById(R.id.idCommentForPost) ;
            tVCommentCount = itemView.findViewById(R.id.idCommentCount) ;
            lLCommentLayout = itemView.findViewById(R.id.idMyCommentLayout) ;
            iVProfileComment = itemView.findViewById(R.id.idIVCommentProfilePicture) ;
            tVUserComment = itemView.findViewById(R.id.idMyCommentForPost) ;
            progressBar = itemView.findViewById(R.id.idPBMyPosts) ;

        }
        // for unknown weired reason
        public void setComment(PictureModel model){
            tVUserComment.setText(model.getUserComment()) ;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_post_layout, parent, false) ;
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setResources(holder,position) ;
        setOnCLicks(holder,position) ;
    }

    /**
     * 1 . post id
     * 2 . user name
     * 3 . user Comment
     * 4 . user profile image file
     * 5 . post image file
     */
    private void setResources(ViewHolder holder, int position) {
        PictureModel model = pictureModelList.get(position) ;
        holder.setComment(model) ; // for unknown weired reason
        holder.tVProfileHeader.setText(String.valueOf(model.getUserName())) ;
        setProfileImageView(holder.iVProfileHeader, model.getProfilePictureFile(), model.getUserName(), holder.progressBar); ;
        setPostImageView(holder.iVPost, model.getPostPictureFile(), model.getUserName(), holder.progressBar); ;

        // for unknown weired reason
        String comment = String.valueOf(holder.tVUserComment.getText()) ;
        if(!comment.isEmpty()){
            holder.lLCommentLayout.setVisibility(View.VISIBLE);
            setProfileImageView(holder.iVProfileComment, model.getProfilePictureFile(), model.getUserName(), holder.progressBar);
        }


        setCount(holder.tVLikeCount, documentFields.realtimePostFields.Comments, model.getPostID());
        setCount(holder.tVCommentCount, documentFields.realtimePostFields.Likes, model.getPostID());
        setAnimation(holder) ;

    }

    private void setAnimation(ViewHolder holder) {
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_in_animation));
    }

    private void setProfileImageView(ImageView imageView , String file, String userName, ProgressBar progressBar) {
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference() ;
        progressBar.setVisibility(View.VISIBLE);
        if(file.equals(DatabaseKeys.Storage.user)){
            storageReference1.child(DatabaseKeys.Storage.noPicture).child(file)
                    .getDownloadUrl().
                    addOnSuccessListener(uri -> {
                        Glide.with(context).load(uri).into(imageView) ;
                        progressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace() ;
                    }) ;
        }
        else {
            storageReference1.child(DatabaseKeys.Storage.usersUploads).child(userName).child(DatabaseKeys.Storage.profilePicture).child(file)
                    .getDownloadUrl().
                    addOnSuccessListener(uri -> {
                        Glide.with(context).load(uri).into(imageView) ;
                        progressBar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace() ;
                    }) ;
        }
    }
    private void setPostImageView(ImageView imageView , String file, String userName, ProgressBar progressBar){
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference() ;
        progressBar.setVisibility(View.VISIBLE);
        storageReference1.child(DatabaseKeys.Storage.usersUploads).child(userName).child(file)
                .getDownloadUrl().
                addOnSuccessListener(uri -> {
                    Glide.with(context).load(uri).into(imageView) ;
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace() ;
                }) ;
    }
    private void setCount(TextView textView, String field, String postUID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(DatabaseKeys.Realtime.posts).child(postUID).child(field).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int count = (int) snapshot.getChildrenCount() ;
                    textView.setText(String.valueOf(count));
                }else {
                    textView.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }
    private void setOnCLicks(ViewHolder holder, int position) {
        PictureModel model = pictureModelList.get(position) ;
    }
    @Override
    public int getItemCount() {
        return pictureModelList.size() ;
    }
}
