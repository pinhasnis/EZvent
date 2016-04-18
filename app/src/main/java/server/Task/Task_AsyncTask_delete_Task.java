package server.Task;

import android.content.Context;
import android.os.AsyncTask;


import rnp.backend.ezvent.Ezvent;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 20/10/2015.
 */
public class Task_AsyncTask_delete_Task extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Task_AsyncTask_delete_Task(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
           // myApiService.taskDelete(params[0],params[1]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

