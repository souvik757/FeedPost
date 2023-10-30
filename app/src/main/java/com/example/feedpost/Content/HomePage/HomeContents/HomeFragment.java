package com.example.feedpost.Content.HomePage.HomeContents ;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.feedpost.R;
import com.example.feedpost.Utility.documentFields;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    // layouts
    View parentHolder ;
    private SwipeRefreshLayout swipeRefreshLayout ;
    // widgets
    private ScrollView mScrollView ;
    private ProgressBar loadingBar ;
    private RecyclerView recyclerView ;
    // firebase
    private FirebaseAuth mAuth ;
    private DocumentReference mReference ;
    private DatabaseReference mRealDatabase ;
    // resources
    private PostDataAdapter adapter ;
    private ArrayList<PostDataModel> postArrayList ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentHolder =  inflater.inflate(R.layout.fragment_home, container, false);
        init() ;
        // swipe refresh
        swipeRefreshLayout = parentHolder.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        return parentHolder ;
    }

    @Override
    public void onRefresh() {
        init() ;
        swipeRefreshLayout.setRefreshing(false);
    }

    // 0 .
    private void init(){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("FeedPost");

        initializeWidgets(parentHolder) ;
        initializeRawResources(parentHolder) ;
        initializeDatabase() ;
        fillListWithData() ;
    }
    // 1 .
    private void initializeWidgets(View view){
        mScrollView = view.findViewById(R.id.contentScrollView) ;
        smoothenScrollView() ;

        loadingBar = view.findViewById(R.id.loadContents) ;
        recyclerView = view.findViewById(R.id.publicPostView) ;
    }
    // 2 .
    private void initializeRawResources(View view){
        postArrayList = new ArrayList<>() ;
        adapter = new PostDataAdapter(view.getContext() , postArrayList) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter) ;
        recyclerView.setNestedScrollingEnabled(false) ;
        recyclerView.setHasFixedSize(true) ;
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext() , LinearLayoutManager.VERTICAL));
    }
    // 3 .
    private void initializeDatabase(){
        mAuth = FirebaseAuth.getInstance() ;
        mRealDatabase = FirebaseDatabase.getInstance().getReference() ;
    }
    // 4 .
    private void fillListWithData(){
        // loading bar start . . .
        loadingBar.setVisibility(View.VISIBLE) ;
        mRealDatabase.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postArrayList.clear() ;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String postUID = dataSnapshot.getKey() ;
                    PostDataModel dataModel = new PostDataModel() ;
                    String Uid = dataSnapshot.child(documentFields.realtimePostFields.Admin)
                            .child(documentFields.realtimePostFields._Admin_.ID).getValue(String.class) ;
                    if(!mAuth.getCurrentUser().getUid().equals(Uid)){
                        String extractedId = dataSnapshot.child(documentFields.realtimePostFields.Admin)
                                .child(documentFields.realtimePostFields._Admin_.EXTRACED_EMAIL).getValue(String.class) ;
                        /* text elements */
                        // - name
                        String name = dataSnapshot.child(documentFields.realtimePostFields.Admin)
                                .child(documentFields.realtimePostFields._Admin_.NAME).getValue(String.class) ;
                        // - comment
                        String comment = dataSnapshot.child(documentFields.realtimePostFields.Admin)
                                .child(documentFields.realtimePostFields._Admin_.COMMENT).getValue(String.class) ;
                        // - image files
                        String image = dataSnapshot.child(documentFields.realtimePostFields.Admin)
                                .child(documentFields.realtimePostFields._Admin_.CONTENTFILE).getValue(String.class)+".jpg" ;

                        dataModel.setID(postUID) ;
                        dataModel.setExtractID(extractedId) ;
                        dataModel.setAdminName(name) ;
                        dataModel.setAdminComment(comment) ;
                        dataModel.setRef(image) ;

                        postArrayList.add(dataModel) ;
                        loadingBar.setVisibility(View.GONE) ;

                        // animation component
                        AnimateRecyclerView(R.anim.animate_scale_in , getContext() , recyclerView) ;

                        adapter.notifyDataSetChanged() ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException() ;
            }
        });
    }




    /**
     *  animation component
     */
    private void smoothenScrollView(){
        ObjectAnimator anim = ObjectAnimator.ofInt(mScrollView, "scrollY", mScrollView.getBottom());
        anim.setDuration(100);
        anim.start();
    }
    private void AnimateRecyclerView(int resId , Context context , RecyclerView recyclerView){
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(context , resId) ;
        recyclerView.setLayoutAnimation(animationController) ;
    }
}