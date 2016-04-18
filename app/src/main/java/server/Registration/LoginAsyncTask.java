package server.Registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.RegistrationRecord;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import rnp.ezvent.MainActivity;
import server.CloudEndpointBuilderHelper;
import server.ServerAsyncResponse;
import utils.Constans.Constants;

/**
 * Created by pinhas on 12/10/2015.
 */
public class LoginAsyncTask extends AsyncTask<String, Void, RegistrationRecord> {
    private static Ezvent myApiService = null;
    private ServerAsyncResponse delegate;
    private GoogleCloudMessaging gcm;
    private Context context;

    public LoginAsyncTask(ServerAsyncResponse delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected RegistrationRecord doInBackground(String... params) {
        if(myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }
        try {
            String msg = "";
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(Constants.SENDER_ID);
            SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.MY_PREFS_NAME, context.MODE_PRIVATE).edit();
            editor.putString("GCM", regId);
            editor.commit();
            return myApiService.authentication(params[0], params[1], regId).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(RegistrationRecord result) {
        String message = "LoginAsyncTask.onPostExecute: Cannot connect the server.. please try again later";
        if(result != null) {
            message = result.getUserID();
            delegate.processFinish(message, result.getNickname());
        }else{
            delegate.processFinish(Constants.Not_Register);
        }
    }
}

