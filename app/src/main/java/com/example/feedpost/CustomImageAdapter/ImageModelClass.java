package com.example.feedpost.CustomImageAdapter;

public class ImageModelClass {
    String postUid ;
    String userName ;
    String fileName ;

    public ImageModelClass() {
    }

    public ImageModelClass(String postUid, String userName , String fileName) {
        this.postUid = postUid;
        this.userName = userName ;
        this.fileName = fileName;
    }

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
