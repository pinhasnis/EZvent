package utils.Constans;

/**
 * Created by Ravid on 04/03/2016.
 */
public final class Table_Vote_Location {
    public final static String Table_Name = "Vote_Location";

    public final static String Event_ID = "Event_ID";
    private final static String Event_ID_SQL_Params = "varchar NOT NULL";
    public final static String Vote_ID = "Vote_ID";
    private final static String Vote_ID_SQL_Params = "varchar NOT NULL";
    public final static String Description = "Description";
    private final static String Description_SQL_Params = "varchar NOT NULL";
    public final static String User_ID = "User_ID";//All users that vote for that option.
    private final static String User_ID_SQL_Params = "varchar NOT NULL";

    public final static int Event_ID_num = 0;
    public final static int Vote_ID_num = 1;
    public final static int Description_num = 2;
    public final static int User_ID_num = 3;


    public final static String[] getAllFields() {
        return new String[]{Event_ID, Vote_ID, Description, User_ID};
    }

    public final static String[] getAllSqlParams() {
        return new String[]{Event_ID_SQL_Params, Vote_ID_SQL_Params, Description_SQL_Params, User_ID_SQL_Params};
    }

    public static int Size() {
        return getAllFields().length;
    }
}