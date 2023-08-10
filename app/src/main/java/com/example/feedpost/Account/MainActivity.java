package com.example.feedpost.Account;
// 20:B8:5A:2D:A4:5C:A6:2C:67:DA:5A:E6:04:4E:DF:BC:0F:7B:90:55
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.HomePage.HomePage;
import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.example.feedpost.spalashScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    // widgets
    private EditText userEmail;
    private EditText passWord ;
    private Button signIn ;
    private ProgressBar loadIndicator ;
    // firebase
    private FirebaseAuth mAuth ;
    private DatabaseReference mRealDatabase ;
    private DocumentReference mReference ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize Widgets
        initializeWidgets() ;
        // initialize Firebase
        initializeDatabase() ;
        // showConsent ?
        showConsent() ;
    }
    private void showConsent(){
        // showSplash
        if(mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, spalashScreen.class));
            // perform onclick events

        }
        else {
            // go to content's page directly
            navigateToHomepage() ;
            finish() ;
        }
    }
    // 1 .
    private void initializeWidgets(){
        userEmail = findViewById(R.id.usernameET) ;
        passWord = findViewById(R.id.passwordET) ;
        signIn = findViewById(R.id.buttonSignIn) ;
        loadIndicator = findViewById(R.id.waitingBar) ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
    }
    public void SignIn(View view) {
        loadIndicator.setVisibility(View.VISIBLE) ;
        if(TextUtils.isEmpty(userEmail.getText())) {
            // show custom toast message
            showCustomToast("invalid email");
            loadIndicator.setVisibility(View.GONE) ;
            return ;
        }
        if(TextUtils.isEmpty(passWord.getText())) {
            // show custom toast message
            showCustomToast("invalid password");
            loadIndicator.setVisibility(View.GONE) ;
            return ;
        }
        String useremail = userEmail.getText().toString().trim() ;
        String password = passWord.getText().toString().trim() ;
        // check if we can sign in with current useremail & password
        mAuth.signInWithEmailAndPassword(useremail , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    assert mAuth.getCurrentUser() != null ;
                    String email = extract.getDocument(mAuth.getCurrentUser().getEmail()) ;
                    String UID = mAuth.getCurrentUser().getUid() ;
                    mReference = FirebaseFirestore.getInstance().collection(email).document(UID);
                    mReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                String userFullName = documentSnapshot.getString(documentFields.UserName) ;
                                String userGender = documentSnapshot.getString(documentFields.Gender) ;
                                String profilePic =  documentSnapshot.getString(documentFields.ProfilePic) ;
                                String profileBg = documentSnapshot.getString(documentFields.ProfileBG) ;
                                String profileBio = documentSnapshot.getString(documentFields.ProfileBio) ;
                                setEntryInToRealtime(useremail , userFullName , userGender , profilePic ,profileBg,profileBio) ;
                                loadIndicator.setVisibility(View.GONE) ;
                            }
                        }
                    }) ;
                    navigateToHomepage() ;
                }
                else {
                    // show custom toast message
                    showCustomToast("sign in failed");
                    loadIndicator.setVisibility(View.GONE);
                    // then sign up the user
                    startActivity(new Intent(MainActivity.this , createAccount.class));
                }
            }
        }) ;
    }

    //  .
    private void setEntryInToRealtime(String usermail, String fullName , String gender ,String profilePic , String profileBg, String profileBio){
        String childPath = mAuth.getCurrentUser().getUid() ;
        // create in realtime
        mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.email).setValue(usermail) ;
        mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.fullName).setValue(fullName) ;
        mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.gender).setValue(gender) ;
        if(profilePic.equals(""))
            mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.hasProfilePic).setValue(false) ;
        else
            mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.hasProfilePic).setValue(true) ;
        if(profileBg.equals(""))
            mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.hasProfileBg).setValue(false) ;
        else
            mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.hasProfileBg).setValue(true) ;
        mRealDatabase.child("users").child(childPath).child(documentFields.realtimeFields.bio).setValue(profileBio) ;
    }
    private void navigateToHomepage(){
        String documentPath = extract.getDocument(String.valueOf(userEmail)) ;
        String currentUserID = mAuth.getCurrentUser().getUid() ;
        // go to content's page directly
        Intent i = new Intent(MainActivity.this , HomePage.class) ;
        i.putExtra("docPath" , documentPath) ;
        i.putExtra("childPath" , currentUserID) ;
        startActivity(i);
        finish() ;
    }
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