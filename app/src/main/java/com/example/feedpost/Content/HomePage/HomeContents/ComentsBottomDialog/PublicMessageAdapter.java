package com.example.feedpost.Content.HomePage.HomeContents.ComentsBottomDialog;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PublicMessageAdapter extends RecyclerView.Adapter<PublicMessageAdapter.ViewHolder>{
    private Context context ;
    private ArrayList<PublicMessageModel> messageList ;

    public PublicMessageAdapter(Context context, ArrayList<PublicMessageModel> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfile ;
        private TextView tvMessage ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.idIVProfilePublicMessage) ;
            tvMessage = itemView.findViewById(R.id.idPublicMessage) ;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.public_message_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the model
        PublicMessageModel model = messageList.get(position) ;
        // setting message text area
        holder.tvMessage.setText(model.getMessage());
        // setting profile picture
        String name = model.getName() ;
        setProfilePicture(name,holder.ivProfile) ;
    }

    @Override
    public int getItemCount() {
        return messageList.size() ;
    }
    // custom methods
    private void setProfilePicture(String name, ImageView imageView){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        String UID = users.getKey() ;
                        String currentName = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                        if (name.equals(currentName)){
                            boolean hasProfilePic = users.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                            if (hasProfilePic){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child(DatabaseKeys.Realtime.users).child(UID).child(DatabaseKeys.Realtime.profile).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            String picFile = snapshot.child(DatabaseKeys.Realtime._profile_.profilePicFile).getValue(String.class) ;
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                                                    .child("userUploads").child(name).child("ProfilePicture").child(picFile);
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Glide.with(context).load(uri).dontAnimate().into(imageView);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace() ;
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        throw error.toException() ;
                                    }
                                });
                            }
                            else
                                imageView.setBackgroundColor(context.getColor(R.color.blue_dark));
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
}
