package utils.Event_Helper_Package;

import utils.Constans.Constants;

/**
 * Created by Ravid on 08/02/2016.
 */
public class Friend_Helper {
    private String permission;
    private String attending;

    public Friend_Helper(){
        this.permission = Constants.Participant;
        this.attending = Constants.Did_Not_Replay;
    }

    public Friend_Helper(String permission, String attending) {
        this.permission = permission;
        this.attending = attending;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getAttending() {
        return attending;
    }

    public void setAttending(String attending) {
        this.attending = attending;
    }

    public Friend_Helper make_copy() {
        return new Friend_Helper(this.permission, this.attending);
    }
}
