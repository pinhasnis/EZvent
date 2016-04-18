package server.Event;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;

import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 03/03/2016.
 */
public class Event_AsyncTask_update_field extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Event_AsyncTask_update_field(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
          //  myApiService.eventUpdateField(params[0], params[1], params[2]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}