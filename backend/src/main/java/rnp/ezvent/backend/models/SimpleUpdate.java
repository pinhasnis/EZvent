package rnp.ezvent.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;

/**
 * Created by Ravid on 19/04/2016.
 */
@Entity
public class SimpleUpdate {

    @Id
    private int action;
    private String Chat_Table_name;
    private String[] values;
    private ArrayList<String> Users_ID;

    public SimpleUpdate() {}

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getChat_Table_name() {
        return Chat_Table_name;
    }

    public void setChat_Table_name(String chat_Table_name) {
        Chat_Table_name = chat_Table_name;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String[] getValues() {
        return values;
    }

    public void setValuesn(String[] description) {
        this.values = description;
    }

    public ArrayList<String> getUsers_ID() {
        return Users_ID;
    }

    public void setUsers_ID(ArrayList<String> users_ID) {
        Users_ID = users_ID;
    }
}
