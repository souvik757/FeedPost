package com.example.feedpost.Utility;
// fields that contains value in Firebase Fire-store and Realtime database
public class documentFields {
    public static final String UserName   = "UserName"   ;
    public static final String Gender     = "Gender"     ;
    public static final String ProfilePic = "ProfilePic" ;
    public static final String ProfileBG  = "ProfileBG"  ;
    public static final String ProfileBio = "ProfileBio" ;
    public static class realtimeFields{
        public static final String email  = "email" ;
        public static final String fullName   = "fullName" ;
        public static final String gender = "gender" ;
        public static final String bio = "bio" ;
        public static final String hasProfilePic = "hasProfilePic"  ;
        public static final String hasProfileBg = "hasProfileBg"  ;
    }
    public static class realtimePostFields{
        public static final String Admin = "Admin" ;
        public static class _Admin_ {
            public static final String ID = "ID" ;
            public static final String NAME = "NAME" ;
            public static final String EXTRACED_EMAIL = "EXTRACEDEMAIL" ;
            public static final String COMMENT = "COMMENT" ;
            public static final String CONTENTFILE = "CONTENTFILE" ;
        }
        public static final String Likes = "Likes" ;
        public static final class Likes {
            public static final String COUNT = "COUNT" ;
            public static final String PERSONS = "PERSONS" ;
        }
    }
}

