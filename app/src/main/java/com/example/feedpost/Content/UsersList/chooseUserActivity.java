package com.example.feedpost.Content.UsersList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.feedpost.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chooseUserActivity extends AppCompatActivity {
    // widgets
    private RecyclerView usersList ;
    private ProgressBar loadingBar ;
    // variables
    private ArrayList<UserListModel> usersDataList ;
    // adapters
    private UserListAdapter adapter ;
    // firebase
    private DatabaseReference mRealDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        initializeWidgetsAndVariables() ;
        initializeDatabase() ;
        fillListWithData() ;
        initializeAdapterAndFillData() ;
    }

    // 1 .
    private void initializeWidgetsAndVariables(){
        usersList = findViewById(R.id.usersCardView) ;
        loadingBar = findViewById(R.id.waitingBar) ;
        usersDataList = new ArrayList<>() ;
    }
    // 2 .
    private void initializeDatabase(){
        mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
    }
    // 3 .
    private void fillListWithData(){
        loadingBar.setVisibility(View.VISIBLE) ;
        mRealDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserListModel userListModel = new UserListModel();
                    userListModel.setProfileName(dataSnapshot.child("name").getValue(String.class));
                    String gender = dataSnapshot.child("gender").getValue(String.class) ;
                    if(gender.equals("male")){
                        // fetch from storage
                    }
                    else if(gender.equals("female")){
                        // fetch from storage
                    }
                    else if(gender.equals("skip")){
                        // fetch from storage
                    }
                    userListModel.setProfilePic(R.drawable.feedpost) ;
                    usersDataList.add(userListModel) ;

                    loadingBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged() ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;
    }
    // 4 .
    private void initializeAdapterAndFillData(){
        adapter = new UserListAdapter(chooseUserActivity.this , usersDataList) ;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this ,
                LinearLayoutManager.VERTICAL , false) ;
        usersList.setLayoutManager(layoutManager) ;
        usersList.setAdapter(adapter) ;
    }
}