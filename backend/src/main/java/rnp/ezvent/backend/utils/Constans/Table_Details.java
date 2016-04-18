package rnp.ezvent.backend.utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Details {

    public final static String Table_Name = "Events";

    public final static String Event_ID = "Event_ID";
    public final static String Name = "Name";
    public final static String Location = "Location";
    public final static String Vote_Location = "Vote_Location";
    public final static String Start_Date = "Start_Date";
    public final static String End_Date = "End_Date";
    public final static String All_Day_Time = "All_Day_Time";
    public final static String Start_Time = "Start_Time";
    public final static String End_Time = "End_Time";
    public final static String Vote_Time = "Vote_Time";
    public final static String Description = "Description";
    public final static String Image_Path = "Image_Path";
    public final static String Update_Time = "Update_Time";

    public final static int Size = 13;
    public final static int Event_ID_num = 0;
    public final static int Name_num = 1;
    public final static int Location_num = 2;
    public final static int Vote_Location_num = 3;
    public final static int Start_Date_num = 4;
    public final static int End_Date_num = 5;
    public final static int All_Day_Time_num = 6;
    public final static int Start_Time_num = 7;
    public final static int End_Time_num = 8;
    public final static int Vote_Time_num = 9;
    public final static int Description_num = 10;
    public final static int Image_Path_num = 11;
    public final static int Update_Time_num = 12;

    public final static String[] getAllFields() {
        return new String[]{Event_ID, Name, Location, Vote_Location, Start_Date, End_Date, All_Day_Time, Start_Time, End_Time, Vote_Time, Description, Image_Path, Update_Time};
    }

    public final static String[] getAllFields_Except_Event_ID() {
        return new String[]{Name, Location, Vote_Location, Start_Date, End_Date, All_Day_Time, Start_Time, End_Time, Vote_Time, Description, Image_Path, Update_Time};
    }

    public static int Size(){
        return getAllFields().length;
    }

}
