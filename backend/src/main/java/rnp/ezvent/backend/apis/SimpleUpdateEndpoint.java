package rnp.ezvent.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import rnp.ezvent.backend.models.SimpleUpdate;
import rnp.ezvent.backend.utils.Constans.Constants;
import rnp.ezvent.backend.utils.Constans.Table_Chat;
import rnp.ezvent.backend.utils.Constans.Table_Events_Users;
import rnp.ezvent.backend.utils.Constans.Table_Tasks;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Date;
import rnp.ezvent.backend.utils.Constans.Table_Vote_Location;
import rnp.ezvent.backend.utils.MySQL_Util;

import static rnp.ezvent.backend.utils.MySQL_Util.addToLog;

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
@ApiClass(resource = "simple_update",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class SimpleUpdateEndpoint {

    @ApiMethod(name = "updateEvent", path = "updateEvent")
    public void Update(SimpleUpdate simpleUpdate) {
        try {
            //update sql
            boolean take_task = true;
            String[] values = simpleUpdate.getValues();
            MessagingEndpoint msg = new MessagingEndpoint();
            switch (simpleUpdate.getAction()) {
                case Constants.Simple_Update_chat_insert: {
                    java.sql.ResultSet resultSet = MySQL_Util.select(null, simpleUpdate.getChat_Table_name(), new String[]{Table_Chat.Message_ID, Table_Chat.User_ID}
                            , new String[]{values[Table_Chat.Message_ID_num], values[Table_Chat.User_ID_num]}, new int[]{1});
                    if (resultSet != null)
                        MySQL_Util.insert(simpleUpdate.getChat_Table_name(), simpleUpdate.getValues());
                    break;
                }
                case Constants.Simple_Update_chat_delete: {
                    java.sql.ResultSet resultSet = MySQL_Util.select(null, simpleUpdate.getChat_Table_name(), new String[]{Table_Chat.Message_ID, Table_Chat.User_ID}
                            , new String[]{values[Table_Chat.Message_ID_num], values[Table_Chat.User_ID_num]}, new int[]{1});
                    if (resultSet != null)
                        MySQL_Util.delete(simpleUpdate.getChat_Table_name(), new String[]{Table_Chat.Message_ID, Table_Chat.User_ID},
                                new String[]{values[Table_Chat.Message_ID_num], values[Table_Chat.User_ID_num]}, new int[]{1});
                    break;
                }
                case Constants.Simple_Update_take_task: {
                    java.sql.ResultSet resultSet = MySQL_Util.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number,
                            Table_Tasks.subTask_ID_Number}, new String[]{values[Table_Tasks.Event_ID_num], values[Table_Tasks.Task_ID_Number_num],
                            values[Table_Tasks.subTask_ID_Number_num]}, new int[]{1});
                    //Check if the task is not signed.
                    if (resultSet.getString(Table_Tasks.User_ID_num).equals(Constants.UnCheck))
                        MySQL_Util.update(Table_Tasks.Table_Name, new String[]{Table_Tasks.User_ID}, new String[]{values[Table_Tasks.User_ID_num]},
                                new String[]{Table_Tasks.Event_ID, Table_Tasks.Task_ID_Number, Table_Tasks.subTask_ID_Number},
                                new String[]{values[Table_Tasks.Event_ID_num], values[Table_Tasks.Task_ID_Number_num], values[Table_Tasks.subTask_ID_Number_num]});
                    else {
                        take_task = false;
                        //update user that other user take the task
                        if (!resultSet.getString(Table_Tasks.User_ID_num).equals(values[Table_Tasks.User_ID_num])) {
                            String set_msg = simpleUpdate.getAction() + values[Table_Tasks.Event_ID_num] + "|" + values[Table_Tasks.Task_ID_Number_num] +
                                    "|" + values[Table_Tasks.subTask_ID_Number_num] + "|" + resultSet.getString(Table_Tasks.User_ID_num);//action|Event_ID|Task_ID|subTask_ID|User_ID.
                            msg.sendMessage(set_msg, values[Table_Tasks.User_ID_num]);
                        }
                    }
                    break;
                }
                case Constants.Simple_Update_vote_for_date: {
                    java.sql.ResultSet resultSet = MySQL_Util.select(null, Table_Vote_Date.Table_Name, new String[]{Table_Vote_Date.Event_ID, Table_Vote_Date.Vote_ID, Table_Vote_Date.User_ID}
                            , new String[]{values[Table_Vote_Date.Event_ID_num], values[Table_Vote_Date.Vote_ID_num], values[Table_Vote_Date.User_ID_num]}, new int[]{1});
                    if (resultSet != null)
                        MySQL_Util.insert(Table_Vote_Date.Table_Name, values);
                    break;
                }
                case Constants.Simple_Update_vote_for_location: {
                    java.sql.ResultSet resultSet = MySQL_Util.select(null, Table_Vote_Location.Table_Name, new String[]{Table_Vote_Location.Event_ID, Table_Vote_Location.Vote_ID,
                            Table_Vote_Location.User_ID}, new String[]{values[Table_Vote_Location.Event_ID_num], values[Table_Vote_Location.Vote_ID_num],
                            values[Table_Vote_Location.User_ID_num]}, new int[]{1});
                    if (resultSet != null)
                        MySQL_Util.insert(Table_Vote_Location.Table_Name, values);
                    break;
                }
                case Constants.Simple_Update_attending: {//0 - Event_ID, 1 - User_ID, 2 - Attending.
                    MySQL_Util.update(Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Attending}, new String[]{values[Table_Events_Users.Attending_num]},
                            new String[]{Table_Events_Users.Event_ID, Table_Events_Users.User_ID}, new String[]{values[Table_Events_Users.Event_ID_num],values[Table_Events_Users.User_ID_num]});
                    break;
                }
            }

            //update users
            if (take_task) {
                String str_simple_update = simpleUpdate.getAction() + "";
                for (String str : simpleUpdate.getValues())
                    str_simple_update += "|" + str;
                for (int i = 0; i < simpleUpdate.getUsers_ID().size(); i++) {
                    msg.sendMessage(str_simple_update, simpleUpdate.getUsers_ID().get(i));
                }
            }
        } catch (Exception e) {
            addToLog(e);
        }
    }

}

