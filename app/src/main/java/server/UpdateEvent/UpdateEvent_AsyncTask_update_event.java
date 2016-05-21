package server.UpdateEvent;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Random;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.UpdateEvent;
import server.CloudEndpointBuilderHelper;
import utils.Constans.Constants;

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


        int backoff = Constants.BACKOFF_INITIAL_DELAY;
        Random random = new Random();
        boolean finish = true;
        for (int attemp = 0; attemp < Constants.MaxSendAttemp; attemp++) {
            finish = true;
            try {
                myApiService.updateEvent(params[0]).execute();
            } catch (Exception e) {
                finish = false;
            }
            finally {
                if(finish){
                    attemp = Constants.MaxSendAttemp;
                }
                else{
                    try {
                        int sleepTime = backoff / 2 + random.nextInt(backoff);
                        Thread.sleep(sleepTime);
                        if (2 * backoff < Constants.MAX_BACKOFF_DELAY) {
                            backoff *= 2;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(!finish){
            //TODO in case message didn't send add it to some database and try again latter..
        }
        /*
        try {
            myApiService.updateEvent(params[0]).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        return null;
    }
}
