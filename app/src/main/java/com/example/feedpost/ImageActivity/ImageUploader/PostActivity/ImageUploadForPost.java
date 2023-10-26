package com.example.feedpost.ImageActivity.ImageUploader.PostActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.feedpost.CustomImageAdapter.ImageModelClass;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ImageUploadForPost extends AppCompatActivity {
    // widgets
    private ImageView imgPreview ;
    private EditText commentText ;
    private ProgressBar progressBar ;
    // firebase & resources
    private FirebaseStorage mStorage ;
    private FirebaseAuth mAuth ;
    private FirebaseFirestore mFirestore ;
    private DocumentReference mReference ;
    private DatabaseReference mRealtime ;
    private final String postUID  = UUID.randomUUID().toString() ;
    private final String fileName = UUID.randomUUID().toString()+".jpg" ;
    // request code
    private Uri filePath ;
    private final int PICK_IMAGE_REQUEST = 22 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_for_post);
        initializeWidgets() ;
        initializeDatabase() ;
    }
    // 1 .
    private void initializeWidgets(){
        imgPreview = findViewById(R.id.PostPreview) ;
        commentText = findViewById(R.id.postDescription) ;
        progressBar = findViewById(R.id.showProgress) ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mStorage = FirebaseStorage.getInstance() ;
        mRealtime = FirebaseDatabase.getInstance().getReference() ;
        mFirestore = FirebaseFirestore.getInstance() ;
    }
    @Override
    protected void onActivityResult(int requestCode , int resultCode , Intent data) {
        super.onActivityResult(requestCode , resultCode , data) ;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            Glide.with(getApplicationContext()).load(filePath).dontAnimate().into(imgPreview) ;
        }
    }
    public void selectPhoto(View view) {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent() ;
        intent.setType("image/*") ;
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here..."),PICK_IMAGE_REQUEST);
    }
    public void postPhoto(View view) {
            progressBar.setVisibility(View.VISIBLE);
            String comment = String.valueOf(commentText.getText()).trim() ;
            String UID = mAuth.getCurrentUser().getUid();
            String email = mAuth.getCurrentUser().getEmail();
            String extractID = extract.getDocument(email);
            mRealtime.child(DatabaseKeys.Realtime.users).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                        uploadPreview(name, UID , extractID , comment , view) ;
                    }
                    else
                        showCustomToast("Something went wrong" , view);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showCustomToast("Something went wrong" , view);
                    throw error.toException() ;
                }
            });

    }
    private void uploadPreview(String name , String UID , String extractID , String comment , View view){
        // Get the data from an ImageView as bytes
        imgPreview.setDrawingCacheEnabled(true);
        imgPreview.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mStorage.getReference().child("userUploads").child(name)
                .child(fileName).putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                int length = fileName.length() ;
                setEntryToRealtime(postUID , UID , name , extractID , comment , fileName.substring(0,length-4) , fileName);
                progressBar.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                progressBar.setVisibility(View.GONE);
                showCustomToast("Something went wrong", view);
            }
        }) ;
    }
    private void setEntryToRealtime(String postUID , String id , String name , String extarctedemail , String comment , String contentfile , String file){
        // posts -> postUID -> Admin ->
        mRealtime.child("posts").child(postUID).
                child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.ID).setValue(id) ;
        mRealtime.child("posts").child(postUID).
                child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.NAME).setValue(name) ;
        mRealtime.child("posts").child(postUID).
                child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.EXTRACED_EMAIL).setValue(extarctedemail) ;
        mRealtime.child("posts").child(postUID).
                child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.COMMENT).setValue(comment) ;
        mRealtime.child("posts").child(postUID).
                child(documentFields.realtimePostFields.Admin).child(documentFields.realtimePostFields._Admin_.CONTENTFILE).setValue(contentfile) ;
        /*---------------------------*/
        ImageModelClass imageModelClass = new ImageModelClass(postUID , name , file) ;
        mRealtime.child("users").child(id).
                child(documentFields.realtimeFields.PostedPicture).child(postUID).setValue(imageModelClass) ;
    }
    @Override
    public void onBackPressed() {
        finish() ;
    }
    private void showCustomToast(String message , View parentHolder) {
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