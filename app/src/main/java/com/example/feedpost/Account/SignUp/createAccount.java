package com.example.feedpost.Account.SignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.HomePage.HomePage;
import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class createAccount extends AppCompatActivity {
    // widgets
    private EditText fName ;
    private EditText lName ;
    private EditText userEmail ;
    private EditText userPassword ;
    private Spinner spinner ;
    private Button signUp ;
    private ProgressBar loadIndicator ;
    // firebase
    private FirebaseAuth mAuth ;
    private FirebaseFirestore mDatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initializeWidgets();
        initializeDatabase();
        setSpinnerItems();
        onClickEvents();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to further activity
            startActivity(new Intent(this, HomePage.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
    // 1 .
    private void initializeWidgets(){
        fName = findViewById(R.id.firstNameET) ;
        lName = findViewById(R.id.lastNameET) ;
        userEmail = findViewById(R.id.usernameETSignUp) ;
        userPassword = findViewById(R.id.passwordETSignUp) ;
        spinner = findViewById(R.id.spinner) ;
        signUp = findViewById(R.id.buttonSignUp) ;
        loadIndicator = findViewById(R.id.showLoading) ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mDatabase = FirebaseFirestore.getInstance() ;
    }
    // 3 .
    private void setSpinnerItems(){
        final String[] contents = new String[]{"skip" ,"male" ,"female"} ;
        ArrayAdapter add = new ArrayAdapter(createAccount.this ,android.R.layout.simple_spinner_dropdown_item , contents) ;
        add.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(add) ;
    }
    // 4 .
    private void onClickEvents(){

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadIndicator.setVisibility(View.VISIBLE) ;
                if(TextUtils.isEmpty(userEmail.getText())) {
                    // show custom toast
                    showCustomToast("Invalid email");
                    return ;
                }
                if(TextUtils.isEmpty(userPassword.getText())){
                    // show custom toast
                    showCustomToast("Invalid password");
                    return ;
                }
                if(TextUtils.isEmpty(fName.getText())){
                    // show custom toast
                    showCustomToast("Invalid first name");
                    return ;
                }
                if(TextUtils.isEmpty(lName.getText())){
                    // show custom toast
                    showCustomToast("Invalid last name");
                    return ;
                }
                if(spinner.getSelectedItem().toString().isEmpty()){
                    // show custom toast
                    showCustomToast("Invalid gender");
                    return ;
                }
                String firstName = String.valueOf(fName.getText()).trim() ;
                String lastName = String.valueOf(lName.getText()).trim() ;
                String fullName = extract.getName(firstName,lastName).trim() ;
                String usermail = String.valueOf(userEmail.getText()).trim() ;
                String collectionPath = extract.getDocument(usermail).trim() ;
                String userpass = String.valueOf(userPassword.getText()).trim() ;
                String gender = String.valueOf(spinner.getSelectedItem()).trim() ;
                mAuth.createUserWithEmailAndPassword(usermail , userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            assert mAuth.getCurrentUser() != null ;
                            String childPath = mAuth.getCurrentUser().getUid() ;
                            Map<String, Object> dataFields = new HashMap<>() ;
                            dataFields.put(documentFields.UserName , fullName) ;
                            dataFields.put(documentFields.Gender , gender) ;
                            dataFields.put(documentFields.ProfileBio , "") ;
                            dataFields.put(documentFields.ProfilePic , "") ;
                            dataFields.put(documentFields.ProfileBG , "") ;
                            mDatabase.collection(collectionPath).document(childPath).set(dataFields).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loadIndicator.setVisibility(View.GONE) ;
                                    // show custom toast
                                    setEntryInToRealtime(usermail , fullName , gender , "" , "" , "");
                                    showCustomToast("Now , you can log in");
                                    finish() ;
                                }
                            }) ;
                        }
                        else{
                            // show custom toast
                            showCustomToast("Something went wrong");
                            loadIndicator.setVisibility(View.GONE);
                        }
                    }
                }) ;
            }
        });
    }
    private void setEntryInToRealtime(String userMail, String fullName , String gender ,String profilePic , String profileBg, String profileBio){
        String childPath = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        // create in realtime
        mRef.child("users").child(childPath).child(documentFields.realtimeFields.email).setValue(userMail) ;
        mRef.child("users").child(childPath).child(documentFields.realtimeFields.fullName).setValue(fullName) ;
        mRef.child("users").child(childPath).child(documentFields.realtimeFields.gender).setValue(gender) ;
        if(profilePic.equals(""))
            mRef.child("users").child(childPath).child(documentFields.realtimeFields.hasProfilePic).setValue(false) ;
        else
            mRef.child("users").child(childPath).child(documentFields.realtimeFields.hasProfilePic).setValue(true) ;
        if(profileBg.equals(""))
            mRef.child("users").child(childPath).child(documentFields.realtimeFields.hasProfileBg).setValue(false) ;
        else
            mRef.child("users").child(childPath).child(documentFields.realtimeFields.hasProfileBg).setValue(true) ;
        mRef.child("users").child(childPath).child(documentFields.realtimeFields.bio).setValue(profileBio) ;
    }
    private void showCustomToast(String message){
        LayoutInflater inflater = getLayoutInflater() ;
        View layout = inflater.inflate(R.layout.custom_toast_layout , (ViewGroup) findViewById(R.id.containerToast)) ;
        ImageView img = layout.findViewById(R.id.imageViewToast) ;
        img.setImageResource(R.drawable.warning) ;
        TextView txt = layout.findViewById(R.id.textViewToast) ;
        txt.setText(message);
        Toast toast = new Toast(getApplicationContext()) ;
        toast.setDuration(Toast.LENGTH_LONG) ;
        toast.setView(layout);
        toast.show() ;
    }
}