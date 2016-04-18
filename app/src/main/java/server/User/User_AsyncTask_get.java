package server.User;

import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.User;

import rnp.backend.ezvent.Ezvent;
import server.CloudEndpointBuilderHelper;
import utils.Constans.Constants;
import utils.Constans.Table_Users;
import utils.Event_Helper_Package.Contacts_List;
import utils.sqlHelper;

/**
 * Created by Ravid on 19/10/2015.
 */
public class User_AsyncTask_get extends AsyncTask<String, Void, User> {
    private static Ezvent myApiService = null;

    public User_AsyncTask_get() {
    }

    @Override
    protected User doInBackground(String... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
        try {
            return myApiService.userGet(params[0]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(User result) {
        if (result != null) {
            sqlHelper.insert(Table_Users.Table_Name, new String[]{result.getUserID(), result.getNickname(), Constants.Yes});
            //Add User to Contacts_List.
            if (!Constants.MY_User_ID.equals(result.getUserID()))
                Contacts_List.contacts.put(result.getUserID(), result.getNickname());
        }
    }
}