package com.example.feedpost.Content;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedpost.Account.MainActivity;
import com.example.feedpost.Content.UsersList.chooseUserActivity;
import com.example.feedpost.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {
    // firebase
    private FirebaseAuth mAuth ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        initializeDatabase() ;
    }
    // 1 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater() ;
        inflater.inflate(R.menu.options, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.createPost){
            startActivity(new Intent(HomePage.this , createFeeds.class));
        }
        else if(item.getItemId() == R.id.logout){
            // sign out
            mAuth.signOut() ;
            startActivity(new Intent(HomePage.this , MainActivity.class));
            showCustomToast("successfully logged out");
            finish() ;
        }
        return super.onOptionsItemSelected(item);
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

    public void navigateToUsersList(View view) {
        startActivity(new Intent(HomePage.this , chooseUserActivity.class));
    }
}