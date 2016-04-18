package utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Tasks {
    public final static String Table_Name = "Tasks";

    public final static String Event_ID = "Event_ID";
    private final static String Event_ID_SQL_Params = "varchar NOT NULL";
    public final static String Task_ID_Number = "Task_ID_Number";
    private final static String Task_ID_Number_SQL_Params = "varchar NOT NULL";
    public final static String subTask_ID_Number = "subTask_ID_Number";
    private final static String subTask_ID_Number_SQL_Params = "varchar NOT NULL";
    public final static String Task_Type = "Task_Type";
    private final static String Task_Type_SQL_Params = "varchar NOT NULL";
    public final static String Description = "Description";
    private final static String Description_SQL_Params = "varchar NOT NULL";
    public final static String User_ID = "User_ID";
    private final static String User_ID_SQL_Params = "varchar NOT NULL";
    public final static String Mark = "Mark";
    private final static String Mark_SQL_Params = "varchar NOT NULL";

    public final static int Event_ID_num = 0;
    public final static int Task_ID_Number_num = 1;
    public final static int subTask_ID_Number_num = 2;
    public final static int Task_Type_num = 3;
    public final static int Description_num = 4;
    public final static int User_ID_num = 5;
    public final static int Mark_num = 6;


    public final static String[] getAllFields() {
        return new String[]{Event_ID, Task_ID_Number, subTask_ID_Number, Task_Type, Description, User_ID, Mark};
    }

    public final static String[] getAllSqlParams() {
        return new String[]{Event_ID_SQL_Params, Task_ID_Number_SQL_Params, subTask_ID_Number_SQL_Params, Task_Type_SQL_Params, Description_SQL_Params, User_ID_SQL_Params, Mark_SQL_Params};
    }

    public static int Size() {
        return getAllFields().length;
    }

}

