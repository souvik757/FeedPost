package com.example.feedpost;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.feedpost.Content.HomePage.Profile.UsersUpload.AdapterForTabLayout;
import com.example.feedpost.Utility.documentFields;
import com.google.android.material.tabs.TabLayout;

public class UsersPostActivity extends AppCompatActivity {

    private String username ;
    private TabLayout tabLayout ;
    private ViewPager viewPager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_post);
        initializeWidgets();
        setResources();
    }
    private void initializeWidgets(){
        tabLayout = findViewById(R.id.idTLPosts) ;
        viewPager = findViewById(R.id.idVPPosts) ;
    }
    private void setResources(){
        username = getIntent().getStringExtra(documentFields.rawDataFields.userName) ;
        ActionBar actionBar = getSupportActionBar() ;
        actionBar.setTitle(username+"'s uploads");
        tabLayout.addTab(tabLayout.newTab().setText("Photos"));
        tabLayout.addTab(tabLayout.newTab().setText("Videos"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final AdapterForTabLayout adapter = new AdapterForTabLayout(getSupportFragmentManager(), tabLayout.getTabCount(), UsersPostActivity.this) ;
        viewPager.setAdapter(adapter) ;
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout)) ;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition()) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}