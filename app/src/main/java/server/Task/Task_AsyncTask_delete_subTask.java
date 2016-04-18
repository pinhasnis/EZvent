package server.Task;

import android.content.Context;
import android.os.AsyncTask;


import rnp.backend.ezvent.Ezvent;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 23/02/2016.
 */
public class Task_AsyncTask_delete_subTask extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Task_AsyncTask_delete_subTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
           // myApiService.subTaskDelete(params[0], params[1], params[2]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
