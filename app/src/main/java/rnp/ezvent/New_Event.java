package rnp.ezvent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import utils.Constans.Constants;
import utils.Constans.Table_Events;
import utils.CustomViewPager;
import utils.Event_Helper_Package.Contacts_List;
import utils.Event_Helper_Package.Event_Helper;
import utils.Event_Helper_Package.Friend_Helper;
import utils.Event_Helper_Package.Task_Helper;
import utils.Event_Helper_Package.Vote_Date_Helper;
import utils.Event_Helper_Package.Vote_Location_Helper;
import utils.Helper;

public class New_Event extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;
    private ImageView imageView;
    private ImageView details;
    private ImageView friends;
    private ImageView task;
    private TextView page_title;
    private ImageButton search;
    private TextView next_done;
    private static FloatingActionButton fab_task;
    private final static boolean apk23 = Build.VERSION.SDK_INT >= 23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        final Bundle b = getIntent().getExtras();
        if (b.getString("Event_ID").equals(Constants.New_Event)) {
            Event_Helper.clear();
            Event_Helper.newEvent_edit_mode = Constants.New_Event;
        } else {
            Event_Helper.make_copy();
            Event_Helper.newEvent_edit_mode = Constants.Update_Event;//Constants.Edit_Event;
        }
        imageView = (ImageView) findViewById(R.id.image);
        page_title = (TextView) findViewById(R.id.event_name);
        search = (ImageButton) findViewById(R.id.search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0: {
                        //imageView.setImageResource(R.drawable.create_event111);
                        page_title.setText("Event Details");
                        search.setVisibility(View.GONE);
                        //friends.setImageResource(R.drawable.not_glow_friend);
                        //task.setImageResource(R.drawable.not_glow_task);
                        break;
                    }
                    case 1: {
                        //imageView.setImageResource(R.drawable.create_event222);
                        page_title.setText("Invite Friends");
                        search.setVisibility(View.VISIBLE);
                        //friends.setImageResource(R.drawable.glow_friend);
                        //task.setImageResource(R.drawable.not_glow_task);
                        break;
                    }
                    case 2: {
                        //imageView.setImageResource(R.drawable.create_event333);
                        page_title.setText("Add Tasks");
                        search.setVisibility(View.GONE);
                        //friends.setImageResource(R.drawable.glow_friend);
                        //task.setImageResource(R.drawable.glow_task);
                        break;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setPagingEnabled(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                //Set view.
                toolbar.setVisibility(View.GONE);
                LinearLayout linearLayout_search = (LinearLayout) findViewById(R.id.linearLayout_search);
                linearLayout_search.setVisibility(View.VISIBLE);
                //Handle search.
                EditText editText = (EditText) findViewById(R.id.editText);
                editText.requestFocus();
                editText.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        String text_search = s.toString().toLowerCase();
                        RecyclerView recyclerview2 = (RecyclerView) findViewById(R.id.recyclerView2);
                        List<ExpandableListAdapter_New_Event_Friends.Item> data = new ArrayList<>();
                        for (Map.Entry<String, String> entry : filterPrefix(Contacts_List.TreeMap_contacts, text_search).entrySet()) {
                            data.add(new ExpandableListAdapter_New_Event_Friends.Item(ExpandableListAdapter_New_Event_Friends.User, entry.getValue()));
                        }
                        ExpandableListAdapter_New_Event_Friends expandableListAdapter_new_event_friends = (ExpandableListAdapter_New_Event_Friends) recyclerview2.getAdapter();
                        expandableListAdapter_new_event_friends.setData(data);
                        expandableListAdapter_new_event_friends.notifyDataSetChanged();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
            }
        });

        next_done = (TextView) findViewById(R.id.next_done);
        next_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (store_editText(mViewPager.getCurrentItem())) {
                    switch (mViewPager.getCurrentItem()) {
                        case 0: {
                            //Hide keyboard.
                            View view = getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            mViewPager.setCurrentItem(1);
                            break;
                        }
                        case 1: {
                            mViewPager.setCurrentItem(2);
                            next_done.setText("Done");
                            break;
                        }
                        case 2: {
                            final String[] details_pointer;
                            if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event))
                                details_pointer = Event_Helper.details;
                            else
                                details_pointer = Event_Helper.details_tmp;
                            new AlertDialog.Builder(v.getContext())
                                    .setTitle("Save Event")
                                    .setMessage("Are you sure you want to Save this Event?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String Event_ID;
                                            if (b.getString("Event_ID").equals(Constants.New_Event)) {
                                                Event_ID = Helper.create_event(getApplicationContext());
                                            } else {
                                                Event_ID = details_pointer[Table_Events.Event_ID_num];
                                                Helper.update_event(getApplicationContext(), Event_ID);
                                            }
                                            Intent intent = new Intent(getApplicationContext(), Event.class);
                                            Bundle data = new Bundle();
                                            data.putString("Event_ID", Event_ID);
                                            intent.putExtras(data);
                                            if (b.getString("Event_ID").equals(Constants.New_Event)) {
                                                startActivity(intent);
                                            }
                                            finish();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            break;
                        }
                    }
                }
            }
        });
    }

    public <V> SortedMap<String, V> filterPrefix(SortedMap<String, V> baseMap, String prefix) {
        if (prefix.length() > 0) {
            char nextLetter = (char) (prefix.charAt(prefix.length() - 1) + 1);
            String end = prefix.substring(0, prefix.length() - 1) + nextLetter;
            return baseMap.subMap(prefix, end);
        }
        return baseMap;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Check if their a search process.
        if (toolbar.getVisibility() == View.GONE) {
            //Clear search and set visibility.
            EditText editText = (EditText) findViewById(R.id.editText);
            editText.setText("");
            LinearLayout linearLayout_search = (LinearLayout) findViewById(R.id.linearLayout_search);
            linearLayout_search.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);

            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK && mViewPager.getCurrentItem() != 0) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                next_done.setText("Next");
                //do whatever you need for the hardware 'back' button
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a new_event_detail_description activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean store_editText(int page) {
        switch (page) {
            case 0: {
                final String[] details_pointer;
                if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event))
                    details_pointer = Event_Helper.details;
                else
                    details_pointer = Event_Helper.details_tmp;
                //Fill name description and location.
                EditText editText_name = (EditText) findViewById(R.id.editText_name);
                EditText editText_description = (EditText) findViewById(R.id.editText_description);
                EditText editText_location = (EditText) findViewById(R.id.editText_location);
                if (editText_name != null) {
                    details_pointer[Table_Events.Name_num] = editText_name.getText().toString();
                    details_pointer[Table_Events.Description_num] = editText_description.getText().toString();
                    details_pointer[Table_Events.Location_num] = editText_location.getText().toString();
                }
                //Set vote pointer.
                HashMap<Long, Vote_Date_Helper> vote_date_pointer;
                HashMap<Long, Vote_Location_Helper> vote_location_pointer;
                if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
                    vote_date_pointer = Event_Helper.vote_date;
                    vote_location_pointer = Event_Helper.vote_location;
                } else {
                    vote_date_pointer = Event_Helper.vote_date_tmp;
                    vote_location_pointer = Event_Helper.vote_location_tmp;

                }
                //Fill vote_location.
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_location);
                ExpandableListAdapter_New_Event_Vote_Location expandableListAdapter_new_event_vote_location = (ExpandableListAdapter_New_Event_Vote_Location) recyclerView.getAdapter();
                if (expandableListAdapter_new_event_vote_location != null) {
                    long vote_id = expandableListAdapter_new_event_vote_location.getVote_ID_num_has_focus();
                    View view = getCurrentFocus();
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        vote_location_pointer.get(vote_id).setDescription(editText.getText().toString());
                    }
                }
                boolean all_fill_date;
                boolean all_fill_time;
                Switch switcher_vote_time = (Switch) findViewById(R.id.switcher_time);
                //Check if vote option is on.
                if (details_pointer[Table_Events.Vote_Time_num].equals(Constants.Yes)) {
                    //Close voting if there are 0 votes.
                    if (vote_date_pointer.size() == 0) {
                        details_pointer[Table_Events.Vote_Time_num] = Constants.No;
                        switcher_vote_time.setChecked(false);
                    } else
                        //Check that all vote_date are filled.
                        for (Vote_Date_Helper vote_date_helper : vote_date_pointer.values()) {
                            all_fill_date = !vote_date_helper.getStart_Date().equals("dd/mm/yyy");//Fill "start date" automatically fill "end date".
                            all_fill_time = vote_date_helper.getAll_Day().equals(Constants.Yes) || !vote_date_helper.getStart_Time().equals("hh:mm");//Fill "start time" automatically fill "end time".
                            if (!(all_fill_date && all_fill_time)) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Please fill all vote date details", Toast.LENGTH_LONG).show();
                                    }
                                });
                                return false;
                            }
                        }
                }
                Switch switcher_vote_location = (Switch) findViewById(R.id.switcher_location);
                //Check if vote option is on.
                if (details_pointer[Table_Events.Vote_Location_num].equals(Constants.Yes)) {
                    //Close voting if there are 0 votes.
                    if (vote_location_pointer.size() == 0) {
                        details_pointer[Table_Events.Vote_Location_num] = Constants.No;
                        switcher_vote_location.setChecked(false);
                    } else
                        //Check that all vote_location are filled.
                        for (Vote_Location_Helper vote_location_helper : vote_location_pointer.values()) {
                            if (vote_location_helper.getDescription().length() == 0) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Please fill all vote location details", Toast.LENGTH_LONG).show();
                                    }
                                });
                                return false;
                            }
                        }
                }
                break;
            }
            case 2: {
                HashMap<Long, Task_Helper> task_pointer;
                if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
                    task_pointer = Event_Helper.task;
                } else {
                    task_pointer = Event_Helper.task_tmp;
                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView4);
                if (recyclerView != null) {
                    ExpandableListAdapter_New_Event_Tasks expandableListAdapter_new_event_tasks = (ExpandableListAdapter_New_Event_Tasks) recyclerView.getAdapter();
                    long task_id = expandableListAdapter_new_event_tasks.getTask_ID_num_has_focus();
                    long subTask_id = expandableListAdapter_new_event_tasks.getSubTask_ID_num_has_focus();
                    View view = getCurrentFocus();
                    if (view instanceof EditText) {
                        EditText editText = (EditText) view;
                        String task_description = editText.getText().toString();
                        if (subTask_id == 0) {
                            task_pointer.get(task_id).setDescription(task_description);
                        } else {
                            String mark = task_pointer.get(task_id).getSubTasks().get(subTask_id)[1];
                            task_pointer.get(task_id).getSubTasks().put(subTask_id, new String[]{task_description, mark});
                        }
                    }
                }
                //Check that all task ans subTask are filled.
                for (final Task_Helper task_helper : task_pointer.values()) {
                    if (task_helper.getDescription().length() == 0) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Please fill all Tasks names", Toast.LENGTH_LONG).show();
                            }
                        });
                        return false;
                    }
                    for (String[] subTask : task_helper.getSubTasks().values()) {
                        if (subTask[0].length() == 0) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Please fill all subTasks names of ths task: " + task_helper.getDescription(), Toast.LENGTH_LONG).show();
                                }
                            });
                            return false;
                        }
                    }

                }

                break;
            }
        }
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
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

        private void date(View v, final TextView date_view1, final TextView date_view2, final Boolean isStartDate) {
            final String[] details_pointer;
            if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event))
                details_pointer = Event_Helper.details;
            else
                details_pointer = Event_Helper.details_tmp;
            final Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.new_event_detail_date_dialog);
            dialog.setCancelable(true);
            final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
            Button done = (Button) dialog.findViewById(R.id.done);
            //Set initial date by previous selection if exist or by the current date.
            String current_date;
            if (isStartDate) {
                current_date = details_pointer[Table_Events.Start_Date_num];
                date_view1.setText(current_date);
            } else {
                current_date = details_pointer[Table_Events.End_Date_num];
                date_view2.setText(current_date);
            }
            if (!current_date.equals("dd/mm/yyyy")) {
                datePicker.updateDate(Integer.parseInt(current_date.split("/")[2]), Integer.parseInt(current_date.split("/")[1]) - 1, Integer.parseInt(current_date.split("/")[0]));
            }
            dialog.show();
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    int month = datePicker.getMonth() + 1;
                    String date = datePicker.getDayOfMonth() + "/" + month + "/" + datePicker.getYear();
                    //Check date is after today.
                    boolean ok_date = !Helper.Is_date1_after_date2(Helper.getCurrentDate(), date);
                    if (ok_date) {
                        if (isStartDate) {
                            details_pointer[Table_Events.Start_Date_num] = date;
                            details_pointer[Table_Events.End_Date_num] = date;
                            date_view1.setText(Helper.format_date(date));
                            date_view2.setText(Helper.format_date(date));
                            dialog.dismiss();
                        } else {
                            //Check end date is after start date.
                            ok_date = !Helper.Is_date1_after_date2(details_pointer[Table_Events.Start_Date_num], date);
                            String[] start_date = details_pointer[Table_Events.Start_Date_num].split("\\/");
                            if (ok_date) {
                                details_pointer[Table_Events.End_Date_num] = date;
                                date_view2.setText(Helper.format_date(date));
                                dialog.dismiss();
                            } else {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(v.getContext(), "Please choose date later than start date", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(v.getContext(), "Please choose date later than now", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }

        @TargetApi(Build.VERSION_CODES.M)
        private void time(View v, final TextView time_view1, final TextView time_view2, final Boolean isStartTime) {
            final String[] details_pointer;
            if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event))
                details_pointer = Event_Helper.details;
            else
                details_pointer = Event_Helper.details_tmp;
            final Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.new_event_detail_time_dialog);
            dialog.setCancelable(true);
            final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
            Button done = (Button) dialog.findViewById(R.id.done);
            //Set initial date by previous selection if exist or by the current date.
            String current_time;
            if (isStartTime) {
                current_time = details_pointer[Table_Events.Start_Time_num];
                time_view1.setText(Helper.format_time(current_time));
            } else {
                current_time = details_pointer[Table_Events.End_Time_num];
                time_view2.setText(Helper.format_time(current_time));
            }
            if (!current_time.equals("hh:mm")) {
                if (apk23) {
                    timePicker.setHour(Integer.parseInt(current_time.split(":")[0]));
                    timePicker.setMinute(Integer.parseInt(current_time.split(":")[1]));
                } else {
                    timePicker.setCurrentHour(Integer.parseInt(current_time.split(":")[0]));
                    timePicker.setCurrentMinute(Integer.parseInt(current_time.split(":")[1]));
                }
            }
            timePicker.setIs24HourView(true);
            dialog.show();
            done.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(final View v) {
                    String time = "";
                    if (apk23) {
                        time = timePicker.getHour() + ":" + timePicker.getMinute();
                    } else {
                        time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                    }
                    if (isStartTime) {
                        details_pointer[Table_Events.Start_Time_num] = time;
                        details_pointer[Table_Events.End_Time_num] = time;
                        time_view1.setText(Helper.format_time(time));
                        time_view2.setText(Helper.format_time(time));
                        dialog.dismiss();
                    } else {
                        //Check if start date is before end date.
                        boolean ok_date = Helper.Is_date1_after_date2(details_pointer[Table_Events.End_Date_num], details_pointer[Table_Events.Start_Date_num]);
                        //Check if start time is before end time.
                        if (ok_date || !Helper.Is_time1_after_time2(details_pointer[Table_Events.Start_Time_num], time)) {
                            details_pointer[Table_Events.End_Time_num] = time;
                            time_view2.setText(Helper.format_time(time));
                            dialog.dismiss();
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(v.getContext(), "Please choose time later than start time", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1: {
                    final View rootView = inflater.inflate(R.layout.fragment_new_event_details, container, false);
                    final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
                    final String[] details_pointer;
                    if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event))
                        details_pointer = Event_Helper.details;
                    else
                        details_pointer = Event_Helper.details_tmp;
                    EditText editText_name = (EditText) rootView.findViewById(R.id.editText_name);
                    EditText editText_description = (EditText) rootView.findViewById(R.id.editText_description);
                    final TextView date1 = (TextView) rootView.findViewById(R.id.date1);
                    final TextView date2 = (TextView) rootView.findViewById(R.id.date2);
                    final TextView time1 = (TextView) rootView.findViewById(R.id.time1);
                    final TextView time2 = (TextView) rootView.findViewById(R.id.time2);
                    final Switch all_day = (Switch) rootView.findViewById(R.id.all_day);
                    final Switch switcher_vote_time = (Switch) rootView.findViewById(R.id.switcher_time);
                    final RecyclerView recyclerView_date = (RecyclerView) rootView.findViewById(R.id.recyclerView_date);
                    final RelativeLayout relativeLayout_all_day = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_all_day);
                    final RelativeLayout relativeLayout_date = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_date);
                    final RelativeLayout relativeLayout_date_titles = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_date_titles);
                    final EditText editText_location = (EditText) rootView.findViewById(R.id.editText_location);
                    final Switch switcher_vote_location = (Switch) rootView.findViewById(R.id.switcher_location);
                    final RecyclerView recyclerView_location = (RecyclerView) rootView.findViewById(R.id.recyclerView_location);
                    final RelativeLayout relativeLayout_location = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_location);
                    final RelativeLayout relativeLayout_location_titles = (RelativeLayout) rootView.findViewById(R.id.relativeLayout_location_titles);
                    //Set all values.
                    editText_name.setText(details_pointer[Table_Events.Name_num]);
                    editText_description.setText(details_pointer[Table_Events.Description_num]);
                    if (details_pointer[Table_Events.Vote_Time_num].equals(Constants.Yes))
                        switcher_vote_time.setChecked(true);
                    else
                        switcher_vote_time.setChecked(false);
                    setSwitcher_time_view(recyclerView_date, relativeLayout_date_titles, relativeLayout_date, relativeLayout_all_day, date1, date2, all_day, time1, time2);
                    if (details_pointer[Table_Events.Vote_Location_num].equals(Constants.Yes))
                        switcher_vote_location.setChecked(true);
                    else
                        switcher_vote_location.setChecked(false);
                    setSwitcher_location_view(recyclerView_location, relativeLayout_location_titles, relativeLayout_location, editText_location);
                    //editText_location.setText(details_pointer[Table_Events.Location_num]);
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    });
                    //date.
                    date1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            date(v, date1, date2, true);
                        }
                    });
                    date2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            date(v, date1, date2, false);
                        }
                    });
                    time1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            time(v, time1, time2, true);
                        }
                    });
                    time2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            time(v, time1, time2, false);
                        }
                    });
                    all_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                time1.setVisibility(View.GONE);
                                time2.setVisibility(View.GONE);
                                details_pointer[Table_Events.All_Day_Time_num] = Constants.Yes;
                            } else {
                                time1.setVisibility(View.VISIBLE);
                                time2.setVisibility(View.VISIBLE);
                                details_pointer[Table_Events.All_Day_Time_num] = Constants.No;
                            }
                        }
                    });
                    switcher_vote_time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (switcher_vote_time.isChecked())
                                details_pointer[Table_Events.Vote_Time_num] = Constants.Yes;
                            else
                                details_pointer[Table_Events.Vote_Time_num] = Constants.No;
                            setSwitcher_time_view(recyclerView_date, relativeLayout_date_titles, relativeLayout_date, relativeLayout_all_day, date1, date2, all_day, time1, time1);
                        }
                    });
                    //location.
                    switcher_vote_location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (switcher_vote_location.isChecked())
                                details_pointer[Table_Events.Vote_Location_num] = Constants.Yes;
                            else
                                details_pointer[Table_Events.Vote_Location_num] = Constants.No;
                            setSwitcher_location_view(recyclerView_location, relativeLayout_location_titles, relativeLayout_location, editText_location);
                        }
                    });

                    return rootView;
                }
                case 2: {
                    View rootView = inflater.inflate(R.layout.fragment_new_event_friends, container, false);
                    //Set values.
                    RecyclerView recyclerview2 = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
                    recyclerview2.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                    List<ExpandableListAdapter_New_Event_Friends.Item> data = new ArrayList<>();
                    for (Map.Entry<String, String> entry : Contacts_List.TreeMap_contacts.entrySet()) {
                        data.add(new ExpandableListAdapter_New_Event_Friends.Item(ExpandableListAdapter_New_Event_Friends.User, entry.getValue()));
                    }
                    recyclerview2.setAdapter(new ExpandableListAdapter_New_Event_Friends(data));

                    /*
                    final RecyclerView recyclerview3 = (RecyclerView) rootView.findViewById(R.id.recyclerView3);
                    recyclerview3.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                    List<ExpandableListAdapter_New_Event_Friends.Item> data3 = new ArrayList<>();
                    for (int j = 0; j < 3; j++) {
                        ExpandableListAdapter_New_Event_Friends.Item group1 = new ExpandableListAdapter_New_Event_Friends.Item(ExpandableListAdapter_New_Event_Friends.Group, "Group Name");
                        group1.invisibleChildren = new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            group1.invisibleChildren.add(new ExpandableListAdapter_New_Event_Friends.Item(ExpandableListAdapter_New_Event_Friends.User, "User_ID"));
                        }
                        data3.add(group1);
                    }
                    recyclerview3.setAdapter(new ExpandableListAdapter_New_Event_Friends(data3));

                    Button btn2 = (Button) rootView.findViewById(R.id.btn1);
                    Button btn3 = (Button) rootView.findViewById(R.id.btn2);

                    final ViewAnimator viewAnimator = (ViewAnimator) rootView.findViewById(R.id.viewSwitcher1);
                    Animation slide_in_left = AnimationUtils.loadAnimation(rootView.getContext(), android.R.anim.slide_in_left);
                    Animation slide_out_right = AnimationUtils.loadAnimation(rootView.getContext(), android.R.anim.slide_out_right);

                    viewAnimator.setInAnimation(slide_in_left);
                    viewAnimator.setOutAnimation(slide_out_right);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (viewAnimator.getCurrentView() == recyclerview3) {
                                viewAnimator.showPrevious();
                            }
                        }
                    });

                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (viewAnimator.getCurrentView() == recyclerview2) {
                                viewAnimator.showNext();
                            }
                        }
                    });
                    */
                    return rootView;
                }
                case 3: {
                    View rootView = inflater.inflate(R.layout.fragment_new_event_tasks, container, false);
                    final RecyclerView recyclerview4 = (RecyclerView) rootView.findViewById(R.id.recyclerView4);
                    fab_task = (FloatingActionButton) rootView.findViewById(R.id.fab_task);

                    recyclerview4.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                    final List<ExpandableListAdapter_New_Event_Tasks.Item> data = new ArrayList<>();
                    final HashMap<Long, Task_Helper> task_pointer;
                    if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
                        task_pointer = Event_Helper.task;
                    } else {
                        task_pointer = Event_Helper.task_tmp;
                    }

                    //Update list for refresh fragment.
                    for (Long task_ID_num : task_pointer.keySet()) {
                        ExpandableListAdapter_New_Event_Tasks.Item task = new ExpandableListAdapter_New_Event_Tasks.Item(ExpandableListAdapter_New_Event_Tasks.Task, task_ID_num, 0);
                        task.invisibleChildren = new ArrayList<>();
                        for (Long subTask_ID_num : task_pointer.get(task_ID_num).getSubTasks().keySet()) {
                            task.invisibleChildren.add(new ExpandableListAdapter_New_Event_Tasks.Item(ExpandableListAdapter_New_Event_Tasks.Task_Child, task_ID_num, subTask_ID_num));
                        }
                        task.invisibleChildren.add(new ExpandableListAdapter_New_Event_Tasks.Item(ExpandableListAdapter_New_Event_Tasks.Add_Tasks_Child_Buttons, task_ID_num, 0));
                        data.add(task);
                    }
                    final ExpandableListAdapter_New_Event_Tasks mAdapter = new ExpandableListAdapter_New_Event_Tasks(data);
                    recyclerview4.setAdapter(mAdapter);
                    fab_task.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //long task_ID_num = Event_Helper.task_ID_generator++;
                            long task_ID_num = System.currentTimeMillis();
                            ExpandableListAdapter_New_Event_Tasks.Item new_task = new ExpandableListAdapter_New_Event_Tasks.Item(ExpandableListAdapter_New_Event_Tasks.Task, task_ID_num, 0);
                            //new_task.invisibleChildren = new ArrayList<ExpandableListAdapter_New_Event_Tasks.Item>();
                            //new_task.invisibleChildren.add(new ExpandableListAdapter_New_Event_Tasks.Item(ExpandableListAdapter_New_Event_Tasks.Add_Tasks_Child_Buttons, task_ID_num, 0));
                            mAdapter.getData().add(0, new_task);
                            ExpandableListAdapter_New_Event_Tasks.Item new_task_child_btn = new ExpandableListAdapter_New_Event_Tasks.Item(ExpandableListAdapter_New_Event_Tasks.Add_Tasks_Child_Buttons, task_ID_num, 0);
                            mAdapter.getData().add(1, new_task_child_btn);
                            //data.add(1, new Item(Add_Tasks_Child_Buttons, task_ID_generator, 0));
                            //data.add(1, new Item(Sub_Task, task_index, 0));
                            //data.add(1, new Item(Personal_Task, task_ID_generator, 0));
                            mAdapter.notifyItemRangeInserted(0, 2);
                            task_pointer.put(task_ID_num, new Task_Helper(Constants.Group_Task, ""));

                        }
                    });
                    return rootView;
                }
            }
            return null;
        }

        private void setSwitcher_time_view(RecyclerView recyclerView_date, RelativeLayout relativeLayout_date_titles, RelativeLayout relativeLayout_date, RelativeLayout relativeLayout_all_day
                , TextView date1, TextView date2, Switch all_day, TextView time1, TextView time2) {
            final String[] details_pointer;
            if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event))
                details_pointer = Event_Helper.details;
            else
                details_pointer = Event_Helper.details_tmp;
            if (details_pointer[Table_Events.Vote_Time_num].equals(Constants.Yes)) {
                final HashMap<Long, Vote_Date_Helper> vote_pointer;
                if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
                    vote_pointer = Event_Helper.vote_date;
                } else {
                    vote_pointer = Event_Helper.vote_date_tmp;
                }
                details_pointer[Table_Events.Vote_Time_num] = Constants.Yes;
                relativeLayout_date.setVisibility(View.GONE);
                relativeLayout_all_day.setVisibility(View.GONE);
                recyclerView_date.setVisibility(View.VISIBLE);
                relativeLayout_date_titles.setVisibility(View.VISIBLE);
                recyclerView_date.setLayoutManager(new LinearLayoutManager(getContext()));
                List<ExpandableListAdapter_New_Event_Vote_Date.Item> data = new ArrayList<>();
                if (Event_Helper.vote_date.size() == 0) {
                    long Vote_ID = System.currentTimeMillis();
                    data.add(new ExpandableListAdapter_New_Event_Vote_Date.Item(ExpandableListAdapter_New_Event_Vote_Date.Vote_Date, Vote_ID));
                    vote_pointer.put(Vote_ID, new Vote_Date_Helper("dd/mm/yyyy", "dd/mm/yyyy", Constants.No, "hh:mm", "hh:mm"));
                    Event_Helper.vote_date_ID_generator++;
                } else {
                    for (long vote_id : Event_Helper.vote_date.keySet()) {
                        data.add(new ExpandableListAdapter_New_Event_Vote_Date.Item(ExpandableListAdapter_New_Event_Vote_Date.Vote_Date, vote_id));
                    }
                }
                data.add(new ExpandableListAdapter_New_Event_Vote_Date.Item(ExpandableListAdapter_New_Event_Vote_Date.Vote_Add, 0));
                recyclerView_date.setAdapter(new ExpandableListAdapter_New_Event_Vote_Date(data, recyclerView_date, getContext()));
            } else {
                details_pointer[Table_Events.Vote_Time_num] = Constants.No;
                recyclerView_date.setVisibility(View.GONE);
                relativeLayout_date_titles.setVisibility(View.GONE);
                relativeLayout_date.setVisibility(View.VISIBLE);
                relativeLayout_all_day.setVisibility(View.VISIBLE);
                date1.setText(Helper.format_date(details_pointer[Table_Events.Start_Date_num]));
                date2.setText(Helper.format_date(details_pointer[Table_Events.End_Date_num]));
                time1.setText(Helper.format_time(details_pointer[Table_Events.Start_Time_num]));
                time2.setText(Helper.format_time(details_pointer[Table_Events.End_Time_num]));
                if (details_pointer[Table_Events.All_Day_Time_num].equals(Constants.Yes)) {
                    all_day.setChecked(true);
                    time1.setVisibility(View.GONE);
                    time2.setVisibility(View.GONE);
                }
            }
        }

        private void setSwitcher_location_view(RecyclerView recyclerView_location, RelativeLayout relativeLayout_location_titles, RelativeLayout relativeLayout_location, EditText editText_location) {
            final String[] details_pointer;
            if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event))
                details_pointer = Event_Helper.details;
            else
                details_pointer = Event_Helper.details_tmp;
            if (details_pointer[Table_Events.Vote_Location_num].equals(Constants.Yes)) {
                final HashMap<Long, Vote_Location_Helper> vote_pointer;
                if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
                    vote_pointer = Event_Helper.vote_location;
                } else {
                    vote_pointer = Event_Helper.vote_location_tmp;
                }
                details_pointer[Table_Events.Vote_Location_num] = Constants.Yes;
                relativeLayout_location.setVisibility(View.GONE);
                recyclerView_location.setVisibility(View.VISIBLE);
                relativeLayout_location_titles.setVisibility(View.VISIBLE);
                recyclerView_location.setLayoutManager(new LinearLayoutManager(getContext()));
                List<ExpandableListAdapter_New_Event_Vote_Location.Item> data = new ArrayList<>();
                if (Event_Helper.vote_location.size() == 0) {
                    long Vote_ID = System.currentTimeMillis();
                    data.add(new ExpandableListAdapter_New_Event_Vote_Location.Item(ExpandableListAdapter_New_Event_Vote_Location.Vote_Location, Vote_ID));
                    vote_pointer.put(Vote_ID, new Vote_Location_Helper(""));
                    Event_Helper.vote_location_ID_generator++;
                } else {
                    for (long vote_id : Event_Helper.vote_location.keySet()) {
                        data.add(new ExpandableListAdapter_New_Event_Vote_Location.Item(ExpandableListAdapter_New_Event_Vote_Location.Vote_Location, vote_id));
                    }
                }
                data.add(new ExpandableListAdapter_New_Event_Vote_Location.Item(ExpandableListAdapter_New_Event_Vote_Location.Vote_Add, 0));
                recyclerView_location.setAdapter(new ExpandableListAdapter_New_Event_Vote_Location(data, recyclerView_location, getContext()));
            } else {
                details_pointer[Table_Events.Vote_Location_num] = Constants.No;
                recyclerView_location.setVisibility(View.GONE);
                relativeLayout_location_titles.setVisibility(View.GONE);
                relativeLayout_location.setVisibility(View.VISIBLE);
                editText_location.setText(details_pointer[Table_Events.Location_num]);
            }
        }
    }
}

class ExpandableListAdapter_New_Event_Vote_Date extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int Vote_Date = 0;
    public static final int Vote_Add = 1;

    private List<Item> data;
    private RecyclerView recyclerView;
    private int recyclerView_height_dp;
    private HashMap<Long, Vote_Date_Helper> vote_date_pointer;
    private final static boolean apk23 = Build.VERSION.SDK_INT >= 23;

    private long Vote_ID;
    private int vote_height;
    private int add_height;

    public List<Item> getData() {
        return data;
    }

    public ExpandableListAdapter_New_Event_Vote_Date(List<Item> data, RecyclerView recyclerView, Context context) {
        this.data = data;
        this.recyclerView = recyclerView;
        if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
            this.vote_date_pointer = Event_Helper.vote_date;
        } else {
            this.vote_date_pointer = Event_Helper.vote_date_tmp;
        }
        vote_height = 58;
        add_height = 34;
        recyclerView_height_dp = (data.size() - 1) * vote_height + add_height;
        int recyclerView_height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, recyclerView_height_dp, context.getResources().getDisplayMetrics());
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, recyclerView_height_px));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater;
        switch (viewType) {
            case Vote_Date: {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_detail_vote_date, parent, false);
                ViewHolder_Vote_Date viewHolder_vote_date = new ViewHolder_Vote_Date(view);
                return viewHolder_vote_date;
            }
            case Vote_Add: {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_detail_vote_add, parent, false);
                ViewHolder_Vote_Add viewHolder_vote_add = new ViewHolder_Vote_Add(view);
                return viewHolder_vote_add;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        final View view = holder.itemView;
        switch (item.type) {
            case Vote_Date: {
                final ViewHolder_Vote_Date itemController = (ViewHolder_Vote_Date) holder;
                itemController.refferalItem = item;
                itemController.option.setText("Option " + (position + 1));
                Vote_ID = itemController.refferalItem.Vote_ID;
                itemController.date1.setText(Helper.format_date(vote_date_pointer.get(Vote_ID).getStart_Date()));
                itemController.date2.setText(Helper.format_date(vote_date_pointer.get(Vote_ID).getEnd_Date()));
                itemController.time1.setText(Helper.format_time(vote_date_pointer.get(Vote_ID).getStart_Time()));
                itemController.time2.setText(Helper.format_time(vote_date_pointer.get(Vote_ID).getEnd_Time()));
                if (vote_date_pointer.get(Vote_ID).getAll_Day().equals(Constants.Yes)) {
                    itemController.all_day.setChecked(true);
                    itemController.time1.setVisibility(View.GONE);
                    itemController.time2.setVisibility(View.GONE);
                }else{
                    itemController.all_day.setChecked(false);
                    itemController.time1.setVisibility(View.VISIBLE);
                    itemController.time2.setVisibility(View.VISIBLE);
                }
                itemController.date1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        date(v, itemController.date1, itemController.date2, true, Vote_ID);
                    }
                });
                itemController.date2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        date(v, itemController.date1, itemController.date2, false, Vote_ID);
                    }
                });
                itemController.time1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        time(v, itemController.time1, itemController.time2, true, Vote_ID);
                    }
                });
                itemController.time2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        time(v, itemController.time1, itemController.time2, false, Vote_ID);
                    }
                });
                itemController.all_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        if (isChecked) {
                            itemController.time1.setVisibility(View.GONE);
                            itemController.time2.setVisibility(View.GONE);
                            vote_date_pointer.get(Vote_ID).setAll_Day(Constants.Yes);
                        } else {
                            itemController.time1.setVisibility(View.VISIBLE);
                            itemController.time2.setVisibility(View.VISIBLE);
                            vote_date_pointer.get(Vote_ID).setAll_Day(Constants.No);
                        }
                    }
                });
                itemController.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        int pos = data.indexOf(itemController.refferalItem);
                        data.remove(pos);
                        vote_date_pointer.remove(Vote_ID);
                        //notifyItemRemoved(pos);
                        notifyDataSetChanged();
                        recyclerView_height_dp -= vote_height;
                        int recyclerView_height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, recyclerView_height_dp, v.getContext().getResources().getDisplayMetrics());
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, recyclerView_height_px));
                    }
                });
                break;
            }
            case Vote_Add: {
                final ViewHolder_Vote_Add itemController = (ViewHolder_Vote_Add) holder;
                itemController.refferalItem = item;
                itemController.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Event_Helper.vote_date_ID_generator++;
                        long Vote_ID = System.currentTimeMillis();
                        //data.add(data.size() - 1, new ExpandableListAdapter_New_Event_Vote_Date.Item(ExpandableListAdapter_New_Event_Vote_Date.Vote_Date, Event_Helper.vote_date_ID_generator));
                        //vote_date_pointer.put(Event_Helper.vote_date_ID_generator,
                        data.add(data.size() - 1, new ExpandableListAdapter_New_Event_Vote_Date.Item(ExpandableListAdapter_New_Event_Vote_Date.Vote_Date, Vote_ID));
                        vote_date_pointer.put(Vote_ID,
                                new Vote_Date_Helper("dd/mm/yyyy", "dd/mm/yyyy", Constants.No, "hh:mm", "hh:mm"));
                        notifyDataSetChanged();
                        //notifyItemInserted(data.size() - 1);
                        recyclerView_height_dp += vote_height;
                        int recyclerView_height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, recyclerView_height_dp, v.getContext().getResources().getDisplayMetrics());
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, recyclerView_height_px));
                    }
                });
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        this.Vote_ID = data.get(position).Vote_ID;
        return data.get(position).type;
    }

    private void date(View v, final TextView date_view1, final TextView date_view2, final Boolean isStartDate, final long vote_id) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.new_event_detail_date_dialog);
        dialog.setCancelable(true);
        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        Button done = (Button) dialog.findViewById(R.id.done);
        //Set initial date by previous selection if exist or by the current date.
        String current_date;
        if (isStartDate) {
            current_date = vote_date_pointer.get(vote_id).getStart_Date();
            date_view1.setText(Helper.format_date(current_date));

        } else {
            current_date = vote_date_pointer.get(vote_id).getEnd_Date();
            date_view2.setText(Helper.format_date(current_date));
        }
        if (!current_date.equals("dd/mm/yyyy")) {
            datePicker.updateDate(Integer.parseInt(current_date.split("/")[2]), Integer.parseInt(current_date.split("/")[1]) - 1, Integer.parseInt(current_date.split("/")[0]));
        }
        dialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int month = datePicker.getMonth() + 1;
                String date = datePicker.getDayOfMonth() + "/" + month + "/" + datePicker.getYear();
                if (isStartDate) {
                    vote_date_pointer.get(vote_id).setStart_Date(date);
                    vote_date_pointer.get(vote_id).setEnd_Date(date);
                    date_view1.setText(Helper.format_date(date));
                    date_view2.setText(Helper.format_date(date));

                } else {
                    vote_date_pointer.get(vote_id).setEnd_Date(date);
                    date_view2.setText(Helper.format_date(date));
                }
                dialog.dismiss();
            }
        });
    }

    private void time(View v, final TextView time_view1, final TextView time_view2, final Boolean isStartTime, final long vote_id) {
        final Dialog dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.new_event_detail_time_dialog);
        dialog.setCancelable(true);
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        Button done = (Button) dialog.findViewById(R.id.done);
        //Set initial date by previous selection if exist or by the current date.
        String current_time;
        if (isStartTime) {
            current_time = vote_date_pointer.get(vote_id).getStart_Time();
            time_view1.setText(Helper.format_time(current_time));
        } else {
            current_time = vote_date_pointer.get(vote_id).getEnd_Time();
            time_view2.setText(Helper.format_time(current_time));
        }
        if (!current_time.equals("hh:mm")) {
            if (apk23) {
                timePicker.setHour(Integer.parseInt(current_time.split(":")[0]));
                timePicker.setMinute(Integer.parseInt(current_time.split(":")[1]));
            } else {
                timePicker.setCurrentHour(Integer.parseInt(current_time.split(":")[0]));
                timePicker.setCurrentMinute(Integer.parseInt(current_time.split(":")[1]));
            }
        }
        timePicker.setIs24HourView(true);
        dialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = "";
                if (apk23) time = timePicker.getHour() + ":" + timePicker.getMinute();
                else time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                if (isStartTime) {
                    vote_date_pointer.get(vote_id).setStart_Time(time);
                    vote_date_pointer.get(vote_id).setEnd_Time(time);
                    time_view1.setText(Helper.format_time(time));
                    time_view2.setText(Helper.format_time(time));

                } else {
                    vote_date_pointer.get(vote_id).setEnd_Time(time);
                    time_view2.setText(Helper.format_time(time));
                }
                dialog.dismiss();
            }
        });
    }

    private static class ViewHolder_Vote_Date extends RecyclerView.ViewHolder {
        public Item refferalItem;
        public TextView option;
        public TextView date1;
        public TextView date2;
        public TextView time1;
        public TextView time2;
        public Switch all_day;
        public ImageButton cancel;

        public ViewHolder_Vote_Date(View itemView) {
            super(itemView);
            option = (TextView) itemView.findViewById(R.id.option);
            date1 = (TextView) itemView.findViewById(R.id.date1);
            date2 = (TextView) itemView.findViewById(R.id.date2);
            time1 = (TextView) itemView.findViewById(R.id.time1);
            time2 = (TextView) itemView.findViewById(R.id.time2);
            all_day = (Switch) itemView.findViewById(R.id.all_day);
            cancel = (ImageButton) itemView.findViewById(R.id.cancel);
        }
    }

    private static class ViewHolder_Vote_Add extends RecyclerView.ViewHolder {
        public Item refferalItem;
        public ImageButton imageButton;

        public ViewHolder_Vote_Add(View itemView) {
            super(itemView);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
        }
    }

    public static class Item {
        public int type;
        public long Vote_ID;

        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, long Vote_ID) {
            this.type = type;
            this.Vote_ID = Vote_ID;
        }
    }
}

class ExpandableListAdapter_New_Event_Vote_Location extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int Vote_Location = 0;
    public static final int Vote_Add = 1;

    private List<Item> data;
    private RecyclerView recyclerView;
    private int recyclerView_height_dp;
    private int vote_height;
    private int add_height;
    private HashMap<Long, Vote_Location_Helper> vote_location_pointer;

    private long Vote_ID;
    private long Vote_ID_num_has_focus;

    public List<Item> getData() {
        return data;
    }

    public long getVote_ID_num_has_focus() {
        return Vote_ID_num_has_focus;
    }

    public ExpandableListAdapter_New_Event_Vote_Location(List<Item> data, RecyclerView recyclerView, Context context) {
        this.data = data;
        this.recyclerView = recyclerView;
        if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
            this.vote_location_pointer = Event_Helper.vote_location;
        } else {
            this.vote_location_pointer = Event_Helper.vote_location_tmp;
        }
        Vote_ID_num_has_focus = 1;
        vote_height = 35;
        add_height = 34;
        recyclerView_height_dp = (data.size() - 1) * vote_height + add_height;
        int recyclerView_height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, recyclerView_height_dp, context.getResources().getDisplayMetrics());
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, recyclerView_height_px));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater;
        switch (viewType) {
            case Vote_Location: {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_detail_vote_location, parent, false);
                ViewHolder_Vote_Location viewHolder_vote_location = new ViewHolder_Vote_Location(view);
                return viewHolder_vote_location;
            }
            case Vote_Add: {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_detail_vote_add, parent, false);
                ViewHolder_Vote_Add viewHolder_vote_add = new ViewHolder_Vote_Add(view);
                return viewHolder_vote_add;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        final View view = holder.itemView;
        switch (item.type) {
            case Vote_Location: {
                final ViewHolder_Vote_Location itemController = (ViewHolder_Vote_Location) holder;
                itemController.refferalItem = item;
                itemController.option.setText("Option " + (position + 1));
                Vote_ID = itemController.refferalItem.Vote_ID;
                itemController.editText.setText(vote_location_pointer.get(Vote_ID).getDescription());
                itemController.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        if (!hasFocus) {
                            vote_location_pointer.put(Vote_ID, new Vote_Location_Helper(itemController.editText.getText().toString()));
                        } else {
                            Vote_ID_num_has_focus = Vote_ID;
                        }
                    }
                });
                itemController.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Vote_ID = itemController.refferalItem.Vote_ID;
                        int pos = data.indexOf(itemController.refferalItem);
                        data.remove(pos);
                        //notifyItemRangeRemoved(pos, 1);
                        notifyDataSetChanged();
                        recyclerView_height_dp -= vote_height;
                        int recyclerView_height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, recyclerView_height_dp, v.getContext().getResources().getDisplayMetrics());
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, recyclerView_height_px));
                        vote_location_pointer.remove(Vote_ID);
                    }
                });
                break;
            }
            case Vote_Add: {
                final ViewHolder_Vote_Add itemController = (ViewHolder_Vote_Add) holder;
                itemController.refferalItem = item;
                itemController.imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Event_Helper.vote_location_ID_generator++;
                        long Vote_ID = System.currentTimeMillis();
                        data.add(data.size() - 1, new ExpandableListAdapter_New_Event_Vote_Location.Item(ExpandableListAdapter_New_Event_Vote_Location.Vote_Location, Vote_ID));
                        vote_location_pointer.put(Vote_ID, new Vote_Location_Helper(""));
                        //notifyItemInserted(data.size() - 1);
                        notifyDataSetChanged();
                        recyclerView_height_dp += vote_height;
                        int recyclerView_height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, recyclerView_height_dp, v.getContext().getResources().getDisplayMetrics());
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, recyclerView_height_px));
                    }
                });
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        this.Vote_ID = data.get(position).Vote_ID;
        return data.get(position).type;
    }


    private static class ViewHolder_Vote_Location extends RecyclerView.ViewHolder {
        public Item refferalItem;
        public TextView option;
        public EditText editText;
        public ImageButton cancel;

        public ViewHolder_Vote_Location(View itemView) {
            super(itemView);
            option = (TextView) itemView.findViewById(R.id.option);
            editText = (EditText) itemView.findViewById(R.id.editText);
            cancel = (ImageButton) itemView.findViewById(R.id.cancel);
        }
    }

    private static class ViewHolder_Vote_Add extends RecyclerView.ViewHolder {
        public Item refferalItem;
        public ImageButton imageButton;

        public ViewHolder_Vote_Add(View itemView) {
            super(itemView);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
        }
    }

    public static class Item {
        public int type;
        public long Vote_ID;

        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, long Vote_ID) {
            this.type = type;
            this.Vote_ID = Vote_ID;
        }
    }
}

class ExpandableListAdapter_New_Event_Friends extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int Group = 0;
    public static final int User = 1;

    private List<Item> data;
    private int position;
    private HashMap<String, Friend_Helper> friend_pointer;

    public void setData(List<Item> data) {
        this.data = data;
    }

    public ExpandableListAdapter_New_Event_Friends(List<Item> data) {
        this.data = data;
        if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
            this.friend_pointer = Event_Helper.friends;
        } else {
            this.friend_pointer = Event_Helper.friends_tmp;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        LayoutInflater inflater;
        switch (type) {
            case Group:
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_friend_group, parent, false);
                ViewHolder_Group viewHolder_group = new ViewHolder_Group(view);
                return viewHolder_group;
            case User:
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_friend_user, parent, false);
                ViewHolder_User viewHolder_user = new ViewHolder_User(view);
                String User_ID = data.get(position).User_ID;
                String nickname = Contacts_List.contacts.get(User_ID);
                viewHolder_user.textView.setText(nickname);
                viewHolder_user.textView_phone.setText(Helper.Convert_User_ID_To_Phone(User_ID));
                if (friend_pointer.get(User_ID) != null) {
                    viewHolder_user.checkBox.setChecked(true);
                    viewHolder_user.spinner.setVisibility(View.VISIBLE);
                    int permission = 0;
                    switch (friend_pointer.get(User_ID).getPermission()) {
                        case Constants.Participant:
                            permission = 0;
                            break;
                        case Constants.Owner:
                            permission = 1;
                            break;
                    }
                    viewHolder_user.spinner.setSelection(permission);
                }
                return viewHolder_user;
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        View view = holder.itemView;
        switch (item.type) {
            case User: {
                final ViewHolder_User itemController = (ViewHolder_User) holder;
                itemController.refferalItem = item;
                final int pos = data.indexOf(itemController.refferalItem);
                //Set values.
                String User_ID = itemController.refferalItem.User_ID;
                itemController.textView.setText(Contacts_List.contacts.get(User_ID));
                itemController.textView_phone.setText(Helper.Convert_User_ID_To_Phone(User_ID));
                if (friend_pointer.get(User_ID) != null) {
                    itemController.checkBox.setChecked(true);
                    itemController.spinner.setVisibility(View.VISIBLE);
                    int permission = 0;
                    switch (friend_pointer.get(User_ID).getPermission()) {
                        case Constants.Participant:
                            permission = 0;
                            break;
                        case Constants.Owner:
                            permission = 1;
                            break;
                    }
                    itemController.spinner.setSelection(permission);
                } else {
                    itemController.checkBox.setChecked(false);
                    itemController.spinner.setVisibility(View.GONE);
                }
                itemController.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemController.checkBox.isChecked()) {
                            itemController.spinner.setVisibility(View.VISIBLE);
                            friend_pointer.put(item.User_ID, new Friend_Helper());
                        } else {
                            itemController.spinner.setVisibility(View.GONE);
                            friend_pointer.remove(item.User_ID);
                        }
                    }
                });
                itemController.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                friend_pointer.get(item.User_ID).setPermission(Constants.Participant);
                                break;
                            case 1:
                                friend_pointer.get(item.User_ID).setPermission(Constants.Owner);
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            }
            case Group: {
                final ViewHolder_Group itemController = (ViewHolder_Group) holder;
                itemController.refferalItem = item;
                if (item.invisibleChildren == null) {
                    itemController.expand_arrow.setImageResource(R.mipmap.ic_collapse_arrow);
                } else {
                    itemController.expand_arrow.setImageResource(R.mipmap.ic_expand_arrow);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int pos = data.indexOf(itemController.refferalItem);
                            int count = 0;
                            while (data.size() > pos + 1 && data.get(pos + 1).type != Group) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.expand_arrow.setImageResource(R.mipmap.ic_expand_arrow);
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);

                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                //index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.expand_arrow.setImageResource(R.mipmap.ic_collapse_arrow);
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        this.position = position;
        return data.get(position).type;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    private static class ViewHolder_User extends RecyclerView.ViewHolder {
        public Item refferalItem;
        public TextView textView;
        public TextView textView_phone;
        public CheckBox checkBox;
        public Spinner spinner;

        public ViewHolder_User(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            textView_phone = (TextView) itemView.findViewById(R.id.textView_phone);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            spinner = (Spinner) itemView.findViewById(R.id.spinner);
        }
    }

    private static class ViewHolder_Group extends RecyclerView.ViewHolder {
        public ImageView expand_arrow;
        public Item refferalItem;

        public ViewHolder_Group(View itemView) {
            super(itemView);
            expand_arrow = (ImageView) itemView.findViewById(R.id.expand_arrow);
        }
    }

    public static class Item {
        public int type;
        public String User_ID;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, String User_ID) {
            this.type = type;
            this.User_ID = User_ID;
        }
    }
}

class ExpandableListAdapter_New_Event_Tasks extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int Add_Tasks_Child_Buttons = 0;
    public static final int Task = 1;
    public static final int Task_Child = 2;

    private List<Item> data;
    int pos;
    long task_ID_num;
    long subTask_ID_num;
    long task_ID_num_has_focus;
    long subTask_ID_num_has_focus;
    private ViewHolder_Task temp_viewHolder_task;
    private HashMap<Long, Task_Helper> task_pointer;

    public List<Item> getData() {
        return data;
    }

    public long getSubTask_ID_num_has_focus() {
        return subTask_ID_num_has_focus;
    }

    public long getTask_ID_num_has_focus() {
        return task_ID_num_has_focus;
    }

    public ExpandableListAdapter_New_Event_Tasks(List<Item> data) {
        this.data = data;
        if (Event_Helper.newEvent_edit_mode.equals(Constants.New_Event)) {
            this.task_pointer = Event_Helper.task;
        } else {
            this.task_pointer = Event_Helper.task_tmp;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        LayoutInflater inflater;
        switch (type) {
            case Add_Tasks_Child_Buttons: {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_tasks_add_child_buttons, parent, false);
                ViewHolder_Add_Tasks_Child_Button viewHolder_add_tasks_child_button = new ViewHolder_Add_Tasks_Child_Button(view);
                return viewHolder_add_tasks_child_button;
            }
            case Task: {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_task, parent, false);
                ViewHolder_Task viewHolder_task = new ViewHolder_Task(view);
                viewHolder_task.editText.setText(task_pointer.get(task_ID_num).getDescription());
                return viewHolder_task;
            }
            case Task_Child: {
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.new_event_task_child, parent, false);
                ViewHolder_Task_Child viewHolder_task_child = new ViewHolder_Task_Child(view);
                viewHolder_task_child.editText.setText(task_pointer.get(task_ID_num).getSubTasks().get(subTask_ID_num)[0]);
                return viewHolder_task_child;
            }
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        View view = holder.itemView;
        switch (item.type) {
            case Task_Child: {
                final ViewHolder_Task_Child itemController = (ViewHolder_Task_Child) holder;
                itemController.refferalItem = item;
                ImageButton cancel = (ImageButton) view.findViewById(R.id.cancel);
                //Set description every time the view change.
                refresh_values(itemController.refferalItem);
                itemController.editText.setText(task_pointer.get(task_ID_num).getSubTasks().get(subTask_ID_num)[0]);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh_values(itemController.refferalItem);
                        data.remove(pos);
                        notifyItemRangeRemoved(pos, 1);
                        task_pointer.get(task_ID_num).getSubTasks().remove(subTask_ID_num);
                    }
                });
                itemController.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        refresh_values(itemController.refferalItem);
                        if (!hasFocus) {
                            String mark = task_pointer.get(task_ID_num).getSubTasks().get(subTask_ID_num)[1];
                            task_pointer.get(task_ID_num).getSubTasks().put(subTask_ID_num, new String[]{itemController.editText.getText().toString(), mark});
                        } else {
                            task_ID_num_has_focus = itemController.refferalItem.task_ID_num;
                            subTask_ID_num_has_focus = itemController.refferalItem.subTask_ID_num;
                        }
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hide_keyboard(v.getContext());
                    }
                });
                break;
            }
            case Task: {
                final ViewHolder_Task itemController = (ViewHolder_Task) holder;
                itemController.refferalItem = item;
                task_pointer.get(task_ID_num).setDescription(itemController.editText.getText().toString());
                if (task_pointer.get(task_ID_num) != null) {
                    task_pointer.get(task_ID_num).setDescription(itemController.editText.getText().toString());
                }
                if (item.invisibleChildren == null) {
                    itemController.expand_arrow.setImageResource(R.mipmap.ic_collapse_arrow);
                } else {
                    itemController.expand_arrow.setImageResource(R.mipmap.ic_expand_arrow);
                }
                //Set type.
                refresh_values(itemController.refferalItem);
                if (task_pointer.get(task_ID_num).getType().equals(Constants.Group_Task)) {
                    itemController.imageView.setImageResource(R.mipmap.ic_group_gray1);
                    itemController.type.setText("Shared task");
                } else {
                    itemController.imageView.setImageResource(R.mipmap.ic_personal_gray);
                    itemController.type.setText("Personal task");
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (temp_viewHolder_task != null && temp_viewHolder_task != itemController) {
                            fold_open_task(temp_viewHolder_task);
                        }
                        fold_open_task(itemController);
                        hide_keyboard(v.getContext());
                    }
                });

                itemController.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        refresh_values(itemController.refferalItem);
                        if (!hasFocus) {
                            if (task_pointer.get(task_ID_num) != null) {
                                task_pointer.get(task_ID_num).setDescription(itemController.editText.getText().toString());
                            }
                        } else {
                            task_ID_num_has_focus = itemController.refferalItem.task_ID_num;
                            subTask_ID_num_has_focus = itemController.refferalItem.subTask_ID_num;
                        }
                    }
                });
                final ImageButton cancel = (ImageButton) view.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh_values(itemController.refferalItem);
                        int count = 1;
                        data.remove(pos);
                        while (pos < data.size() && data.get(pos).type != Task) {
                            data.remove(pos);
                            count++;
                        }
                        notifyItemRangeRemoved(pos, count);
                        task_pointer.remove(task_ID_num);
                    }
                });
                itemController.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh_values(itemController.refferalItem);
                        if (task_pointer.get(task_ID_num).getType().equals(Constants.Group_Task)) {
                            itemController.imageView.setImageResource(R.mipmap.ic_personal_gray);
                            itemController.type.setText("Personal task");
                            task_pointer.get(task_ID_num).setType(Constants.Personal_Task);
                        } else {
                            itemController.imageView.setImageResource(R.mipmap.ic_group_gray1);
                            itemController.type.setText("Shared task");
                            task_pointer.get(task_ID_num).setType(Constants.Group_Task);
                        }
                    }
                });
                break;
            }
            case Add_Tasks_Child_Buttons: {
                final ViewHolder_Add_Tasks_Child_Button itemController = (ViewHolder_Add_Tasks_Child_Button) holder;
                itemController.refferalItem = item;
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButton);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh_values(itemController.refferalItem);
                        //subTask_ID_num = task_pointer.get(task_ID_num).getSubTask_ID_generator() + 1;
                        subTask_ID_num = System.currentTimeMillis();
                        //task_pointer.get(task_ID_num).setSubTask_ID_generator(subTask_ID_num);
                        data.add(pos, new Item(Task_Child, task_ID_num, subTask_ID_num));
                        notifyItemRangeInserted(pos, 1);
                        task_pointer.get(task_ID_num).getSubTasks().put(subTask_ID_num, new String[]{"", Constants.No});
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hide_keyboard(v.getContext());
                    }
                });
                break;
            }
        }
    }

    private void fold_open_task(ViewHolder_Task itemController) {
        refresh_values(itemController.refferalItem);
        if (itemController.refferalItem.invisibleChildren == null) {
            itemController.refferalItem.invisibleChildren = new ArrayList<Item>();
            int count = 0;
            while (data.size() > pos + 1 && data.get(pos + 1).type != Task) {
                itemController.refferalItem.invisibleChildren.add(data.remove(pos + 1));
                count++;
            }
            notifyItemRangeRemoved(pos + 1, count);
            itemController.expand_arrow.setImageResource(R.mipmap.ic_expand_arrow);
            temp_viewHolder_task = null;
        } else {
            int index = pos + 1;
            for (Item i : itemController.refferalItem.invisibleChildren) {
                data.add(index, i);
                index++;
            }
            notifyItemRangeInserted(pos + 1, index - pos - 1);
            itemController.expand_arrow.setImageResource(R.mipmap.ic_collapse_arrow);
            itemController.refferalItem.invisibleChildren = null;
            temp_viewHolder_task = itemController;
        }
    }

    private void refresh_values(Item refferalItem) {
        this.pos = data.indexOf(refferalItem);
        this.task_ID_num = refferalItem.task_ID_num;
        this.subTask_ID_num = refferalItem.subTask_ID_num;
    }

    private void hide_keyboard(Context context) {
        Activity activity = (Activity) context;
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        task_ID_num = data.get(position).task_ID_num;
        subTask_ID_num = data.get(position).subTask_ID_num;
        return data.get(position).type;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ViewHolder_Add_Tasks_Child_Button extends RecyclerView.ViewHolder {
        public Item refferalItem;

        public ViewHolder_Add_Tasks_Child_Button(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolder_Task extends RecyclerView.ViewHolder {
        public ImageView expand_arrow;
        public Item refferalItem;
        public ImageView imageView;
        public EditText editText;
        public TextView type;

        public ViewHolder_Task(View itemView) {
            super(itemView);
            expand_arrow = (ImageView) itemView.findViewById(R.id.expand_arrow);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(R.mipmap.ic_group_gray1);
            editText = (EditText) itemView.findViewById(R.id.editText);
            type = (TextView) itemView.findViewById(R.id.type);
        }
    }

    private static class ViewHolder_Task_Child extends RecyclerView.ViewHolder {
        public Item refferalItem;
        public EditText editText;

        public ViewHolder_Task_Child(View itemView) {
            super(itemView);
            editText = (EditText) itemView.findViewById(R.id.editText);
        }
    }

    public static class Item {
        public int type;
        public List<Item> invisibleChildren;
        public long task_ID_num;
        public long subTask_ID_num;

        public Item() {
        }

        public Item(int type, long task_ID_num, long subTask_ID_num) {
            this.type = type;
            this.task_ID_num = task_ID_num;
            this.subTask_ID_num = subTask_ID_num;
        }
    }
}
