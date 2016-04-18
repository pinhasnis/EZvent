package utils.Constans;

/**
 * Created by Ravid on 05/03/2016.
 */
public class Table_Vote_Date {
    public final static String Table_Name = "Vote_Date";

    public final static String Event_ID = "Event_ID";
    private final static String Event_ID_SQL_Params = "varchar NOT NULL";
    public final static String Vote_ID = "Vote_ID";
    private final static String Vote_ID_SQL_Params = "varchar NOT NULL";
    public final static String Start_Date = "Start_Date";
    private final static String Start_Date_SQL_Params = "varchar NOT NULL";
    public final static String End_Date = "End_Date";
    private final static String End_Date_SQL_Params = "varchar NOT NULL";
    public final static String All_Day_Time = "All_Day_Time";
    private final static String All_Day_Time_SQL_Params = "varchar NOT NULL";
    public final static String Start_Time = "Start_Time";
    private final static String Start_Time_SQL_Params = "varchar NOT NULL";
    public final static String End_Time = "End_Time";
    private final static String End_Time_SQL_Params = "varchar NOT NULL";
    public final static String User_ID = "User_ID";//All users that vote for that option.
    private final static String User_ID_SQL_Params = "varchar NOT NULL";

    public final static int Event_ID_num = 0;
    public final static int Vote_ID_num = 1;
    public final static int Start_Date_num = 2;
    public final static int End_Date_num = 3;
    public final static int All_Day_Time_num = 4;
    public final static int Start_Time_num = 5;
    public final static int End_Time_num = 6;
    public final static int User_ID_num = 7;


    public final static String[] getAllFields() {
        return new String[]{Event_ID, Vote_ID, Start_Date, End_Date, All_Day_Time, Start_Time, End_Time, User_ID};
    }

    public final static String[] getAllSqlParams() {
        return new String[]{Event_ID_SQL_Params, Vote_ID_SQL_Params, Start_Date_SQL_Params, End_Date_SQL_Params, All_Day_Time_SQL_Params,
                Start_Time_SQL_Params, End_Time_SQL_Params, User_ID_SQL_Params};
    }

    public static int Size() {
        return getAllFields().length;
    }
}
