package rnp.ezvent.backend.apis; /**
package com.example.some_lie.backend.apis;

import com.example.some_lie.backend.models.Event;
import com.example.some_lie.backend.utils.Constans.Constants;
import com.example.some_lie.backend.utils.Constans.Table_Vote_Location;
import com.example.some_lie.backend.utils.MySQL_Util;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
/**
@Api(
        name = "ezvent",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "vote_Location",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class Vote_LocationEndpoint {

    private static final Logger logger = Logger.getLogger(Vote_LocationEndpoint.class.getName());


    /**
     * Returns the {@link Event} with the corresponding ID.
     *
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Event} with the provided ID.
     */
/**
 *
 *
 * ApiMethod(name = "Vote_LocationGet", path = "Vote_LocationGet")
    public Vote_Location Get(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_User_ID") String User_ID) {
        Vote_Location vote_location = new Vote_Location();
        try {
            ResultSet rs = MySQL_Util.select(null, Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID, Table_Vote_Location.User_ID},
                    new String[]{Event_ID, Vote_ID, User_ID}, new int[]{1});
            if (rs.next()) {
                vote_location.setEvent_ID(rs.getString(Table_Vote_Location.Event_ID));
                vote_location.setVote_ID(rs.getString(Table_Vote_Location.Vote_ID));
                vote_location.setDescription(rs.getString(Table_Vote_Location.Description));
                vote_location.setUser_ID(rs.getString(Table_Vote_Location.User_ID));
            }
            rs.close();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LocalDateTime now = LocalDateTime.now();
            try {
                int year = now.getYear();
                int month = now.getMonthOfYear();
                int day = now.getDayOfMonth();
                int hour = now.getHourOfDay();
                int minute = now.getMinuteOfHour();
                int second = now.getSecondOfMinute();
                int millis = now.getMillisOfSecond();
                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute + ":" + second + ":" + millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), date, time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return vote_location;
    }

    @ApiMethod(name = "Vote_LocationGetAll", path = "Vote_LocationGetAll")
    public ArrayList<Vote_Location> GetAll(@Named("A_Event_ID") String Event_ID) {
        ArrayList<Vote_Location> vote_locationsArrayList = new ArrayList<>();
        try {
            ResultSet rs = MySQL_Util.select(null, Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{Event_ID}, null);
            while (rs.next()) {
                vote_locationsArrayList.add(new Vote_Location(rs.getString(Table_Vote_Location.Event_ID), rs.getString(Table_Vote_Location.Vote_ID),
                        rs.getString(Table_Vote_Location.Description), rs.getString(Table_Vote_Location.User_ID)));
            }
            rs.close();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LocalDateTime now = LocalDateTime.now();
            try {
                int year = now.getYear();
                int month = now.getMonthOfYear();
                int day = now.getDayOfMonth();
                int hour = now.getHourOfDay();
                int minute = now.getMinuteOfHour();
                int second = now.getSecondOfMinute();
                int millis = now.getMillisOfSecond();
                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute + ":" + second + ":" + millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), date, time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return vote_locationsArrayList;
    }

    /**
     * Inserts a new {@code Event}.
     */
   /** @ApiMethod(name = "Vote_LocationInsert", path = "Vote_LocationInsert")
    public void Insert(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_Description") String Description,
                       @Named("D_User_ID") String User_ID) {
        try {
            MySQL_Util.insert(Table_Vote_Location.Table_Name, new String[]{Event_ID, Vote_ID, Description, User_ID});

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            LocalDateTime now = LocalDateTime.now();
            try {
                int year = now.getYear();
                int month = now.getMonthOfYear();
                int day = now.getDayOfMonth();
                int hour = now.getHourOfDay();
                int minute = now.getMinuteOfHour();
                int second = now.getSecondOfMinute();
                int millis = now.getMillisOfSecond();
                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute + ":" + second + ":" + millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), date, time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     *

     */
/**
    @ApiMethod(name = "Vote_LocationUpdate", path = "Vote_LocationUpdate")
    public void Update(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_Description") String Description,
                       @Named("D_User_ID") String User_ID) {
        try {
            MySQL_Util.update(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Description}, new String[]{Description},
                    new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID, Table_Vote_Location.User_ID}, new String[]{Event_ID, Vote_ID, User_ID});

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LocalDateTime now = LocalDateTime.now();
            try {
                int year = now.getYear();
                int month = now.getMonthOfYear();
                int day = now.getDayOfMonth();
                int hour = now.getHourOfDay();
                int minute = now.getMinuteOfHour();
                int second = now.getSecondOfMinute();
                int millis = now.getMillisOfSecond();
                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute + ":" + second + ":" + millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), date, time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Deletes the specified {@code Event}.
     *
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Event}
     */
/**
    @ApiMethod(name = "Vote_LocationDelete", path = "Vote_LocationDelete")
    public void Delete_Vote(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_User_ID") String User_ID) {
        try {
            MySQL_Util.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID, Table_Vote_Location.User_ID},
                    new String[]{Event_ID, Vote_ID, User_ID}, new int[]{1});
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LocalDateTime now = LocalDateTime.now();
            try {
                int year = now.getYear();
                int month = now.getMonthOfYear();
                int day = now.getDayOfMonth();
                int hour = now.getHourOfDay();
                int minute = now.getMinuteOfHour();
                int second = now.getSecondOfMinute();
                int millis = now.getMillisOfSecond();
                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute + ":" + second + ":" + millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), date, time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Deletes the specified {@code Event}.
     *
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Event}
     */
   /** @ApiMethod(name = "Vote_LocationDelete_By_Vote_ID", path = "Vote_LocationDelete_By_Vote_ID")
    public void Delete_by_Vote_ID(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID) {
        try {
            MySQL_Util.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID},
                    new String[]{Event_ID, Vote_ID}, null);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LocalDateTime now = LocalDateTime.now();
            try {
                int year = now.getYear();
                int month = now.getMonthOfYear();
                int day = now.getDayOfMonth();
                int hour = now.getHourOfDay();
                int minute = now.getMinuteOfHour();
                int second = now.getSecondOfMinute();
                int millis = now.getMillisOfSecond();
                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute + ":" + second + ":" + millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), date, time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    @ApiMethod(name = "Vote_LocationDelete_By_Event_ID", path = "Vote_LocationDelete_By_Event_ID")
    public void Delete_by_Event_ID(@Named("A_Event_ID") String Event_ID) {
        try {
            MySQL_Util.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{Event_ID}, null);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            LocalDateTime now = LocalDateTime.now();
            try {
                int year = now.getYear();
                int month = now.getMonthOfYear();
                int day = now.getDayOfMonth();
                int hour = now.getHourOfDay();
                int minute = now.getMinuteOfHour();
                int second = now.getSecondOfMinute();
                int millis = now.getMillisOfSecond();
                String date = day + "/" + month + "/" + year;
                String time = hour + ":" + minute + ":" + second + ":" + millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), date, time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
    **/