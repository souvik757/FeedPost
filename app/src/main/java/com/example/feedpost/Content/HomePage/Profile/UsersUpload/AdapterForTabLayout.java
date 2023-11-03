package com.example.feedpost.Content.HomePage.Profile.UsersUpload;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.PictureFragment.PictureFragment;
import com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.VideoFragment;

public class AdapterForTabLayout extends FragmentPagerAdapter {
    int totalTabs ;
    private Context context ;

    public AdapterForTabLayout(@NonNull FragmentManager fm, int totalTabs, Context context) {
        super(fm);
        this.totalTabs = totalTabs;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 :
                PictureFragment frag1 = new PictureFragment() ;
                return frag1 ;
            case 1 :
                VideoFragment frag2 = new VideoFragment() ;
                return frag2 ;
            default:
                return null ;
        }
    }

    @Override
    public int getCount() {
        return totalTabs ;
    }
}
