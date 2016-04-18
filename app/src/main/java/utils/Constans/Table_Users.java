package utils.Constans;

/**
 * Created by Ravid on 28/10/2015.
 */
public final class Table_Users {
    public final static String Table_Name = "Users";

    public final static String User_ID = "User_ID";
    private final static String User_ID_SQL_Params = "varchar NOT NULL";
    public final static String Nickname = "Nickname";
    private final static String Nickname_SQL_Params = "varchar NOT NULL";
    public final static String Register = "Register";
    private final static String Register_SQL_Params = "varchar NOT NULL";

    public final static int User_ID_num = 0;
    public final static int Nickname_num = 1;
    public final static int Register_num = 2;

    public final static String[] getAllFields(){
        return new String[]{User_ID, Nickname, Register};
    }

    public final static String[] getAllSqlParams(){
        return new String[]{User_ID_SQL_Params, Nickname_SQL_Params, Register_SQL_Params};
    }

    public static int Size(){
        return getAllFields().length;
    }
}
