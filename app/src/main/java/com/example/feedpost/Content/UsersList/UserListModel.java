package com.example.feedpost.Content.UsersList;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class UserListModel {
    private String profilePicFile ;
    private String profileName ;
    public UserListModel(String profilePicFile, String profileName) {
        this.profilePicFile = profilePicFile;
        this.profileName = profileName;
    }

    public String getProfilePic() {
        return profilePicFile;
    }

    public void setProfilePic(String profilePicFile) {
        this.profilePicFile = profilePicFile;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
