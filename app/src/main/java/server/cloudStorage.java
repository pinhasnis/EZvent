package server;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import rnp.ezvent.R;
import utils.Constans.Constants;

/**
 * Simple wrapper around the Google Cloud Storage API
 */
public class cloudStorage {

    private static Properties properties;
    private static Storage storage;
    private static String STORAGE_SCOPE = Constants.cloudStorageScope;
    private static final String PROJECT_ID_PROPERTY = Constants.SENDER_ID;
    private static final String APPLICATION_NAME_PROPERTY = Constants.projectName;
    private static final String ACCOUNT_ID_PROPERTY = Constants.cloudACCOUNT_ID_PROPERTY;
    private static final String PRIVATE_KEY_PATH_PROPERTY = Constants.cloudPRIVATE_KEY_PATH_PROPERTY;
    private static Context context;
    /**
     * Uploads a file to a bucket. Filename and content type will be based on
     * the original file.
     *
     * @param bucketName
     *            Bucket where file will be uploaded
     * @param filePath
     *            Absolute path of the file to upload
     * @throws Exception
     */
    public static void uploadFile(String bucketName, String filePath,Context _context,String file_name)
            throws Exception {
        context = _context;
        Storage storage = getStorage();

        StorageObject object = new StorageObject();
        object.setBucket(bucketName);

        File file = new File(filePath);

        InputStream stream = new FileInputStream(file);
        try {
            String contentType = URLConnection
                    .guessContentTypeFromStream(stream);
            InputStreamContent content = new InputStreamContent(contentType,
                    stream);

            Storage.Objects.Insert insert = storage.objects().insert(
                    bucketName, null, content);
            insert.setName(file_name);

            insert.execute();
        } finally {
            stream.close();
            File dir = new File (Constants.imageSaveLocation);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            File _file = new File(dir.getAbsolutePath() + "/" + file_name+".jpg");


            Storage.Objects.Get get = storage.objects().get(bucketName, file_name);
            FileOutputStream _stream = new FileOutputStream(_file);
            try {
                get.executeAndDownloadTo(_stream);
            }catch(Exception e){
                e.printStackTrace();
            } finally {
                _stream.close();
            }
        }
    }

    public static void downloadFile(String bucketName, String fileName,Context _context) throws Exception {
        if(context == null) {
            context = _context;
        }
        File dir = new File (Constants.imageSaveLocation);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir.getAbsolutePath() + "/" + fileName+".jpg");

        Storage storage = getStorage();

        Storage.Objects.Get get = storage.objects().get(bucketName, fileName);
        FileOutputStream stream = new FileOutputStream(file);
        try {
            get.executeAndDownloadTo(stream);
        }catch(Exception e) {
            e.printStackTrace();
        }
        finally
        {
            stream.close();
        }
    }

    /**
     * Deletes a file within a bucket
     *
     * @param bucketName
     *            Name of bucket that contains the file
     * @param fileName
     *            The file to delete
     * @throws Exception
     */
    public static void deleteFile(String bucketName, String fileName)
            throws Exception {

        Storage storage = getStorage();

        storage.objects().delete(bucketName, fileName).execute();
    }

    /**
     * Creates a bucket
     *
     * @param bucketName
     *            Name of bucket to create
     * @throws Exception
     */
    public static void createBucket(String bucketName) throws Exception {

        Storage storage = getStorage();

        Bucket bucket = new Bucket();
        bucket.setName(bucketName);

        storage.buckets().insert(
                getProperties().getProperty(PROJECT_ID_PROPERTY), bucket).execute();
    }

    /**
     * Deletes a bucket
     *
     * @param bucketName
     *            Name of bucket to delete
     * @throws Exception
     */
    public static void deleteBucket(String bucketName) throws Exception {

        Storage storage = getStorage();

        storage.buckets().delete(bucketName).execute();
    }

    /**
     * Lists the objects in a bucket
     *
     * @param bucketName bucket name to list
     * @return Array of object names
     * @throws Exception
     */
    public static List<String> listBucket(String bucketName) throws Exception {

        Storage storage = getStorage();

        List<String> list = new ArrayList<String>();

        List<StorageObject> objects = storage.objects().list(bucketName).execute().getItems();
        if(objects != null) {
            for(StorageObject o : objects) {
                list.add(o.getName());
            }
        }

        return list;
    }

    /**
     * List the buckets with the project
     * (Project is configured in properties)
     *
     * @return
     * @throws Exception
     */
    public static List<String> listBuckets() throws Exception {

        Storage storage = getStorage();

        List<String> list = new ArrayList<String>();

        List<Bucket> buckets = storage.buckets().list(getProperties().getProperty(PROJECT_ID_PROPERTY)).execute().getItems();
        if(buckets != null) {
            for(Bucket b : buckets) {
                list.add(b.getName());
            }
        }

        return list;
    }

    private static Properties getProperties() throws Exception {

        if (properties == null) {
            properties = new Properties();
            InputStream stream = cloudStorage.class
                    .getResourceAsStream("/cloudstorage.properties");
            try {
                properties.load(stream);
            } catch (IOException e) {
                throw new RuntimeException(
                        "cloudstorage.properties must be present in classpath",
                        e);
            } finally {
                stream.close();
            }
        }
        return properties;
    }

    private static Storage getStorage() throws Exception {

        if (storage == null) {

            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();//new JacksonFactory();

            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(context.getResources().openRawResource(R.raw.test),
                    Constants.cloudPassword.toCharArray());

            PrivateKey key = (PrivateKey) keystore.getKey("privatekey", Constants.cloudPassword.toCharArray());

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountPrivateKey(key)
                    .setServiceAccountId(Constants.cloudEmail)
                    .setServiceAccountScopes(Collections.singleton(STORAGE_SCOPE))
                    .build();
            credential.refreshToken();
            storage = new Storage.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("test").build();
        }

        return storage;
    }
}