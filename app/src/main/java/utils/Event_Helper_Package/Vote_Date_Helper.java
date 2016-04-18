package utils.Event_Helper_Package;

import java.util.HashMap;

/**
 * Created by Ravid on 05/03/2016.
 */
public class Vote_Date_Helper {

    private String Start_Date;
    private String End_Date;
    private String All_Day;
    private String Start_Time;
    private String End_Time;
    private HashMap<String, String> votes;//User_ID, User_ID



    public Vote_Date_Helper(String start_Date, String end_Date, String all_Day, String start_Time, String end_Time) {
        Start_Date = start_Date;
        End_Date = end_Date;
        All_Day = all_Day;
        Start_Time = start_Time;
        End_Time = end_Time;
        this.votes = new HashMap<>();
    }

    public String getAll_Day() {
        return All_Day;
    }

    public void setAll_Day(String all_Day) {
        All_Day = all_Day;
    }

    public String getStart_Date() {
        return Start_Date;
    }

    public void setStart_Date(String start_Date) {
        Start_Date = start_Date;
    }

    public String getEnd_Date() {
        return End_Date;
    }

    public void setEnd_Date(String end_Date) {
        End_Date = end_Date;
    }

    public String getStart_Time() {
        return Start_Time;
    }

    public void setStart_Time(String start_Time) {
        Start_Time = start_Time;
    }

    public String getEnd_Time() {
        return End_Time;
    }

    public void setEnd_Time(String end_Time) {
        End_Time = end_Time;
    }

    public HashMap<String, String> getVotes() {
        return votes;
    }

    public void setVotes(HashMap<String, String> votes) {
        this.votes = votes;
    }
}
