package com.example.feedpost.Content.UsersList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.example.feedpost.Utility.extract;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.checkerframework.checker.guieffect.qual.UI;

import java.util.ArrayList;

public class chooseUserActivity extends AppCompatActivity {
    // resources
    private String currentUserName ;
    // widgets
    private RecyclerView usersList ;
    private ProgressBar loadingBar ;
    // variables
    private ArrayList<UserListModel> usersDataList ;
    // adapters
    private UserListAdapter adapter ;
    // firebase
    private FirebaseAuth mAuth ;
    private DocumentReference mReference ;
    private DatabaseReference mRealDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        initializeWidgetsAndVariables() ;
        initializeDatabase() ;
        fillListWithData() ;
        initializeAdapter() ;

    }

    // 1 .
    private void initializeWidgetsAndVariables(){
        usersList = findViewById(R.id.usersCardView) ;
        loadingBar = findViewById(R.id.waitingBar) ;
        usersDataList = new ArrayList<>() ;
    }
    // 2 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
    }
    // 3 .
    private void fillListWithData(){
        loadingBar.setVisibility(View.VISIBLE) ;
        String email = extract.getDocument(mAuth.getCurrentUser().getEmail()) ;
        String UID = mAuth.getCurrentUser().getUid() ;
        mReference = FirebaseFirestore.getInstance().collection(email).document(UID);
        mReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    currentUserName = documentSnapshot.getString(documentFields.UserName) ;
                    mRealDatabase.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                UserListModel userListModel = new UserListModel();
                                String name = dataSnapshot.child("name").getValue(String.class) ;
                                if (!currentUserName.equals(name)) {
                                    userListModel.setProfileName(name);
                                    String gender = dataSnapshot.child("gender").getValue(String.class) ;
                                    if(gender.equals("male"))
                                        userListModel.setProfilePic(R.drawable.male);
                                    if(gender.equals("female"))
                                        userListModel.setProfilePic(R.drawable.female) ;
                                    if(gender.equals("skip") || gender.isEmpty())
                                        userListModel.setProfilePic(R.drawable.skip) ;
                                    usersDataList.add(userListModel) ;

                                    loadingBar.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged() ;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }) ;
                }
            }
        }) ;

    }
    // 4 .
    private void initializeAdapter(){
        adapter = new UserListAdapter(chooseUserActivity.this , usersDataList) ;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this ,
                LinearLayoutManager.VERTICAL , false) ;
        usersList.setLayoutManager(layoutManager) ;
        usersList.setAdapter(adapter) ;
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