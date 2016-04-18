package server;

/**
 * Created by pinhas on 11/10/2015.
 */
public interface ServerAsyncResponse {
     void processFinish(String... output);
     String currentLocation();
     void closeActivity();
}
