package com.example.feedpost.Content.HomePage.Profile.RefinedList;

public class RefinedModelClass {
    private String parameter ; // 'follower' | 'following'
    private String name ;
    private String imgFile ;

    public RefinedModelClass(String parameter, String name, String imgFile) {
        this.parameter = parameter;
        this.name = name;
        this.imgFile = imgFile;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }
}
