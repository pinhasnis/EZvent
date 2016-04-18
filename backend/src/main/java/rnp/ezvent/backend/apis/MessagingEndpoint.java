/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package rnp.ezvent.backend.apis;

import rnp.ezvent.backend.utils.Constans.Constants;
import rnp.ezvent.backend.utils.Constans.Table_Users_Devices;
import rnp.ezvent.backend.utils.MySQL_Util;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint to send messages to devices registered with the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(name = "ezvent", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "messaging",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());
    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    //TODO improve this !!!!
    @ApiMethod(name = "SendMessage", path = "SendMessage")
    public void sendMessage(@Named("B_Message") String Message, @Named("C_To") String To) throws IOException {
        try {
            if (Message == null || Message.trim().length() == 0) {
                log.warning("Not sending message because it is empty");
                return;
            }
            // crop longer messages
            if (Message.length() > 1000) {
                Message = Message.substring(0, 1000) + "[...]";
            }
            Sender sender = new Sender(API_KEY);
            Message msg = new Message.Builder().addData("Message", Message).build();//The format of the message is affecting the way GcmIntentService class handle the message.
            ArrayList<String> regId = checkIfUserExist(To);
            for (String id : regId) {
                if (!id.equals("NOT FOUND")) {
                    Result result = sender.send(msg, id, 5);
                    if (result.getMessageId() != null) {
                        String canonicalRegId = result.getCanonicalRegistrationId();
                        if (canonicalRegId != null) {
                            update(To, id, canonicalRegId);
                        }
                    } else {
                        //TODO
                    }
                } else {
                    log.warning("NOT FOUND");
                }
            }
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

    private void update(String User_ID, String prevId, String RegId) {
        try {
            MySQL_Util.update(Table_Users_Devices.Table_Name, new String[]{Table_Users_Devices.Registration_ID}, new String[]{RegId},
                    new String[]{Table_Users_Devices.User_ID, Table_Users_Devices.Registration_ID}, new String[]{User_ID, prevId});
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

    private ArrayList<String> checkIfUserExist(String user) {
        ArrayList<String> regID = new ArrayList<>();
        try {
            ResultSet rs = MySQL_Util.select(null, Table_Users_Devices.Table_Name, new String[]{Table_Users_Devices.User_ID}, new String[]{user}, null);
            while (rs.next()) {
                regID.add(rs.getString(2));
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
        return regID;
    }

    private boolean legalMessage(String msg) {
        boolean ok = false;
        if (msg.contains(" - ")) {
            String content[] = msg.split(" - ");
            if (content.length == 2) {
                if (content[0].equals("M")) {//message
                    ok = true;
                } else if (content[0].equals("NE") || content[0].equals("UE")) {//event
                    String[] data = content[1].split(", ");
                    if (data.length == 2) {
                        ok = true;
                    }
                }
            }
        }
        return ok;
    }
}
