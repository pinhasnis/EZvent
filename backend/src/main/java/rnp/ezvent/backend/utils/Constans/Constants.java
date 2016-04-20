/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rnp.ezvent.backend.utils.Constans;

/**
 * API Keys, Client Ids and Audience Ids for accessing APIs and configuring
 * Cloud Endpoints.
 * When you deploy your solution, you need to use your own API Keys and IDs.
 * Please refer to the documentation for this sample for more details.
 */
public final class Constants {
    // User: Update keys

    /**
     * Google Cloud Messaging API key.
     */
    public static final String GCM_API_KEY = "AIzaSyDEAV2-JZrcBQRpBBFn3OAznT0bV0a5TdE";

    /**
     * Android client ID from Google Cloud console.
     */
    public static final String ANDROID_CLIENT_ID = "192098515949-6rvvffut03pl536qipm02jnj518kr3h4.apps.googleusercontent.com";

    /**
     * iOS client ID from Google Cloud console.
     */
    public static final String IOS_CLIENT_ID = "YOUR-IOS-CLIENT-ID";

    /**
     * Web client ID from Google Cloud console.
     */
    public static final String WEB_CLIENT_ID = "192098515949-bcdb4j7od526dp909qolea9d78jesp2u.apps.googleusercontent.com";

    /**
     * Audience ID used to limit access to some client to the API.
     */
    public static final String AUDIENCE_ID = WEB_CLIENT_ID;

    /**
     * API package name.
     */
    public static final String API_OWNER =
            "backend.rnp";

    /**
     * API package path.
     */
    public static final String API_PACKAGE_PATH = "";

    /**
     * Database package path.
     */
    public static final String Database_PATH = "jdbc:google:mysql://encoded-keyword-106406:test/datdbase1?user=root";

    public static final String sqlClassName = "com.mysql.jdbc.GoogleDriver";
    /**
     * Default constrictor, never called.
     */

    public final static String New_Event = "A";
    public final static String Update_Event = "B";
    public final static String Delete_Event = "C";
    public final static String Chat_Message = "D";
    public final static String Request_New_Event = "E";
    public final static String New_Chat_Message = "F";
    public final static String Delete_Chat_Message = "G";
    public final static String Take_Task = "H";
    public final static String Vote_For_Date = "I";
    public final static String Vote_For_Location = "J";
    public final static String Update_Attending = "K";

    public final static int update_event_not_change = 0;
    public final static int update_event_new = 1;
    public final static int update_event_delete = 2;
    public final static int update_event_update = 3;

    public final static int index_object_sql_diff = 1;
    public final static int False = 0;
    public final static int True = 1;
    public final static String UnCheck = "0";//"UnCheck";


    private Constants() {
    }
}
