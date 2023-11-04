package com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.PictureFragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.PictureFragment.ModelAndAdapter.PictureAdapter;
import com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.PictureFragment.ModelAndAdapter.PictureModel;
import com.example.feedpost.R;
import com.example.feedpost.Utility.DatabaseKeys;
import com.example.feedpost.Utility.documentFields;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictureFragment newInstance(String param1, String param2) {
        PictureFragment fragment = new PictureFragment();
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
    // view
    private View parentViewHolder ;
    // widgets
    private SwipeRefreshLayout swipeRefreshLayout ;
    private RecyclerView recyclerView ;
    private ArrayList<PictureModel> pictureModelLists ;
    private PictureAdapter adapter ;
    private ProgressBar progressBar ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentViewHolder =  inflater.inflate(R.layout.fragment_picture, container, false);
        init(parentViewHolder) ;
        swipeRefreshLayout = parentViewHolder.findViewById(R.id.idSRLPictureFragment) ;
        swipeRefreshLayout.setOnRefreshListener(this);
        return parentViewHolder ;
    }

    @Override
    public void onRefresh() {
        init(parentViewHolder) ;
        swipeRefreshLayout.setRefreshing(false);
    }
    private void init(View view){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Uploads");
        recyclerView = view.findViewById(R.id.idRVPictureFragment) ;
        progressBar = view.findViewById(R.id.idProgressBar) ;
        pictureModelLists = new ArrayList<>() ;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext()) ;
        adapter = new PictureAdapter(view.getContext(), pictureModelLists) ;
        recyclerView.setLayoutManager(layoutManager) ;
        recyclerView.setAdapter(adapter) ;
        recyclerView.setNestedScrollingEnabled(false) ;
        recyclerView.setHasFixedSize(true) ;
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext() , LinearLayoutManager.VERTICAL));
        sync() ;
    }
    private void sync(){
        progressBar.setVisibility(View.VISIBLE);
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference() ;
        ref.child(DatabaseKeys.Realtime.users).child(UID).child(documentFields.realtimeFields.PostedPicture).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot posts : snapshot.getChildren()){
                        // 1 . postUID
                        String postUid = posts.getKey() ;
                        // 2 . username
                        String userName = posts.child(documentFields.realtimeFields._PostedPicture_.userName).getValue(String.class) ;
                        // 3 . posted picture file
                        String fileName = posts.child(documentFields.realtimeFields._PostedPicture_.fileName).getValue(String.class) ;
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                        ref1.child(DatabaseKeys.Realtime.posts).child(postUid).child(documentFields.realtimePostFields.Admin).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    // 4 . user's comment
                                    String comment = snapshot.child(documentFields.realtimePostFields._Admin_.COMMENT).getValue(String.class) ;
                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference() ;
                                    ref2.child(DatabaseKeys.Realtime.users).child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean hasProfilePic = snapshot.child(documentFields.realtimeFields.hasProfilePic).getValue(Boolean.class) ;
                                            // 5 . user's profile picture
                                            String profilePicFile ;
                                            if(hasProfilePic)
                                                profilePicFile = snapshot.child(DatabaseKeys.Realtime.profile).child(DatabaseKeys.Realtime._profile_.profilePicFile).getValue(String.class) ;
                                            else
                                                profilePicFile = DatabaseKeys.Storage.user ;
                                            PictureModel model = new PictureModel(postUid,userName,comment,profilePicFile,fileName) ;
                                            pictureModelLists.add(model) ;
                                            progressBar.setVisibility(View.GONE) ;
                                            adapter.notifyDataSetChanged() ;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            throw error.toException() ;
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                throw error.toException() ;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }
}