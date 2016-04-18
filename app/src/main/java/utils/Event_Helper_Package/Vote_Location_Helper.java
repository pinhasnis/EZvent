package utils.Event_Helper_Package;

import java.util.HashMap;

/**
 * Created by Ravid on 05/03/2016.
 */
public class Vote_Location_Helper {

    private String Description;
    private HashMap<String, String> votes;//User_ID, User_ID

    public Vote_Location_Helper(String description) {
        Description = description;
        this.votes = new HashMap<>();
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public HashMap<String, String> getVotes() {
        return votes;
    }

    public void setVotes(HashMap<String, String> votes) {
        this.votes = votes;
    }
}
