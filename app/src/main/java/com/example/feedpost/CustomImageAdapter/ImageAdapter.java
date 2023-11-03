package com.example.feedpost.CustomImageAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.R;
import com.example.feedpost.UsersPostActivity;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<ImageModelClass> imageList;

    public ImageAdapter(ArrayList<ImageModelClass> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FrameLayout container ;
        ProgressBar progressBar ;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            container = (FrameLayout) itemView.findViewById(R.id.item_layout_container);
            imageView = (ImageView) itemView.findViewById(R.id.gridImages);
            progressBar = (ProgressBar) itemView.findViewById(R.id.imageLoader) ;
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_grid_items_profile_pics,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        setImageGridViews(imageList , holder , position) ;
        setAnimation(holder.itemView) ;
        setOnClick(holder,position) ;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
    private void setImageGridViews(ArrayList<ImageModelClass> imageList , ImageViewHolder holder , int pos) {
        holder.progressBar.setVisibility(View.VISIBLE) ;

        String name = imageList.get(pos).getUserName() ;
        String file = imageList.get(pos).getFileName() ;

        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child(DatabaseKeys.Storage.usersUploads).child(name).child(file);
        ref.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context).load(uri).into(holder.imageView) ;
            holder.progressBar.setVisibility(View.GONE) ;
        }).addOnFailureListener(e -> e.printStackTrace()) ;
    }
    private void setAnimation(View viewToAnimate) {
        viewToAnimate.setAnimation(AnimationUtils.loadAnimation(context , R.anim.scale_in_animation));
    }
    private void setOnClick(ImageViewHolder holder, int position){
        ImageModelClass model = imageList.get(position) ;
        holder.itemView.setOnClickListener(view ->{
            // Start an Intent . . . .
            Intent i = new Intent(context, UsersPostActivity.class) ;
            i.putExtra(documentFields.rawDataFields.userName, model.getUserName()) ;
            i.putExtra(documentFields.rawDataFields.adapterPosition, String.valueOf(position)) ;
            context.startActivity(i) ;
        });
    }
}
