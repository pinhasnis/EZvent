package rnp.ezvent.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;

import rnp.ezvent.backend.utils.Constans.Constants;

/**
 * Created by Pinhas on 4/18/2016.
 */
@Entity
public class UpdateEvent {
    @Id
    private String id;
    private String user_id;
    private int details_changed;
    private String[] details;
    private ArrayList<String[]>[] event_users;
    private ArrayList<String[]>[] tasks;
    private ArrayList<String[]>[] vote_dates;
    private ArrayList<String[]>[] vote_locations;

    public void clearUnchangedData() {
        if (details_changed == Constants.False)
            details = null;
        if (event_users != null)
            event_users[Constants.update_event_not_change] = null;
        if (tasks != null)
            tasks[Constants.update_event_not_change] = null;
        if (vote_dates != null)
            vote_dates[Constants.update_event_not_change] = null;
        if (vote_locations != null)
            vote_locations[Constants.update_event_not_change] = null;
    }

    public UpdateEvent() {
    }

    public int isDetails_changed() {
        return details_changed;
    }

    public void setDetails_changed(int details_changed) {
        this.details_changed = details_changed;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getDetails() {
        return details;
    }

    public void setDetails(String[] details) {
        this.details = details;
    }

    public ArrayList<String[]>[] getEvent_users() {
        return event_users;
    }

    public void setEvent_users(ArrayList<String[]>[] event_users) {
        this.event_users = event_users;
    }

    public ArrayList<String[]>[] getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String[]>[] tasks) {
        this.tasks = tasks;
    }

    public ArrayList<String[]>[] getVote_dates() {
        return vote_dates;
    }

    public void setVote_dates(ArrayList<String[]>[] vote_dates) {
        this.vote_dates = vote_dates;
    }

    public ArrayList<String[]>[] getVote_locations() {
        return vote_locations;
    }

    public void setVote_locations(ArrayList<String[]>[] vote_locations) {
        this.vote_locations = vote_locations;
    }

    //"[" - separate between tables. "]" - separate between fileds. Thier is no spaciel sign between records.
    public String toString() {
        String str = id + "[" + details_changed;
        for (int i = 0; i < details.length - 1; i++) {
            str += details[i] + "]";
        }
        str += details[details.length - 1] + "[";

        for (ArrayList<String[]> users : event_users) {
            for (int i = 0; i < users.size(); i++) {
                for (int j = 0; j < users.get(0).length; j++) {
                    str += users.get(i)[j] + "]";
                }
            }
            if (str.charAt(str.length() - 1) == ']')
                str = str.substring(0, str.length() - 1);
            str += "[";
        }

        for (ArrayList<String[]> task : tasks) {
            for (int i = 0; i < task.size(); i++) {
                for (int j = 0; j < task.get(0).length; j++) {
                    str += task.get(i)[j] + "]";
                }
            }
            if (str.charAt(str.length() - 1) == ']')
                str = str.substring(0, str.length() - 1);
            str += "[";
        }

        for (ArrayList<String[]> date : vote_dates) {
            for (int i = 0; i < date.size(); i++) {
                for (int j = 0; j < date.get(0).length; j++) {
                    str += date.get(i)[j] + "]";
                }
            }
            if (str.charAt(str.length() - 1) == ']')
                str = str.substring(0, str.length() - 1);
            str += "[";
        }

        for (ArrayList<String[]> location : vote_locations) {

            for (int i = 0; i < location.size(); i++) {
                for (int j = 0; j < location.get(0).length; j++) {
                    str += location.get(i)[j] + "]";
                }
            }
            if (str.charAt(str.length() - 1) == ']')
                str = str.substring(0, str.length() - 1);
            str += "[";
        }
        str = str.substring(0, str.length() - 1);
        return str;
    }

}
