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
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Content.HomePage;
import com.example.feedpost.R;
import com.example.feedpost.Utility.extract;
import com.example.feedpost.spalashScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    // widgets
    private EditText userEmail;
    private EditText passWord ;
    private Button signIn ;
    // firebase
    private FirebaseAuth mAuth ;
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
            onCLickEvents() ;
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
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
    }
    // 3 .
    private void onCLickEvents(){
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userEmail.getText())) {
                    // show custom toast message
                    showCustomToast("invalid email");
                    return ;
                }
                if(TextUtils.isEmpty(passWord.getText())) {
                    // show custom toast message
                    showCustomToast("invalid password");
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
                            navigateToHomepage() ;
                        }
                        else {
                            // show custom toast message
                            showCustomToast("sign in failed");
                            // then sign up the user
                            startActivity(new Intent(MainActivity.this , createAccount.class));
                        }
                    }
                }) ;
            }
        });
    }
    private void navigateToHomepage(){
        String documentPath = extract.getDocument(String.valueOf(userEmail)) ;
        String currentUserID = mAuth.getCurrentUser().getUid() ;
        // go to content's page directly
        Intent i = new Intent(MainActivity.this , HomePage.class) ;
        i.putExtra("docPath" , documentPath) ;
        i.putExtra("childPath" , currentUserID) ;
        startActivity(i);
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