package server.Vote_Location;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 08/03/2016.
 */
public class Vote_Location_AsyncTask_deleteByEvent extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Vote_Location_AsyncTask_deleteByEvent(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
         //   myApiService.voteLocationDeleteByEventID(params[0]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}