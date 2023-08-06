package com.example.feedpost.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
        final String[] contents = new String[]{
                "skip" ,
                "male" ,
                "female"
        } ;
        ArrayAdapter add = new ArrayAdapter(createAccount.this ,
                android.R.layout.simple_spinner_dropdown_item , contents) ;
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
                String firstName = String.valueOf(fName.getText()) ;
                String lastName = String.valueOf(lName.getText()) ;
                String fullName = extract.getName(firstName,lastName) ;
                String usermail = String.valueOf(userEmail.getText()) ;
                String collectionPath = extract.getDocument(usermail) ;
                String userpass = String.valueOf(userPassword.getText()) ;
                String gender = String.valueOf(spinner.getSelectedItem()) ;
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
                                    showCustomToast("Now , you can log in");
                                    finish() ;
                                }
                            }) ;
                        }
                        else{
                            // show custom toast
                            showCustomToast("Something went wrong");
                        }
                    }
                }) ;
            }
        });
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