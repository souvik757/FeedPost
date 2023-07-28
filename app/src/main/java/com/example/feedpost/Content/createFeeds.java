package com.example.feedpost.Content;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest ;
import android.content.pm.PackageManager ;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.UsersList.chooseUserActivity;
import com.example.feedpost.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class createFeeds extends AppCompatActivity {
    // widgets
    private AppCompatImageView choosePost ;
    private EditText chooseMessage ;
    private Button selectPost ;
    private Button confirmPost ;
    private ProgressBar progressBar ;
    // firebase
    private FirebaseStorage mStorage ;
    private final String fileName = UUID.randomUUID().toString()+".jpg" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_feeds);

        initializeWidgets() ;
        initializeDatabase();
        onClickEvents() ;
    }
    // 1 .
    private void initializeWidgets(){
        choosePost = findViewById(R.id.feedPostIMG) ;
        chooseMessage = findViewById(R.id.feedPostMSG) ;
        selectPost = findViewById(R.id.feedPostIMGBTN) ;
        confirmPost = findViewById(R.id.feedPostDONE) ;
        progressBar = findViewById(R.id.progressImg) ;
    }
    // 2 .
    private void initializeDatabase(){
        mStorage = FirebaseStorage.getInstance() ;
    }
    // 3 .
    private void onClickEvents(){
        selectPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
                else
                    getPhoto() ;
            }
        });
        confirmPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                // Get the data from an ImageView as bytes
                choosePost.setDrawingCacheEnabled(true);
                choosePost.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) choosePost.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = mStorage.getReference().child("images")
                        .child(fileName).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressBar.setVisibility(View.GONE);
                        showCustomToast("Something went wrong");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        progressBar.setVisibility(View.GONE);
                        // go to all users list
                        startActivity(new Intent(createFeeds.this , chooseUserActivity.class));
                        finish() ;
                    }
                });
            }
        });
    }
    //  .
    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
        startActivityForResult(intent , 1);
    }
    //  .
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getPhoto() ;
        }
    }

    //  .
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImg = data.getData() ;
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , selectedImg) ;
                choosePost.setImageBitmap(bitmap) ;
            }   catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //  .
    private void showCustomToast(String message){
        LayoutInflater inflater = getLayoutInflater() ;
        View layout = inflater.inflate(R.layout.custom_toast_layout , (ViewGroup) findViewById(R.id.containerToast)) ;
        ImageView img = layout.findViewById(R.id.imageViewToast) ;
        img.setImageResource(R.drawable.warning);
        TextView txt = layout.findViewById(R.id.textViewToast) ;
        txt.setText(message);
        Toast toast = new Toast(getApplicationContext()) ;
        toast.setDuration(Toast.LENGTH_LONG) ;
        toast.setView(layout);
        toast.show() ;
    }
}