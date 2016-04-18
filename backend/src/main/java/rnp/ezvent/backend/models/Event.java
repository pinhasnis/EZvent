package rnp.ezvent.backend.models;

import rnp.ezvent.backend.utils.Constans.Constants;
import rnp.ezvent.backend.utils.Constans.Table_Details;
import rnp.ezvent.backend.utils.Constans.Table_Events_Users;
import rnp.ezvent.backend.utils.Constans.Table_Tasks;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Date;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Location;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Ravid on 25/09/2015.
 */
@Entity
public class Event {
    @Id
    private String id;
    private String[] details;
    private ArrayList<String[]> event_users;
    private ArrayList<String[]> tasks;
    private ArrayList<String[]> vote_dates;
    private ArrayList<String[]> vote_locations;

    public Event() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDetails(String[] details) {
        this.details = details;
    }

    public void setEvent_users(ArrayList<String[]> event_users) {
        this.event_users = event_users;
    }

    public void setTasks(ArrayList<String[]> tasks) {
        this.tasks = tasks;
    }

    public void setVote_dates(ArrayList<String[]> vote_dates) {
        this.vote_dates = vote_dates;
    }

    public void setVote_locations(ArrayList<String[]> vote_locations) {
        this.vote_locations = vote_locations;
    }


    public void setDetailsFromSql(ResultSet rs_details) throws SQLException {
        details = new String[Table_Details.Size - Constants.index_object_sql_diff];
        if (rs_details.next()) {
            for (int i = 0; i < details.length; i++) {
                details[i] = rs_details.getString(i+2);
            }
        }
    }

    public void setEvent_usersFromSql(ResultSet rs_event_users)throws SQLException {
        event_users = new ArrayList<>();
        while (rs_event_users.next()) {
            String [] arr = new String[Table_Events_Users.Size - Constants.index_object_sql_diff];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = rs_event_users.getString(i+2);
            }
            event_users.add(arr);
        }
    }

    public void setTasksFromSql(ResultSet rs_tasks)throws SQLException {
        tasks = new ArrayList<>();
        while (rs_tasks.next()) {
            String [] arr = new String[Table_Tasks.Size - Constants.index_object_sql_diff];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = rs_tasks.getString(i+2);
            }
            tasks.add(arr);
        }
    }

    public void setVote_datesFromSql(ResultSet rs_vote_dates)throws SQLException {
        vote_dates = new ArrayList<>();
        while (rs_vote_dates.next()) {
            String [] arr = new String[Table_Vote_Date.Size - Constants.index_object_sql_diff];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = rs_vote_dates.getString(i+2);
            }
            vote_dates.add(arr);
        }
    }

    public void setVote_locationsFromSql(ResultSet rs_vote_locations)throws SQLException {
        vote_locations = new ArrayList<>();
        while (rs_vote_locations.next()) {
            String [] arr = new String[Table_Vote_Location.Size - Constants.index_object_sql_diff];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = rs_vote_locations.getString(i+2);
            }
            vote_locations.add(arr);
        }
    }

    public String getId() {

        return id;
    }

    public String[] getDetails() {
        return details;
    }

    public ArrayList<String[]> getEvent_users() {
        return event_users;
    }

    public ArrayList<String[]> getTasks() {
        return tasks;
    }

    public ArrayList<String[]> getVote_dates() {
        return vote_dates;
    }

    public ArrayList<String[]> getVote_locations() {
        return vote_locations;
    }

    public Event(String id, String[] details, ArrayList<String[]> event_users, ArrayList<String[]> tasks, ArrayList<String[]> vote_dates, ArrayList<String[]> vote_locations) {

        this.id = id;
        this.details = details;
        this.event_users = event_users;
        this.tasks = tasks;
        this.vote_dates = vote_dates;
        this.vote_locations = vote_locations;
    }

    public String toString() {
        String str = id + "[";
        for (int i = 0; i < details.length - 1; i++) {
            str += details[i] + "]";
        }
        str += details[details.length - 1] + "[";

        for (int i = 0; i < event_users.size(); i++) {
            for (int j = 0; j < event_users.get(0).length; j++) {
                str += event_users.get(i)[j] + "]";
            }
        }
        if (str.charAt(str.length() - 1) == ']')
            str = str.substring(0, str.length() - 1);
        str += "[";

        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < tasks.get(0).length; j++) {
                str += tasks.get(i)[j] + "]";
            }
        }
        if (str.charAt(str.length() - 1) == ']')
            str = str.substring(0, str.length() - 1);
        str += "[";

        for (int i = 0; i < vote_dates.size(); i++) {
            for (int j = 0; j < vote_dates.get(0).length; j++) {
                str += vote_dates.get(i)[j] + "]";
            }
        }
        if (str.charAt(str.length() - 1) == ']')
            str = str.substring(0, str.length() - 1);
        str += "[";

        for (int i = 0; i < vote_locations.size(); i++) {
            for (int j = 0; j < vote_locations.get(0).length; j++) {
                str += vote_locations.get(i)[j] + "]";
            }
        }
        return str;
    }

}

