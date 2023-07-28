package com.example.feedpost.Content.UsersList;

public class UserListModel {
    private int profilePic ;
    private String profileName ;

    public UserListModel() {
    }

    public UserListModel(int profilePic, String profileName) {
        this.profilePic = profilePic;
        this.profileName = profileName;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
