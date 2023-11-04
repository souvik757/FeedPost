package com.example.feedpost.Content.UsersList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
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

public class chooseUserActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    // layouts
    private SwipeRefreshLayout swipeRefreshLayout ;
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
        init() ;
        swipeRefreshLayout = findViewById(R.id.swipeRefresh) ;
        swipeRefreshLayout.setOnRefreshListener(this);

    }
    @Override
    public void onRefresh() {
        init() ;
        swipeRefreshLayout.setRefreshing(false);
    }
    private void init(){
        initializeWidgetsAndVariables() ;
        initializeDatabase() ;
        fillListWithData() ;
        initializeAdapter() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater() ;
        inflater.inflate(R.menu.search_user , menu) ;
        MenuItem searchItem = menu.findItem(R.id.actionSearch) ;
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText) ;
                return false;
            }
        });

        return true ;
    }
    //  .
    private void filter(String text){
        ArrayList<UserListModel> filteredList = new ArrayList<>() ;
        for (UserListModel item : usersDataList){
            if(item.getProfileName().toLowerCase().contains(text.toLowerCase()))
                filteredList.add(item) ;
        }
        if (!filteredList.isEmpty())
            adapter.filterList(filteredList) ;
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(DatabaseKeys.Realtime.users).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUserName = snapshot.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                    mRealDatabase.child(DatabaseKeys.Realtime.users).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot users : snapshot.getChildren()){
                                String uid = users.getKey() ;
                                String name = users.child(documentFields.realtimeFields.fullName).getValue(String.class) ;
                                if (!currentUserName.equals(name)) {
                                    String profilePicFile ;
                                    boolean hasProfilePic = users.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                                    if(hasProfilePic)
                                        profilePicFile = users.child(DatabaseKeys.Realtime.profile).child(DatabaseKeys.Realtime._profile_.profilePicFile).getValue(String.class) ;
                                    else
                                        profilePicFile = DatabaseKeys.Storage.user ;
                                    UserListModel model = new UserListModel(profilePicFile,name) ;
                                    usersDataList.add(model) ;
                                    loadingBar.setVisibility(View.GONE) ;
                                    adapter.notifyDataSetChanged() ;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            throw error.toException() ;
                        }
                    }) ;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
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
        toast.setDuration((int)1200) ;
        toast.setView(layout);
        toast.show() ;
    }
}