package utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Events_Users {
    public final static String Table_Name = "Events_Users";

    public final static String Event_ID = "Event_ID";
    private final static String Event_ID_SQL_Params = "varchar NOT NULL";
    public final static String User_ID = "User_ID";
    private final static String User_ID_SQL_Params = "varchar NOT NULL";
    public final static String Attending = "Attending";
    private final static String Attending_SQL_Params = "varchar NOT NULL";
    public final static String Permission = "Permission";
    private final static String Permission_SQL_Params = "varchar NOT NULL";

    public final static int Event_ID_num = 0;
    public final static int User_ID_num = 1;
    public final static int Attending_num = 2;
    public final static int Permission_num = 3;

    public final static String[] getAllFields(){
        return new String[]{Event_ID, User_ID, Attending, Permission};
    }

    public final static String[] getAllSqlParams(){
        return new String[]{Event_ID_SQL_Params, User_ID_SQL_Params, Attending_SQL_Params, Permission_SQL_Params};
    }

    public static int Size(){
        return getAllFields().length;
    }
}

