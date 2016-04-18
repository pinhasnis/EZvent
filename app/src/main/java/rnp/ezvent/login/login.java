package rnp.ezvent.login;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import rnp.ezvent.MainActivity;
import rnp.ezvent.R;
import server.Registration.LoginAsyncTask;
import server.ServerAsyncResponse;
import utils.Constans.Constants;
import utils.Constans.Table_Users;
import utils.Event_Helper_Package.Contacts_List;
import utils.sqlHelper;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by pinhas on 11/10/2015.
 */
public class login extends AppCompatActivity implements ServerAsyncResponse {
    private TextView tvName;
    private TextView tvPass;
    private EditText etName;
    private EditText etPass;
    private Button login;
    private Button register;
    private GoogleCloudMessaging gcm;

    ProgressDialog progressBar;
    private boolean progressBarStatus;
    private Handler progressBarHandler = new Handler();

    private final static int READ_CONTACTS_RESULT = 104;

    private SharedPreferences sharedPreferences;
    private FrameLayout permissionSuccess;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;
    private View coordinatorLayoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        sqlHelper.setContext(this);
        sqlHelper.createALLTables();
        permissionSuccess = (FrameLayout) findViewById(R.id.permissionSuccess);
        login();
        tvName = (TextView) findViewById(R.id.tv_login_name);
        tvPass = (TextView) findViewById(R.id.tv_login_password);
        etName = (EditText) findViewById(R.id.et_login_name);
        etPass = (EditText) findViewById(R.id.et_login_password);
        login = (Button) findViewById(R.id.b_login_login);
        register = (Button) findViewById(R.id.b_login_register);

    }

    @Override
    public void onPause() {
        super.onPause();
        if ((progressBar != null))
            progressBar.dismiss();
        progressBarStatus = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressBar != null) {
            progressBar.dismiss();
            progressBar = null;
        }
    }

    public void signIn(View view) {
        progress(view.getContext(), "Sign in ...");
        new LoginAsyncTask(this, this).execute(etName.getText().toString(), etPass.getText().toString());
    }

    public void register(View view) {
        Intent Register = new Intent(this, Registration.class);
        startActivity(Register);
        finish();
    }

    public void progress(Context context, String message) {
        progressBar = new ProgressDialog(context);
        progressBar.setMessage(message);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        progressBarStatus = true;
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus) {

                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            //progressBar.setProgress(progressBarStatus);
                        }
                    });
                }

                // ok, file is downloaded,
                /*
                if (progressBarStatus == false) {

                    // sleep 2 seconds, so that you can see the 100%
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // close the progress bar dialog
                    progressBar.dismiss();
                }*/
            }
        }).start();
    }

    private void login() {

        sharedPreferences = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = sharedPreferences.getString(Constants.r_user_key, null);
        if (restoredText != null) {
            Constants.MY_User_Nickname = sharedPreferences.getString(Constants.MY_User_Nickname_Key, Constants.no_name_defined);
            Constants.MY_User_ID = sharedPreferences.getString(Constants.MY_User_ID_Key, Constants.no_name_defined);//"No name defined" is the default value.
            Constants.MY_User_Password = sharedPreferences.getString(Constants.MY_User_Password_Key, Constants.no_name_defined);

            //if (gcm == null) {
             //   gcm = GoogleCloudMessaging.getInstance(this);
            //}
            String regId = null;
            try {
                InstanceID instanceID = InstanceID.getInstance(this);
                regId = instanceID.getToken(Constants.SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
               // regId = gcm.register(Constants.SENDER_ID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String gcmUpdate = null;
            if (regId != null) {
                gcmUpdate = sharedPreferences.getString("GCM", "NO Value");
                if (gcmUpdate != regId) {
                    SharedPreferences.Editor editor = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("GCM", regId);
                    editor.apply();
                } else {
                    gcmUpdate = null;
                }

            }
            try {
                if (addPermission("READ_CONTACTS")) {
                    progress(this, "Synchronizing Contacts ...");
                    getUsers(regId, gcmUpdate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //TODO get updates from server

        }
    }

    private void getUsers(String gcmUpdate, String oldGCM) {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        //Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);
        ArrayList<String>[] list;
        HashMap<String, String> data = new HashMap<>();
        ArrayList<String> ph = new ArrayList<>();
        phones.getCount();
        int index = 0;
        while (phones.moveToNext()) {
            String Nickname = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String User_ID = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .replaceAll("-", "").replaceAll(" ", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\+", "");
            if (User_ID.charAt(0) == '0') {
                User_ID = Constants.country_code + User_ID.substring(1);
            }
            if (data.get(Nickname) == null) {
                data.put(Nickname, User_ID);
                list = sqlHelper.select(null, Table_Users.Table_Name, new String[]{Table_Users.User_ID}, new String[]{User_ID}, new int[]{1});
                if (list[0].isEmpty()) {
                    sqlHelper.insert(Table_Users.Table_Name, new String[]{User_ID, Nickname, Constants.No});
                }
                ph.add(User_ID);
            }
            //  String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            //   String name2 = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME));
            //   if(name2 != null)
            //    Toast.makeText(this,name2+": "+email,Toast.LENGTH_LONG).show();
        }
        //Construct Contacts_List.
        ArrayList<String>[] dbUsers = sqlHelper.select(null, Table_Users.Table_Name, null, null, null);
        for (int i = 0; i < dbUsers[0].size(); i++) {
            Contacts_List.contacts.put(dbUsers[Table_Users.User_ID_num].get(i), dbUsers[Table_Users.Nickname_num].get(i));
            Contacts_List.TreeMap_contacts.put(dbUsers[Table_Users.Nickname_num].get(i).toLowerCase(), dbUsers[Table_Users.User_ID_num].get(i));
        }
        //Toast.makeText(this,count+"",Toast.LENGTH_LONG).show();
        phones.close();
        //Check friends registration.
        /*
        ArrayList<String> gu = null;
        if (gcmUpdate != null) {
            gu = new ArrayList<>();
            gu.add(gcmUpdate);
            gu.add(oldGCM);
        }
        int size = ph.size() / 100 + 1;
        ArrayList<String>[] arrPh = new ArrayList[size];
        int count = 0;
        for (int i = 0; i < size && count < ph.size(); i++) {
            arrPh[i] = new ArrayList<>();
            for (int j = 0; j < 100 && count < ph.size(); j++, count++) {
                arrPh[i].add(ph.get(count));
            }
            new CheckUsersRegistrationAsyncTask(this).execute(arrPh[i], gu);
        }
        */
        Intent mainActivity = new Intent(this, MainActivity.class);
        progressBarStatus = false;
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void processFinish(String... output) {
        if (output[0].equals(Constants.Not_Register)) {
            Toast.makeText(this, "User is not exist.. please register!", Toast.LENGTH_LONG).show();
            progressBarStatus = false;
        } else {
            String User_ID = output[0];
            String password = etPass.getText().toString();
            SharedPreferences.Editor editor = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString(Constants.r_user_key, Constants.r_user);
            editor.putString(Constants.MY_User_ID_Key, User_ID);
            editor.putString(Constants.MY_User_Password_Key, password);
            editor.putString(Constants.MY_User_Nickname_Key, output[1]);
            editor.commit();
            login();
        }
    }

    @Override
    public String currentLocation() {
        return Constants.login;
    }

    @Override
    public void closeActivity() {
        //TODO ?
    }

    //Handle permissions.

    @TargetApi(Build.VERSION_CODES.M)
    public boolean addPermission(String permission) {
        boolean hasPermission = false;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        permissionSuccess.setVisibility(View.GONE);
        ArrayList<String> permissions = new ArrayList<>();
        int resultCode = 0;
        switch (permission) {
            case "READ_CONTACTS":
                permissions.add(READ_CONTACTS);
                resultCode = READ_CONTACTS_RESULT;
                break;
        }

        //filter out the permissions we have already accepted
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        permissionsRejected = findRejectedPermissions(permissions);

        if (permissionsToRequest.size() > 0) {//we need to ask for permissions
            //but have we already asked for them?
            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), resultCode);
            //mark all these as asked..
            for (String perm : permissionsToRequest) {
                markAsAsked(perm);
            }
        } else {
            hasPermission = true;
            //show the success banner
            if (permissionsRejected.size() < permissions.size()) {
                //this means we can show success because some were already accepted.
                permissionSuccess.setVisibility(View.VISIBLE);
            }

            if (permissionsRejected.size() > 0) {
                //we have none to request but some previously rejected..tell the user.
                //It may be better to show a dialog here in a prod application
                Snackbar
                        .make(coordinatorLayoutView, String.valueOf(permissionsRejected.size()) + " permission(s) were previously rejected", Snackbar.LENGTH_LONG)
                        .setAction("Allow to Ask Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (String perm : permissionsRejected) {
                                    clearMarkAsAsked(perm);
                                }
                            }
                        })
                        .show();
            }
        }
        return hasPermission;
    }


    /**
     * This is the method that is hit after the user accepts/declines the
     * permission you requested. For the purpose of this example I am showing a "success" header
     * when the user accepts the permission and a snackbar when the user declines it.  In your application
     * you will want to handle the accept/decline in a way that makes sense.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case READ_CONTACTS_RESULT:
                if (hasPermission(READ_CONTACTS)) {
                    permissionSuccess.setVisibility(View.VISIBLE);
                    login();
                } else {
                    permissionsRejected.add(READ_CONTACTS);
                    makePostRequestSnack();
                }
                break;

        }

    }

    /**
     * a method that will centralize the showing of a snackbar
     */
    private void makePostRequestSnack() {
        Snackbar
                .make(coordinatorLayoutView, String.valueOf(permissionsRejected.size()) + " permission(s) were rejected", Snackbar.LENGTH_LONG)
                .setAction("Allow to Ask Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String perm : permissionsRejected) {
                            clearMarkAsAsked(perm);
                        }
                    }
                })
                .show();
    }

    /**
     * method that will return whether the permission is accepted. By default it is true if the user is using a device below
     * version 23
     *
     * @param permission
     * @return
     */
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    /**
     * method to determine whether we have asked
     * for this permission before.. if we have, we do not want to ask again.
     * They either rejected us or later removed the permission.
     *
     * @param permission
     * @return
     */
    private boolean shouldWeAsk(String permission) {
        return (sharedPreferences.getBoolean(permission, true));
    }

    /**
     * we will save that we have already asked the user
     *
     * @param permission
     */
    private void markAsAsked(String permission) {
        sharedPreferences.edit().putBoolean(permission, false).apply();
    }

    /**
     * We may want to ask the user again at their request.. Let's clear the
     * marked as seen preference for that permission.
     *
     * @param permission
     */
    private void clearMarkAsAsked(String permission) {
        sharedPreferences.edit().putBoolean(permission, true).apply();
    }


    /**
     * This method is used to determine the permissions we do not have accepted yet and ones that we have not already
     * bugged the user about.  This comes in handle when you are asking for multiple permissions at once.
     *
     * @param wanted
     * @return
     */
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * this will return us all the permissions we have previously asked for but
     * currently do not have permission to use. This may be because they declined us
     * or later revoked our permission. This becomes useful when you want to tell the user
     * what permissions they declined and why they cannot use a feature.
     *
     * @param wanted
     * @return
     */
    private ArrayList<String> findRejectedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && !shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * Just a check to see if we have marshmallows (version 23)
     *
     * @return
     */
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

}
