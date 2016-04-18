package server.Event_User;/*
package server.Event_User;

import android.os.AsyncTask;

import com.example.some_lie.backend.brings.Brings;
//import com.example.some_lie.backend.brings.model.EventUserCollection;

import server.CloudEndpointBuilderHelper;
import server.ServerAsyncResponse;

/**
 * Created by Ravid on 11/10/2015.
 */
/*
public class EventUser_AsyncTask_getFriend_by_event extends AsyncTask<String, Void, EventUserCollection> {
    private static Brings myApiService = null;
    private ServerAsyncResponse delegate;


    public EventUser_AsyncTask_getFriend_by_event(ServerAsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected EventUserCollection doInBackground(String... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
        try {
           return myApiService.eventUserGetEvents(params[0]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(EventUserCollection result) {
        //delegate.EventFriendCollectionProcessFinish(result);
    }
}
*/