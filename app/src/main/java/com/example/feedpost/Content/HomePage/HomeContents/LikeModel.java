package com.example.feedpost.Content.HomePage.HomeContents;

public class LikeModel {
    private String name ;
    private boolean like ;
    private String profileImgFile ;

    public LikeModel(String name, boolean like, String profileImgFile) {
        this.name = name;
        this.like = like ;
        this.profileImgFile = profileImgFile ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getProfileImgFile() {
        return profileImgFile;
    }

    public void setProfileImgFile(String profileImgFile) {
        this.profileImgFile = profileImgFile;
    }
}
