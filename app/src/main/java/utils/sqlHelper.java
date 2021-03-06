package utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import rnp.ezvent.MainActivity;
import server.Messageing.GcmIntentService;
import server.add_logAsyncTask;
import utils.Constans.Constants;
import utils.Constans.Table_Events;
import utils.Constans.Table_Events_Users;
import utils.Constans.Table_Tasks;
import utils.Constans.Table_Users;
import utils.Constans.Table_Vote_Date;
import utils.Constans.Table_Vote_Location;


/**
 * Created by pinhas on 14/10/2015.
 */
public final class sqlHelper {

    private static Context context;

    public static void setContext(Context context) {
        sqlHelper.context = context;
    }

    private static SQLiteDatabase getConnection() {
        SQLiteDatabase db = null;
        if (context != null) {
            db = context.openOrCreateDatabase(Constants.SQL_DB_NAME, context.MODE_PRIVATE, null);
           // addToLog(context.getDatabasePath(Constants.SQL_DB_NAME).toString());
        }
        else{
           db = SQLiteDatabase.openOrCreateDatabase(Constants.SQL_DIR + Constants.SQL_DB_NAME, null);
        }
        // SQLiteDatabase.openOrCreateDatabase(Constants.SQL_DIR + Constants.SQL_DB_NAME, null);
        //SQLiteDatabase db = SQLiteDatabase.openDatabase(Constants.SQL_DIR + Constants.SQL_DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return db;
    }

    private static String getEventIDField(String table) {
        switch (table) {
            case Table_Events.Table_Name:
                return Table_Events.Event_ID;
            case Table_Events_Users.Table_Name:
                return Table_Events_Users.Event_ID;
            case Table_Tasks.Table_Name:
                return Table_Tasks.Event_ID;
            case Table_Vote_Location.Table_Name:
                return Table_Vote_Location.Event_ID;
            case Table_Vote_Date.Table_Name:
                return Table_Vote_Date.Event_ID;
        }
        return null;
    }

    public static void update(String table_name, String event_id, String[] set_values) {
        try {
            clean(set_values);
            String set_columns[] = getAllFields(table_name);

            String query = "update `" + table_name + "` set ";
            int end = set_values.length - 1;
            query += "`" + set_columns[0] + "` = '" + event_id + "',";
            for (int i = 0; i < end; i++) {
                query += "`" + set_columns[i+Constants.index_object_sql_diff] + "` = '" + set_values[i] + "',";
            }
            query += "`" + set_columns[end+Constants.index_object_sql_diff] + "` = '" + set_values[end] + "' ";
            query += "where `"+ getEventIDField(table_name) +"` = '"+event_id+"';";

            SQLiteDatabase db = getConnection();
            db.execSQL(query);
            db.close();
        }catch(Exception e){
            addToLog(e);
        }
    }


    private static String[] getAllFieldsUpdate(String table) {
        switch (table) {
            case Table_Events.Table_Name:
                return Table_Events.getAllFields();
            case Table_Events_Users.Table_Name:
                return new String[]{Table_Events_Users.Attending, Table_Events_Users.Permission};
            case Table_Tasks.Table_Name: {
                return new String[]{Table_Tasks.Task_Type,Table_Tasks.Description,Table_Tasks.User_ID,Table_Tasks.Mark};
            }
            case Table_Vote_Location.Table_Name:
                return new String[]{Table_Vote_Location.Description , Table_Vote_Location.User_ID};
            case Table_Vote_Date.Table_Name:
                return new String[]{Table_Vote_Date.Start_Date, Table_Vote_Date.End_Date
                        ,Table_Vote_Date.All_Day_Time,Table_Vote_Date.Start_Date,Table_Vote_Date.End_Time,Table_Vote_Date.User_ID};
        }
        return null;
    }


    private static int[] getAllFieldsUpdateIndexs(String table) {
        switch (table) {
            case Table_Events.Table_Name: {
                int size = Table_Events.getAllFields().length;
                int [] indexs = new int[size];
                for (int i = 0; i < size; i++) {
                    indexs[i] = i;
                }
                return indexs;
            }
            case Table_Events_Users.Table_Name:
                return new int[]{Table_Events_Users.Attending_num, Table_Events_Users.Permission_num};
            case Table_Tasks.Table_Name: {
                return new int[]{Table_Tasks.Task_Type_num,Table_Tasks.Description_num,Table_Tasks.User_ID_num,Table_Tasks.Mark_num};
            }
            case Table_Vote_Location.Table_Name:
                return new int[]{Table_Vote_Location.Description_num , Table_Vote_Location.User_ID_num};
            case Table_Vote_Date.Table_Name:
                return new int[]{Table_Vote_Date.Start_Date_num, Table_Vote_Date.End_Date_num
                        ,Table_Vote_Date.All_Day_Time_num,Table_Vote_Date.Start_Date_num
                        ,Table_Vote_Date.End_Time_num,Table_Vote_Date.User_ID_num};
        }
        return null;
    }

    private static String[] getAllFieldsUpdateWhere(String table) {
        switch (table) {
            case Table_Events.Table_Name:
                return new String[0];
            case Table_Events_Users.Table_Name:
                return new String[]{Table_Events_Users.User_ID};
            case Table_Tasks.Table_Name: {
                return new String[]{Table_Tasks.Task_ID_Number,Table_Tasks.subTask_ID_Number};
            }
            case Table_Vote_Location.Table_Name:
                return new String[]{Table_Vote_Location.Vote_ID};
            case Table_Vote_Date.Table_Name:
                return new String[]{Table_Vote_Date.Vote_ID};
        }
        return null;
    }


    private static int[] getAllFieldsUpdateIndexsWhere(String table) {
        switch (table) {
            case Table_Events.Table_Name:
                return new int[0];
            case Table_Events_Users.Table_Name:
                return new int[]{Table_Events_Users.User_ID_num};
            case Table_Tasks.Table_Name: {
                return new int[]{Table_Tasks.Task_ID_Number_num,Table_Tasks.subTask_ID_Number_num};
            }
            case Table_Vote_Location.Table_Name:
                return new int[]{Table_Vote_Location.Vote_ID_num};
            case Table_Vote_Date.Table_Name:
                return new int[]{Table_Vote_Date.Vote_ID_num};
        }
        return null;
    }

    public static void updateAll(String table_name,String event_id,List<List<String>> values) {
        try {
            event_id = event_id.replaceAll("\'", "\'\'");
            String set_columns[] = getAllFieldsUpdate(table_name);
            int set_indexs[] = getAllFieldsUpdateIndexs(table_name);
            String where_columns[] = getAllFieldsUpdateWhere(table_name);
            int where_indexs[] = getAllFieldsUpdateIndexsWhere(table_name);

            String query ="";
            for (List<String> val_list : values) {

                query = "update `" + table_name + "` set ";
                String[] vals = val_list.toArray(new String[0]);
                int end = set_indexs.length;
                clean(vals);
                for (int i = 0; i < end; i++) {
                    query += "`" + set_columns[i] + "` = '" + vals[set_indexs[i]-Constants.index_object_sql_diff] + "',";
                }
                query = query.substring(0,query.length()-1);
                query += " where `" + getEventIDField(table_name) + "` = '" + event_id + "'";
                for (int i = 0; i < where_columns.length; i++) {
                    query += " and `"+where_columns[i]+"` = '" + vals[where_indexs[i]-Constants.index_object_sql_diff] +"'";
                }
                query += ";";

                SQLiteDatabase db = getConnection();
                db.execSQL(query);
                db.close();
            }
        }catch(Exception e){
            addToLog(e);
        }
    }



    public static void update(String table_name, String[] set_columns, String[] set_values, String[] where_columns, String[] where_values) {
        try {
            clean(set_values);
            clean(where_values);
            String query = "update `" + table_name + "` set ";
            int end = set_columns.length - 1;
            for (int i = 0; i < end; i++) {
                query += "`" + set_columns[i] + "` = '" + set_values[i] + "',";
            }
            query += "`" + set_columns[end] + "` = '" + set_values[end] + "' ";
            query += "where ";
            end = where_columns.length - 1;
            for (int i = 0; i < end; i++) {
                query += "`" + where_columns[i] + "` = '" + where_values[i] + "' and ";
            }
            query += "`" + where_columns[end] + "` = '" + where_values[end] + "';";

            SQLiteDatabase db = getConnection();
            db.execSQL(query);
            db.close();
        }catch(Exception e){
            addToLog(e);
        }
    }

    public static void insert(String table_name,String value, String[] values) {
        try{
            value = value.replaceAll("\'", "\'\'");
            clean(values);
            String query = "insert into `" + table_name + "` values('";
            query += value + "','";
            int end = values.length - 1;
            for (int i = 0; i < end; i++) {
                query += values[i] + "','";
            }
            query += values[end];
            query += "');";
            SQLiteDatabase db = getConnection();
            db.execSQL(query);
            db.close();
        }catch(Exception e){
            addToLog(e);
        }
    }


    public static void insert(String table_name, String[] values) {
        try{
        clean(values);
        String query = "insert into `" + table_name + "` values('";
        int end = values.length - 1;
        for (int i = 0; i < end; i++) {
            query += values[i] + "','";
        }
        query += values[end];
        query += "');";
        SQLiteDatabase db = getConnection();
        db.execSQL(query);
        db.close();
        }catch(Exception e){
            addToLog(e);
        }
    }

    public static void insertAll(String table_name,String value,List<List<String>> values) {
        try {
            value = value.replaceAll("\'", "\'\'");
            String query = "insert into `" + table_name + "` values";
            for (int j = 0; j < values.size(); j++) {
                query += "('" + value + "','";
                String[] vals = values.get(j).toArray(new String[0]);
                clean(vals);
                int end = vals.length - 1;
                for (int i = 0; i < end; i++) {
                    query += vals[i] + "','";
                }
                query += vals[end];
                query += "'),";
            }
            query = query.substring(0, query.length() - 1);
            query += ";";
            SQLiteDatabase db = getConnection();
            sqlHelper.addToLog(db+"");
            db.execSQL(query);
            db.close();
            sqlHelper.addToLog((sqlHelper.select(null,Table_Events.Table_Name,new String[]{Table_Events.Event_ID},new String[]{value},null))[0].toString());

        }catch (Exception e){
            addToLog(e);
        }

    }


    public static void deleteAll(String table_name,String value,List<List<String>> values) {
        try {
            value = value.replaceAll("\'", "\'\'");

            String query = "delete from `" + table_name + "` WHERE (";
            String [] where = getAllFields(table_name);
            for (String col : where) {
                query += col + ",";
            }
            query = query.substring(0, query.length() - 1);
            query += ") in (";

            for (int j = 0; j < values.size(); j++) {
                query += "('" + value + "','";
                String[] vals = values.get(j).toArray(new String[0]);
                clean(vals);
                int end = vals.length - 1;
                for (int i = 0; i < end; i++) {
                    query += vals[i] + "','";
                }
                query += vals[end];
                query += "'),";
            }
            query = query.substring(0, query.length() - 1);
            query += ");";


            SQLiteDatabase db = getConnection();
            db.execSQL(query);
            db.close();

        }catch (Exception e){
            addToLog(e);
        }

    }

    private static String[] getAllFields(String table) {
        switch (table) {
            case Table_Events.Table_Name:
                return Table_Events.getAllFields();
            case Table_Events_Users.Table_Name:
                return Table_Events_Users.getAllFields();
            case Table_Tasks.Table_Name:
                return Table_Tasks.getAllFields();
            case Table_Vote_Location.Table_Name:
                return Table_Vote_Location.getAllFields();
            case Table_Vote_Date.Table_Name:
                return Table_Vote_Date.getAllFields();
        }
        return null;
    }


    public static void delete(String table, String[] where_columns, String[] where_values, int[] limit) {
        try{
        clean(where_values);
        int end = where_columns.length - 1;
        String query = "delete from `" + table + "` where ";
        for (int i = 0; i < end; i++) {
            query += "`" + where_columns[i] + "` = '" + where_values[i] + "' and ";
        }
        query += "`" + where_columns[end] + "` = '" + where_values[end] + "' ";
        if (false) {//limit != null) {
            query += "limit ";
            end = limit.length - 1;
            for (int i = 0; i < end; i++) {
                query += limit[i] + ", ";
            }
            query += limit[end];
        }

        query += ";";
        SQLiteDatabase db = getConnection();
        db.execSQL(query);
        db.close();
        }catch(Exception e){
            addToLog(e);
        }
    }

    /**
     * @param what          set null for *
     * @param table
     * @param where_columns set null for all
     * @param where_values  assuming that the size is equal to 'where_columns' size!
     * @param limit         set null for no limit
     * @return
     */
    public static ArrayList<String>[] select(String[] what, String table, String[] where_columns, String[] where_values, int[] limit) {
        try{
        clean(where_values);
        String query = "select ";
        if (what == null) {
            query += "* ";
        } else {
            int end = what.length - 1;
            for (int i = 0; i < end; i++) {
                query += "`" + what[i] + "`,";
            }
            query += "`" + what[end] + "` ";
        }
        query += "from `" + table + "` ";
        if (where_columns != null) {
            int end = where_columns.length - 1;
            query += "where ";
            for (int i = 0; i < end; i++) {
                query += "`" + where_columns[i] + "` = '" + where_values[i] + "' and ";
            }
            query += "`" + where_columns[end] + "` = '" + where_values[end] + "'";
        }
        if (limit != null) {
            query += " limit ";
            int end = limit.length - 1;
            for (int i = 0; i < end; i++) {
                query += limit[i] + " , ";
            }
            query += limit[end];
        }
        query += ";";
        SQLiteDatabase db = getConnection();
        Cursor c = db.rawQuery(query, null);
        ArrayList<String>[] result = new ArrayList[c.getColumnCount()];

        for (int i = 0; i < result.length; i++) {
            result[i] = new ArrayList<>();
        }
        while (c.moveToNext()) {
            for (int i = 0; i < result.length; i++) {
                result[i].add(c.getString(i));
            }
        }
        c.close();
        db.close();
        return result;
        }catch(Exception e){
            addToLog(e);
        }
        return null;
    }

    public static void Create_Table(String table_name, String[]Fields, String[]SQL_Params){
        try{
            SQLiteDatabase db = getConnection();
            String execSQL = "create table if not exists "+table_name+" (";
            for(int i=0;i<Fields.length-1;i++){
                execSQL += Fields[i] + " " + SQL_Params[i] + ",";
            }
            execSQL += Fields[Fields.length-1] + " " + SQL_Params[Fields.length-1] + ")";
            db.execSQL(execSQL);
            //db.execSQL("create table if not exists "+table_name+" ("+Constants.Table_Chat_Fields[0]+" varchar NOT NULL,"+Constants.Table_Chat_Fields[1]+" varchar NOT NULL,"
                    //+Constants.Table_Chat_Fields[2]+" varchar NOT NULL,"+Constants.Table_Chat_Fields[3]+" varchar NOT NULL,"+Constants.Table_Chat_Fields[4]+" varchar NOT NULL)");
            db.close();
        }catch(Exception e){
            addToLog(e);
        }
    }

    public static void Delete_Table(String table_name){
        try{
            SQLiteDatabase db = getConnection();
            db.execSQL("DROP TABLE IF EXISTS "+table_name);
            db.close();
        }catch(Exception e){
            addToLog(e);
        }
    }

    private static void clean(String[] values) {
        if (values != null)
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null)
                    values[i] = values[i].replaceAll("\'", "\'\'");
            }
    }

    public static void createALLTables() {
        //Delete_Table(Table_Events.Table_Name);
        Create_Table(Table_Events.Table_Name, Table_Events.getAllFields(), Table_Events.getAllSqlParams());
        Create_Table(Table_Events_Users.Table_Name, Table_Events_Users.getAllFields(), Table_Events_Users.getAllSqlParams());
        Create_Table(Table_Tasks.Table_Name, Table_Tasks.getAllFields(), Table_Tasks.getAllSqlParams());
        Create_Table(Table_Users.Table_Name, Table_Users.getAllFields(), Table_Users.getAllSqlParams());
        Create_Table(Table_Vote_Date.Table_Name, Table_Vote_Date.getAllFields(), Table_Vote_Date.getAllSqlParams());
        Create_Table(Table_Vote_Location.Table_Name, Table_Vote_Location.getAllFields(), Table_Vote_Location.getAllSqlParams());

    }

    public static void addToLog(String eString){
        Logger.getLogger("DEBUG").log(Level.INFO,eString);
        LocalDateTime now = LocalDateTime.now();
        try {
            int year = now.getYear();
            int month = now.getMonthOfYear();
            int day = now.getDayOfMonth();
            int hour = now.getHourOfDay();
            int minute = now.getMinuteOfHour();
            int second = now.getSecondOfMinute();
            int millis = now.getMillisOfSecond();
            String date = day+"/"+month+"/"+year;
            String time = hour+":"+minute+":"+second+":"+millis;
            if(eString.length() > 1000){
                eString = eString.substring(0,1000)+"...";
            }
            new add_logAsyncTask().execute(eString, date, time);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public static void addToLog(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        LocalDateTime now = LocalDateTime.now();
        try {
            int year = now.getYear();
            int month = now.getMonthOfYear();
            int day = now.getDayOfMonth();
            int hour = now.getHourOfDay();
            int minute = now.getMinuteOfHour();
            int second = now.getSecondOfMinute();
            int millis = now.getMillisOfSecond();
            String date = day+"/"+month+"/"+year;
            String time = hour+":"+minute+":"+second+":"+millis;
            String eString = sw.toString();
            if(eString.length() > 1000){
                eString = eString.substring(0,1000)+"...";
            }
            new add_logAsyncTask().execute(eString, date, time);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }


}
