package rnp.ezvent.backend.utils;

import rnp.ezvent.backend.models.Chat;
import rnp.ezvent.backend.models.Event;
import rnp.ezvent.backend.models.RegistrationRecord;
import rnp.ezvent.backend.models.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {

    static {
        ObjectifyService.register(RegistrationRecord.class);
        ObjectifyService.register(Event.class);
      //  ObjectifyService.register(Event_User.class);
        ObjectifyService.register(User.class);
      //  ObjectifyService.register(Task.class);
        ObjectifyService.register(Chat.class);
      //  ObjectifyService.register(Vote_Date.class);
      //  ObjectifyService.register(Vote_Location.class);
    }


    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
