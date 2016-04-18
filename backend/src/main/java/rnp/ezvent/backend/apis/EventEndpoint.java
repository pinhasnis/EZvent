package rnp.ezvent.backend.apis;

import rnp.ezvent.backend.models.Event;
import rnp.ezvent.backend.utils.Constans.Constants;
import rnp.ezvent.backend.utils.Constans.Table_Details;
import rnp.ezvent.backend.utils.Constans.Table_Events_Users;
import rnp.ezvent.backend.utils.Constans.Table_Tasks;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Date;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Location;
import rnp.ezvent.backend.utils.MySQL_Util;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;

import java.sql.ResultSet;

import static rnp.ezvent.backend.utils.EndpointUtil.byteSizeUTF8;
import static rnp.ezvent.backend.utils.MySQL_Util.addToLog;
import static rnp.ezvent.backend.utils.MySQL_Util.select;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */


@Api(name = "ezvent", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "event",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class EventEndpoint {

    @ApiMethod(name = "newEvent", path = "newEvent")
    public void Insert(Event event) {

        try {
            String userId = event.getId().substring(0, event.getId().length() - 13);

            // inset event_details to sql
            MySQL_Util.insert(Table_Details.Table_Name, event.getId(), event.getDetails());

            // insert event users
            MySQL_Util.insertAll(Table_Events_Users.Table_Name, event.getId(), event.getEvent_users());

            // insert event task
            if (event.getTasks().size() != 0)
                MySQL_Util.insertAll(Table_Tasks.Table_Name, event.getId(), event.getTasks());

            // insert vote location
            if (event.getVote_locations().size() != 0)
                MySQL_Util.insertAll(Table_Vote_Location.Table_Name, event.getId(), event.getVote_locations());

            // insert vote date
            if (event.getVote_dates().size() != 0)
                MySQL_Util.insertAll(Table_Vote_Date.Table_Name, event.getId(), event.getVote_dates());

            ChatEndpoint chat = new ChatEndpoint();
            chat.CreateByEvent("Chat_" + event.getId());

            // if the data is less then 4kb send all data now else send a notification to request a new event
            String data = Constants.New_Event + event.toString();
            int byteSize = byteSizeUTF8(data);
            MessagingEndpoint msg = new MessagingEndpoint();
            String message = Constants.Request_New_Event + event.getId();
            if (byteSize != -1 && byteSize < 4000) {// 4000 bytes = 4kb
                message = data;
            }

            for (int i = 0; i < event.getEvent_users().size() - 1; i++) {
                if (!userId.equals(event.getEvent_users().get(i)[Table_Events_Users.User_ID_num - Constants.index_object_sql_diff]))
                    msg.sendMessage(message, event.getEvent_users().get(i)[Table_Events_Users.User_ID_num - Constants.index_object_sql_diff]);
            }

        } catch (Exception e) {
            addToLog(e);
        }
    }

    @ApiMethod(name = "requestEvent", path = "requestEvent")
    public Event request(@Named("event_id") String event_id) {
        try {
            Event event = new Event();
            event.setId(event_id);
            // get details from sql
            event.setDetailsFromSql(select(null, Table_Details.Table_Name, new String[]{Table_Details.Event_ID}, new String[]{event_id}, null));
            // get event users from sql
            event.setEvent_usersFromSql(select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{event_id}, null));
            // get tasks from sql
            event.setTasksFromSql(select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{event_id}, null));
            // get vote date from sql
            event.setVote_datesFromSql(select(null, Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{event_id}, null));
            // get vote location from sql
            event.setVote_locationsFromSql(select(null, Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{event_id}, null));

            return event;
        } catch (Exception e) {
            addToLog(e);
        }
        return null;
    }

    @ApiMethod(name = "deleteEvent", path = "deleteEvent")
    public void delete(@Named("event_id") String event_id, @Named("user_id") String user_id) {
        try {
            // get event users from sql
            ResultSet users = select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{event_id}, null);

            //send message to all users to delete the event
            MessagingEndpoint msg = new MessagingEndpoint();
            String current_user = "";
            while (users.next()) {
                current_user = users.getString(Table_Events_Users.User_ID);
                if (!current_user.equals(user_id))
                    msg.sendMessage(Constants.Delete_Event + event_id, current_user);
            }

            // delete details from sql
            MySQL_Util.delete(Table_Details.Table_Name, new String[]{Table_Details.Event_ID}, new String[]{event_id}, null);
            // delete event users from sql
            MySQL_Util.delete(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID}, new String[]{event_id}, null);
            // delete tasks from sql
            MySQL_Util.delete(Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID}, new String[]{event_id}, null);
            // delete vote date from sql
            MySQL_Util.delete(Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID}, new String[]{event_id}, null);
            // get vote location from sql
            MySQL_Util.delete(Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID}, new String[]{event_id}, null);
        } catch (Exception e) {
            addToLog(e);
        }
    }
}