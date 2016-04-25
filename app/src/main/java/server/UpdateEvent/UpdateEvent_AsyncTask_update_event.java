package server.UpdateEvent;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.UpdateEvent;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 24/04/2016.
 */
public class UpdateEvent_AsyncTask_update_event extends AsyncTask<UpdateEvent, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public UpdateEvent_AsyncTask_update_event(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(UpdateEvent... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
            myApiService.updateEvent(params[0]).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
