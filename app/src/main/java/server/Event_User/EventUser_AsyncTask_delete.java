package server.Event_User;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;

import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 18/10/2015.
 */
public class EventUser_AsyncTask_delete extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public EventUser_AsyncTask_delete(Context context) {
            this.context = context;
            }

    @Override
    protected Void doInBackground(String... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
          //  myApiService.eventUserDelete(params[0], params[1]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
