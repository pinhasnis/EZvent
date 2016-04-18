package server.Registration;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.CollectionResponseRegistrationRecord;
import rnp.backend.ezvent.model.RegistrationRecord;

import java.util.ArrayList;
import java.util.List;

import server.CloudEndpointBuilderHelper;
import utils.Constans.Constants;

/**
 * Created by pinhas on 14/10/2015.
 */
public class CheckUsersRegistrationAsyncTask extends AsyncTask<ArrayList<String>, Void, List<RegistrationRecord>> {
    private static Ezvent myApiService = null;
    private Context context;
    //private ArrayList<String> phones;
    public CheckUsersRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<RegistrationRecord> doInBackground(ArrayList<String>... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
        try {
            String new_regId = "!";
            String old_regId = "!";
            if(params[1] != null){
                new_regId = params[1].get(0);
                old_regId = params[1].get(1);
            }
            //this.phones = params[0];
            CollectionResponseRegistrationRecord result = myApiService.checkUserRegistration(Constants.MY_User_ID, Constants.MY_User_Password, new_regId, old_regId).execute();
            return result.getItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(List<RegistrationRecord> result) {
        /*if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                sqlHelper.update(Table_Users.Table_Name,new String[]{Table_Users.User_ID, Table_Users.Register},
                        new String[]{result.get(i).getUserID(),Constants.Yes},new String[]{Table_Users.Phone},new String[]{result.get(i).getPhone()});
            }
        }*/
    }
}
