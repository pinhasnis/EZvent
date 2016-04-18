package server.Vote_Date;

import android.content.Context;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 10/03/2016.
 */
public class Vote_Date_AsyncTask_deleteByVote_ID extends AsyncTask<String, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;

    public Vote_Date_AsyncTask_deleteByVote_ID(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
           // myApiService.voteDateDeleteByVoteID(params[0], params[1]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}