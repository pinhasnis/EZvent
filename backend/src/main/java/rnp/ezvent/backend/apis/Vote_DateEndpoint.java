package rnp.ezvent.backend.apis; /**
package com.example.some_lie.backend.apis;

import com.example.some_lie.backend.models.Event;
import com.example.some_lie.backend.utils.Constans.Constants;
import com.example.some_lie.backend.utils.Constans.Table_Vote_Date;
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
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
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
@ApiClass(resource = "vote_Date",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class Vote_DateEndpoint {

    private static final Logger logger = Logger.getLogger(Vote_DateEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;


    /**
     * Returns the {@link Event} with the corresponding ID.
     *
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Event} with the provided ID.
     */
   /** @ApiMethod(name = "Vote_DateGet", path = "Vote_DateGet")
    public Vote_Date Get(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_User_ID") String User_ID) {
        Vote_Date vote_date = new Vote_Date();
        try {
            ResultSet rs = MySQL_Util.select(null, Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID},
                    new String[]{Event_ID, Vote_ID, User_ID}, new int[]{1});
            if (rs.next()) {
                vote_date.setEvent_ID(rs.getString(Table_Vote_Date.Event_ID));
                vote_date.setVote_ID(rs.getString(Table_Vote_Date.Vote_ID));
                vote_date.setStart_Date(rs.getString(Table_Vote_Date.Start_Date));
                vote_date.setEnd_Date(rs.getString(Table_Vote_Date.End_Date));
                vote_date.setAll_Day_Time(rs.getString(Table_Vote_Date.All_Day_Time));
                vote_date.setStart_Time(rs.getString(Table_Vote_Date.Start_Time));
                vote_date.setEnd_Time(rs.getString(Table_Vote_Date.End_Time));
                vote_date.setUser_ID(rs.getString(Table_Vote_Date.User_ID));
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
        return vote_date;
    }

    @ApiMethod(name = "Vote_DateGetAll", path = "Vote_DateGetAll")
    public ArrayList<Vote_Date> GetAll(@Named("A_Event_ID") String Event_ID) {
        ArrayList<Vote_Date> vote_datesArrayList = new ArrayList<>();
        try {
            ResultSet rs = MySQL_Util.select(null, Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{Event_ID}, null);
            while (rs.next()) {
                vote_datesArrayList.add(new Vote_Date(rs.getString(Table_Vote_Date.Event_ID), rs.getString(Table_Vote_Date.Vote_ID), rs.getString(Table_Vote_Date.Start_Date),
                        rs.getString(Table_Vote_Date.End_Date), rs.getString(Table_Vote_Date.All_Day_Time), rs.getString(Table_Vote_Date.Start_Time),
                        rs.getString(Table_Vote_Date.End_Time), rs.getString(Table_Vote_Date.User_ID)));
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
        return vote_datesArrayList;
    }

    /**
     * Inserts a new {@code Event}.
     */
/**
    @ApiMethod(name = "Vote_DateInsert", path = "Vote_DateInsert")
    public void Insert(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_Start_Date") String Start_Date,
                       @Named("D_End_Date") String End_Date, @Named("E_All_Day_Time") String All_Day_Time, @Named("E_Start_Time") String Start_Time,
                       @Named("F_End_Time") String End_Time, @Named("G_User_ID") String User_ID) {
        try {
            MySQL_Util.insert(Table_Vote_Date.Table_Name, new String[]{Event_ID, Vote_ID, Start_Date, End_Date, All_Day_Time, Start_Time, End_Time, User_ID});

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
   /** @ApiMethod(name = "Vote_DateUpdate", path = "Vote_DateUpdate")
    public void Update(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_Start_Date") String Start_Date,
                       @Named("D_End_Date") String End_Date, @Named("E_All_Day_Time") String All_Day_Time, @Named("E_Start_Time") String Start_Time,
                       @Named("F_End_Time") String End_Time, @Named("G_User_ID") String User_ID) {
        try {
            MySQL_Util.update(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Start_Date, Table_Vote_Date.End_Date,
                            Table_Vote_Date.All_Day_Time, Table_Vote_Date.Start_Time, Table_Vote_Date.End_Time},
                    new String[]{Start_Date, End_Date, All_Day_Time, Start_Time, End_Time},
                    new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID}, new String[]{Event_ID, Vote_ID, User_ID});

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
    @ApiMethod(name = "Vote_DateDelete", path = "Vote_DateDelete")
    public void Delete_Vote(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID, @Named("C_User_ID") String User_ID) {
        try {
            MySQL_Util.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID},
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
   /** @ApiMethod(name = "Vote_DateDelete_By_Vote_ID", path = "Vote_DateDelete_By_Vote_ID")
    public void Delete_by_Vote_ID(@Named("A_Event_ID") String Event_ID, @Named("B_Vote_ID") String Vote_ID) {
        try {
            MySQL_Util.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID},
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


    @ApiMethod(name = "Vote_DateDelete_By_Event_ID", path = "Vote_DateDelete_By_Event_ID")
    public void Delete_by_Event_ID(@Named("A_Event_ID") String Event_ID) {
        try {
            MySQL_Util.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{Event_ID}, null);

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