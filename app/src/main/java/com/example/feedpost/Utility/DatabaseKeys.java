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
        public static final String following = "following" ;
        public static final String follower = "follower" ;
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
    }
}
