package rnp.ezvent.backend.utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Users {
    public final static String Table_Name = "Users";

    public final static String User_ID = "User_ID";
    public final static String Nickname = "Nickname";
    public final static String Password = "Password";

    public final static String[] getAllFields(){
        return new String[]{User_ID, Nickname, Password};
    }

    public static int Size(){
        return getAllFields().length;
    }
}
