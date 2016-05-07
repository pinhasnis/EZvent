package server.Messageing;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.Chat;
import rnp.backend.ezvent.model.ChatCollection;
import rnp.backend.ezvent.model.Event;
import rnp.ezvent.MainActivity;
import rnp.ezvent.R;
import server.CloudEndpointBuilderHelper;
import server.ServerAsyncResponse;
import utils.Constans.Constants;
import utils.Constans.Table_Chat;
import utils.Constans.Table_Events;
import utils.Constans.Table_Events_Users;
import utils.Constans.Table_Tasks;
import utils.Constans.Table_Vote_Date;
import utils.Constans.Table_Vote_Location;
import utils.Event_Helper_Package.Contacts_List;
import utils.Helper;
import utils.sqlHelper;


/**
 * Created by pinhas on 24/09/2015.
 */
public class GcmIntentService extends GcmListenerService {
    private static Ezvent myApiService = null;
    public static ServerAsyncResponse delegate = null;

    public GcmIntentService() {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        Logger.getLogger("GCM_RECEIVED").log(Level.INFO, data.toString());
        String action = data.getString(Constants.Message).substring(0, 1);
        String details = data.getString(Constants.Message).substring(1);
        switch (action) {
            case Constants.New_Event: {
                try {
                    Event event = getEvent(details);
                    addEventWithSafeSQL(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.Request_New_Event: {
                try {
                    Event event = myApiService.requestEvent(details).execute();
                    if (event != null) {
                        //Check if the event is alredy exist. If it exist delete and insert.
                        if (!sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID},
                                new String[]{event.getId()}, new int[]{1})[0].isEmpty()){
                            //Delete event.
                            sqlHelper.delete(Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{details}, new int[]{1});
                            //Delete event_user.
                            sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{details}, null);
                            //Delete tasks.
                            sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{details}, null);
                            //Delete vote_date.
                            sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{details}, null);
                            //Delete vote_location.
                            sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{details}, null);
                        }
                        addEventWithSafeSQL(event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.Update_Event:{
                hundleUpdate(details);
                break;
            }
            //simple update.
            case Constants.Delete_Event: {
                deleteEvent(details);
                break;
            }
            //0 - event_id, 1 - user_id that leave the event.
            case Constants.Leave_Event: {
                String[] leave = details.split("\\|");
                sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, leave, new int[]{1});
                sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.User_ID}, leave, null);
                sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.User_ID}, leave, null);
                break;
            }
            case Constants.New_Chat_Message: {
                String Chat_Table_Name = details.split("\\^")[0];
                String Message = details.split("\\^")[1];
                String[] Chat = Message.split("\\|");
                if (sqlHelper.select(null, Chat_Table_Name, new String[]{Table_Chat.Message_ID, Table_Chat.User_ID},
                        new String[]{Chat[Table_Chat.Message_ID_num], Chat[Table_Chat.User_ID_num]}, null)[0].isEmpty()) {
                    sqlHelper.insert(Chat_Table_Name, Chat);
                    String Event_ID = Chat_Table_Name.substring(Table_Chat.Table_Name.length());
                    ArrayList<String>[] event = sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{Event_ID}, new int[]{1});
                    String event_name = event[Table_Events.Name_num].get(0);
                    if (event_name.length() == 0) event_name = "Event Name";
                    String sender = Contacts_List.contacts.get(Chat[Table_Chat.User_ID_num]);
                    addNotification("New Message - " + event_name, sender + ": \n" + Chat[Table_Chat.Message_num]);
                }
                break;
            }
            case Constants.Delete_Chat_Message: {
                String Chat_Table_Name = details.split("\\^")[0];
                String Message = details.split("\\^")[1];
                String[] Chat = Message.split("\\|");
                if (!sqlHelper.select(null, Chat_Table_Name, new String[]{Table_Chat.Message_ID, Table_Chat.User_ID},
                        new String[]{Chat[Table_Chat.Message_ID_num], Chat[Table_Chat.User_ID_num]}, null)[0].isEmpty())
                    sqlHelper.delete(Chat_Table_Name, new String[]{Table_Chat.Message_ID, Table_Chat.User_ID},
                            new String[]{Chat[Table_Chat.Message_ID_num], Chat[Table_Chat.User_ID_num]}, new int[]{1});
                break;
            }
            case Constants.Take_Task: {
                String[] Task = details.split("\\|");
                if (!sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                        new String[]{Task[Table_Tasks.Event_ID_num], Task[Table_Tasks.Task_ID_Number_num], Task[Table_Tasks.subTask_ID_Number_num]}, null)[0].isEmpty())
                    sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{Task[Table_Tasks.User_ID_num]},
                            new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                            new String[]{Task[Table_Tasks.Event_ID_num], Task[Table_Tasks.Task_ID_Number_num], Task[Table_Tasks.subTask_ID_Number_num]});
                break;
            }
            case Constants.UnTake_Task: {
                String[] Task = details.split("\\|");
                if (!sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                        new String[]{Task[Table_Tasks.Event_ID_num], Task[Table_Tasks.Task_ID_Number_num], Task[Table_Tasks.subTask_ID_Number_num]}, null)[0].isEmpty())
                    sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{Constants.UnCheck},
                            new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                            new String[]{Task[Table_Tasks.Event_ID_num], Task[Table_Tasks.Task_ID_Number_num], Task[Table_Tasks.subTask_ID_Number_num]});
                break;
            }
            case Constants.Vote_For_Date: {
                String[] Vote = details.split("\\|");
                if (sqlHelper.select(null, Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID},
                        new String[]{Vote[Table_Vote_Date.Event_ID_num], Vote[Table_Vote_Date.Vote_ID_num], Vote[Table_Vote_Date.User_ID_num]}, null)[0].isEmpty())
                    sqlHelper.insert(Table_Vote_Date.Table_Name, Vote);
                break;
            }
            case Constants.UnVote_For_Date: {
                String[] Vote = details.split("\\|");
                if (!sqlHelper.select(null, Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID},
                        new String[]{Vote[Table_Vote_Date.Event_ID_num], Vote[Table_Vote_Date.Vote_ID_num], Vote[Table_Vote_Date.User_ID_num]}, null)[0].isEmpty())
                    sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID},
                            new String[]{Vote[Table_Vote_Date.Event_ID_num], Vote[Table_Vote_Date.Vote_ID_num], Vote[Table_Vote_Date.User_ID_num]}, new int[]{1});
                break;
            }
            case Constants.Vote_For_Location: {
                String[] Vote = details.split("\\|");
                if (sqlHelper.select(null, Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID, Table_Vote_Location.User_ID},
                        new String[]{Vote[Table_Vote_Location.Event_ID_num], Vote[Table_Vote_Location.Vote_ID_num], Vote[Table_Vote_Location.User_ID_num]}, null)[0].isEmpty())
                    sqlHelper.insert(Table_Vote_Location.Table_Name, Vote);
                break;
            }
            case Constants.UnVote_For_Location: {
                String[] Vote = details.split("\\|");
                if (!sqlHelper.select(null, Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID, Table_Vote_Location.User_ID},
                        new String[]{Vote[Table_Vote_Location.Event_ID_num], Vote[Table_Vote_Location.Vote_ID_num], Vote[Table_Vote_Location.User_ID_num]}, null)[0].isEmpty())
                    sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID, Table_Vote_Location.User_ID},
                            new String[]{Vote[Table_Vote_Location.Event_ID_num], Vote[Table_Vote_Location.Vote_ID_num], Vote[Table_Vote_Location.User_ID_num]}, new int[]{1});
                break;
            }
            case Constants.Update_Attending: {
                String[] Event_User = details.split("\\|");
                if (!sqlHelper.select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
                        new String[]{Event_User[Table_Events_Users.Event_ID_num], Event_User[Table_Events_Users.User_ID_num]}, null)[0].isEmpty())
                    sqlHelper.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Attending}, new String[]{Event_User[Table_Events_Users.Attending_num]},
                            new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
                            new String[]{Event_User[Table_Events_Users.Event_ID_num], Event_User[Table_Events_Users.User_ID_num]});
                break;
            }


            //old
            case Constants.Update_Event_Details_Filed: {
                String EventID = details.split("\\^")[0];
                String Filed = details.split("\\^")[1];
                String Update = details.split("\\^")[2];
                Helper.update_Event_details_field_MySQL(EventID, Filed, Update);
                break;
            }
            case Constants.Delete_User: {
                String Event_ID = details.split("\\^")[0];
                String USer_ID = details.split("\\^")[1];
                if (USer_ID.equals(Constants.MY_User_ID)) {
                    Helper.Delete_Event_MySQL(Event_ID);
                } else {
                    sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
                            new String[]{Event_ID, USer_ID}, new int[]{1});
                }
                break;
            }
            case Constants.Delete_Task: {
                String Event_ID = details.split("\\^")[0];
                String Task_ID_Number = details.split("\\^")[1];
                sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number},
                        new String[]{Event_ID, Task_ID_Number}, new int[]{1});
                break;
            }

            default: {
                showToast(data.getString("message"));
                break;
            }
        }
        delegate.processFinish(Constants.Update_Activity);
    }

    private void deleteEvent(String event_id) {
        boolean close_event = false;
        if (delegate != null) {
            String current_event_id = delegate.currentLocation();
            if (current_event_id.equals(event_id)) {
                close_event = true;
            }
        }
        if (close_event) delegate.closeActivity();
        String Chat_Table_Name = Table_Chat.Table_Name + event_id;
        //Delete event.
        sqlHelper.delete(Table_Events.Table_Name, new String[]{Table_Events.Event_ID}, new String[]{event_id}, new int[]{1});
        //Delete event_user.
        sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{event_id}, null);
        //Delete tasks.
        sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{event_id}, null);
        //Delete chat.
        sqlHelper.Delete_Table(Chat_Table_Name);
        //Delete vote_date.
        sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{event_id}, null);
        //Delete vote_location.
        sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{event_id}, null);

    }

    private void hundleUpdate(String data) {
        ArrayList<ArrayList<List<String>>> result = new ArrayList<>();
        String event_id = parsingUpdate(result,data);
        ArrayList<String>[] check = sqlHelper.select(null,Table_Events.Table_Name
                ,new String[]{Table_Events.Event_ID},new String[]{event_id},null);
        int details_index = 0;
        int users_start_index = 1; // in result users indexes are 1 - 4
        int tasks_start_index = 5; // in result tasks indexes are 5 - 8
        int date_start_index = 9; // in result dates indexes are  9 - 12
        int location_start_index = 13; // in result locations indexes are 13 - 16

        int user_action = Constants.update_event_new;
        if(check[0].size() > 0){
            user_action = Constants.update_event_update;
            int delete_users_index = (Constants.update_event_delete)+users_start_index;
            for(List<String> del_users : result.get(delete_users_index)){
                String del_id =del_users.get(Table_Events_Users.User_ID_num - Constants.index_object_sql_diff);
                if(del_id.equals(Constants.MY_User_ID)){
                    user_action = Constants.update_event_delete;
                    break;
                }
            }
        }
        switch (user_action){
            case Constants.update_event_new: {
                Event event = new Event();
                event.setId(event_id);
                //set Details
                event.setDetails(result.get(details_index).get(0));
                //set Event users
                ArrayList<List<String>> users = new ArrayList<>();
                if(result.get(users_start_index+Constants.update_event_new).get(0).size() > 1)
                    users.addAll(result.get(users_start_index+Constants.update_event_new));
                if(result.get(users_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    users.addAll(result.get(users_start_index+Constants.update_event_not_change));
                if(result.get(users_start_index+Constants.update_event_update).get(0).size() > 1)
                    users.addAll(result.get(users_start_index+Constants.update_event_update));
                event.setEventUsers(users);
                //set tasks
                ArrayList<List<String>> tasks = new ArrayList<>();
                if(result.get(tasks_start_index+Constants.update_event_new).get(0).size() > 1)
                    tasks.addAll(result.get(tasks_start_index+Constants.update_event_new));
                if(result.get(tasks_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    tasks.addAll(result.get(tasks_start_index+Constants.update_event_not_change));
                if(result.get(tasks_start_index+Constants.update_event_update).get(0).size() > 1)
                    tasks.addAll(result.get(tasks_start_index+Constants.update_event_update));
                event.setTasks(tasks);
                //set Dates
                ArrayList<List<String>> dates = new ArrayList<>();
                if(result.get(date_start_index+Constants.update_event_new).get(0).size() > 1)
                    dates.addAll(result.get(date_start_index+Constants.update_event_new));
                if(result.get(date_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    dates.addAll(result.get(date_start_index+Constants.update_event_not_change));
                if(result.get(date_start_index+Constants.update_event_update).get(0).size() > 1)
                    dates.addAll(result.get(date_start_index+Constants.update_event_update));
                event.setVoteDates(dates);

                //set Locations
                ArrayList<List<String>> locations = new ArrayList<>();
                if(result.get(location_start_index+Constants.update_event_new).get(0).size() > 1)
                    locations.addAll(result.get(location_start_index+Constants.update_event_new));
                if(result.get(location_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    locations.addAll(result.get(location_start_index+Constants.update_event_not_change));
                if(result.get(location_start_index+Constants.update_event_update).get(0).size() > 1)
                    locations.addAll(result.get(location_start_index+Constants.update_event_update));
                event.setVoteLocations(locations);

                addEventWithSafeSQL(event);
                break;
            }
            case Constants.update_event_update: {

                //update Details if needed
                if(result.get(details_index).get(0).size() > 1){
                    //update details in my sql.
                    sqlHelper.update(Table_Events.Table_Name, event_id, result.get(details_index).get(0).toArray(new String[0]));
                }
                /*
                //update Event users
                ArrayList<List<String>> users = new ArrayList<>();
                if(result.get(users_start_index+Constants.update_event_new).get(0).size() > 1)
                    users.addAll(result.get(users_start_index+Constants.update_event_new));
                if(result.get(users_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    users.addAll(result.get(users_start_index+Constants.update_event_not_change));
                if(result.get(users_start_index+Constants.update_event_update).get(0).size() > 1)
                    users.addAll(result.get(users_start_index+Constants.update_event_update));
                event.setEventUsers(users);
                //set tasks
                ArrayList<List<String>> tasks = new ArrayList<>();
                if(result.get(tasks_start_index+Constants.update_event_new).get(0).size() > 1)
                    tasks.addAll(result.get(tasks_start_index+Constants.update_event_new));
                if(result.get(tasks_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    tasks.addAll(result.get(tasks_start_index+Constants.update_event_not_change));
                if(result.get(tasks_start_index+Constants.update_event_update).get(0).size() > 1)
                    tasks.addAll(result.get(tasks_start_index+Constants.update_event_update));
                event.setTasks(tasks);
                //set Dates
                ArrayList<List<String>> dates = new ArrayList<>();
                if(result.get(date_start_index+Constants.update_event_new).get(0).size() > 1)
                    dates.addAll(result.get(date_start_index+Constants.update_event_new));
                if(result.get(date_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    dates.addAll(result.get(date_start_index+Constants.update_event_not_change));
                if(result.get(date_start_index+Constants.update_event_update).get(0).size() > 1)
                    dates.addAll(result.get(date_start_index+Constants.update_event_update));
                event.setVoteDates(dates);

                //set Locations
                ArrayList<List<String>> locations = new ArrayList<>();
                if(result.get(location_start_index+Constants.update_event_new).get(0).size() > 1)
                    locations.addAll(result.get(location_start_index+Constants.update_event_new));
                if(result.get(location_start_index+Constants.update_event_not_change).get(0).size() > 1)
                    locations.addAll(result.get(location_start_index+Constants.update_event_not_change));
                if(result.get(location_start_index+Constants.update_event_update).get(0).size() > 1)
                    locations.addAll(result.get(location_start_index+Constants.update_event_update));
                event.setVoteLocations(locations);
*/
                break;
            }
            case Constants.update_event_delete: {
                deleteEvent(event_id);
                break;
            }
        }

    }

    private String parsingUpdate(ArrayList<ArrayList<List<String>>> result, String data) {
        char split_row = '[';
        String split_col = "\\]";
        String[] event_data = MySplit(data,split_row);
        String id = event_data[0];

        int new_item[] = {3, 3, 3, 3, 5, 5, 5, 5, 7, 7, 7, 7, 3, 3, 3, 3};

        for (int i = 1; i < event_data.length; i++) {
            String[] split_data = event_data[i].split(split_col);
            List<String> list = new ArrayList<>();
            ArrayList<List<String>> arrL = new ArrayList<>();
            for (int j = 0; j < split_data.length; j++) {
                if (i > 1 && j % new_item[i - 2] == 0 && j > 0) {
                    arrL.add(list);
                    list = new ArrayList<>();
                }
                list.add(split_data[j]);
            }
            arrL.add(list);
            result.add(arrL);
        }
        return id;
    }
    private String[] MySplit(String line, char split_row) {
        ArrayList<String> spl = new ArrayList<>();
        String data="";
        for (int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == split_row){
                spl.add(data);
                data = "";//new String();
            }
            else{
                data += line.charAt(i);
            }
        }
        if(line.charAt(line.length()-1) == split_row)
            spl.add(data);
        return spl.toArray(new String[0]);
    }

/*
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String details = "";
        String action = "";
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messprivate void parsingUpdate(String ) {
    }ageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                //if (extras.getString(Constants.Message).split("\\|").length > 1) {
                action = extras.getString(Constants.Message).substring(0, 1); //extras.getString(Constants.Message).split("\\|")[0].split(": ")[1]; //TODO change format (no more "from")
                details = extras.getString(Constants.Message).substring(1); //extras.getString(Constants.Message).split("\\|")[1];
                // }
                switch (action) {
                    case Constants.New_Event: {
                        try {
                            Event event = getEvent(details);
                            addEventWithSafeSQL(event);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case Constants.Request_New_Event: {
                        try {
                            Event event = myApiService.requestEvent(details).execute();
                            if (event != null) {
                                addEventWithSafeSQL(event);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case Constants.Delete_Event: {
                        boolean close_event = false;
                        if (delegate != null) {
                            String event_id = delegate.currentLocation();
                            if (event_id.equals(details)) {
                                close_event = true;
                            }
                        }
                        if (close_event) delegate.closeActivity();
                        Helper.Delete_Event_MySQL(details);

                        break;
                    }
                   /* case Constants.Update_Event: {
                        String Event_ID = details.split("\\|")[0].split("\\^")[0];
                        String[] update_section = new String[]{details.split("\\^")[1], details.split("\\^")[2], details.split("\\^")[3], details.split("\\^")[4], details.split("\\^")[5]};
                        //Update event details.
                        String[] event = getEvent(Event_ID);
                        if (update_section[0].equals(Constants.Yes)) {
                            Helper.Update_Event_details_MySQL(event);
                            //Helper.Update_Event_details_MySQL(event[0], event[1], event[2], event[3], event[4], event[5], event[6], event[7], Constants.imageSaveLocation + "/" + event[8], event[9]);
                        }
                        //Update event users.
                        if (update_section[1].equals(Constants.Yes)) {
                            //Delete all users from the event.
                            sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{Event_ID}, null);
                            //Add all Users.
                            ArrayList<String[]> allUsers = getAllAttending(Event_ID);
                            for (String[] User : allUsers) {
                                //Add user to my sql.
                                sqlHelper.insert(Table_Events_Users.Table_Name, User);
                                //Add User to my sql Users (check inside if the user already exist).
                                String User_ID = User[Table_Events_Users.User_ID_num];
                                Helper.User_Insert_MySQL(User_ID);
                            }
                        }
                        //Update event tasks.
                        if (update_section[2].equals(Constants.Yes)) {
                            //Delete all tasks from the event.
                            sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{Event_ID}, null);
                            ArrayList<String[]> allTasks = getAllTasks(Event_ID);
                            //Add task to my sql.
                            for (String[] task : allTasks) {
                                sqlHelper.insert(Table_Tasks.Table_Name, task);
                            }
                        }
                        //Update event vote_date.
                        if (update_section[3].equals(Constants.Yes)) {
                            //Delete all tasks from the event.
                            sqlHelper.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{Event_ID}, null);
                            ArrayList<String[]> allVote_Date = getAllVotes_Date(Event_ID);
                            //Add task to my sql.
                            for (String[] vote : allVote_Date) {
                                sqlHelper.insert(Table_Vote_Date.Table_Name, vote);
                            }
                        }
                        //Update event vote_location.
                        if (update_section[4].equals(Constants.Yes)) {
                            //Delete all tasks from the event.
                            sqlHelper.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{Event_ID}, null);
                            ArrayList<String[]> allVote_Location = getAllVotes_Location(Event_ID);
                            //Add task to my sql.
                            for (String[] vote : allVote_Location) {
                                sqlHelper.insert(Table_Vote_Location.Table_Name, vote);
                            }
                        }
                        try {
                            cloudStorage.downloadFile(Constants.bucket_name, event[8], getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                    case Constants.New_User: {
                        String Event_ID = details.split("\\^")[0];
                        String USer_ID = details.split("\\^")[1];
                        String[] event_friend = getEventUser(Event_ID, USer_ID);
                        sqlHelper.insert(Table_Events_Users.Table_Name, event_friend);
                        Helper.User_Insert_MySQL(USer_ID);
                        break;
                    }*/
   /*                 case Constants.Delete_User: {
                        String Event_ID = details.split("\\^")[0];
                        String USer_ID = details.split("\\^")[1];
                        if (USer_ID.equals(Constants.MY_User_ID)) {
                            Helper.Delete_Event_MySQL(Event_ID);
                        } else {
                            sqlHelper.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
                                    new String[]{Event_ID, USer_ID}, new int[]{1});
                        }
                        break;
                    }
                    case Constants.Update_User_Attending: {
                        String Event_ID = details.split("\\^")[0];
                        String USer_ID = details.split("\\^")[1];
                        String attend = details.split("\\^")[2];
                        sqlHelper.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Attending}, new String[]{attend},
                                new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, new String[]{Event_ID, USer_ID});
                        break;
                    }/*
                    case Constants.New_Task: {
                        String Event_ID = details.split("\\^")[0];
                        String Task_ID_Number = details.split("\\^")[1];
                        String subTask_ID_Number = details.split("\\^")[2];
                        String[] task = getTask(Event_ID, Task_ID_Number, subTask_ID_Number);
                        if (sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number},
                                new String[]{task[0], task[1]}, null)[0].isEmpty()) {
                            sqlHelper.insert(Table_Tasks.Table_Name, task);
                        }
                        break;
                    }*/
     /*               case Constants.Delete_Task: {
                        String Event_ID = details.split("\\^")[0];
                        String Task_ID_Number = details.split("\\^")[1];
                        sqlHelper.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number},
                                new String[]{Event_ID, Task_ID_Number}, new int[]{1});
                        break;
                    }/*
                    case Constants.Update_Task: {
                        String Event_ID = details.split("\\^")[0];
                        String Task_ID_Number = details.split("\\^")[1];
                        String subTask_ID_Number = details.split("\\^")[2];
                        String[] task = getTask(Event_ID, Task_ID_Number, subTask_ID_Number);
                        sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.Description, Table_Tasks.Description, Table_Tasks.User_ID},
                                new String[]{task[2], task[3], task[4]}, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number}, new String[]{Event_ID, Task_ID_Number});
                        break;
                    }*/
       /*             case Constants.Update_Task_User_ID: {
                        String Event_ID = details.split("\\^")[0];
                        String Task_ID_Number = details.split("\\^")[1];
                        String User_ID = details.split("\\^")[2];
                        sqlHelper.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{User_ID},
                                new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number}, new String[]{Event_ID, Task_ID_Number, 0 + ""});
                        break;
                    }
                    case Constants.New_Chat_Message: {
                        String Chat_ID = details.split("\\^")[0];
                        String Message_ID = details.split("\\^")[1];
                        String User_ID = details.split("\\^")[2];
                        String[] chat = getChat(Chat_ID, Message_ID, User_ID);
                        String Event_ID = Chat_ID.substring("Chat_".length()).replace("_", " - ");
                        ArrayList<String>[] event = sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID},
                                new String[]{Event_ID}, new int[]{1});
                        if (sqlHelper.select(null, Chat_ID, new String[]{Table_Chat.Message_ID, Table_Chat.User_ID}, new String[]{Message_ID, User_ID}, null)[0].isEmpty()) {
                            sqlHelper.insert(Chat_ID, chat);
                            String event_name = event[Table_Events.Name_num].get(0);
                            if (event_name.length() == 0) event_name = "Event Name";
                            String sender = Contacts_List.contacts.get(chat[1]);
                            addNotification("New Message - " + event_name, sender + ": \n" + chat[2]);
                        }
                        break;
                    }// commit
                    case Constants.Delete_Chat_Message: {
                        String Chat_ID = details.split("\\^")[0];
                        String Message_ID = details.split("\\^")[1];
                        String User_ID = details.split("\\^")[2];
                        sqlHelper.delete(Chat_ID, new String[]{Table_Chat.Message_ID, Table_Chat.User_ID},
                                new String[]{Message_ID, User_ID}, new int[]{1});
                        break;
                    }
                    case Constants.Update_Event_Details_Filed: {
                        String EventID = details.split("\\^")[0];
                        String Filed = details.split("\\^")[1];
                        String Update = details.split("\\^")[2];
                        Helper.update_Event_details_field_MySQL(EventID, Filed, Update);
                        break;
                    }
                    case Constants.Insert_Vote_Date: {
                        String EventID = details.split("\\^")[0];
                        int Vote_ID = Integer.parseInt(details.split("\\^")[1]);
                        String User_ID = details.split("\\^")[2];
                        Helper.add_vote_date_User_ID_MySQL(EventID, Vote_ID, User_ID);
                        break;
                    }
                    case Constants.Delete_Vote_Date: {
                        String EventID = details.split("\\^")[0];
                        int Vote_ID = Integer.parseInt(details.split("\\^")[1]);
                        String User_ID = details.split("\\^")[2];
                        Helper.delete_vote_date_User_ID_MySQL(EventID, Vote_ID, User_ID);
                        break;
                    }
                    case Constants.Insert_Vote_Location: {
                        String EventID = details.split("\\^")[0];
                        int Vote_ID = Integer.parseInt(details.split("\\^")[1]);
                        String User_ID = details.split("\\^")[2];
                        Helper.add_vote_location_User_ID_MySQL(EventID, Vote_ID, User_ID);
                        break;
                    }
                    case Constants.Delete_Vote_Location: {
                        String EventID = details.split("\\^")[0];
                        int Vote_ID = Integer.parseInt(details.split("\\^")[1]);
                        String User_ID = details.split("\\^")[2];
                        Helper.delete_vote_location_User_ID_MySQL(EventID, Vote_ID, User_ID);
                        break;
                    }
                    default: {
                        showToast(extras.getString("message"));
                        break;
                    }
                }
            }
        }
        //GcmBroadcastReceiver.completeWakefulIntent(intent);
        delegate.processFinish(Constants.Update_Activity);
    }
*/

    private void addEventWithSafeSQL(Event event) {
        // task sql in server as only 6 column and task sql in client as 7 column
        if (event.getTasks() == null)
            event.setTasks(new ArrayList<List<String>>());
        if (event.getVoteDates() == null)
            event.setVoteDates(new ArrayList<List<String>>());
        if (event.getVoteLocations() == null)
            event.setVoteLocations(new ArrayList<List<String>>());

        for (List<String> ls : event.getTasks()) {
            if (ls != null && ls.size() == 5) {
                ls.add(ls.get(4));
            }
        }
        addEvent(event);
    }

    private void addEvent(Event event) {
        String[] allDetails = event.getDetails().toArray(new String[0]);
        List<List<String>> allUsers = event.getEventUsers();//getAllAttending(details);
        List<List<String>> allTasks = event.getTasks();//getAllTasks(details);
        List<List<String>> allVote_Date = event.getVoteDates();//getAllVotes_Date(details);
        List<List<String>> allVote_Location = event.getVoteLocations();//getAllVotes_Location(details);

        String Chat_ID = Table_Chat.Table_Name + event.getId();

        //Add details to my sql.
        sqlHelper.insert(Table_Events.Table_Name, event.getId(), allDetails);
        //Add users to my sql.
        sqlHelper.insertAll(Table_Events_Users.Table_Name, event.getId(), allUsers);
        if (allTasks.size() > 0)
            sqlHelper.insertAll(Table_Tasks.Table_Name, event.getId(), allTasks);
        //Add vote_date to my sql.
        if (allVote_Date.size() > 0)
            sqlHelper.insertAll(Table_Vote_Date.Table_Name, event.getId(), allVote_Date);
        //Add vote_location to my sql.
        if (allVote_Location.size() > 0)
            sqlHelper.insertAll(Table_Vote_Location.Table_Name, event.getId(), allVote_Location);

        sqlHelper.Create_Table(Chat_ID, Table_Chat.getAllFields(), Table_Chat.getAllSqlParams());


        //Add all missing messages.
        ArrayList<String[]> allChat = getAllChat(Chat_ID);

        for (String[] chat : allChat) {
            sqlHelper.insert(Chat_ID, chat);
        }
/*
        try {
            //cloudStorage.downloadFile(Constants.bucket_name, picName, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
  */
        addNotification("New Event", "You got invite to new event: " + event.getDetails().get(Table_Events.Name_num));

    }


    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Event getEvent(String str_event) {
        Event event = new Event();
        String split_row = "\\[";
        String split_col = "\\]";
        str_event = replaceEmptySpace(str_event);
        String[] event_data = str_event.split(split_row);
        event.setId(event_data[0]);

        int new_item[] = {3, 5, 7, 3};

        for (int i = 1; i < event_data.length; i++) {
            String[] split_data = event_data[i].split(split_col);
            List<String> list = new ArrayList<>();
            ArrayList<List<String>> arrL = new ArrayList<>();
            for (int j = 0; j < split_data.length; j++) {
                if (i > 1 && j % new_item[i - 2] == 0 && j > 0) {
                    arrL.add(list);
                    list = new ArrayList<>();
                }
                if (!split_data[j].equals(" "))
                    list.add(split_data[j]);
            }
            arrL.add(list);
            switch (i) {
                case 1:
                    event.setDetails(arrL.get(0));
                    break;
                case 2:
                    event.setEventUsers(arrL);
                    break;
                case 3:
                    event.setTasks(arrL);
                    break;
                case 4:
                    event.setVoteDates(arrL);
                    break;
                case 5:
                    event.setVoteLocations(arrL);
                    break;
            }
        }
        return event;
    }

    private String replaceEmptySpace(String str_event) {
        String newStr = "";
        for (int i = 0; i < str_event.length() - 1; i++) {
            if (str_event.charAt(i) == '[' && str_event.charAt(i + 1) == '[') {
                newStr += "[ ";
            } else {
                newStr += str_event.charAt(i);
            }
        }
        if (str_event.charAt(str_event.length() - 1) == '[')
            newStr += "[ ";

        return newStr;
    }

    /*
    private String[] getEvent(String event_id) {
        Event event;
        String[] result = new String[Table_Events.Size()];
        try {
            event = myApiService.eventGet(event_id).execute();
            result[Table_Events.Event_ID_num] = event.getId();
            result[Table_Events.Name_num] = event.getName();
            result[Table_Events.Location_num] = event.getLocation();
            result[Table_Events.Vote_Location_num] = event.getVoteLocation();
            result[Table_Events.Start_Date_num] = event.getStartDate();
            result[Table_Events.End_Date_num] = event.getEndDate();
            result[Table_Events.All_Day_Time_num] = event.getAllDayTime();
            result[Table_Events.Start_Time_num] = event.getStartTime();
            result[Table_Events.End_Time_num] = event.getEndTime();
            result[Table_Events.Vote_Time_num] = event.getVoteTime();
            result[Table_Events.Description_num] = event.getDescription();
            result[Table_Events.Image_Path_num] = event.getImageUrl();
            result[Table_Events.Update_Time_num] = event.getUpdateTime();
            //Clean unsigned filed.
            for (int i = 0; i < result.length; i++) {
                if (result[i] == null)
                    result[i] = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
*/
    /*
    private String[] getEventUser(String event_id, String user_id) {
        EventUser eventUser;
        String[] result = new String[Table_Events_Users.Size()];
        try {
            eventUser = myApiService.eventUserGet(event_id, user_id).execute();
            result[Table_Events_Users.Event_ID_num] = eventUser.getEventID();
            result[Table_Events_Users.User_ID_num] = eventUser.getUserID();
            result[Table_Events_Users.Attending_num] = eventUser.getAttending();
            result[Table_Events_Users.Permission_num] = eventUser.getPermission();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String[] getTask(String event_id, String task_id, String subTask_id) {
        Task task;
        String[] result = new String[Table_Tasks.Size()];
        try {
            task = myApiService.taskGet(event_id, task_id, subTask_id).execute();
            result[Table_Tasks.Event_ID_num] = task.getEventID();
            result[Table_Tasks.Task_ID_Number_num] = task.getTaskIDNumber();
            result[Table_Tasks.subTask_ID_Number_num] = task.getSubTaskIDNumber();
            result[Table_Tasks.Task_Type_num] = task.getTaskType();
            result[Table_Tasks.Description_num] = task.getDescription();
            result[Table_Tasks.User_ID_num] = task.getUserID();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
*/
    private String[] getChat(String chat_id, String message_id, String user_id) {
        Chat chat;
        String[] result = new String[Table_Chat.Size()];
        try {
            chat = myApiService.chatGet(chat_id, message_id, user_id).execute();
            result[0] = chat.getMessageID();
            result[1] = chat.getUserIDSender();
            result[2] = chat.getMessage();
            result[3] = chat.getDate();
            result[4] = chat.getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
    private ArrayList<String[]> getAllAttending(String event_id) {
        ArrayList<String[]> result = new ArrayList<>();
        EventUserCollection eventUserCollection;
        try {
            eventUserCollection = myApiService.eventUserGetUsers(event_id).execute();
            for (int i = 0; i < eventUserCollection.getItems().size(); i++) {
                result.add(new String[]{eventUserCollection.getItems().get(i).getEventID(), eventUserCollection.getItems().get(i).getUserID(),
                        eventUserCollection.getItems().get(i).getAttending(), eventUserCollection.getItems().get(i).getPermission()});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String[]> getAllTasks(String event_id) {
        ArrayList<String[]> result = new ArrayList<>();
        TaskCollection taskCollection;
        try {
            taskCollection = myApiService.taskGetAll(event_id).execute();
            if (taskCollection.getItems() != null) {
                String[] task;
                for (int i = 0; i < taskCollection.getItems().size(); i++) {
                    task = new String[Table_Tasks.Size()];
                    task[Table_Tasks.Event_ID_num] = taskCollection.getItems().get(i).getEventID();
                    task[Table_Tasks.Task_ID_Number_num] = taskCollection.getItems().get(i).getTaskIDNumber();
                    task[Table_Tasks.subTask_ID_Number_num] = taskCollection.getItems().get(i).getSubTaskIDNumber();
                    task[Table_Tasks.Task_Type_num] = taskCollection.getItems().get(i).getTaskType();
                    task[Table_Tasks.Description_num] = taskCollection.getItems().get(i).getDescription();
                    task[Table_Tasks.User_ID_num] = taskCollection.getItems().get(i).getUserID();
                    task[Table_Tasks.Mark_num] = taskCollection.getItems().get(i).getUserID();
                    result.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String[]> getAllVotes_Date(String event_id) {
        ArrayList<String[]> result = new ArrayList<>();
        VoteDateCollection voteDateCollection;
        try {
            voteDateCollection = myApiService.voteDateGetAll(event_id).execute();
            if (voteDateCollection.getItems() != null) {
                String[] vote = new String[Table_Vote_Date.Size()];
                for (int i = 0; i < voteDateCollection.getItems().size(); i++) {
                    vote[Table_Vote_Date.Event_ID_num] = voteDateCollection.getItems().get(i).getEventID();
                    vote[Table_Vote_Date.Vote_ID_num] = voteDateCollection.getItems().get(i).getVoteID();
                    vote[Table_Vote_Date.Start_Date_num] = voteDateCollection.getItems().get(i).getStartDate();
                    vote[Table_Vote_Date.End_Date_num] = voteDateCollection.getItems().get(i).getEndDate();
                    vote[Table_Vote_Date.All_Day_Time_num] = voteDateCollection.getItems().get(i).getAllDayTime();
                    vote[Table_Vote_Date.Start_Time_num] = voteDateCollection.getItems().get(i).getStartTime();
                    vote[Table_Vote_Date.End_Time_num] = voteDateCollection.getItems().get(i).getEndTime();
                    vote[Table_Vote_Date.User_ID_num] = voteDateCollection.getItems().get(i).getUserID();
                    result.add(vote);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String[]> getAllVotes_Location(String event_id) {
        ArrayList<String[]> result = new ArrayList<>();
        VoteLocationCollection voteLocationCollection;
        try {
            voteLocationCollection = myApiService.voteLocationGetAll(event_id).execute();
            if (voteLocationCollection.getItems() != null) {
                String[] vote = new String[Table_Vote_Location.Size()];
                for (int i = 0; i < voteLocationCollection.getItems().size(); i++) {
                    vote[Table_Vote_Location.Event_ID_num] = voteLocationCollection.getItems().get(i).getEventID();
                    vote[Table_Vote_Location.Vote_ID_num] = voteLocationCollection.getItems().get(i).getVoteID();
                    vote[Table_Vote_Location.Description_num] = voteLocationCollection.getItems().get(i).getDescription();
                    vote[Table_Vote_Location.User_ID_num] = voteLocationCollection.getItems().get(i).getUserID();
                    result.add(vote);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
*/
    private ArrayList<String[]> getAllChat(String Chat_ID) {
        ArrayList<String[]> result = new ArrayList<>();
        ChatCollection chatCollection;
        try {
            chatCollection = myApiService.chatGetAll(Chat_ID).execute();
            if (chatCollection.getItems() != null) {
                for (int i = 0; i < chatCollection.getItems().size(); i++) {
                    result.add(new String[]{chatCollection.getItems().get(i).getMessageID(), chatCollection.getItems().get(i).getUserIDSender(),
                            chatCollection.getItems().get(i).getMessage(), chatCollection.getItems().get(i).getDate(), chatCollection.getItems().get(i).getTime()});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Add app running notification

    private void addNotification(String title, String content) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }

    // Remove notification
    private void removeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(0);
    }

}