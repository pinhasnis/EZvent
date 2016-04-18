package server.Event;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;

import server.CloudEndpointBuilderHelper;
import utils.Constans.Constants;

/**
 * Created by Ravid on 11/10/2015.
 */
public class Event_AsyncTask_delete  extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Event_AsyncTask_delete(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
            myApiService.deleteEvent(params[0], Constants.MY_User_ID).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

