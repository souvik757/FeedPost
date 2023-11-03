package com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.PictureFragment.ModelAndAdapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PictureAdapter {
    private Context context ;
    private ArrayList<PictureModel> pictureModelList ;

    public PictureAdapter(Context context, ArrayList<PictureModel> pictureModelList) {
        this.context = context;
        this.pictureModelList = pictureModelList;
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
