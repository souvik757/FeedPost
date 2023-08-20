package com.example.feedpost.Content.HomePage.HomeContents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.OthersProfile.OthersProfileActivity;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * @implNote
 * This adapter class is the main driver code
 * - i. that will be interacting with database in realtime
 * - ii. handling dynamic responses
 * - iii. updating the database at the same time
 * - this is possibly one of the most challenging part of this application .
 */
public class PostDataAdapter extends RecyclerView.Adapter<PostDataAdapter.PostViewHolder> {
    private int likeToggle = -1 ;
    private Context context ;
    private ArrayList<PostDataModel> postsList ;


    public PostDataAdapter(Context context, ArrayList<PostDataModel> postsList) {
        this.context = context;
        this.postsList = postsList;
    }
    /**
     *  ViewHolder class
     */
    class PostViewHolder extends RecyclerView.ViewHolder{
        // initialize widgets from layout to be inflate
        private ImageView profilePic_L ;
        private ImageView profilePic_S ;
        private ImageView content ;
        private TextView name ;
        private TextView likes ;
        private TextView comments ;
        private TextView comment ;
        private Button follow ;
        private Button like ;
        private ProgressBar loading ;
        private LinearLayout comment_layout ;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic_L  = itemView.findViewById(R.id.profileOfAdmin1) ;
            profilePic_S  = itemView.findViewById(R.id.profileOfAdmin2) ;
            content  = itemView.findViewById(R.id.contentOfPost) ;
            name  = itemView.findViewById(R.id.nameOfAdmin) ;
            likes  = itemView.findViewById(R.id.likeCount) ;
            comments  = itemView.findViewById(R.id.commentCount) ;
            comment  = itemView.findViewById(R.id.adminCommentForPost) ;
            follow  = itemView.findViewById(R.id.followAdmin) ;
            like  = itemView.findViewById(R.id.likeForPost) ;
            loading = itemView.findViewById(R.id.loadImages) ;
            comment_layout  = itemView.findViewById(R.id.adminCommentLayout) ;
        }
        public void setDetails(PostDataModel dataModel){
            name.setText(dataModel.getAdminName()) ;
            likes.setText(String.valueOf(dataModel.getCountOfLike())) ;
            comments.setText(String.valueOf(dataModel.getCountOfComment())) ;
            comment.setText(dataModel.getAdminComment()) ;
        }
    }

    /**
     * @Overrided methods
     * */
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_public_posts , parent , false) ;

        return new PostViewHolder(view) ;
    }




    /**
     *  handle onClick & dynamic events here ...
     */
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostDataModel dataModel = postsList.get(position) ;
        holder.setDetails(dataModel) ;
        /* see profile */
        // 1 .
        holder.profilePic_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTappedProfile(dataModel.getAdminName());
            }
        });
        // 2 .
        holder.profilePic_S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTappedProfile(dataModel.getAdminName());
            }
        });
        // 3 .
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTappedProfile(dataModel.getAdminName());
            }
        });

        /**
         * set image content files
         */
        SetPicture(dataModel.getRef()  , holder.content , dataModel , context , holder) ;

        /**
         * set profile picture's
         */
        SetProfilePics(dataModel.getExtractID(), dataModel.getID(), holder.profilePic_L , context) ;
        SetProfilePics(dataModel.getExtractID(), dataModel.getID(), holder.profilePic_S , context) ;
        /**
         * follow Button event
         */
        followEvent(holder.follow , dataModel.getAdminName());
        /**
         * Like counter
         */
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeToggle++ ;
                if(likeToggle%2 == 0){
                    holder.like.setBackground(context.getDrawable(R.drawable.baseline_favorite_24)) ;
                    // make changes on realtime database
                    showCustomToast("liked" , v , R.drawable.baseline_favorite_24) ;
                }
                else {
                    holder.like.setBackground(context.getDrawable(R.drawable.baseline_favorite_border_24));
                    // make changes on realtime database
                }
            }
        });

        /**
         * comment layout
         */
        String comment = String.valueOf(holder.comment.getText()) ;
        if(comment.isEmpty())
            holder.comment_layout.setVisibility(View.GONE) ;
        else
            holder.comment_layout.setVisibility(View.VISIBLE) ;
    }

    @Override
    public int getItemCount() {
        return postsList.size() ;
    }
    /**
     * Most DB activities should be performed within this adapter class :
     * @param imgReference
     * @param imageView
     * @param dataModel
     * @param context
     * @param holder
     */

    private void SetPicture(String imgReference , ImageView imageView ,
                            PostDataModel dataModel ,Context context , PostViewHolder holder){
        holder.loading.setVisibility(View.VISIBLE) ;
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("userUploads")
                .child(dataModel.getAdminName()).child(imgReference) ;
        // Fetch the download URL for the image
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                holder.loading.setVisibility(View.GONE) ;
                // Use the download URL to load the image into the ImageView
                Glide.with(context).load(uri).into(imageView) ;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur while fetching the image
            }
        });
    }
    /**
     *
     * @param extractID
     * @param ID
     * @param imageView
     * @param context
     */
    private void SetProfilePics(String extractID, String ID, ImageView imageView , Context context){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(extractID).document(ID) ;
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String profilePicFile = documentSnapshot.getString(documentFields.ProfilePic);
                String name = documentSnapshot.getString(documentFields.UserName);
                if (profilePicFile.equals(""))
                    return ;
                else {
                    StorageReference ref = FirebaseStorage.getInstance().getReference()
                            .child("userUploads").child(name).child("ProfilePicture").child(profilePicFile);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace() ;
                    }
                }) ;
    }
    private void followEvent(Button followBtn,String tempUser){
        FirebaseAuth mAuth = FirebaseAuth.getInstance() ;
        String myID = mAuth.getCurrentUser().getUid() ;
        DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;

        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot usersSnapShots : snapshot.getChildren()){
                    String uid = usersSnapShots.getKey() ;
                    DataSnapshot followingSnapShot = usersSnapShots.child(DatabaseKeys.Realtime.following) ;
                    for (DataSnapshot followingInfo : followingSnapShot.getChildren()){
                        String name = followingInfo.child("name").getValue(String.class) ;
                        if(tempUser.equals(name)){
                            followBtn.setText("following");
                            followBtn.setBackgroundColor(context.getResources().getColor(R.color.blue_dark)) ;
                            followBtn.setClickable(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update following of current User
                updateMyFollowing(myID , tempUser) ;
                // update follower of clicked user
                String documentPath = extract.getDocument(mAuth.getCurrentUser().getEmail()) ;
                String UID = mAuth.getCurrentUser().getUid() ;
                DocumentReference storeReference = FirebaseFirestore.getInstance().collection(documentPath).document(UID);
                storeReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.getString(documentFields.UserName) ;
                        updateOthersFollower(myID , tempUser,name) ;
                        followBtn.setText("following");
                        followBtn.setBackgroundColor(context.getResources().getColor(R.color.blue_dark)) ;
                        followBtn.setClickable(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace() ;
                    }
                }) ;
            }
        });
    }

    /**
     * updating my followings in realtime db
     */
    private void updateMyFollowing(String UID , String tempUser) {
        DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String uid = users.getKey() ;
                    // confirm uid by name
                    String name= users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
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

    /**
     * update others follower
     */
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
        }); ;
    }
    //
    private void navigateToTappedProfile(String name){
        Intent i = new Intent(context , OthersProfileActivity.class) ;
        i.putExtra("TappedUsersName" , name) ;
        context.startActivity(i) ;
    }
    private void showCustomToast(String message , View v , int res){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View layout = inflater.inflate(R.layout.custom_toast_layout , v.findViewById(R.id.containerToast)) ;
        ImageView img = layout.findViewById(R.id.imageViewToast) ;
        img.setImageResource(res);
        TextView txt = layout.findViewById(R.id.textViewToast) ;
        txt.setText(message);
        Toast toast = new Toast(context) ;
        toast.setDuration(Toast.LENGTH_SHORT) ;
        toast.setView(layout);
        toast.show() ;
    }
}