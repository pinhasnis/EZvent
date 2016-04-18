package rnp.ezvent.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Ravid on 25/09/2015.
 */
@Entity
public class User {
    @Id
    private String User_ID;
    private String Nickname;

    public User() {
    }

    public User(String user_ID, String nickname) {
        User_ID = user_ID;
        Nickname = nickname;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }
}
