package rnp.ezvent.backend.apis; /**
package com.example.some_lie.backend.apis;

import com.example.some_lie.backend.utils.Constans.Constants;
import com.example.some_lie.backend.utils.Constans.Table_Details;
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
import java.util.logging.Logger;

import javax.inject.Named;



    @Api(name = "ezvent", version = "v1",
            namespace = @ApiNamespace(
                    ownerDomain = Constants.API_OWNER,
                    ownerName = Constants.API_OWNER,
                    packagePath = Constants.API_PACKAGE_PATH
            )
    )
    @ApiClass(resource = "Details",
            clientIds = {
                    Constants.ANDROID_CLIENT_ID,
                    Constants.IOS_CLIENT_ID,
                    Constants.WEB_CLIENT_ID},
            audiences = {Constants.AUDIENCE_ID}
    )
    public class DetailsEndpoint {

        private static final Logger logger = Logger.getLogger(DetailsEndpoint.class.getName());

        private static final int DEFAULT_LIST_LIMIT = 20;


        /**
         * Returns the {@link Details} with the corresponding ID.
         *
         * @param Details_ID the ID of the entity to be retrieved
         * @return the entity with the corresponding ID
         * @throws NotFoundException if there is no {@code Details} with the provided ID.
         */
  /**      @ApiMethod(name = "DetailsGet", path = "DetailsGet")
        public Details Get(@Named("Details_ID") String Details_ID) {
            Details Details = new Details();
            try {
                ResultSet rs = MySQL_Util.select(null, Table_Details.Table_Name, new String[]{Table_Details.Details_ID}, new String[]{Details_ID}, new int[]{1});
                if (rs.next()) {
                    Details.setName(rs.getString(Table_Details.Name));
                    Details.setLocation(rs.getString(Table_Details.Location));
                    Details.setVote_location(rs.getString(Table_Details.Vote_Location));
                    Details.setStart_date(rs.getString(Table_Details.Start_Date));
                    Details.setEnd_date(rs.getString(Table_Details.End_Date));
                    Details.setAll_day_time(rs.getString(Table_Details.All_Day_Time));
                    Details.setStart_time(rs.getString(Table_Details.Start_Time));
                    Details.setEnd_time(rs.getString(Table_Details.End_Time));
                    Details.setVote_time(rs.getString(Table_Details.Vote_Time));
                    Details.setDescription(rs.getString(Table_Details.Description));
                    Details.setImage_url(rs.getString(Table_Details.Image_Path));
                    Details.setUpdate_time(rs.getString(Table_Details.Update_Time));
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
            return Details;
        }

        /**
         * Inserts a new {@code Details}.
         */
     /**   @ApiMethod(name = "DetailsInsert", path = "DetailsInsert")
        public images_path Insert(@Named("A_Details_ID") String Details_ID, @Named("B_Name") String Name, @Named("C_Location") String Location, @Named("D_Vote_Location") String Vote_Location,
                                  @Named("E_Start_Date") String Start_Date, @Named("F_End_Date") String End_Date, @Named("G_All_Day_Time") String All_Day_Time,
                                  @Named("H_Start_Time") String Start_Time, @Named("I_End_Time") String End_Time, @Named("J_Vote_Time") String Vote_Time,
                                  @Named("K_Description") String Description, @Named("L_Image_Path") String Image_Path, @Named("M_Update_Time") String Update_Time) {
            try {
                String uploadURL = Details_ID + "_pic0";
                MySQL_Util.insert(Table_Details.Table_Name, new String[]{Details_ID, Name, Location, Vote_Location, Start_Date, End_Date,
                        All_Day_Time, Start_Time, End_Time, Vote_Time, Description, uploadURL, Update_Time});
                images_path im_path = new images_path();
                //im_path.setPath(uploadURL);
                return im_path;

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
            return null;
        }

        /**

         */
      /**  @ApiMethod(name = "DetailsUpdate", path = "DetailsUpdate")
        public void Update(@Named("A_Details_ID") String Details_ID, @Named("B_Name") String Name, @Named("C_Location") String Location, @Named("D_Vote_Location") String Vote_Location,
                           @Named("E_Start_Date") String Start_Date, @Named("F_End_Date") String End_Date, @Named("G_All_Day_Time") String All_Day_Time,
                           @Named("H_Start_Time") String Start_Time, @Named("I_End_Time") String End_Time, @Named("J_Vote_Time") String Vote_Time,
                           @Named("K_Description") String Description, @Named("L_Image_Path") String Image_Path, @Named("M_Update_Time") String Update_Time) {
            try {
                MySQL_Util.update(Table_Detailss.Table_Name, Table_Detailss.getAllFields_Except_Details_ID(),
                        new String[]{Name, Location, Vote_Location, Start_Date, End_Date, All_Day_Time, Start_Time, End_Time, Vote_Time,
                                Description, Image_Path, Update_Time}, new String[]{Table_Detailss.Details_ID}, new String[]{Details_ID});

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
         * Deletes the specified {@code Details}.
         *
         * @param Details_ID the ID of the entity to delete
         * @throws NotFoundException if the {@code id} does not correspond to an existing
         *                           {@code Details}
         */
       /** @ApiMethod(name = "DetailsDelete", path = "DetailsDelete")
        public void Delete(@Named("Details_ID") String Details_ID) {
            try {
                MySQL_Util.delete(Table_Details.Table_Name, new String[]{Table_Details.Details_ID}, new String[]{Details_ID}, new int[]{1});
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
         * Deletes the specified {@code Details}.
         *
         * @param Details_ID the ID of the entity to delete
         * @throws NotFoundException if the {@code id} does not correspond to an existing
         *                           {@code Details}
         */
       /** @ApiMethod(name = "DetailsUpdateField", path = "DetailsUpdateField")
        public void Update_Filed(@Named("A_Details_ID") String Details_ID, @Named("B_Filed") String Filed, @Named("C_Update") String Update) {
            try {
                MySQL_Util.update(Table_Details.Table_Name, new String[]{Filed},
                        new String[]{Update}, new String[]{Table_Details.Details_ID}, new String[]{Details_ID});
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



/**
 package com.example.some_lie.backend.apis;

 import com.example.some_lie.backend.utils.Constans.Constants;
 import com.example.some_lie.backend.utils.Constans.Table_Events_Users;
 import com.example.some_lie.backend.utils.MySQL_Util;
 import com.google.api.server.spi.config.Api;
 import com.google.api.server.spi.config.ApiClass;
 import com.google.api.server.spi.config.ApiMethod;
 import com.google.api.server.spi.config.ApiNamespace;
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
 @Api(name = "ezvent", version = "v1",
 namespace = @ApiNamespace(
 ownerDomain = Constants.API_OWNER,
 ownerName = Constants.API_OWNER,
 packagePath = Constants.API_PACKAGE_PATH
 )
 )
 @ApiClass(resource = "eventUser",
 clientIds = {
 Constants.ANDROID_CLIENT_ID,
 Constants.IOS_CLIENT_ID,
 Constants.WEB_CLIENT_ID},
 audiences = {Constants.AUDIENCE_ID}
 )
 public class Event_User_Endpoint {

 private static final Logger logger = Logger.getLogger(Event_User_Endpoint.class.getName());


 /**
  * This inserts a new <code>Event_User</code> object.
  *
  * @param User_ID The object to be added.
 * @return The object to be added.
 */
/** @ApiMethod(name = "EventUserDeleteByUser", path = "EventUserDeleteByUser")
public void DeleteBy_User(@Named("User_ID") String User_ID) {
try {
MySQL_Util.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.User_ID}, new String[]{User_ID}, null);
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
 * This inserts a new <code>Event_User</code> object.
 *
 * @param Event_ID The object to be added.
 * @return The object to be added.
 */
/**  @ApiMethod(name = "EventUserDeleteByEvent", path = "EventUserDeleteByEvent")
public void DeleteBy_Event(@Named("Event_ID") String Event_ID) {
try {
MySQL_Util.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{Event_ID}, null);
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
 * @param Event_ID
 * @param User_ID
 */
/**  @ApiMethod(name = "EventUserDelete", path = "EventUserDelete")
public void Delete(@Named("A_Event_ID") String Event_ID, @Named("B_User_ID") String User_ID) {
try {
MySQL_Util.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
new String[]{Event_ID, User_ID}, new int[]{1});
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
 * This inserts a new <code>Event_User</code> object.
 *
 * @param User_ID The object to be added.
 * @return The object to be added.
 */
/**   @ApiMethod(name = "EventUserInsert", path = "EventUserInsert")
public void Insert(@Named("AEvent_ID") String Event_ID, @Named("BUser_ID") String User_ID, @Named("CAttending") String attending, @Named("DPermission") String permission) {
try {
MySQL_Util.insert(Table_Events_Users.Table_Name, new String[]{Event_ID, User_ID, attending, permission});

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
 * This inserts a new <code>Event_User</code> object.
 *
 * @param User_ID The object to be added.
 * @return The object to be added.
 */
/** @ApiMethod(name = "EventUserGetEvents", path = "EventUserGetEvents")
public ArrayList<Event_User> GetEvents(@Named("User_ID") String User_ID) {
ArrayList<Event_User> eventUserArrayList = new ArrayList<>();
try {
ResultSet rs = MySQL_Util.select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.User_ID}, new String[]{User_ID}, null);
while (rs.next()) {
eventUserArrayList.add(new Event_User(rs.getString(Table_Events_Users.Event_ID), rs.getString(Table_Events_Users.User_ID),
rs.getString(Table_Events_Users.Attending), rs.getString(Table_Events_Users.Permission)));
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
return eventUserArrayList;
}

/**
 * This method gets the <code>Event_User</code> object associated with the specified <code>id</code>.
 *
 * @param Event_ID The id of the object to be returned.
 * @return The <code>Event_User</code> associated with <code>id</code>.
 */
/**    @ApiMethod(name = "EventUserGetUsers", path = "EventUserGetUsers")
public ArrayList<Event_User> GetUsers(@Named("Event_ID") String Event_ID) {
ArrayList<Event_User> eventUserArrayList = new ArrayList<>();
try {
ResultSet rs = MySQL_Util.select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{Event_ID}, null);
while (rs.next()) {
eventUserArrayList.add(new Event_User(rs.getString(Table_Events_Users.Event_ID), rs.getString(Table_Events_Users.User_ID),
rs.getString(Table_Events_Users.Attending), rs.getString(Table_Events_Users.Permission)));
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
return eventUserArrayList;
}

/**
 * This method gets the <code>Event_User</code> object associated with the specified <code>id</code>.
 *
 * @param User_ID The id of the object to be returned.
 * @return The <code>Event_User</code> associated with <code>id</code>.
 */
/**   @ApiMethod(name = "EventUserGet", path = "EventUserGet")
public Event_User Get(@Named("AEvent_ID") String Event_ID, @Named("BUser_ID") String User_ID) {
Event_User event_user = new Event_User();
try {
ResultSet rs = MySQL_Util.select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID},
new String[]{Event_ID, User_ID}, null);
event_user.setEvent_ID(rs.getString(Table_Events_Users.Event_ID));
event_user.setUser_ID(rs.getString(Table_Events_Users.User_ID));
event_user.setAttending(rs.getString(Table_Events_Users.Attending));
event_user.setPermission(rs.getString(Table_Events_Users.Permission));
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
return event_user;
}

 @ApiMethod(name = "EventUserUpdateAttending", path = "EventUserUpdateAttending")
 public void UpdateAttending(@Named("A_Event_ID") String Event_ID, @Named("B_User_ID") String User_ID, @Named("C_Attending") String Attending) {
 try {
 MySQL_Util.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Attending},
 new String[]{Attending},
 new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, new String[]{Event_ID, User_ID});

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

 @ApiMethod(name = "EventUserUpdatePermission", path = "EventUserUpdatePermission")
 public void UpdatePermission(@Named("A_Event_ID") String Event_ID, @Named("B_User_ID") String User_ID, @Named("C_Permission") String Permission) {
 try {
 MySQL_Util.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Permission}, new String[]{Permission},
 new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, new String[]{Event_ID, User_ID});

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