package utils.Event_Helper_Package;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Ravid on 17/02/2016.
 */
public class Contacts_List {
    //Key - Friend ID(phone/mail), Val - Friend nickname.
    public static HashMap<String, String> contacts = new HashMap<>();//For find by User_ID.
    public static SortedMap<String, String> TreeMap_contacts = new TreeMap<String, String>();//For search and sort.

}
