package server.Vote_Date;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 06/03/2016.
 */
public class Vote_Date_AsyncTask_delete_vote_user_id extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Vote_Date_AsyncTask_delete_vote_user_id(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
          //  myApiService.voteDateDelete(params[0], params[1], params[2]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}