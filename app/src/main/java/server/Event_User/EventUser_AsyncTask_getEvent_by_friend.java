package server.Event_User;/*
package server.Event_User;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.some_lie.backend.brings.Brings;
import com.example.some_lie.backend.brings.model.EventUserCollection;

import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 11/10/2015.
 */
/*
public class EventUser_AsyncTask_getEvent_by_friend extends AsyncTask<String, Void, EventUserCollection> {
    private static Brings myApiService = null;
    private Context context;
    private EventUserCollection eventUserCollection;

    public EventUserCollection getEventFriendCollection() {
        return eventUserCollection;
    }

    public EventUser_AsyncTask_getEvent_by_friend(Context context) {
        this.context = context;
    }

    @Override
    protected EventUserCollection doInBackground(String... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
        try {
            return  myApiService.eventUserGetEvents(params[0]).execute();

            /* EventFriendCollection eventFriendCollection = myApiService.eventFriendGetEvents(params[0]).execute();
            EventFriend eventFriend;
            for(int i = 0;i<eventFriendCollection.getItems().size();i++){
                eventFriend = eventFriendCollection.getItems().get(i);
            }
            */
  /*      } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(EventUserCollection result) {
        //Toast.makeText(context,result.getMessage(),Toast.LENGTH_LONG).show();
        /*Toast.makeText(context,result.getFrom(),Toast.LENGTH_LONG).show();
        */
/*
if(result != null) {
            eventUserCollection = result;
            Toast.makeText(context, "הודעה נשלחה", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context,"ההודעה לא נשלחה",Toast.LENGTH_LONG).show();
        }
    }
}
*/