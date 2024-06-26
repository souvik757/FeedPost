package com.example.feedpost.ImageActivity.ImageUploader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageUploadForProfilePic extends AppCompatActivity {
    // widgets
    private ImageView profilePicture ;
    private ProgressBar loadingBar ;
    // firebase & resources
    private FirebaseStorage mStorage ;
    private FirebaseAuth mAuth ;
    private FirebaseFirestore mFirestore ;
    private DocumentReference mReference ;
    private DatabaseReference mRealtime ;
    private final String fileName = UUID.randomUUID().toString()+".jpg" ;
    // request code
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_for_profile_pic);

        initializeWidgets() ;
        initializeDatabase();
    }
    // 1 .
    private void initializeWidgets(){
        profilePicture = findViewById(R.id.profilePreview) ;
        loadingBar = findViewById(R.id.ShowProgress) ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mStorage = FirebaseStorage.getInstance() ;
        mFirestore = FirebaseFirestore.getInstance() ;
        mRealtime = FirebaseDatabase.getInstance().getReference() ;
    }


    public void uploadPhoto(View view) {
            loadingBar.setVisibility(View.VISIBLE);
            String UID = mAuth.getCurrentUser().getUid();
            mRealtime.child(DatabaseKeys.Realtime.users).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String name = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                        // showCustomToast(name , view);
                        // Get the data from an ImageView as bytes
                        profilePicture.setDrawingCacheEnabled(true);
                        profilePicture.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = mStorage.getReference().child("userUploads").child(name).child("ProfilePicture")
                                                                 .child(fileName).putBytes(data);
                          uploadTask.addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception exception) {
                                  // Handle unsuccessful uploads
                                  loadingBar.setVisibility(View.GONE);
                                  showCustomToast("Something went wrong", view);
                              }
                          }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                  showCustomToast("Successfully", view);
                                  mRealtime.child("users").child(UID).child(documentFields.realtimeFields.hasProfilePic).setValue(true);
                                  mRealtime.child("users").child(UID).child("profile").child("profilePicFile").setValue(fileName) ;
                                  loadingBar.setVisibility(View.GONE);
                                  finish();
                              }
                          });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    @Override
    public void onBackPressed() {
        finish() ;
    }

    public void selectPhotoFromDevice(View view) {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent() ;
        intent.setType("image/*") ;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here..."),PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data) {
        super.onActivityResult(requestCode , resultCode , data) ;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            Glide.with(getApplicationContext()).load(filePath).into(profilePicture) ;
        }
    }
    private void showCustomToast(String message , View parentHolder){
        LayoutInflater inflater = getLayoutInflater() ;
        View layout = inflater.inflate(R.layout.custom_toast_layout , (ViewGroup) parentHolder.findViewById(R.id.containerToast)) ;
        ImageView img = layout.findViewById(R.id.imageViewToast) ;
        img.setImageResource(R.drawable.warning);
        TextView txt = layout.findViewById(R.id.textViewToast) ;
        txt.setText(message);
        Toast toast = new Toast(this) ;
        toast.setDuration(Toast.LENGTH_LONG) ;
        toast.setView(layout);
        toast.show() ;
    }
}