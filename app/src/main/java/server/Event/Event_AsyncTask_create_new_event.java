package server.Event;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.StorageObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import rnp.backend.ezvent.Ezvent;
import rnp.backend.ezvent.model.Event;
import server.CloudEndpointBuilderHelper;

/**
 * Created by Ravid on 25/09/2015.
 */
public class Event_AsyncTask_create_new_event extends AsyncTask<Event, Void, Void> {
    private static Ezvent myApiService = null;
    private Context context;
    //private final GcsService gcsService =
            //GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
    private static Storage storageService;

    public Event_AsyncTask_create_new_event(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Event... params) {
        if (myApiService == null) { // Only do this once
            myApiService = CloudEndpointBuilderHelper.getEndpoints();
        }

        try {
            myApiService.newEvent(params[0]).execute();
           /*
            File f = new File(params[8]);
            if (f.exists()) {
                String file_name = ipath.getPath();
                //cloudStorage.uploadFile(Constants.bucket_name, params[8], context, file_name);

            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void uploadStream(
            String name, String contentType, InputStream stream, String bucketName)
            throws IOException, GeneralSecurityException {
        InputStreamContent contentStream = new InputStreamContent(contentType, stream);
        StorageObject objectMetadata = new StorageObject()
                // Set the destination object name
                .setName(name)
                        // Set the access control list to publicly read-only
                .setAcl(Arrays.asList(
                        new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        // Do the insert

        Storage client = getService();
        Storage.Objects.Insert insertRequest = client.objects().insert(
                bucketName, objectMetadata, contentStream);

        insertRequest.execute();
    }

    private static Storage getService() throws IOException, GeneralSecurityException {
        if (null == storageService) {
            GoogleCredential credential = GoogleCredential.getApplicationDefault();
            // Depending on the environment that provides the default credentials (e.g. Compute Engine,
            // App Engine), the credentials may require us to specify the scopes we need explicitly.
            // Check for this case, and inject the Cloud Storage scope if required.
            if (credential.createScopedRequired()) {
                credential = credential.createScoped(StorageScopes.all());
            }
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            storageService = new Storage.Builder(httpTransport, null, credential)
                    .setApplicationName("test").build();
        }
        return storageService;
    }

}


