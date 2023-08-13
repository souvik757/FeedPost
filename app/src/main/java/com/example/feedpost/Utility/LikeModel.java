package com.example.feedpost.Utility;

public class LikeModel {
    private String name ;
    private String profileImgFile ;

    public LikeModel(String name, String profileImgFile) {
        this.name = name;
        this.profileImgFile = profileImgFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImgFile() {
        return profileImgFile;
    }

    public void setProfileImgFile(String profileImgFile) {
        this.profileImgFile = profileImgFile;
    }
}
