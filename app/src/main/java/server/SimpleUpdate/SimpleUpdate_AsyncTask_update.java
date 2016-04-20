package server.SimpleUpdate;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.SimpleUpdate;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 20/04/2016.
 */
public class SimpleUpdate_AsyncTask_update extends AsyncTask<SimpleUpdate, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public SimpleUpdate_AsyncTask_update(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(SimpleUpdate... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
            myApiService.simpleUpdate(params[0]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}