package rnp.ezvent.backend.utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Tasks {
    public final static String Table_Name = "Tasks";

    public final static String Event_ID = "Event_ID";
    public final static String Task_ID_Number = "Task_ID_Number";
    public final static String subTask_ID_Number = "subTask_ID_Number";
    public final static String Task_Type = "Task_Type";
    public final static String Description = "Description";
    public final static String User_ID = "User_ID";

    public final static int Size = 6;
    public final static int Event_ID_num = 0;
    public final static int Task_ID_Number_num = 1;
    public final static int subTask_ID_Number_num = 2;
    public final static int Task_Type_num = 3;
    public final static int Description_num = 4;
    public final static int User_ID_num = 5;

    public final static String[] getAllFields() {
        return new String[]{Event_ID, Task_ID_Number, subTask_ID_Number, Task_Type, Description, User_ID};
    }

    public static int Size(){
        return getAllFields().length;
    }

}

