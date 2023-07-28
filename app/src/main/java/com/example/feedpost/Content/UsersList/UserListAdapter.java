package com.example.feedpost.Content.UsersList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedpost.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private Context context ;
    private ArrayList<UserListModel> dataLists ;

    public UserListAdapter(Context context, ArrayList<UserListModel> dataLists) {
        this.context = context;
        this.dataLists = dataLists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImageView ;
        private MaterialTextView profileNameView ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.profileIMG) ;
            profileNameView  = itemView.findViewById(R.id.profileUserName) ;
        }

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
        UserListModel dataModel = dataLists.get(position) ;

        holder.profileImageView.setImageResource(dataModel.getProfilePic());
        holder.profileNameView.setText(dataModel.getProfileName());
    }

    @Override
    public int getItemCount() {
        return dataLists.size() ;
    }
}
