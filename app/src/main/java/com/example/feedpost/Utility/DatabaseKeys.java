package com.example.feedpost.Utility;

/**
 * keeps all database primary keys
 */
public class DatabaseKeys {
    /**
     * Realtime database
     */
    public static final class Realtime{
        public static final String users = "users" ;
        public static final String posts = "posts" ;
        public static final String follower = "follower" ;
        public static final String following = "following" ;
        public static final String Likes = "Likes" ;
        public static class _Likes_{
            public static String name = "name" ;
        }
        public static final String Comments = "Comments" ;
        public static final class _Comments_{
            public static final String comment = "comment" ;
        }
        public static final String profile = "profile" ;
        public static final class  _profile_ {
            public static final String profilePicFile = "profilePicFile" ;
            public static final String profileBgFile = "profileBgFile" ;
        }
    }
    /**
     * Fire-Store References
     */
    public static final class FireStore{

    }
    /**
     * Storage
     */
    public static final class Storage{
        public static final String usersUploads = "userUploads" ;
        public static final String profilePicture = "ProfilePicture" ;
        public static final String profileBanner  = "ProfileBanner" ;
    }
}
