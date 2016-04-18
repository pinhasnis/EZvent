package utils.Event_Helper_Package;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Constans.Constants;
import utils.Constans.Table_Events;
import utils.Constans.Table_Events_Users;
import utils.Constans.Table_Tasks;
import utils.Constans.Table_Vote_Date;
import utils.Constans.Table_Vote_Location;
import utils.sqlHelper;

/**
 * Created by Ravid on 11/01/2016.
 */
public final class Event_Helper {
    public static String newEvent_edit_mode = Constants.New_Event;
    public static String[] details = new String[Table_Events.Size()];
    public static HashMap<String, Friend_Helper> friends = new HashMap<>();//0-User_ID.
    public static HashMap<String, Friend_Helper> friends_tmp;//Use for edit event.
    public static HashMap<Integer, Task_Helper> task = new HashMap();//0-Task_ID
    public static HashMap<Integer, Task_Helper> task_tmp;//Use for edit event.
    public static HashMap<Integer, Vote_Date_Helper> vote_date = new HashMap();//0-Vote_ID
    public static HashMap<Integer, Vote_Date_Helper> vote_date_tmp;//Use for edit event.
    public static HashMap<Integer, Vote_Location_Helper> vote_location = new HashMap();//0-Vote_ID
    public static HashMap<Integer, Vote_Location_Helper> vote_location_tmp;//Use for edit event.
    public static int task_ID_generator = 0;//Store the last ID number that was in use.
    public static int vote_date_ID_generator = 0;//Store the last ID number that was in use.
    public static int vote_location_ID_generator = 0;//Store the last ID number that was in use.


    public static void clear() {
        for (int i = 0; i < details.length; i++) {
            details[i] = "";
        }
        details[Table_Events.Start_Date_num] = "dd/mm/yyyy";
        details[Table_Events.End_Date_num] = "dd/mm/yyyy";
        details[Table_Events.All_Day_Time_num] = Constants.No;
        details[Table_Events.Start_Time_num] = "hh:mm";
        details[Table_Events.End_Time_num] = "hh:mm";
        details[Table_Events.Vote_Location_num] = Constants.No;
        details[Table_Events.Vote_Time_num] = Constants.No;
        friends.clear();
        task.clear();
        vote_date.clear();
        vote_location.clear();
        task_ID_generator = 0;
        vote_date_ID_generator = 0;
        vote_location_ID_generator = 0;
    }

    public static void make_copy() {
        friends_tmp = new HashMap<>();
        task_tmp = new HashMap<>();
        vote_date_tmp = new HashMap<>();
        vote_location_tmp = new HashMap<>();
        for (String friend : friends.keySet()) {
            Friend_Helper friend_helper = friends.get(friend).make_copy();
            friends_tmp.put(friend, friend_helper);
        }
        for (Integer task_id : task.keySet()) {
            Task_Helper task_helper = task.get(task_id).make_copy();
            task_tmp.put(task_id, task_helper);
        }
        for (Integer vote_id : vote_date.keySet()) {
            Vote_Date_Helper vote_date_helper = vote_date.get(vote_id);
            vote_date_tmp.put(vote_id, vote_date_helper);
        }
        for (Integer vote_id : vote_location.keySet()) {
            Vote_Location_Helper vote_location_helper = vote_location.get(vote_id);
            vote_location_tmp.put(vote_id, vote_location_helper);
        }
    }

    public static int load_event(String event_id) {
        clear();
        //Load details.
        details[Table_Events.Event_ID_num] = event_id;
        ArrayList<String>[] dbSql = sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{event_id}, new int[]{1});
        if(dbSql[Table_Events.Name_num].size() == 0) return -1;
        details[Table_Events.Name_num] = dbSql[Table_Events.Name_num].get(0);
        details[Table_Events.Location_num] = dbSql[Table_Events.Location_num].get(0);
        details[Table_Events.Vote_Location_num] = dbSql[Table_Events.Vote_Location_num].get(0);
        details[Table_Events.Start_Date_num] = dbSql[Table_Events.Start_Date_num].get(0);
        details[Table_Events.End_Date_num] = dbSql[Table_Events.End_Date_num].get(0);
        details[Table_Events.All_Day_Time_num] = dbSql[Table_Events.All_Day_Time_num].get(0);
        details[Table_Events.Start_Time_num] = dbSql[Table_Events.Start_Time_num].get(0);
        details[Table_Events.End_Time_num] = dbSql[Table_Events.End_Time_num].get(0);
        details[Table_Events.Vote_Time_num] = dbSql[Table_Events.Vote_Time_num].get(0);
        details[Table_Events.Description_num] = dbSql[Table_Events.Description_num].get(0);
        details[Table_Events.Image_Path_num] = dbSql[Table_Events.Image_Path_num].get(0);
        details[Table_Events.Update_Time_num] = dbSql[Table_Events.Update_Time_num].get(0);

        //Load Users.
        dbSql = sqlHelper.select(null, Table_Events_Users.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{event_id}, null);
        for (int i = 0; i < dbSql[0].size(); i++) {
            friends.put(dbSql[Table_Events_Users.User_ID_num].get(i), new Friend_Helper(dbSql[Table_Events_Users.Permission_num].get(i), dbSql[Table_Events_Users.Attending_num].get(i)));
        }

        //Load Tasks.
        dbSql = sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{event_id}, null);
        Task_Helper task_helper;
        int task_id, subTask_id;
        for (int i = 0; i < dbSql[0].size(); i++) {
            task_id = Integer.parseInt(dbSql[Table_Tasks.Task_ID_Number_num].get(i));
            subTask_id = Integer.parseInt(dbSql[Table_Tasks.subTask_ID_Number_num].get(i));
            if (subTask_id == 0) {//Check if it's a task or subTask.
                task_helper = new Task_Helper(dbSql[Table_Tasks.Task_Type_num].get(i), dbSql[Table_Tasks.Description_num].get(i),
                        dbSql[Table_Tasks.User_ID_num].get(i), dbSql[Table_Tasks.Mark_num].get(i));
                task.put(task_id, task_helper);
                task_ID_generator = Math.max(task_ID_generator, task_id + 1);
            } else {
                task.get(task_id).getSubTasks().put(subTask_id, new String[]{dbSql[Table_Tasks.Description_num].get(i), dbSql[Table_Tasks.Mark_num].get(i)});
                task.get(task_id).setSubTask_ID_generator(Math.max(task.get(task_id).getSubTask_ID_generator(), subTask_id + 1));
            }
        }
        //Load vote_date.
        dbSql = sqlHelper.select(null, Table_Vote_Date.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{event_id}, null);
        Vote_Date_Helper vote_date_helper;
        int vote_date_id;
        for (int i = 0; i < dbSql[0].size(); i++) {
            vote_date_id = Integer.parseInt(dbSql[Table_Vote_Date.Vote_ID_num].get(i));
            if (dbSql[Table_Vote_Date.User_ID_num].get(i).equals(Constants.UnCheck)) {//Check if it's a location or user_id.
                vote_date_helper = new Vote_Date_Helper(dbSql[Table_Vote_Date.Start_Date_num].get(i), dbSql[Table_Vote_Date.End_Date_num].get(i),
                        dbSql[Table_Vote_Date.All_Day_Time_num].get(i), dbSql[Table_Vote_Date.Start_Time_num].get(i), dbSql[Table_Vote_Date.End_Time_num].get(i));
                vote_date.put(vote_date_id, vote_date_helper);
                vote_date_ID_generator = Math.max(vote_date_ID_generator, vote_date_id);
            } else {
                String User_ID = dbSql[Table_Vote_Date.User_ID_num].get(i);
                vote_date.get(vote_date_id).getVotes().put(User_ID, User_ID);
            }
        }
        //Load vote_location.
        dbSql = sqlHelper.select(null, Table_Vote_Location.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{event_id}, null);
        Vote_Location_Helper vote_location_helper;
        int vote_location_id;
        for (int i = 0; i < dbSql[0].size(); i++) {
            vote_location_id = Integer.parseInt(dbSql[Table_Vote_Location.Vote_ID_num].get(i));
            if (dbSql[Table_Vote_Location.User_ID_num].get(i).equals(Constants.UnCheck)) {//Check if it's a location or user_id.
                vote_location_helper = new Vote_Location_Helper(dbSql[Table_Vote_Location.Description_num].get(i));
                vote_location.put(vote_location_id, vote_location_helper);
                vote_location_ID_generator = Math.max(vote_location_ID_generator, vote_location_id);
            } else {
                String User_ID = dbSql[Table_Vote_Location.User_ID_num].get(i);
                vote_location.get(vote_location_id).getVotes().put(User_ID, User_ID);
            }
        }
        return 0;
    }
}


