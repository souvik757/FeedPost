package com.example.feedpost.Content.UsersList;

import android.content.Context;
import android.content.Intent;
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

import com.example.feedpost.OthersProfile.OthersProfileActivity;
import com.example.feedpost.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private Context context ;
    private ArrayList<UserListModel> dataLists ;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

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
    public void filterList(ArrayList<UserListModel> filterlist) {
        dataLists = filterlist;
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
        UserListModel dataModel = dataLists.get(position) ;

        holder.profileImageView.setImageResource(dataModel.getProfilePic());
        holder.profileNameView.setText(dataModel.getProfileName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show users uploaded images and activities on realtime data channel
                String userName = dataLists.get(holder.getAdapterPosition()).getProfileName() ;
                Intent tappedUser = new Intent(context , OthersProfileActivity.class) ;
                tappedUser.putExtra("TappedUsersName" , userName) ;
                context.startActivity(tappedUser) ;
            }
        });
        setAnimation(holder.itemView , position) ;
    }

    @Override
    public int getItemCount() {
        return dataLists.size() ;
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_in_animation);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
