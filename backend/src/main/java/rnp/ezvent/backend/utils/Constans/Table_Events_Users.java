package rnp.ezvent.backend.utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Events_Users {
    public final static String Table_Name = "Events_Users";

    public final static String Event_ID = "Event_ID";
    public final static String User_ID = "User_ID";
    public final static String Attending = "Attending";
    public final static String Permission = "Permission";

    public final static int Size = 4;
    public final static int Event_ID_num = 0;
    public final static int User_ID_num = 1;
    public final static int Attending_num = 2;
    public final static int Permission_num = 3;


    public final static String[] getAllFields(){
        return new String[]{Event_ID, User_ID, Attending, Permission};
    }

    public static int Size(){
        return getAllFields().length;
    }


}

