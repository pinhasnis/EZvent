package rnp.ezvent.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.io.IOException;
import java.util.ArrayList;

import rnp.ezvent.backend.models.UpdateEvent;
import rnp.ezvent.backend.utils.Constans.Constants;
import rnp.ezvent.backend.utils.Constans.Table_Details;
import rnp.ezvent.backend.utils.Constans.Table_Events_Users;
import rnp.ezvent.backend.utils.Constans.Table_Tasks;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Date;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Location;
import rnp.ezvent.backend.utils.MySQL_Util;

import static rnp.ezvent.backend.utils.EndpointUtil.byteSizeUTF8;
import static rnp.ezvent.backend.utils.MySQL_Util.addToLog;

/**
 * Created by Pinhas on 4/18/2016.
 */
public class UpdateEventEndpoint {
    @Api(name = "ezvent", version = "v1",
            namespace = @ApiNamespace(
                    ownerDomain = Constants.API_OWNER,
                    ownerName = Constants.API_OWNER,
                    packagePath = Constants.API_PACKAGE_PATH
            )
    )
    @ApiClass(resource = "update_event",
            clientIds = {
                    Constants.ANDROID_CLIENT_ID,
                    Constants.IOS_CLIENT_ID,
                    Constants.WEB_CLIENT_ID},
            audiences = {Constants.AUDIENCE_ID}
    )
    public class EventEndpoint {

        private void handleNewUsers(UpdateEvent update_event) throws IOException {
            ArrayList<String[]> newUsers = update_event.getEvent_users()[Constants.update_event_new];
            sendToUsers(update_event, newUsers);
        }

        private void sendToUsers(UpdateEvent update_event, ArrayList<String[]> users) throws IOException {
            if (users != null) {
                String str_event_update = update_event.toString();
                int byteSize = byteSizeUTF8(str_event_update);
                if (byteSize != -1 && byteSize < 4000) {// 4000 bytes = 4kb
                    str_event_update = Constants.Request_New_Event + update_event.getId();
                }
                MessagingEndpoint msg = new MessagingEndpoint();
                for (int i = 0; i < users.size(); i++) {
                    msg.sendMessage(str_event_update, users.get(i)[Table_Events_Users.User_ID_num - Constants.index_object_sql_diff]);
                }
            }
        }

        @ApiMethod(name = "updateEvent", path = "updateEvent")
        public void Update(UpdateEvent update_event) {

            try {
                String userId = update_event.getUser_id();
                handleSQLUpdate(update_event);
                handleNewUsers(update_event);
                handleExistUsers(update_event);
                handleRemovedUsers(update_event);
            } catch (Exception e) {
                addToLog(e);
            }
        }

        private void handleRemovedUsers(UpdateEvent update_event) throws IOException {
            if (update_event.getEvent_users()[Constants.update_event_delete] != null) {
                MessagingEndpoint msg = new MessagingEndpoint();
                for (String user : update_event.getEvent_users()[Constants.update_event_delete].get(Table_Events_Users.User_ID_num - Constants.index_object_sql_diff)) {
                    msg.sendMessage(Constants.Delete_Event + update_event.getId(), user);
                }
            }
        }

        private void handleExistUsers(UpdateEvent update_event) throws Exception {
            update_event.clearUnchangedData();
            ArrayList<String[]> old_users = new ArrayList<>();
            old_users.addAll(update_event.getEvent_users()[Constants.update_event_not_change]);
            old_users.addAll(update_event.getEvent_users()[Constants.update_event_update]);
            sendToUsers(update_event, old_users);
        }

        private void handleSQLUpdate(UpdateEvent update_event) throws Exception {
            //update details.
            if (update_event.isDetails_changed() == Constants.True)
                MySQL_Util.update(Table_Details.Table_Name, Table_Details.getAllFields_Except_Event_ID(), update_event.getDetails(),
                        new String[]{Table_Details.Event_ID}, new String[]{update_event.getId()});
            update_sql_table(update_event.getEvent_users(), Table_Events_Users.Table_Name, update_event.getId());
            update_sql_table(update_event.getTasks(), Table_Tasks.Table_Name, update_event.getId());
            update_sql_table(update_event.getVote_dates(), Table_Vote_Date.Table_Name, update_event.getId());
            update_sql_table(update_event.getVote_locations(), Table_Vote_Location.Table_Name, update_event.getId());


        }

        private void update_sql_table(ArrayList<String[]>[] update, String table_name, String event_id) throws Exception {
            if (update != null) {
                ArrayList<String[]> insert = new ArrayList<>();
                ArrayList<String[]> delete = new ArrayList<>();

                if (update[Constants.update_event_update] != null) {
                    insert.addAll(update[Constants.update_event_update]);
                    delete.addAll(update[Constants.update_event_update]);
                }
                if (update[Constants.update_event_delete] != null) {
                    delete.addAll(update[Constants.update_event_delete]);
                    MySQL_Util.deleteAll(table_name, event_id, delete);
                }
                if (update[Constants.update_event_new] != null) {
                    insert.addAll(update[Constants.update_event_new]);
                    MySQL_Util.insertAll(table_name, event_id, insert);
                }
            }
        }

    }
}
