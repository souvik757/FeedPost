package com.example.feedpost.Content.HomePage.HomeContents;

import com.google.firebase.storage.StorageReference;

public class PostDataModel {
    // textview
    private String ID ;
    private String extractID ;
    private String adminName ;
    private int countOfLike ;
    private int countOfComment ;
    private String adminComment ;
    private String ref ;

    public PostDataModel() {
    }

    public PostDataModel(String ID ,
                         String extractID,
                         String adminName,
                         int countOfLike,
                         int countOfComment,
                         String adminComment ,
                         String ref) {
        this.ID = ID ;
        this.extractID = extractID ;
        this.adminName = adminName;
        this.countOfLike = countOfLike;
        this.countOfComment = countOfComment;
        this.adminComment = adminComment;
        this.ref = ref ;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getExtractID() {
        return extractID;
    }

    public void setExtractID(String extractID) {
        this.extractID = extractID;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public int getCountOfLike() {
        return countOfLike;
    }

    public void setCountOfLike(int countOfLike) {
        this.countOfLike = countOfLike;
    }

    public int getCountOfComment() {
        return countOfComment;
    }

    public void setCountOfComment(int countOfComment) {
        this.countOfComment = countOfComment;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
