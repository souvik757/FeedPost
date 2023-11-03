package com.example.feedpost.Content.HomePage.HomeContents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.feedpost.Content.HomePage.HomeContents.ComentsBottomDialog.PublicMessageAdapter;
import com.example.feedpost.Content.HomePage.HomeContents.ComentsBottomDialog.PublicMessageModel;
import com.example.feedpost.OthersProfile.OthersProfileActivity;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
public class PostDataAdapter extends RecyclerView.Adapter<PostDataAdapter.PostViewHolder> {
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
        private Button btnComment ;
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
            btnComment = itemView.findViewById(R.id.commentForPost) ;
            follow  = itemView.findViewById(R.id.followAdmin) ;
            like  = itemView.findViewById(R.id.likeForPost) ;
            loading = itemView.findViewById(R.id.loadImages) ;
            comment_layout  = itemView.findViewById(R.id.adminCommentLayout) ;
        }
        public void setDetails(PostDataModel dataModel){
            name.setText(dataModel.getAdminName()) ;
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
        /**
         * see profile
         */
        // 1 .
        holder.profilePic_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTappedProfile(dataModel);
            }
        });
        // 2 .
        holder.profilePic_S.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTappedProfile(dataModel);
            }
        });
        // 3 .
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTappedProfile(dataModel);
            }
        });
        /**
         * set image content files
         */
        SetPicture(dataModel.getRef()  , holder.content , dataModel , context , holder) ;
        /**
         * set profile picture's
         */
        SetProfilePics(dataModel.getID(), holder.profilePic_L , context) ;
        SetProfilePics(dataModel.getID(), holder.profilePic_S , context) ;
        /**
         * follow Button event
         */
        followEvent(holder.follow , dataModel.getAdminName());
        /**
         * Like counter
         */
        LikeEvent(holder.like , dataModel.getAdminName()) ;
        /**
         * comment button
         */
        holder.btnComment.setOnClickListener(view ->{
            initBottomDialog(dataModel, holder) ;
        });
        /**
         * setting like count
         */
        setCount(holder.likes, dataModel.getID(), documentFields.realtimePostFields.Likes) ;
        /**
         * setting comment count
         */
        setCount(holder.comments, dataModel.getID(), documentFields.realtimePostFields.Comments) ;
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
    private void SetPicture(String imgReference , ImageView imageView ,PostDataModel dataModel ,Context context , PostViewHolder holder){
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
     * @param ID
     * @param imageView
     * @param context
     */
    private void SetProfilePics(String ID, ImageView imageView , Context context){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child(DatabaseKeys.Realtime.posts).child(ID).child(documentFields.realtimePostFields.Admin).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                    String UID = snapshot.child(documentFields.realtimePostFields._Admin_.ID).getValue(String.class) ;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child(DatabaseKeys.Realtime.users).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                boolean hasProfilePic = snapshot.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                                if (hasProfilePic){
                                    String picFile = snapshot.child(DatabaseKeys.Realtime.profile).child(DatabaseKeys.Realtime._profile_.profilePicFile).getValue(String.class) ;
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
                                else
                                    imageView.setImageResource(R.drawable.skip) ;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            throw error.toException() ;
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
    /**
     * handel like event as per both end user
     * @param followBtn
     * @param tempUser
     */
    private void followEvent(Button followBtn,String tempUser){
        FirebaseAuth mAuth = FirebaseAuth.getInstance() ;
        String myID = mAuth.getCurrentUser().getUid() ;
        DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
        // see if user is already present in following list
        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot users : snapshot.getChildren()){
                        String uid = users.getKey() ;
                        String currentName = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                        if(currentName.equals(tempUser)){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child(DatabaseKeys.Realtime.users).child(myID).child(DatabaseKeys.Realtime.following).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        followBtn.setText("following");
                                        followBtn.setBackgroundColor(context.getColor(R.color.blue));
                                    }
                                    else {
                                        followBtn.setText("follow");
                                        followBtn.setBackgroundColor(context.getColor(R.color.green));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    throw error.toException() ;
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
        // click event
        followBtn.setOnClickListener(view ->{
            String textOnFollowBtn = followBtn.getText().toString() ;
            if(textOnFollowBtn.equals("follow")){
                followBtn.setText("following");
                followBtn.setBackgroundColor(context.getColor(R.color.blue));
                // add tempUser to my following
                updateMyFollowing(myID,tempUser) ;
                // add me in tempUser's follower
                mRealDatabase.child(DatabaseKeys.Realtime.users).child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String myName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                            updateOthersFollower(myID,tempUser,myName);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException() ;
                    }
                });
            }
            if(textOnFollowBtn.equals("following")) {
                followBtn.setText("follow");
                followBtn.setBackgroundColor(context.getColor(R.color.green));
                // remove tempUser from my following
                removeUser(myID,tempUser) ;
                // remove me from tempUser's follower
                mRealDatabase.child(DatabaseKeys.Realtime.users).child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String myName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                            removeMe(myID,tempUser) ;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException() ;
                    }
                });
            }
        });
    }
    private void updateMyFollowing(String UID , String tempUser) {
        DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
        mRealDatabase.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String uid = users.getKey() ;
                    // confirm uid by name
                    String name = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
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
        });
    }
    private void removeUser(String myUID, String tempUser){
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference() ;
        ref1.child(DatabaseKeys.Realtime.users).child(myUID).child(DatabaseKeys.Realtime.following).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot followingUsers : snapshot.getChildren()){
                        String uid = followingUsers.getKey() ;
                        String currentUser = followingUsers.child("name").getValue(String.class) ;
                        if(currentUser.equals(tempUser)){
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                            ref2.child(DatabaseKeys.Realtime.users).child(myUID).child(DatabaseKeys.Realtime.following).child(uid).removeValue() ;
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
    private void removeMe(String myUID, String tempUser){
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference() ;
        ref1.child(DatabaseKeys.Realtime.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String uid = users.getKey() ;
                    String currentUserName = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    if(tempUser.equals(currentUserName)){
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                        ref2.child(DatabaseKeys.Realtime.users).child(uid).child(DatabaseKeys.Realtime.follower).child(myUID).removeValue() ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }
    /**
     * Like event
     */
    private void LikeEvent(Button like, String tempUser) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance() ;
        String myID = mAuth.getCurrentUser().getUid() ;
        DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
        // check if post is already liked by me
        mRealDatabase.child(DatabaseKeys.Realtime.posts).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        String uid = posts.getKey() ;
                        String currentName = posts.child(documentFields.realtimePostFields.Admin)
                                .child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                        if (tempUser.equals(currentName)){
                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                            ref1.child(DatabaseKeys.Realtime.users).child(myID).child(DatabaseKeys.Realtime.Likes).child(uid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                like.setText("unlike");
                                                like.setBackground(context.getDrawable(R.drawable.baseline_favorite_24));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                                throw error.toException() ;
                                        }
                                    });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
        like.setOnClickListener(view ->{
            String txt = like.getText().toString() ;
            if(txt.equals("like")) {
                like.setText("unlike");
                like.setBackground(context.getDrawable(R.drawable.baseline_favorite_24));
                // add post to my Likes
                updateMyLike(myID,tempUser);
                // add me to user's post's Likes
                mRealDatabase.child(DatabaseKeys.Realtime.users).child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String myName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                            updateOthersLike(myID, myName, tempUser);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException() ;
                    }
                });
            }
            if(txt.equals("unlike")){
                like.setText("like");
                like.setBackground(context.getDrawable(R.drawable.baseline_favorite_border_24));
                // remove post from my Like
                removePostFromMyLike(myID, tempUser);
                // remove me from user's post's Like
                removeMyLikeFromUserPost(myID, tempUser);
            }
        });
    }
    private void updateOthersLike(String myID , String myName , String adminName){
        DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
        mRealDatabase.child(DatabaseKeys.Realtime.posts).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String uid = users.getKey() ;
                    String name = users.child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                    if(name.equals(adminName)){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference() ;
                        ref.child(DatabaseKeys.Realtime.posts).child(uid).child(DatabaseKeys.Realtime.Likes).child(myID).child("name").setValue(myName) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }
    private void updateMyLike(String myID , String adminName){
        DatabaseReference mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
        mRealDatabase.child(DatabaseKeys.Realtime.posts).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users : snapshot.getChildren()){
                    String uid = users.getKey() ;
                    String name = users.child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                    if(name.equals(adminName)){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference() ;
                        ref.child(DatabaseKeys.Realtime.users).child(myID).child(DatabaseKeys.Realtime.Likes).child(uid).child("name").setValue(name) ;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }
    private void removePostFromMyLike(String myID, String tempUser){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child(DatabaseKeys.Realtime.posts).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        String uid = posts.getKey() ;
                        String currentName = posts.child(documentFields.realtimePostFields.Admin)
                                .child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                        if (tempUser.equals(currentName)){
                            DatabaseReference fRef = FirebaseDatabase.getInstance().getReference();
                            fRef.child(DatabaseKeys.Realtime.users).child(myID).child(DatabaseKeys.Realtime.Likes).child(uid).removeValue() ;
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
    private void removeMyLikeFromUserPost(String myID, String tempUser){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child(DatabaseKeys.Realtime.posts).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        String uid = posts.getKey() ;
                        String currentName = posts.child(documentFields.realtimePostFields.Admin)
                                .child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                        if (tempUser.equals(currentName)){
                            DatabaseReference fRef = FirebaseDatabase.getInstance().getReference();
                            fRef.child(DatabaseKeys.Realtime.posts).child(uid).child(documentFields.realtimePostFields.Likes)
                                    .child(myID).removeValue() ;
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
    /**
     * navigate to personal profile of user
     * @param dataModel
     */
    private void navigateToTappedProfile(PostDataModel dataModel){
        Intent i = new Intent(context , OthersProfileActivity.class) ;
        i.putExtra(documentFields.rawDataFields.currentUserName , dataModel.getAdminName()) ;
        context.startActivity(i) ;
    }
    private void initBottomDialog(PostDataModel model, PostViewHolder holder){
        String postUID = model.getID() ;
        String adminName = model.getAdminName() ;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context) ;
        bottomSheetDialog.setContentView(R.layout.messege_bottom_dialoge_layout) ;
        // widgets
        RelativeLayout RL1 = bottomSheetDialog.findViewById(R.id.idRL1) ;
        ImageView adminProfile = bottomSheetDialog.findViewById(R.id.idIVAdminProfile) ;
        TextView postAdminName = bottomSheetDialog.findViewById(R.id.idTVAdminName) ;
        TextView adminMessage = bottomSheetDialog.findViewById(R.id.idTVAdminMessage) ;
        TextView noCommentTxtView = bottomSheetDialog.findViewById(R.id.idTVNoComment) ;
        ProgressBar progressBar = bottomSheetDialog.findViewById(R.id.idPBForComment) ;
        ImageView profileIV = bottomSheetDialog.findViewById(R.id.idIVProfile) ;
        EditText commentEDT = bottomSheetDialog.findViewById(R.id.idEdtComment) ;
        ImageView addCommentIV = bottomSheetDialog.findViewById(R.id.idBtnSendMessage) ;
        // comments RV
        RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.idCommentRV) ;
        ArrayList<PublicMessageModel> messageModelArrayList = new ArrayList<>() ;
        PublicMessageAdapter adapter = new PublicMessageAdapter(context , messageModelArrayList) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter) ;
        recyclerView.setNestedScrollingEnabled(false) ;
        recyclerView.setHasFixedSize(true) ;

        setAdminResources(model,holder,RL1,adminProfile,postAdminName,adminMessage) ;
        loadRecyclerViewWithData(noCommentTxtView , progressBar , messageModelArrayList ,adapter, adminName) ;
        picForBottomDialogLayout(profileIV);
        initBottomDialogOnClick(bottomSheetDialog, postUID, adminName, commentEDT, profileIV, addCommentIV, adapter) ;
        bottomSheetDialog.show() ;
    }

    private void setAdminResources(PostDataModel model, PostViewHolder holder,RelativeLayout rl,ImageView adminProfile, TextView postAdminName, TextView adminMessage) {
        String message = String.valueOf(holder.comment.getText()) ;
        if(message.isEmpty())
            rl.setVisibility(View.GONE);
        else {
            SetProfilePics(model.getID(), adminProfile, context) ;
            postAdminName.setText(String.valueOf(model.getAdminName()));
            adminMessage.setText(String.valueOf(model.getAdminComment())) ;
        }
    }

    private void loadRecyclerViewWithData(TextView txt , ProgressBar pBar , ArrayList<PublicMessageModel> messageModelArrayList, PublicMessageAdapter adapter, String adminName) {
        DatabaseReference mRealtime = FirebaseDatabase.getInstance().getReference();
        pBar.setVisibility(View.VISIBLE);
        mRealtime.child(DatabaseKeys.Realtime.posts).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot posts : snapshot.getChildren()){
                    String uid = posts.getKey() ;
                    String currentName = posts.child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                    if(adminName.equals(currentName)){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(DatabaseKeys.Realtime.posts).child(uid).child(documentFields.realtimePostFields.Comments).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int count = (int) snapshot.getChildrenCount() ;
                                if(count == 0)
                                    txt.setVisibility(View.VISIBLE) ;
                                messageModelArrayList.clear();
                                for (DataSnapshot commentedUser : snapshot.getChildren()){
                                    String commenterName = commentedUser.child("name").getValue(String.class) ;
                                    String comment = commentedUser.child(DatabaseKeys.Realtime._Comments_.comment).getValue(String.class) ;
                                    PublicMessageModel model = new PublicMessageModel(commenterName , comment) ;
                                    messageModelArrayList.add(model) ;
                                    pBar.setVisibility(View.GONE) ;
                                }
                                pBar.setVisibility(View.GONE) ;
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                throw error.toException() ;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        }) ;
    }
    private void picForBottomDialogLayout(ImageView imageView){
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference() ;
        mRef.child(DatabaseKeys.Realtime.users).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String userName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    boolean hasProfilePic = snapshot.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                    if(hasProfilePic){
                        String picFile = snapshot.child(DatabaseKeys.Realtime.profile).child(DatabaseKeys.Realtime._profile_.profilePicFile).getValue(String.class) ;
                        if(!picFile.equals("")){
                            StorageReference ref = FirebaseStorage.getInstance().getReference()
                                    .child("userUploads").child(userName).child("ProfilePicture").child(picFile) ;
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context).load(uri).into(imageView) ;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            }) ;
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

    private void initBottomDialogOnClick(BottomSheetDialog bottomSheetDialog,String postUID, String adminName, EditText editText, ImageView buttonIV, ImageView button, PublicMessageAdapter adapter){
        String myID = FirebaseAuth.getInstance().getCurrentUser().getUid() ;

        button.setOnClickListener(view ->{
            String comment = String.valueOf(editText.getText()) ;
            if (comment.isEmpty()){
                Toast.makeText(context , "Type to comment" , Toast.LENGTH_SHORT).show();
            }
            else {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                mRef.child(DatabaseKeys.Realtime.posts).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot users : snapshot.getChildren()){
                            String uid = users.getKey() ;
                            String name = users.child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                            if (name.equals(adminName)){
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                                mRef.child(DatabaseKeys.Realtime.users).child(myID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){

                                            String myName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                            ref.child(DatabaseKeys.Realtime.posts).child(uid).
                                                    child(DatabaseKeys.Realtime.Comments).child(myID).
                                                    child("name").setValue(myName) ;
                                            ref.child(DatabaseKeys.Realtime.posts).child(uid).
                                                    child(DatabaseKeys.Realtime.Comments).child(myID).
                                                    child(DatabaseKeys.Realtime._Comments_.comment).setValue(comment) ;
                                            ref.child(DatabaseKeys.Realtime.users).child(myID).child(DatabaseKeys.Realtime.Comments).child(postUID).child("name").setValue(adminName) ;
                                            adapter.notifyDataSetChanged() ;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        throw error.toException() ;
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException() ;
                    }
                });
            }

        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // nothing happens on dismiss event
            }
        });
    }
    private void setCount(TextView txt, String postUID, String parameter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(DatabaseKeys.Realtime.posts).child(postUID).child(parameter) ;
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    long childrenCount = task.getResult().getChildrenCount();
                    int count = (int) childrenCount ;
                    txt.setText(String.valueOf(count));
                }
            }
        }) ;
    }
    private void showCustomToast(String message , View v , int res) {
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