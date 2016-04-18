package rnp.ezvent.backend.utils.Constans;

/**
 * Created by Ravid on 31/10/2015.
 */
public final class Table_Users_Devices {
    public final static String Table_Name = "Users_Devices";

    public final static String User_ID = "User_ID";
    public final static String Registration_ID = "Registration_ID";

    public final static String[] getAllFields(){
        return new String[]{User_ID, Registration_ID};
    }


    public static int Size(){
        return getAllFields().length;
    }
}
