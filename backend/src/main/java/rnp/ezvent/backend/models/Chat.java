package rnp.ezvent.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Ravid on 21/10/2015.
 */
@Entity
public class Chat {
    @Id
    private String Message_ID;
    private String User_ID_Sender;
    private String Message;
    private String Date;
    private String Time;

    public Chat() {}

    public Chat(String message_ID, String User_ID_Sender, String message, String date, String time) {
        Message_ID = message_ID;
        this.User_ID_Sender = User_ID_Sender;
        Message = message;
        Date = date;
        Time = time;
    }

    public String getMessage_ID() {
        return Message_ID;
    }

    public void setMessage_ID(String message_ID) {
        Message_ID = message_ID;
    }

    public String getUser_ID_Sender() {
        return User_ID_Sender;
    }

    public void setUser_ID_Sender(String user_ID_Sender) {
        User_ID_Sender = user_ID_Sender;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
