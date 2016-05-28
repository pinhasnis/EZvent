package rnp.ezvent;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;

import server.CloudEndpointBuilderHelper;
import server.Messageing.GcmIntentService;
import server.ServerAsyncResponse;
import utils.Constans.Constants;
import utils.Constans.Table_Events;
import utils.Constans.Table_Events_Users;
import utils.Constans.Table_Tasks;
import utils.Event_Helper_Package.Event_Helper;
import utils.Helper;
import utils.bitmapHelper;
import utils.sqlHelper;

//import rnp.ezvent.backend

public class MainActivity extends AppCompatActivity implements ServerAsyncResponse {
    @Override
    public String currentLocation() {
        return Constants.mainActivity;
    }

    @Override
    public void closeActivity() {
        //TODO ?
    }

    /**
     * Time limit for the application to wait on a response from Play Services.
     */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String MY_PREFS_NAME = "Brings";

    private ImageButton ibAdd;
    private TextView tvSearch;
    private SearchView search;
    //private static ArrayList<String> users_names;
    //private static ArrayList<Integer> IDS;
    private static ArrayList<String> Event_IDs;
    private static final String PROPERTY_REG_ID = "registrationId";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    //private Brings EZventApi;
    /**
     * Google Cloud Messaging API.
     */
    private GoogleCloudMessaging gcm;

    /**
     * The registration ID.
     */
    private String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GcmIntentService.delegate = this;
        //EZventApi =
                CloudEndpointBuilderHelper.getEndpoints();
        //users_names = new ArrayList<>();
        //IDS = new ArrayList<>();
        Event_IDs = new ArrayList<>();
        //Set text font for the app name;
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/GreatVibes_Regular.ttf");
        TextView application_name = (TextView) findViewById(R.id.application_name);
        application_name.setTypeface(type, Typeface.BOLD);
        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "Normal",
                        "Light View",
                }));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // When the given dropdown item is selected, show its contents in the
                // container view.
                setList();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commitAllowingStateLoss();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
/*
        tvSearch = (TextView) findViewById(R.id.tvSearch);
        ListView listview;
        listview = (ListView) findViewById(R.id.lvMain);
        mode_view = Constants.Light_View;
        old_setList();
        */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), New_Event.class);
                Bundle data = new Bundle();
                data.putString("Event_ID", Constants.New_Event);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        /*ImageButton new_event = (ImageButton) findViewById(R.id.new_event);
        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), New_Event.class);
                Bundle data = new Bundle();
                data.putString("Event_ID", Constants.New_Event);
                intent.putExtras(data);
                startActivity(intent);
            }
        });*/
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getString(Constants.Notification_Event_Id)!=null){
            Intent IEvent = new Intent(this,Event.class);
            IEvent.putExtras(bundle);
            startActivity(IEvent);
        }
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @param applicationContext the Application context.
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(final Context applicationContext) {
        final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            //       Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs
                .getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(getApplicationContext());
        if (registeredVersion != currentVersion) {
            //     Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    /**
     * @param applicationContext the Application context.
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(final Context
                                                        applicationContext) {
        // This sample app persists the registration ID in shared preferences,
        // but how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Returns the application version.
     *
     * @param context the Application context.
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    private void sql() {

        //sqlHelper.delete(Table_Events.Table_Name,new String[]{Table_Events.Event_ID},new String[]{"@ - []"}, null);
        ArrayList<String>[] sqlresult = sqlHelper.select(null, Table_Events.Table_Name, null, null, null);
        if (sqlresult != null)
            for (String str : sqlresult[0]) {
                //String[] s = str.split(" - ");
                //users_names.add(s[0]);
                //IDS.add(Integer.parseInt(s[1]));
                Event_IDs.add(str);
            }

    }

    /**
     * Checks if Google Play Services are installed and if not it initializes
     * opening the dialog to allow user to install Google Play Services.
     *
     * @return a boolean indicating if the Google Play Services are available.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    {/*
    private void setOnClick() {

        final Intent new_event = new Intent(this, newEvent.class);

        ibAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new_event);
            }

        });

        tvSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }

        });
    }
    */
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_add) {
            //final Intent new_event = new Intent(this, newEvent.class);
            //startActivity(new_event);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        GcmIntentService.delegate = this;
        setList();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public void setList() {
        //users_names.clear();
        //IDS.clear();
        Event_IDs.clear();
        sql();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param applicationContext application's context.
     * @param registrationId     registration ID
     */
    private void storeRegistrationId(final Context applicationContext,
                                     final String registrationId) {
        final SharedPreferences prefs = getGCMPreferences(applicationContext);
        int appVersion = getAppVersion(applicationContext);
        //   Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, registrationId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    @Override
    public void processFinish(String... output) {
        if (output[0].equals(Constants.Update_Activity)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Update Event list when new event received.
                    setList();
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                    if (recyclerView != null) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            });

        }
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public void setDropDownViewTheme(Resources.Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }

        @Override
        public Resources.Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            CustomAdapter mAdapter;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1: {
                    mAdapter = new CustomAdapter(Event_IDs, Constants.Normal_View);
                    break;
                }
                case 2: {
                    mAdapter = new CustomAdapter(Event_IDs, Constants.Light_View);
                    break;
                }
                default: {
                    mAdapter = new CustomAdapter(Event_IDs, Constants.Light_View);
                    break;
                }
            }
            mRecyclerView.setAdapter(mAdapter);

            return rootView;
        }
    }


}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    // private static ArrayList<String> users_names;
    //private static ArrayList<Integer> IDS;
    private static ArrayList<String> Event_IDs;
    private String Mode;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param mode String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(ArrayList<String> Event_IDs, String mode) {
        this.Mode = mode;
        //this.IDS = IDS;
        //this.users_names = users_names;
        this.Event_IDs = Event_IDs;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View view;
        switch (Mode) {
            case Constants.Normal_View: {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.main_big_event, viewGroup, false);
                break;
            }
            case Constants.Light_View: {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.main_small_event, viewGroup, false);
                break;
            }
            default: {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.main_big_event, viewGroup, false);
                break;
            }
        }

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        View view = viewHolder.itemView;
        final String Event_ID = Event_IDs.get(position);
        ArrayList<String>[] dbEvent = sqlHelper.select(null, Table_Events.Table_Name, new String[]{Table_Events.Event_ID},
                new String[]{Event_ID}, new int[]{1});
        switch (Mode) {
            case Constants.Normal_View: {
                ImageView iv = (ImageView) view.findViewById(R.id.imageView);
                TextView name = (TextView) view.findViewById(R.id.event_name);
                TextView location = (TextView) view.findViewById(R.id.location);
                TextView date = (TextView) view.findViewById(R.id.date);
                TextView attending = (TextView) view.findViewById(R.id.attending);
                TextView tasks = (TextView) view.findViewById(R.id.tasks);
                name.setText(dbEvent[Table_Events.Name_num].get(0));
                if (dbEvent[Table_Events.Vote_Location_num].get(0).equals(Constants.Yes))
                    location.setText("Open for voting");
                else if (dbEvent[Table_Events.Location_num].get(0).equals(""))
                    location.setText("Location having been set yet");
                else
                    location.setText(dbEvent[Table_Events.Location_num].get(0));
                if (dbEvent[Table_Events.Vote_Time_num].get(0).equals(Constants.Yes))
                    date.setText("Open for voting");
                else {
                    String date_text = Helper.date_text_view(dbEvent[Table_Events.Start_Date_num].get(0), dbEvent[Table_Events.End_Date_num].get(0),
                            dbEvent[Table_Events.All_Day_Time_num].get(0), dbEvent[Table_Events.Start_Time_num].get(0), dbEvent[Table_Events.End_Time_num].get(0));
                    date.setText(date_text);
                }
                //Set up attending for the event.
                ArrayList<String>[] dbEvent_Users = sqlHelper.select(null, Table_Events_Users.Table_Name, new String[]{Table_Events_Users.Event_ID},
                        new String[]{Event_ID}, null);
                int yes = 0, maybe = 0, no = 0, did_not_replay = 0;
                String attending_string;
                for (int i = 0; i < dbEvent_Users[Table_Events_Users.Attending_num].size(); i++) {
                    attending_string = dbEvent_Users[Table_Events_Users.Attending_num].get(i);
                    switch (attending_string) {
                        case Constants.Yes:
                            yes++;
                            break;
                        case Constants.Maybe:
                            maybe++;
                            break;
                        case Constants.No:
                            no++;
                            break;
                        case Constants.Did_Not_Replay:
                            did_not_replay++;
                            break;
                    }
                }
                attending_string = "";
                if (yes > 0) {
                    attending_string = yes + " coming";
                }
                if (maybe > 0) {
                    if (attending_string.length() > 0)
                        attending_string += ", ";
                    attending_string += maybe + " maybe";
                }
                if (did_not_replay > 0) {
                    if (attending_string.length() > 0)
                        attending_string += ", ";
                    attending_string += did_not_replay + " didn't replay";
                }
                attending.setText(attending_string);
                //Set up tasks.
                ArrayList<String>[] dbEvent_Tasks = sqlHelper.select(null, Table_Tasks.Table_Name, new String[]{Table_Tasks.Event_ID, Table_Tasks.subTask_ID_Number, Table_Tasks.Task_Type,
                        Table_Tasks.User_ID}, new String[]{Event_ID, 0 + "", Constants.Group_Task, Constants.UnCheck}, null);
                String tasks_string = "";
                int open_tasks;
                if (dbEvent_Tasks != null) {
                    open_tasks = dbEvent_Tasks[Table_Tasks.User_ID_num].size();
                } else {
                    open_tasks = 0;
                }
                if (open_tasks > 0) {
                    if (open_tasks == 1)
                        tasks_string = "one task open";
                    else
                        tasks_string = open_tasks + " tasks are open";
                } else {
                    tasks_string = "all tasks are complete";
                }
                tasks.setText(tasks_string);
                //Set up Image.
                String Image_Path = dbEvent[Table_Events.Image_Path_num].get(0);
                Bitmap bitmap = bitmapHelper.decodeSampledBitmapFromFile(Image_Path, 100, 100);
                if (bitmap != null) {
                    // RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    //img.setCircular(true);
                    //iv.setImageDrawable(img);
                }
                //iv.setImageBitmap(bitmap);
                break;
            }
            case Constants.Light_View: {
                ImageView iv = (ImageView) view.findViewById(R.id.imageView);
                TextView tvName = (TextView) view.findViewById(R.id.event_name);
                TextView tvDate = (TextView) view.findViewById(R.id.date);
                tvName.setText(dbEvent[Table_Events.Name_num].get(0));
                String date_text = Helper.date_text_view(dbEvent[Table_Events.Start_Date_num].get(0), dbEvent[Table_Events.End_Date_num].get(0),
                        dbEvent[Table_Events.All_Day_Time_num].get(0), dbEvent[Table_Events.Start_Time_num].get(0), dbEvent[Table_Events.End_Time_num].get(0));
                tvDate.setText(date_text);
                //String Image_Path = dbEvent[Table_Events.Image_Path_num].get(0);
                Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.view1);
                //if (bitmap != null) {
                RoundedBitmapDrawable img = RoundedBitmapDrawableFactory.create(view.getResources(), bitmap);
                img.setCircular(true);
                iv.setImageDrawable(img);
                //}
                //iv.setImageBitmap(bitmap);
                break;
            }
        }
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                String my_permission = Helper.getMyPermission(Event_ID);
                //final String Event_ID = users_names.get(position) + " - " + IDS.get(position);
                if (my_permission.equals(Constants.Owner)) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete/Leave Event")
                            .setMessage("Are you sure you want to Delete/Leave this Event?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Event_Helper.load_event(Event_ID);
                                    Helper.simple_update(v.getContext(), Constants.Delete_Event, new String[]{Event_ID});
                                    //users_names.remove(position);
                                    //IDS.remove(position);
                                    //notifyItemRangeRemoved(position, 1);
                                    Event_IDs.remove(Event_ID);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNeutralButton("Leave", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Event_Helper.load_event(Event_ID);
                                    Helper.simple_update(v.getContext(), Constants.Leave_Event, new String[]{Event_ID, Constants.MY_User_ID});
                                    //users_names.remove(position);
                                    //IDS.remove(position);
                                    //notifyItemRangeRemoved(position, 1);
                                    Event_IDs.remove(Event_ID);
                                    notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Leave Event")
                            .setMessage("Are you sure you want to Leave this Event?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Helper.simple_update(v.getContext(), Constants.Leave_Event, new String[]{Event_ID, Constants.MY_User_ID});
                                    //users_names.remove(position);
                                    //IDS.remove(position);
                                    Event_IDs.remove(Event_ID);
                                    //int pos = users_names.indexOf(Event_ID);
                                    //notifyItemRangeRemoved(pos, 1);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return false;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Event.class);
                Bundle data = new Bundle();
                //int pos = users_names.indexOf(Event_ID);
                data.putString("Event_ID", Event_ID);
                intent.putExtras(data);
                v.getContext().startActivity(intent);
            }
        });
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Event_IDs.size();
    }


}
