package com.example.feedpost.Content.HomePage.Profile.RefinedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class RefinedUserAdapter extends RecyclerView.Adapter<RefinedUserAdapter.ViewHolder>{
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
        holder.tvProfile.setText(String.valueOf(model.getName())) ;

    }

    @Override
    public int getItemCount() {
        return userList.size() ;
    }
}
