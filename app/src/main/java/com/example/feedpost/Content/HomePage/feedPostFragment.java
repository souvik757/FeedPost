package com.example.feedpost.Content.HomePage;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.UsersList.chooseUserActivity;
import com.example.feedpost.Content.createFeeds;
import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link feedPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class feedPostFragment extends Fragment {
    //
    View parentHolder ;
    // widgets
    private AppCompatImageView choosePost ;
    private EditText chooseMessage ;
    private Button selectPost ;
    private Button confirmPost ;
    private ProgressBar progressBar ;
    // firebase
    private FirebaseStorage mStorage ;
    private FirebaseAuth mAuth ;
    private FirebaseFirestore mFirestore ;
    private DocumentReference mReference ;
    private final String fileName = UUID.randomUUID().toString()+".jpg" ;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public feedPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment feedPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static feedPostFragment newInstance(String param1, String param2) {
        feedPostFragment fragment = new feedPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentHolder =  inflater.inflate(R.layout.fragment_feed_post, container, false);
        initializeWidgets(parentHolder) ;
        initializeDatabase() ;
        onClickEvents() ;

        return parentHolder ;
    }
    // 1 .
    private void initializeWidgets(View parentHolder){
        choosePost    = parentHolder.findViewById(R.id.feedPostIMG) ;
        chooseMessage = parentHolder.findViewById(R.id.feedPostMSG) ;
        selectPost    = parentHolder.findViewById(R.id.feedPostIMGBTN) ;
        confirmPost   = parentHolder.findViewById(R.id.feedPostDONE) ;
        progressBar   = parentHolder.findViewById(R.id.progressImg) ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mStorage = FirebaseStorage.getInstance() ;
        mFirestore = FirebaseFirestore.getInstance() ;
    }
    // 3 .
    private void onClickEvents(){
        selectPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
                else
                    getPhoto() ;
            }
        });
        confirmPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String UID = mAuth.getCurrentUser().getUid() ;
                String email = mAuth.getCurrentUser().getEmail() ;
                String extractID = extract.getDocument(email) ;
                mReference = mFirestore.collection(extractID).document(UID) ;

                mReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String name = documentSnapshot.getString(documentFields.UserName) ;
                            // Get the data from an ImageView as bytes
                            choosePost.setDrawingCacheEnabled(true);
                            choosePost.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) choosePost.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = mStorage.getReference().child("userUploads").child(name)
                                    .child(fileName).putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    progressBar.setVisibility(View.GONE);
                                    showCustomToast("Something went wrong" , v);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                    progressBar.setVisibility(View.GONE);
                                    // go to all users list
                                    startActivity(new Intent(getContext() , chooseUserActivity.class));
                                }
                            });
                        }
                    }
                }) ;
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImg = data.getData() ;
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver() , selectedImg) ;
                choosePost.setImageBitmap(bitmap) ;
            }   catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //  .
    private void showCustomToast(String message , View parentHolder){
        LayoutInflater inflater = getLayoutInflater() ;
        View layout = inflater.inflate(R.layout.custom_toast_layout , (ViewGroup) parentHolder.findViewById(R.id.containerToast)) ;
        ImageView img = layout.findViewById(R.id.imageViewToast) ;
        img.setImageResource(R.drawable.warning);
        TextView txt = layout.findViewById(R.id.textViewToast) ;
        txt.setText(message);
        Toast toast = new Toast(getContext()) ;
        toast.setDuration(Toast.LENGTH_LONG) ;
        toast.setView(layout);
        toast.show() ;
    }
}