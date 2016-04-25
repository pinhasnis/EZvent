package utils;

import android.content.Context;

import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rnp.backend.ezvent.model.Event;
import rnp.backend.ezvent.model.SimpleUpdate;
import rnp.backend.ezvent.model.UpdateEvent;
import server.Chat.Chat_AsyncTask_delete;
import server.Chat.Chat_AsyncTask_deleteByEvent;
import server.Chat.Chat_AsyncTask_insert;
import server.Event.Event_AsyncTask_create_new_event;
import server.Event.Event_AsyncTask_delete;
import server.Event.Event_AsyncTask_update_field;
import server.Event_User.EventUser_AsyncTask_UpdateAttending;
import server.Event_User.EventUser_AsyncTask_delete;
import server.Event_User.EventUser_AsyncTask_delete_by_event;
import server.Messageing.SendMessage_AsyncTask;
import server.SimpleUpdate.SimpleUpdate_AsyncTask_update;
import server.Task.Task_AsyncTask_deleteByEvent;
import server.Task.Task_AsyncTask_update_User_ID;
import server.UpdateEvent.UpdateEvent_AsyncTask_update_event;
import server.User.User_AsyncTask_get;
import server.Vote_Date.Vote_Date_AsyncTask_deleteByEvent;
import server.Vote_Date.Vote_Date_AsyncTask_delete_vote_user_id;
import server.Vote_Date.Vote_Date_AsyncTask_insert;
import server.Vote_Location.Vote_Location_AsyncTask_deleteByEvent;
import server.Vote_Location.Vote_Location_AsyncTask_delete_vote_user_id;
import server.Vote_Location.Vote_Location_AsyncTask_insert;
import utils.Constans.Constants;
import utils.Constans.Table_Chat;
import utils.Constans.Table_Events;
import utils.Constans.Table_Events_Users;
import utils.Constans.Table_Tasks;
import utils.Constans.Table_Users;
import utils.Constans.Table_Vote_Date;
import utils.Constans.Table_Vote_Location;
import utils.Event_Helper_Package.Event_Helper;
import utils.Event_Helper_Package.Friend_Helper;
import utils.Event_Helper_Package.Task_Helper;
import utils.Event_Helper_Package.Vote_Date_Helper;
import utils.Event_Helper_Package.Vote_Location_Helper;

/**
 * Created by Ravid on 23/10/2015.
 */
public class Helper {

    //private static ArrayList<String[]> Update_Users;//Use for send different message to each friend according to his status (New Event/Update Event/Delete Event). [0] - User_ID, [1] - message.
    //private static String[] update_section;//Use for determined what section have been updated (details/users/tasks).
   // private static int details_num = 0;
    // static int users_num = 1;
   // private static int tasks_num = 2;
   // private static int vote_date_num = 3;
    //private static int vote_location_num = 4;


    //----------------------------------------------Server SQL && My SQL Functions----------------------------------------------

    public static String create_event(Context context) {
        //Generate Event_ID.
        String time = "" + System.currentTimeMillis();
        String Event_ID = Constants.MY_User_ID + time;


        Event_Helper.details[Table_Events.Event_ID_num] = Event_ID;
        // Date time = Calendar.getInstance().getTime();
        Event_Helper.details[Table_Events.Update_Time_num] = time;
        //Create event in app sql + server sql.
        Create_Event_MySQL();
        Create_Event_ServerSQL(context);
        //Send message to all users_ to create the event.
        //String message = Constants.New_Event + "|" + Event_ID;
        //Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);

        return Event_ID;
    }

    public static void update_event(Context context, String Event_ID) {
        //Update event in app sql + server sql.
        Update_Event_MySQL(Event_ID);
        Update_Event_ServerSQL(context, Event_ID);
        //Send message to all users to new/update/delete the event.
        /*
        String message;
        for (String[] update_user : Update_Users) {
            if (update_user[1].equals(Constants.Update_Event)) {
                message = update_user[1] + "|" + Event_ID + "^" + update_section[details_num] + "^" + update_section[users_num] + "^" + update_section[tasks_num]
                        + "^" + update_section[vote_date_num] + "^" + update_section[vote_location_num];
            } else {
                message = update_user[1] + "|" + Event_ID;
            }
            new SendMessage_AsyncTask(context).execute(Constants.MY_User_ID, message, update_user[0]);
        }
        */
        //Update Event_Helper.
        Event_Helper.details = Event_Helper.details_tmp;
        Event_Helper.friends = Event_Helper.friends_tmp;
        Event_Helper.task = Event_Helper.task_tmp;
        Event_Helper.vote_date = Event_Helper.vote_date_tmp;
        Event_Helper.vote_location = Event_Helper.vote_location_tmp;
    }

    public static void delete_event(Context context, String Event_ID) {
        //Delete event in app sql + server sql.
        Delete_Event_MySQL(Event_ID);
        Delete_Event_ServerSQL(context, Event_ID);
        //Send message to all users to delete the event.
        String message = Constants.Delete_Event + "|" + Event_ID;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void leave_event(Context context, String Event_ID, String User_ID) {
        //Delete event in app sql.
        Delete_Event_MySQL(Event_ID);
        //Delete user from server sql.
        new EventUser_AsyncTask_delete(context).execute(Event_ID, User_ID);
        //Send message to all users to delete the event.
        String message = Constants.Delete_User + "|" + Event_ID + "^" + User_ID;
        //Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void update_attending(Context context, String Event_ID, String User_ID, String attending) {
        update_attending_MySQL(Event_ID, User_ID, attending);
        update_attending_ServerSQL(context, Event_ID, User_ID, attending);
        //Send message to all users.
        String message = Constants.Update_User_Attending + "|" + Event_ID + "^" + Constants.MY_User_ID + "^" + attending;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void set_task_user_ID(Context context, String Event_ID, int task_id, String user_id) {
        set_task_user_ID_MySQL(Event_ID, task_id, user_id);
        set_task_user_ID_ServerSQL(context, Event_ID, task_id, user_id);
        String message = Constants.Update_Task_User_ID + "|" + Event_ID + "^" + task_id + "^" + user_id;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void delete_chat_message(Context context, String Chat_ID, String Message_ID, String User_ID) {
        //Remove from MySql
        sqlHelper.delete(Chat_ID, new String[]{Table_Chat.Message_ID, Table_Chat.User_ID},
                new String[]{Message_ID, User_ID}, new int[]{1});
        //Remove from server.
        new Chat_AsyncTask_delete(context).execute(Chat_ID, Message_ID, User_ID);
        //Send message to all friends.
        String message = Constants.Delete_Chat_Message + "|" + Chat_ID + "^" + Message_ID + "^" + User_ID;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_Helper.details[Table_Events.Event_ID_num], message);
    }

    public static void update_Event_details_field(Context context, String Event_ID, String field_name, String update) {
        update_Event_details_field_MySQL(Event_ID, field_name, update);
        update_Event_details_field_ServerSQL(context, Event_ID, field_name, update);
        //Send message to all users.
        String message = Constants.Update_Event_Details_Filed + "|" + Event_ID + "^" + field_name + "^" + update;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void add_vote_date_User_ID(Context context, int Vote_ID, String User_ID) {
        String Event_ID = Event_Helper.details[Table_Events.Event_ID_num];
        add_vote_date_User_ID_MySQL(Event_ID, Vote_ID, User_ID);
        add_vote_date_User_ID_ServerSQL(context, Event_ID, Vote_ID, User_ID);
        //Send message to all users.
        String message = Constants.Insert_Vote_Date + "|" + Event_ID + "^" + Vote_ID + "^" + User_ID;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void delete_vote_date_User_ID(Context context, int Vote_ID, String User_ID) {
        String Event_ID = Event_Helper.details[Table_Events.Event_ID_num];
        delete_vote_date_User_ID_MySQL(Event_ID, Vote_ID, User_ID);
        delete_vote_date_User_ID_ServerSQL(context, Event_ID, Vote_ID, User_ID);
        //Send message to all users.
        String message = Constants.Delete_Vote_Date + "|" + Event_ID + "^" + Vote_ID + "^" + User_ID;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void add_vote_location_User_ID(Context context, int Vote_ID, String User_ID) {
        String Event_ID = Event_Helper.details[Table_Events.Event_ID_num];
        add_vote_location_User_ID_MySQL(Event_ID, Vote_ID, User_ID);
        add_vote_location_User_ID_ServerSQL(context, Event_ID, Vote_ID, User_ID);
        //Send message to all users.
        String message = Constants.Insert_Vote_Location + "|" + Event_ID + "^" + Vote_ID + "^" + User_ID;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void delete_vote_location_User_ID(Context context, int Vote_ID, String User_ID) {
        String Event_ID = Event_Helper.details[Table_Events.Event_ID_num];
        delete_vote_location_User_ID_MySQL(Event_ID, Vote_ID, User_ID);
        delete_vote_location_User_ID_ServerSQL(context, Event_ID, Vote_ID, User_ID);
        //Send message to all users.
        String message = Constants.Delete_Vote_Location + "|" + Event_ID + "^" + Vote_ID + "^" + User_ID;
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_ID, message);
    }

    public static void simple_update(Context context, String action, String[] values) {
        //update my SQL.
        String Chat_Table_Name = null;
        switch (action) {
            //delete event: 0 - event_id. leave event: 0 - event_id, 1 - user_id.
            case Constants.Delete_Event:
            case Constants.Leave_Event: {
                Chat_Table_Name = Table_Chat.Table_Name + Event_Helper.details[Table_Events.Event_ID_num];
                //Delete event.
                sqlHelper.delete(Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{values[0]}, new int[]{1});
                //Delete event_user.
                sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{values[0]}, null);
                //Delete tasks.
                sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{values[0]}, null);
                //Delete chat.
                sqlHelper.Delete_Table(Chat_Table_Name);
                //Delete vote_date.
                sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{values[0]}, null);
                //Delete vote_location.
                sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{values[0]}, null);
                if (action.equals(Constants.Leave_Event))
                    Chat_Table_Name = null;
                break;
            }
            case Constants.New_Chat_Message: {
                Chat_Table_Name = Table_Chat.Table_Name + Event_Helper.details[Table_Events.Event_ID_num];
                sqlHelper.insert(Chat_Table_Name, values);
                break;
            }
            case Constants.Delete_Chat_Message: {
                Chat_Table_Name = Table_Chat.Table_Name + Event_Helper.details[Table_Events.Event_ID_num];
                sqlHelper.delete(Chat_Table_Name, new String[]{Table_Chat.Message_ID, Table_Chat.User_ID}, new String[]{values[Table_Chat.Message_ID_num],
                        Constants.MY_User_ID}, new int[]{1});
                break;
            }
            case Constants.Take_Task: {
                sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{Constants.MY_User_ID},
                        new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                        new String[]{values[Table_Tasks.Event_ID_num], values[Table_Tasks.Task_ID_Number_num], values[Table_Tasks.subTask_ID_Number_num]});
                break;
            }
            case Constants.UnTake_Task: {
                sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{Constants.UnCheck},
                        new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                        new String[]{values[Table_Tasks.Event_ID_num], values[Table_Tasks.Task_ID_Number_num], values[Table_Tasks.subTask_ID_Number_num]});
                break;
            }
            case Constants.Vote_For_Date: {
                if (sqlHelper.select(null, Table_Vote_Date.Table_Name, Table_Vote_Date.getAllFields(), values, new int[]{1})[0].isEmpty())
                    sqlHelper.insert(Table_Vote_Date.Table_Name, values);
                break;
            }
            case Constants.UnVote_For_Date: {
                if (!sqlHelper.select(null, Table_Vote_Date.Table_Name, Table_Vote_Date.getAllFields(), values, new int[]{1})[0].isEmpty())
                    sqlHelper.delete(Table_Vote_Date.Table_Name, Table_Vote_Date.getAllFields(), values, new int[]{1});
                break;
            }
            case Constants.Vote_For_Location: {
                if (sqlHelper.select(null, Table_Vote_Location.Table_Name, Table_Vote_Location.getAllFields(), values, new int[]{1})[0].isEmpty())
                    sqlHelper.insert(Table_Vote_Location.Table_Name, values);
                break;
            }
            case Constants.UnVote_For_Location: {
                if (!sqlHelper.select(null, Table_Vote_Location.Table_Name, Table_Vote_Location.getAllFields(), values, new int[]{1})[0].isEmpty())
                    sqlHelper.delete(Table_Vote_Location.Table_Name, Table_Vote_Location.getAllFields(), values, new int[]{1});
                break;
            }
            case Constants.Update_Attending: {
                sqlHelper.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Attending}, new String[]{values[Table_Events_Users.Attending_num]},
                        new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
                        new String[]{values[Table_Events_Users.Event_ID_num], values[Table_Events_Users.User_ID_num]});
                break;
            }
        }
        //update server and useres
        //get all user id's of the event.
        ArrayList<String> user_ids = new ArrayList<>();
        for (String user_id : Event_Helper.friends.keySet())
            if (!user_id.equals(Constants.MY_User_ID)) {
                user_ids.add(user_id);
            }
        SimpleUpdate simpleUpdate = new SimpleUpdate();
        simpleUpdate.setAction(action);
        simpleUpdate.setChatTableName(Chat_Table_Name);
        simpleUpdate.setValues(new ArrayList<>(Arrays.asList(values)));
        simpleUpdate.setUsersID(user_ids);
        new SimpleUpdate_AsyncTask_update(context).execute(simpleUpdate);


    }

    //----------------------------------------------My SQL Functions------------------------------------------------
    private static void Create_Event_MySQL() {
        //create event.
        sqlHelper.insert(Table_Events.Table_Name, Event_Helper.details);
        //Add My user.
        sqlHelper.insert(Table_Events_Users.Table_Name, new String[]{Event_Helper.details[Table_Events.Event_ID_num], Constants.MY_User_ID, Constants.Yes, Constants.Owner});
        //Add all friends.
        for (String User_ID : Event_Helper.friends.keySet()) {
            Friend_Helper friend_helper = Event_Helper.friends.get(User_ID);
            add_newFriend_MySQL(friend_helper, User_ID);
        }
        //Add tasks.
        for (int task_id : Event_Helper.task.keySet()) {
            Task_Helper task_helper = Event_Helper.task.get(task_id);
            add_newTask_MySQL(task_helper, task_id, 0);
            //Add all subTasks.
            for (int subTask_id : task_helper.getSubTasks().keySet()) {
                add_newTask_MySQL(task_helper, task_id, subTask_id);
            }
        }
        //Create Chat table.
        String Chat_ID = Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_Helper.details[Table_Events.Event_ID_num]);
        sqlHelper.Create_Table(Chat_ID, Table_Chat.getAllFields(), Table_Chat.getAllSqlParams());
        //Add vote_date.
        for (int vote_id : Event_Helper.vote_date.keySet()) {
            Vote_Date_Helper vote_date_helper = Event_Helper.vote_date.get(vote_id);
            add_newVote_Date_Option_MySQL(vote_date_helper, vote_id);
        }
        //Add vote_location.
        for (int vote_id : Event_Helper.vote_location.keySet()) {
            Vote_Location_Helper vote_location_helper = Event_Helper.vote_location.get(vote_id);
            add_newVote_Location_Option_MySQL(vote_location_helper, vote_id);
        }
    }

    private static void Update_Event_MySQL(String Event_ID) {
        //Update event (delete and then insert).
        sqlHelper.delete(Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{Event_ID}, new int[]{1});
        sqlHelper.insert(Table_Events.Table_Name, Event_Helper.details);
        //Update Friends list invention.
        Friend_Helper friend_helper;
        for (String User_ID : Event_Helper.friends_tmp.keySet()) {
            friend_helper = Event_Helper.friends_tmp.get(User_ID);
            if (Event_Helper.friends.get(User_ID) == null) {//Add new friends.
                add_newFriend_MySQL(friend_helper, User_ID);
            } else {//Change permission.
                if (!User_ID.equals(Constants.MY_User_ID))//No need to send update message to myself.
                if (!friend_helper.getPermission().equals(Event_Helper.friends.get(User_ID).getPermission())) {
                    sqlHelper.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Permission}, new String[]{friend_helper.getPermission()},
                            new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, new String[]{Event_ID, User_ID});
                }
            }
        }
        //Delete friends.
        for (String User_ID : Event_Helper.friends.keySet()) {
            if (Event_Helper.friends_tmp.get(User_ID) == null) {
                sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
                        new String[]{Event_Helper.details[Table_Events.Event_ID_num], User_ID}, new int[]{1});
            }
        }
        //Update Tasks.
        boolean new_task, new_subTask;
        Task_Helper task_helper;
        for (int task_id : Event_Helper.task_tmp.keySet()) {
            task_helper = Event_Helper.task_tmp.get(task_id);
            new_task = Event_Helper.task.get(task_id) == null;
            if (new_task) {//Add new tasks.
                add_newTask_MySQL(task_helper, task_id, 0);
            } else {//Update task description.
                if (!task_helper.getDescription().equals(Event_Helper.task.get(task_id).getDescription())) {
                    sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.Description}, new String[]{task_helper.getDescription()},
                            new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number}, new String[]{Event_ID, task_id + "", 0 + ""});
                }
            }
            for (int subTask_id : task_helper.getSubTasks().keySet()) {
                new_subTask = (new_task || Event_Helper.task.get(task_id).getSubTasks().get(subTask_id) == null);
                if (new_subTask) {//Add new subTasks.
                    add_newTask_MySQL(task_helper, task_id, subTask_id);
                } else {//Update subTask description.
                    if (!task_helper.getSubTasks().get(subTask_id)[0].equals(Event_Helper.task.get(task_id).getSubTasks().get(subTask_id)[0])) {
                        sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.Description}, new String[]{task_helper.getSubTasks().get(subTask_id)[0]},
                                new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number}, new String[]{Event_ID, task_id + "", subTask_id + ""});
                    }
                }
            }
        }
        //Delete Tasks.
        for (int task_id : Event_Helper.task.keySet()) {
            task_helper = Event_Helper.task.get(task_id);
            if (Event_Helper.task_tmp.get(task_id) == null) {
                sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number},
                        new String[]{Event_Helper.details[Table_Events.Event_ID_num], task_id + ""}, null);
            } else {
                for (int subTask_id : task_helper.getSubTasks().keySet()) {
                    if (Event_Helper.task_tmp.get(task_id).getSubTasks().get(subTask_id) == null) {
                        sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                                new String[]{Event_Helper.details[Table_Events.Event_ID_num], task_id + "", subTask_id + ""}, null);
                    }
                }
            }
        }
        //Update Vote_date.
        //Update vote will reset the votes users.
        boolean new_vote_date;
        Vote_Date_Helper vote_date_helper_tmp;
        Vote_Date_Helper vote_date_helper;
        for (int vote_id : Event_Helper.vote_date_tmp.keySet()) {
            vote_date_helper_tmp = Event_Helper.vote_date_tmp.get(vote_id);
            vote_date_helper = Event_Helper.vote_date.get(vote_id);
            new_vote_date = vote_date_helper == null;
            if (new_vote_date) {//Add new vote.
                add_newVote_Date_Option_MySQL(vote_date_helper_tmp, vote_id);
            } else {//Update vote date.
                if (!vote_date_helper_tmp.getStart_Date().equals(vote_date_helper.getStart_Date()) || !vote_date_helper_tmp.getEnd_Date().equals(vote_date_helper.getEnd_Date())
                        || !vote_date_helper_tmp.getAll_Day().equals(vote_date_helper.getAll_Day()) || !vote_date_helper_tmp.getStart_Time().equals(vote_date_helper.getStart_Time())
                        || !vote_date_helper_tmp.getEnd_Time().equals(vote_date_helper.getStart_Time())) {
                    //reset the votes users.
                    sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID}, new String[]{Event_ID, vote_id + ""}, null);
                    add_newVote_Date_Option_MySQL(vote_date_helper_tmp, vote_id);
                }
            }
        }
        //Delete Vote_date.
        for (int vote_id : Event_Helper.vote_date.keySet()) {
            vote_date_helper = Event_Helper.vote_date.get(vote_id);
            if (Event_Helper.vote_date_tmp.get(vote_id) == null) {
                sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID}, new String[]{Event_ID, vote_id + ""}, null);
            }
        }

        //Update Vote_location.
        //Update vote will reset the votes users.
        boolean new_vote_location;
        Vote_Location_Helper vote_location_helper_tmp;
        Vote_Location_Helper vote_location_helper;
        for (int vote_id : Event_Helper.vote_location_tmp.keySet()) {
            vote_location_helper_tmp = Event_Helper.vote_location_tmp.get(vote_id);
            vote_location_helper = Event_Helper.vote_location.get(vote_id);
            new_vote_location = vote_location_helper == null;
            if (new_vote_location) {//Add new vote.
                add_newVote_Location_Option_MySQL(vote_location_helper_tmp, vote_id);
            } else {//Update vote date.
                if (!vote_location_helper_tmp.getDescription().equals(vote_location_helper.getDescription())) {
                    //reset the votes users.
                    sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID}, new String[]{Event_ID, vote_id + ""}, null);
                    add_newVote_Location_Option_MySQL(vote_location_helper_tmp, vote_id);
                }
            }
        }
        //Delete Vote_location.
        for (int vote_id : Event_Helper.vote_location.keySet()) {
            vote_location_helper = Event_Helper.vote_location.get(vote_id);
            if (Event_Helper.vote_location_tmp.get(vote_id) == null) {
                sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID}, new String[]{Event_ID, vote_id + ""}, null);
            }
        }
    }

    public static void Delete_Event_MySQL(String Event_ID) {
        //Delete event.
        sqlHelper.delete(Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{Event_ID}, new int[]{1});
        //Delete event_user.
        sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{Event_ID}, null);
        //Delete tasks.
        sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{Event_ID}, null);
        //Delete chat.
        sqlHelper.Delete_Table(Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_ID));
        //Delete vote_date.
        sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{Event_ID}, null);
        //Delete vote_location.
        sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{Event_ID}, null);
    }

    private static void add_newFriend_MySQL(Friend_Helper friend_helper, String User_ID) {
        String[] event_user = new String[Table_Events_Users.Size()];
        event_user[Table_Events_Users.Event_ID_num] = Event_Helper.details[Table_Events.Event_ID_num];
        event_user[Table_Events_Users.User_ID_num] = User_ID;
        event_user[Table_Events_Users.Attending_num] = Constants.Did_Not_Replay;
        event_user[Table_Events_Users.Permission_num] = friend_helper.getPermission();
        sqlHelper.insert(Table_Events_Users.Table_Name, event_user);
    }

    private static void add_newTask_MySQL(Task_Helper task_helper, int task_id, int subTask_id) {
        String[] task = new String[Table_Tasks.Size()];
        task[Table_Tasks.Event_ID_num] = Event_Helper.details[Table_Events.Event_ID_num];
        task[Table_Tasks.Task_ID_Number_num] = task_id + "";
        task[Table_Tasks.subTask_ID_Number_num] = subTask_id + "";
        task[Table_Tasks.Task_Type_num] = task_helper.getType();
        if (subTask_id == 0) {
            task[Table_Tasks.Description_num] = task_helper.getDescription();
        } else {
            task[Table_Tasks.Description_num] = task_helper.getSubTasks().get(subTask_id)[0];
        }
        task[Table_Tasks.User_ID_num] = task_helper.getUser_ID();
        task[Table_Tasks.Mark_num] = Constants.No;
        sqlHelper.insert(Table_Tasks.Table_Name, task);
    }

    private static void add_newVote_Date_Option_MySQL(Vote_Date_Helper vote_date_helper, int vote_id) {
        String[] vote_date = new String[Table_Vote_Date.Size()];
        vote_date[Table_Vote_Date.Event_ID_num] = Event_Helper.details[Table_Events.Event_ID_num];
        vote_date[Table_Vote_Date.Vote_ID_num] = vote_id + "";
        vote_date[Table_Vote_Date.Start_Date_num] = vote_date_helper.getStart_Date();
        vote_date[Table_Vote_Date.End_Date_num] = vote_date_helper.getEnd_Date();
        vote_date[Table_Vote_Date.All_Day_Time_num] = vote_date_helper.getAll_Day();
        vote_date[Table_Vote_Date.Start_Time_num] = vote_date_helper.getStart_Time();
        vote_date[Table_Vote_Date.End_Time_num] = vote_date_helper.getEnd_Time();
        vote_date[Table_Vote_Date.User_ID_num] = Constants.UnCheck;
        sqlHelper.insert(Table_Vote_Date.Table_Name, vote_date);
    }

    public static void add_vote_date_User_ID_MySQL(String event_id, int vote_id, String user_id) {
        String[] vote_date = new String[Table_Vote_Date.Size()];
        vote_date[Table_Vote_Date.Event_ID_num] = event_id;
        vote_date[Table_Vote_Date.Vote_ID_num] = vote_id + "";
        vote_date[Table_Vote_Date.Start_Date_num] = "";
        vote_date[Table_Vote_Date.End_Date_num] = "";
        vote_date[Table_Vote_Date.All_Day_Time_num] = "";
        vote_date[Table_Vote_Date.Start_Time_num] = "";
        vote_date[Table_Vote_Date.End_Time_num] = "";
        vote_date[Table_Vote_Date.User_ID_num] = user_id;
        sqlHelper.insert(Table_Vote_Date.Table_Name, vote_date);
    }

    private static void add_newVote_Location_Option_MySQL(Vote_Location_Helper vote_location_helper, int vote_id) {
        String[] vote_location = new String[Table_Vote_Location.Size()];
        vote_location[Table_Vote_Location.Event_ID_num] = Event_Helper.details[Table_Events.Event_ID_num];
        vote_location[Table_Vote_Location.Vote_ID_num] = vote_id + "";
        vote_location[Table_Vote_Location.Description_num] = vote_location_helper.getDescription();
        vote_location[Table_Vote_Location.User_ID_num] = Constants.UnCheck;
        sqlHelper.insert(Table_Vote_Location.Table_Name, vote_location);
    }

    public static void add_vote_location_User_ID_MySQL(String event_id, int vote_id, String user_id) {
        String[] vote_location = new String[Table_Vote_Location.Size()];
        vote_location[Table_Vote_Location.Event_ID_num] = event_id;
        vote_location[Table_Vote_Location.Vote_ID_num] = vote_id + "";
        vote_location[Table_Vote_Location.Description_num] = "";
        vote_location[Table_Vote_Location.User_ID_num] = user_id;
        sqlHelper.insert(Table_Vote_Location.Table_Name, vote_location);
    }

    public static void mark_task_and_subTasks_MySQL(String Event_ID, int task_id, String mark) {
        sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.Mark}, new String[]{mark},
                new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number}, new String[]{Event_ID, task_id + ""});
    }

    public static void mark_one_task_MySQL(String Event_ID, int task_id, int subTask_id, String mark) {
        sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.Mark}, new String[]{mark},
                new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number}, new String[]{Event_ID, task_id + "", subTask_id + ""});
    }

    public static void update_attending_MySQL(String Event_ID, String User_ID, String attending) {
        sqlHelper.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Attending}, new String[]{attending},
                new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, new String[]{Event_ID, User_ID});
    }

    public static void set_task_user_ID_MySQL(String Event_ID, int task_id, String user_id) {
        sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{user_id},
                new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number}, new String[]{Event_ID, task_id + "", 0 + ""});
    }

    public static void User_Insert_MySQL(String User_ID) {
        ArrayList<String>[] dbUsers = sqlHelper.select(null, Table_Users.Table_Name, new String[]{Table_Users.User_ID}, new String[]{User_ID}, new int[]{1});
        if (dbUsers[0].isEmpty()) {
            //Add user to my SQL + Add User to Contacts_List(inside the function).
            new User_AsyncTask_get().execute(User_ID);
        }
    }

    public static void Update_Event_details_MySQL(String[] details) {
        sqlHelper.update(Table_Events.Table_Name, Table_Events.getAllFields(), details,
                new String[]{Table_Events.Event_ID}, new String[]{details[Table_Events.Event_ID_num]});
    }

    public static void update_Event_details_field_MySQL(String Event_ID, String field_name, String update) {
        sqlHelper.update(Table_Events.Table_Name, new String[]{field_name}, new String[]{update},
                new String[]{Table_Events.Event_ID}, new String[]{Event_ID});
    }

    public static void delete_vote_date_User_ID_MySQL(String Event_ID, int Vote_ID, String User_ID) {
        sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID},
                new String[]{Event_ID, Vote_ID + "", User_ID}, new int[]{1});
    }

    public static void delete_vote_location_User_ID_MySQL(String Event_ID, int Vote_ID, String User_ID) {
        sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID, Table_Vote_Location.User_ID},
                new String[]{Event_ID, Vote_ID + "", User_ID}, new int[]{1});
    }

    public static Event Event_From_Helper() {
        Event event = new Event();
        // Set id
        event.setId(Event_Helper.details[Table_Events.Event_ID_num]);

        //Set details
        ArrayList<String> details = new ArrayList<>();
        details.add(Event_Helper.details[Table_Events.Name_num]);
        details.add(Event_Helper.details[Table_Events.Location_num]);
        details.add(Event_Helper.details[Table_Events.Vote_Location_num]);
        details.add(Event_Helper.details[Table_Events.Start_Date_num]);
        details.add(Event_Helper.details[Table_Events.End_Date_num]);
        details.add(Event_Helper.details[Table_Events.All_Day_Time_num]);
        details.add(Event_Helper.details[Table_Events.Start_Time_num]);
        details.add(Event_Helper.details[Table_Events.End_Time_num]);
        details.add(Event_Helper.details[Table_Events.Vote_Time_num]);
        details.add(Event_Helper.details[Table_Events.Description_num]);
        details.add(Event_Helper.details[Table_Events.Image_Path_num]);
        details.add(Event_Helper.details[Table_Events.Update_Time_num]);
        event.setDetails(details);

        //Set Event users
        ArrayList<List<String>> event_users = new ArrayList<>();

        for (String User_ID : Event_Helper.friends.keySet()) {
            Friend_Helper friend_helper = Event_Helper.friends.get(User_ID);
            List<String> list = new ArrayList<>();
            list.add(User_ID);
            list.add(friend_helper.getAttending());
            list.add(friend_helper.getPermission());
            event_users.add(list);
        }
        event.setEventUsers(event_users);

        // Set Tasks

        ArrayList<List<String>> tasks = new ArrayList<>();

        for (int task_id : Event_Helper.task.keySet()) {
            Task_Helper task_helper = Event_Helper.task.get(task_id);

            List<String> list = new ArrayList<>();
            list.add(task_id + "");
            list.add("0");
            list.add(task_helper.getType());
            list.add(task_helper.getDescription());
            list.add(task_helper.getUser_ID());
            tasks.add(list);

            //Add all subTasks.
            for (int subTask_id : task_helper.getSubTasks().keySet()) {
                list = new ArrayList<>();
                list.add(task_id + "");
                list.add(subTask_id + "");
                list.add(task_helper.getType());
                list.add(task_helper.getSubTasks().get(subTask_id)[0]);
                list.add(task_helper.getUser_ID());
                tasks.add(list);
            }
        }
        event.setTasks(tasks);

        // set vote location

        ArrayList<List<String>> vote_location = new ArrayList<>();

        for (int vote_id : Event_Helper.vote_location.keySet()) {
            Vote_Location_Helper vote_location_helper = Event_Helper.vote_location.get(vote_id);
            List<String> list = new ArrayList<>();
            list.add(vote_id + "");
            list.add(vote_location_helper.getDescription());
            list.add(Constants.UnCheck);
            vote_location.add(list);
        }
        event.setVoteLocations(vote_location);

        // set vote date
        ArrayList<List<String>> vote_date = new ArrayList<>();

        for (int vote_id : Event_Helper.vote_date.keySet()) {
            Vote_Date_Helper vote_date_helper = Event_Helper.vote_date.get(vote_id);
            List<String> list = new ArrayList<>();
            list.add(vote_id + "");
            list.add(vote_date_helper.getStart_Date());
            list.add(vote_date_helper.getEnd_Date());
            list.add(vote_date_helper.getAll_Day());
            list.add(vote_date_helper.getStart_Time());
            list.add(vote_date_helper.getEnd_Time());
            list.add(Constants.UnCheck);
            vote_date.add(list);
        }
        event.setVoteDates(vote_date);

        return event;
    }

    //----------------------------------------------Server SQL Functions----------------------------------------------
    public static void Create_Event_ServerSQL(Context context) {
        //new Event_AsyncTask_create_new_event(context).execute(Event_ID, Name, Location, Start_Date, Start_Time, End_Date, End_Time, Description, ImagePath, Update_Time);

        Event event = Event_From_Helper();
        //TODO add the user in the create process
        addUserIfNotExist(event);
        new Event_AsyncTask_create_new_event(context).execute(event);
        /*
        //create event.
        new Event_AsyncTask_create_new_event(context).execute(Event_Helper.details);
        //Add My user.
        new EventUser_AsyncTask_insert(context).execute(Event_Helper.details[Table_Events.Event_ID_num], Constants.MY_User_ID, Constants.Yes, Constants.Owner);
        //Add all friends.
        for (String User_ID : Event_Helper.friends.keySet()) {
            Friend_Helper friend_helper = Event_Helper.friends.get(User_ID);
            new EventUser_AsyncTask_insert(context).execute(Event_Helper.details[Table_Events.Event_ID_num], User_ID, friend_helper.getAttending(), friend_helper.getPermission());
        }
        //Add tasks
        for (int task_id : Event_Helper.task.keySet()) {
            Task_Helper task_helper = Event_Helper.task.get(task_id);
            new Task_AsyncTask_insert(context).execute(Event_Helper.details[Table_Events.Event_ID_num], task_id + "", 0 + "", task_helper.getType(),
                    task_helper.getDescription(), task_helper.getUser_ID());
            //Add all subTasks.
            for (int subTask_id : task_helper.getSubTasks().keySet()) {
                new Task_AsyncTask_insert(context).execute(Event_Helper.details[Table_Events.Event_ID_num], task_id + "", subTask_id + "", task_helper.getType(),
                        task_helper.getSubTasks().get(subTask_id)[0], task_helper.getUser_ID());
            }
        }
        //Create Chat table.
        String Chat_ID = Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_Helper.details[Table_Events.Event_ID_num]);
        new Chat_AsyncTask_CreateByEvent(context).execute(Chat_ID);
        //Add vote_dates.
        for (int vote_id : Event_Helper.vote_date.keySet()) {
            Vote_Date_Helper vote_date_helper = Event_Helper.vote_date.get(vote_id);
            new Vote_Date_AsyncTask_insert(context).execute(Event_Helper.details[Table_Events.Event_ID_num], vote_id + "", vote_date_helper.getStart_Date(), vote_date_helper.getEnd_Date(),
                    vote_date_helper.getAll_Day(), vote_date_helper.getStart_Time(), vote_date_helper.getEnd_Time(), Constants.UnCheck);
        }
        //Add vote_dates.
        for (int vote_id : Event_Helper.vote_location.keySet()) {
            Vote_Location_Helper vote_location_helper = Event_Helper.vote_location.get(vote_id);
            new Vote_Location_AsyncTask_insert(context).execute(Event_Helper.details[Table_Events.Event_ID_num], vote_id + "", vote_location_helper.getDescription(), Constants.UnCheck);
        }
        */
    }

    private static void addUserIfNotExist(Event event) {
        boolean exist = false;
        for (int i = 0; i < event.getEventUsers().size() && !false; i++) {
            for (int j = 0; j < event.getEventUsers().get(i).size() && !false; j++) {
                if (event.getEventUsers().get(i).get(j).equals(Constants.MY_User_ID))
                    exist = true;
            }
        }
        if (!exist) {
            List<String> list = new ArrayList<>();
            list.add(Constants.MY_User_ID);
            list.add(Constants.Yes);
            list.add(Constants.Owner);
            event.getEventUsers().add(list);
        }
    }

    private static void Update_Event_ServerSQL(Context context, String Event_ID) {
        //Create Update Event object.
        UpdateEvent updateEvent = new UpdateEvent();
        updateEvent.setId(Event_ID);
        //Update event details.
        updateEvent.setDetailsChanged(Constants.False);
        updateEvent.setDetails(new ArrayList<String>());
        //Check if details changed.
        for (int i = 0; i < Event_Helper.details.length; i++) {
            if (!Event_Helper.details[i].equals(Event_Helper.details_tmp[i])) {
                updateEvent.setDetailsChanged(Constants.True);
                ArrayList<String> details = new ArrayList<>(Arrays.asList(Event_Helper.details_tmp));
                details.remove(0);//remove event_id.
                updateEvent.setDetails(details);
                break;
            }
        }
        //Update Friends list invention.
        updateEvent.setEventUsers(new ArrayList<List<List<String>>>());
        for (int i = 0; i < 4; i++)//create 4 array list. one for each update type.
            updateEvent.getEventUsers().add(new ArrayList<List<String>>());
        Friend_Helper friend_helper;
        String[] friend = new String[Table_Events_Users.Size() - Constants.index_object_sql_diff];
        for (String User_ID : Event_Helper.friends_tmp.keySet()) {
            friend_helper = Event_Helper.friends_tmp.get(User_ID);
            friend[Table_Events_Users.User_ID_num - Constants.index_object_sql_diff] = User_ID;
            friend[Table_Events_Users.Attending_num - Constants.index_object_sql_diff] = friend_helper.getAttending();
            friend[Table_Events_Users.Permission_num - Constants.index_object_sql_diff] = friend_helper.getPermission();
            if (Event_Helper.friends.get(User_ID) == null) {//Add new friends.
                updateEvent.getEventUsers().get(Constants.update_event_new).add(Arrays.asList(friend));
            } else {//Change permission.
                if (!friend_helper.getPermission().equals(Event_Helper.friends.get(User_ID).getPermission())) {
                    updateEvent.getEventUsers().get(Constants.update_event_update).add(Arrays.asList(friend));
                } else {//Unchanged
                    updateEvent.getEventUsers().get(Constants.update_event_not_change).add(Arrays.asList(friend));
                }
            }
        }
        //Delete friends.
        for (String User_ID : Event_Helper.friends.keySet()) {
            friend_helper = Event_Helper.friends.get(User_ID);
            friend[Table_Events_Users.User_ID_num - Constants.index_object_sql_diff] = User_ID;
            friend[Table_Events_Users.Attending_num - Constants.index_object_sql_diff] = "";
            friend[Table_Events_Users.Permission_num - Constants.index_object_sql_diff] = "";
            if (Event_Helper.friends_tmp.get(User_ID) == null) {
                updateEvent.getEventUsers().get(Constants.update_event_delete).add(Arrays.asList(friend));
            }
        }

        //Update Tasks.
        updateEvent.setTasks(new ArrayList<List<List<String>>>());
        for (int i = 0; i < 4; i++)//create 4 array list. one for each update type.
            updateEvent.getTasks().add(new ArrayList<List<String>>());
        boolean new_task, new_subTask;
        Task_Helper task_helper;
        String[] task = new String[Table_Tasks.Size() - (Constants.index_object_sql_diff + 1)];//no event_id and no mark filed.
        for (int task_id : Event_Helper.task_tmp.keySet()) {
            task_helper = Event_Helper.task_tmp.get(task_id);
            task[Table_Tasks.Task_ID_Number_num - Constants.index_object_sql_diff] = task_id + "";
            task[Table_Tasks.subTask_ID_Number_num - Constants.index_object_sql_diff] = 0 + "";
            task[Table_Tasks.Task_Type_num - Constants.index_object_sql_diff] = task_helper.getType();
            task[Table_Tasks.Description_num - Constants.index_object_sql_diff] = task_helper.getDescription();
            task[Table_Tasks.User_ID_num - Constants.index_object_sql_diff] = task_helper.getUser_ID();
            new_task = Event_Helper.task.get(task_id) == null;
            if (new_task) {//Add new tasks.
                updateEvent.getTasks().get(Constants.update_event_new).add(Arrays.asList(task));
            } else {//Update task description.
                if (!task_helper.getDescription().equals(Event_Helper.task.get(task_id).getDescription())) {
                    updateEvent.getTasks().get(Constants.update_event_update).add(Arrays.asList(task));
                } else {
                    updateEvent.getTasks().get(Constants.update_event_not_change).add(Arrays.asList(task));
                }
                for (int subTask_id : task_helper.getSubTasks().keySet()) {
                    task[Table_Tasks.subTask_ID_Number_num - Constants.index_object_sql_diff] = subTask_id + "";
                    task[Table_Tasks.Description_num - Constants.index_object_sql_diff] = task_helper.getSubTasks().get(subTask_id)[0];
                    new_subTask = (new_task || Event_Helper.task.get(task_id).getSubTasks().get(subTask_id) == null);
                    if (new_subTask) {//Add new subTasks.
                        updateEvent.getTasks().get(Constants.update_event_new).add(Arrays.asList(task));
                    } else {//Update subTask description.
                        if (!task_helper.getSubTasks().get(subTask_id)[0].equals(Event_Helper.task.get(task_id).getSubTasks().get(subTask_id)[0])) {
                            updateEvent.getTasks().get(Constants.update_event_update).add(Arrays.asList(task));
                        } else {
                            updateEvent.getTasks().get(Constants.update_event_not_change).add(Arrays.asList(task));
                        }
                    }
                }
            }
        }
        //Delete Tasks.
        for (int task_id : Event_Helper.task.keySet()) {
            task_helper = Event_Helper.task.get(task_id);
            task[Table_Tasks.Task_ID_Number_num - Constants.index_object_sql_diff] = task_id + "";
            task[Table_Tasks.subTask_ID_Number_num - Constants.index_object_sql_diff] = 0 + "";
            task[Table_Tasks.Task_Type_num - Constants.index_object_sql_diff] = "";
            task[Table_Tasks.Description_num - Constants.index_object_sql_diff] = "";
            task[Table_Tasks.User_ID_num - Constants.index_object_sql_diff] = "";
            if (Event_Helper.task_tmp.get(task_id) == null) {
                updateEvent.getTasks().get(Constants.update_event_delete).add(Arrays.asList(task));
            } else {
                for (int subTask_id : task_helper.getSubTasks().keySet()) {
                    task[Table_Tasks.subTask_ID_Number_num] = subTask_id + "";
                    if (Event_Helper.task_tmp.get(task_id).getSubTasks().get(subTask_id) == null) {
                        updateEvent.getTasks().get(Constants.update_event_delete).add(Arrays.asList(task));
                    }
                }
            }
        }

        //Update Vote_date.
        //Update vote will reset the votes users.
        boolean new_vote_date;
        Vote_Date_Helper vote_date_helper_tmp;
        Vote_Date_Helper vote_date_helper;
        String[] vote_date = new String[Table_Vote_Date.Size() - Constants.index_object_sql_diff];//no event_id.
        for (int vote_id : Event_Helper.vote_date_tmp.keySet()) {
            vote_date_helper_tmp = Event_Helper.vote_date_tmp.get(vote_id);
            vote_date_helper = Event_Helper.vote_date.get(vote_id);
            new_vote_date = vote_date_helper == null;
            vote_date[Table_Vote_Date.Vote_ID_num - Constants.index_object_sql_diff] = vote_id + "";
            vote_date[Table_Vote_Date.Start_Date_num - Constants.index_object_sql_diff] = vote_date_helper_tmp.getStart_Date();
            vote_date[Table_Vote_Date.End_Date_num - Constants.index_object_sql_diff] = vote_date_helper_tmp.getEnd_Date();
            vote_date[Table_Vote_Date.All_Day_Time_num - Constants.index_object_sql_diff] = vote_date_helper_tmp.getAll_Day();
            vote_date[Table_Vote_Date.Start_Time_num - Constants.index_object_sql_diff] = vote_date_helper_tmp.getStart_Time();
            vote_date[Table_Vote_Date.End_Time_num - Constants.index_object_sql_diff] = vote_date_helper_tmp.getEnd_Time();
            vote_date[Table_Vote_Date.User_ID_num - Constants.index_object_sql_diff] = Constants.UnCheck;
            if (new_vote_date) {//Add new vote.
                updateEvent.getVoteDates().get(Constants.update_event_new).add(Arrays.asList(vote_date));
            } else {//Update vote date.
                if (!vote_date_helper_tmp.getStart_Date().equals(vote_date_helper.getStart_Date()) || !vote_date_helper_tmp.getEnd_Date().equals(vote_date_helper.getEnd_Date())
                        || !vote_date_helper_tmp.getAll_Day().equals(vote_date_helper.getAll_Day()) || !vote_date_helper_tmp.getStart_Time().equals(vote_date_helper.getStart_Time())
                        || !vote_date_helper_tmp.getEnd_Time().equals(vote_date_helper.getStart_Time())) {
                    //reset the votes users.
                    updateEvent.getVoteDates().get(Constants.update_event_update).add(Arrays.asList(vote_date));
                } else {
                    updateEvent.getVoteDates().get(Constants.update_event_not_change).add(Arrays.asList(vote_date));
                }
            }
        }
        //Delete Vote_date.
        for (int vote_id : Event_Helper.vote_date.keySet()) {
            vote_date[Table_Vote_Date.Vote_ID_num - Constants.index_object_sql_diff] = vote_id + "";
            vote_date[Table_Vote_Date.Start_Date_num - Constants.index_object_sql_diff] = "";
            vote_date[Table_Vote_Date.End_Date_num - Constants.index_object_sql_diff] = "";
            vote_date[Table_Vote_Date.All_Day_Time_num - Constants.index_object_sql_diff] = "";
            vote_date[Table_Vote_Date.Start_Time_num - Constants.index_object_sql_diff] = "";
            vote_date[Table_Vote_Date.End_Time_num - Constants.index_object_sql_diff] = "";
            vote_date[Table_Vote_Date.User_ID_num - Constants.index_object_sql_diff] = "";
            if (Event_Helper.vote_date_tmp.get(vote_id) == null) {
                updateEvent.getVoteDates().get(Constants.update_event_delete).add(Arrays.asList(vote_date));
            }
        }

        //Update Vote_location.
        //Update vote will reset the votes users.
        boolean new_vote_location;
        Vote_Location_Helper vote_location_helper_tmp;
        Vote_Location_Helper vote_location_helper;
        String[] vote_location = new String[Table_Vote_Location.Size() - Constants.index_object_sql_diff];//no event_id.
        for (int vote_id : Event_Helper.vote_location_tmp.keySet()) {
            vote_location_helper_tmp = Event_Helper.vote_location_tmp.get(vote_id);
            vote_location_helper = Event_Helper.vote_location.get(vote_id);
            new_vote_location = vote_location_helper == null;
            vote_location[Table_Vote_Location.Event_ID_num] = Event_ID;
            vote_location[Table_Vote_Location.Vote_ID_num] = vote_id + "";
            vote_location[Table_Vote_Location.Description_num] = vote_location_helper_tmp.getDescription();
            vote_location[Table_Vote_Location.User_ID_num] = Constants.UnCheck;
            if (new_vote_location) {//Add new vote.
                updateEvent.getVoteLocations().get(Constants.update_event_new).add(Arrays.asList(vote_location));
            } else {//Update vote date.
                if (!vote_location_helper_tmp.getDescription().equals(vote_location_helper.getDescription())) {
                    //reset the votes users.
                    updateEvent.getVoteLocations().get(Constants.update_event_update).add(Arrays.asList(vote_location));
                }else {
                    updateEvent.getVoteLocations().get(Constants.update_event_not_change).add(Arrays.asList(vote_location));
                }
            }
        }
        //Delete Vote_location.
        for (int vote_id : Event_Helper.vote_location.keySet()) {
            vote_location[Table_Vote_Location.Event_ID_num] = Event_ID;
            vote_location[Table_Vote_Location.Vote_ID_num] = vote_id + "";
            vote_location[Table_Vote_Location.Description_num] = "";
            vote_location[Table_Vote_Location.User_ID_num] = "";
            if (Event_Helper.vote_location_tmp.get(vote_id) == null) {
                updateEvent.getVoteLocations().get(Constants.update_event_delete).add(Arrays.asList(vote_location));
            }
        }
        new UpdateEvent_AsyncTask_update_event(context).execute(updateEvent);
    }

    public static void Delete_Event_ServerSQL(Context context, String Event_ID) {
        //Delete event.
        new Event_AsyncTask_delete(context).execute(Event_ID);
        //Delete event_user.
        new EventUser_AsyncTask_delete_by_event(context).execute(Event_ID);
        //Delete tasks.
        new Task_AsyncTask_deleteByEvent(context).execute(Event_ID);
        //Delete chat.
        String Chat_ID = Table_Chat.Table_Name + Helper.Clean_Event_ID(Event_ID);
        new Chat_AsyncTask_deleteByEvent(context).execute(Chat_ID);
        //Delete vote_date.
        new Vote_Date_AsyncTask_deleteByEvent(context).execute(Event_ID);
        //Delete vote_location.
        new Vote_Location_AsyncTask_deleteByEvent(context).execute(Event_ID);
    }

    public static void update_attending_ServerSQL(Context context, String Event_ID, String User_ID, String attending) {
        new EventUser_AsyncTask_UpdateAttending(context).execute(Event_ID, User_ID, attending);
    }

    public static void set_task_user_ID_ServerSQL(Context context, String Event_ID, int task_id, String user_id) {
        new Task_AsyncTask_update_User_ID(context).execute(Event_ID, task_id + "", 0 + "", user_id);
    }

    public static void Send_Chat_Message_ServerSQL(Context context, String Chat_ID, String[] chat) {
        //Add chat message to server.
        String[] chat_server = new String[]{Chat_ID, chat[Table_Chat.Message_ID_num], chat[Table_Chat.User_ID_num], chat[Table_Chat.Message_num]
                , chat[Table_Chat.Date_num], chat[Table_Chat.Time_num]};
        new Chat_AsyncTask_insert(context).execute(chat_server);
        //Update all friends form the event.
        String message = Constants.New_Chat_Message + "|" + Chat_ID + "^" + chat[Table_Chat.Message_ID_num] + "^" + chat[Table_Chat.User_ID_num];
        Send_Message_To_All_My_Friend_By_Event_ServerSQL(context, Event_Helper.details[Table_Events.Event_ID_num], message);
    }

    public static void Send_Message_To_All_My_Friend_By_Event_ServerSQL(Context context, String Event_ID, String message) {
        for (String User_ID : Event_Helper.friends.keySet()) {
            if (!User_ID.equals(Constants.MY_User_ID)) {
                new SendMessage_AsyncTask(context).execute(Constants.MY_User_ID, message, User_ID);
            }
        }
    }

    private static void update_Event_details_field_ServerSQL(Context context, String Event_ID, String field_name, String update) {
        new Event_AsyncTask_update_field(context).execute(Event_ID, field_name, update);
    }

    private static void add_vote_date_User_ID_ServerSQL(Context context, String Event_ID, int Vote_ID, String User_ID) {
        new Vote_Date_AsyncTask_insert(context).execute(Event_ID, Vote_ID + "", "", "", "", "", "", User_ID);
    }

    private static void add_vote_location_User_ID_ServerSQL(Context context, String Event_ID, int Vote_ID, String User_ID) {
        new Vote_Location_AsyncTask_insert(context).execute(Event_ID, Vote_ID + "", User_ID);
    }

    private static void delete_vote_date_User_ID_ServerSQL(Context context, String Event_ID, int Vote_ID, String User_ID) {
        new Vote_Date_AsyncTask_delete_vote_user_id(context).execute(Event_ID, Vote_ID + "", User_ID);


    }

    private static void delete_vote_location_User_ID_ServerSQL(Context context, String Event_ID, int Vote_ID, String User_ID) {
        new Vote_Location_AsyncTask_delete_vote_user_id(context).execute(Event_ID, Vote_ID + "", User_ID);


    }


    //----------------------------------------------General Functions----------------------------------------------

    public static String Clean_Event_ID(String Event_ID) {
        String[] toReplace = new String[]{"_", " - ", "@", "\\."};
        String[] replaceWith = new String[]{"$", "_", "_", "_"};
        return replaceAll(Event_ID, toReplace, replaceWith);
    }

    private static String replaceAll(String str, String[] toReplace, String[] replaceWith) {
        for (int i = 0; i < toReplace.length; i++) {
            str = str.replaceAll(toReplace[i], replaceWith[i]);
        }
        return str;
    }

    public static String getMyPermission(String Event_ID) {
        ArrayList<String>[] dbResult = sqlHelper.select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
                new String[]{Event_ID, Constants.MY_User_ID}, null);
        if (dbResult[Table_Events_Users.Permission_num].size() == 0) return Constants.Owner;
        return dbResult[Table_Events_Users.Permission_num].get(0);
    }

    public static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHourOfDay();
        int minute = now.getMinuteOfHour();
        int second = now.getSecondOfMinute();
        String time = hour + ":" + minute + ":" + second;
        return time;
    }

    public static String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthOfYear();
        int day = now.getDayOfMonth();
        String date = day + "/" + month + "/" + year;
        return date;
    }

    public static String Convert_User_ID_To_Phone(String User_ID) {
        if (User_ID.substring(0, 3).equals("972")) {
            User_ID = 0 + User_ID.substring(3);
            User_ID = User_ID.substring(0, 3) + "-" + User_ID.substring(3);
        }
        return User_ID;
    }

    //Check id date1 is later than date2.
    public static boolean Is_date1_after_date2(String date1, String date2) {
        if (date1.equals("dd/mm/yyyy"))
            return false;
        String[] date1_array = date1.split("\\/");
        String[] date2_array = date2.split("\\/");
        for (int i = date1_array.length - 1; i >= 0; i--) {
            if (Integer.parseInt(date1_array[i]) > Integer.parseInt(date2_array[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean Is_time1_after_time2(String time1, String time2) {
        if (time1.equals("hh:mm"))
            return false;
        String[] time1_array = time1.split(":");
        String[] time2_array = time2.split(":");
        for (int i = 0; i < time1_array.length; i++) {
            if (Integer.parseInt(time1_array[i]) > Integer.parseInt(time2_array[i])) {
                return true;
            }
        }
        return false;
    }

    public static String date_text_view(String start_date, String end_date, String all_day, String start_time, String end_time) {
        if (start_date.equals("dd/mm/yyyy"))
            return "Date having been set yet";
        if (all_day.equals(Constants.Yes))
            if (start_date.equals(end_date))
                return format_date(start_date) + "\nthis is a all day event";
            else
                return format_date(start_date) + "-" + format_date(end_date);
        else if (start_time.equals("hh:mm"))
            return "Date having been set yet";
        else if (start_date.equals(end_date))
            return format_date(start_date) + ", " + format_time(start_time) + "-" + format_time(end_time);
        return format_date(start_date) + ", " + format_time(start_time) + "\n" + format_date(end_date) + ", " + format_time(end_time);
    }

    public static String date_text_view_vote(String start_date, String end_date) {
        if (start_date.equals(end_date))
            return format_date_short(start_date);
        return format_date_short(start_date) + "\n" + format_date_short(end_date);
    }

    public static String time_text_view_vote(String all_day, String start_time, String end_time) {
        if (all_day.equals(Constants.Yes))
            return "all day\nevent";
        return format_time(start_time) + "\n" + format_time(end_time);
    }

    public static String format_date(String date) {
        String[] a_date = date.split("\\/");
        if (a_date.length == 1) return "dd/mm/yyyy";
        String day = a_date[0];
        String month = a_date[1];
        String year = a_date[2];
        if (day.length() == 1)
            day = 0 + day;
        if (month.length() == 1)
            month = 0 + month;
        return day + "/" + month + "/" + year;
    }

    private static String format_date_short(String date) {
        String day = date.split("\\/")[0];
        String month = date.split("\\/")[1];
        String year = date.split("\\/")[2];
        if (day.length() == 1)
            day = 0 + day;
        if (month.length() == 1)
            month = 0 + month;
        year = year.substring(2);
        return day + "/" + month + "/" + year;
    }

    public static String format_time(String time) {
        String[] a_time = time.split(":");
        if (a_time.length == 1) return "hh:mm";
        String hour = a_time[0];
        String minute = a_time[1];
        if (hour.length() == 1)
            hour = 0 + hour;
        if (minute.length() == 1)
            minute = 0 + minute;
        return hour + ":" + minute;
    }


}
