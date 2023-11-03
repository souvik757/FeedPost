package com.example.feedpost.Content.HomePage.Profile.UsersUpload.Fragments.PictureFragment.ModelAndAdapter;

public class PictureModel {
    /**
     * 1 . post id
     * 2 . user name
     * 3 . user profile image file
     * 4 . post image file
     */
    private String postID ;
    private String userName ;
    private String profilePictureFile ;
    private String postPictureFile ;

    public PictureModel(String postID, String userName, String profilePictureFile, String postPictureFile) {
        this.postID = postID;
        this.userName = userName;
        this.profilePictureFile = profilePictureFile;
        this.postPictureFile = postPictureFile;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePictureFile() {
        return profilePictureFile;
    }

    public void setProfilePictureFile(String profilePictureFile) {
        this.profilePictureFile = profilePictureFile;
    }

    public String getPostPictureFile() {
        return postPictureFile;
    }

    public void setPostPictureFile(String postPictureFile) {
        this.postPictureFile = postPictureFile;
    }
}
