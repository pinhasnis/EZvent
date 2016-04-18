package server.Vote_Location;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 08/03/2016.
 */
public class Vote_Location_AsyncTask_insert extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Vote_Location_AsyncTask_insert(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
          //  myApiService.voteLocationInsert(params[0], params[1], params[2], params[3]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}