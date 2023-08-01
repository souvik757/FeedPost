package com.example.feedpost.OthersProfile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.ImageActivity.ImageActivity;
import com.example.feedpost.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> imageList;


    public ImageAdapter(ArrayList<String> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }
    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_grid_items_profile_pics,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        // loading the images from the position
        Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gridImages);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap b = getBitmapFromViewUsingCanvas(v) ;
                    Intent imageIntent = new Intent(context , ImageActivity.class) ;
                    imageIntent.putExtra("selectedPost" , b) ;
                    context.startActivity(imageIntent) ;
                }
            });
        }
    }
    private Bitmap getBitmapFromViewUsingCanvas(View view) {
        // Create a new Bitmap object with the desired width and height
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a new Canvas object using the Bitmap
        Canvas canvas = new Canvas(bitmap);

        // Draw the View into the Canvas
        view.draw(canvas);

        // Return the resulting Bitmap
        return bitmap;
    }
}
