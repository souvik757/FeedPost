package com.example.feedpost.Utility;

public class extract {
    public static String getName(String FN , String LN){
        return FN +" "+ LN;
    }
    public static String getDocument(String email){
        int index= 0 ;
        char[] ch = new char[email.length()] ;
        for (int i = 0 ; i < email.length() ; i ++){
            if(email.charAt(i) == '@')
                break ;
            else {
                ch[index] = email.charAt(i);
                index++;
            }
        }

        return new String(ch) ;
    }
}
