package utils.Constans;

/**
 * Created by pinhas on 28/09/2015.
 */

import android.net.Uri;

import rnp.ezvent.BuildConfig;

/**
 * API Keys, Client Ids and Audience Ids for accessing APIs and configuring
 * Cloud Endpoints. When you deploy your solution, you need to use your own API
 * Keys and IDs. Please update config.gradle to define them.
 */
public final class Constants {

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static final String SENDER_ID = BuildConfig.SENDER_ID;

    /**
     * Web client ID from Google Cloud console.
     */
    public static final String WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID;

    /**
     * The web client ID from Google Cloud Console.
     */
    public static final String AUDIENCE_ANDROID_CLIENT_ID =
            "server:client_id:" + WEB_CLIENT_ID;

    /**
     * The URL to the API. Default when running locally on your computer:
     * "http://10.0.2.2:8080/_ah/api/"
     */
    public static final String ROOT_URL = BuildConfig.ROOT_URL;

    /**
     * Defines whether authentication is required or not.
     */
    public static final boolean SIGN_IN_REQUIRED = BuildConfig.SIGN_IN_REQUIRED;

    public static final String SQL_DIR = "/data/data/some_lie.brings/databases/";
    public static final String SQL_DB_NAME = "_edata";


    public static final String bucket_name = "a_bucket";
    public static final String cloudStorageScope = "https://www.googleapis.com/auth/devstorage.read_write";
    public static final String projectName = "Brings";
    public static final String cloudACCOUNT_ID_PROPERTY = "de42c51fc9947859ae15a25bd4e170f4fbb85fde";
    public static final String cloudPRIVATE_KEY_PATH_PROPERTY = Uri.parse("android.resource://raw/test_de42c51fc994").getPath();
    public static final String cloudEmail = "192098515949-evapq8mcu9v6btbnp7d0lqm7o0rf4jet@developer.gserviceaccount.com";
    public static final String cloudPassword = "notasecret";
    public static final String imageSaveLocation = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Brings/images";

    public final static int update_event_not_change = 0;
    public final static int update_event_new = 1;
    public final static int update_event_delete = 2;
    public final static int update_event_update = 3;

    /**
     * Default constructor, never called.
     */
    private Constants() {
    }

    public static String MY_User_ID;
    public static String MY_User_Nickname;
    public static String MY_User_Password;
    public static final String MY_User_ID_Key = "MY_User_ID_Key";
    public static final String MY_User_Password_Key = "MY_User_Password_Key";
    public static final String MY_User_Nickname_Key = "MY_User_Nickname_Key";
    public final static String country_code = "972";
    public final static String r_user = "R-USER";
    public final static String r_user_key = "USER";
    public final static String no_name_defined = "No name defined";
    public final static String Message = "Message";
    public final static String Not_Register = "Not_Register";

    public final static String New_Event = "A";
    public final static String Update_Event = "B";
    public final static String Delete_Event = "C";
    public final static String Leave_Event = "D";
    public final static String Request_New_Event = "E";
    public final static String New_Chat_Message = "F";
    public final static String Delete_Chat_Message = "G";
    public final static String Take_Task = "H";
    public final static String UnTake_Task = "I";
    public final static String Vote_For_Date = "J";
    public final static String UnVote_For_Date = "K";
    public final static String Vote_For_Location = "L";
    public final static String UnVote_For_Location = "M";
    public final static String Update_Attending = "N";
    public final static int index_object_sql_diff = 1;//We remove user_id from the Event and UpdateEvent objects to save space. because of that thier 1 place diff between the arrays.


//    public final static String New_Event = "New_Event";
//    public final static String Edit_Event = "Edit_Event";
//    public final static String Delete_Event = "Delete_Event";
//    public final static String Update_Event = "Update_Event";
    public final static String New_User = "New_User";
    public final static String Delete_User = "Delete_User";
    public final static String Update_User_Attending = "Update_User_Attending";
    public final static String New_Task = "New_Task";
    public final static String Delete_Task = "Delete_Task";
    public final static String Update_Task = "Update_Task";
    public final static String Update_Task_User_ID = "Update_Task_User_ID";
    //public final static String New_Chat_Message = "New_Chat_Message";
    //public final static String Delete_Chat_Message = "Delete_Chat_Message";
    public final static String Update_Event_Details_Filed = "Update_Event_Details_Filed";
    public final static String Insert_Vote_Date = "Insert_Vote_Date";
    public final static String Delete_Vote_Date = "Delete_Vote_Date";
    public final static String Insert_Vote_Location = "Insert_Vote_Location";
    public final static String Delete_Vote_Location = "Delete_Vote_Location";
    public final static String Yes = "2";//"Yes";
    public final static String No = "3";//"No";
    public final static String OK = "O.K";
    public final static String Maybe = "1";//"Maybe";
    public final static String Did_Not_Replay = "0";//"Did_Not_Replay";
    public final static String UnCheck = "0";//"UnCheck";
    public final static String Owner = "1";//"Owner";
    //public final static String Editor = "Editor";
    public final static String Participant = "0";//"Participant";
    public final static String Update_Activity = "Update_Activity";
    public final static String Personal_Task = "1";//"Personal_Task";
    public final static String Group_Task = "0";//"Group_Task";
    public final static String Sub_Task = "2";//"Sub_Task";
    public final static String Normal_View = "Normal_View";
    public final static String Light_View = "Light_View";
    public final static String Vote_Date = "Vote_Date";
    public final static String Vote_Location = "Vote_Location";

    public final static String mainActivity = "MainActivity";
    public final static String event = "Event";
    public final static String login = "Login";
    public final static String registration = "Registration";

    public final static int False = 0;
    public final static int True = 1;


}