package rnp.ezvent.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.repackaged.org.joda.time.LocalDateTime;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Named;

import rnp.ezvent.backend.utils.Constans.Constants;
import rnp.ezvent.backend.utils.MySQL_Util;

/**
 * Created by pinhas on 21/10/2015.
 */
@Api(name = "ezvent", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "appsLogs",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class apps_logs {
    @ApiMethod(name = "addLog", path = "addLog")
    public void addLog(@Named("exception_string") String eString, @Named("date") String date, @Named("time") String time) {
        try {
            MySQL_Util.insert("Logs_App", new String[]{eString,date,time});
        } catch (Exception e) {
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
                String _date = day+"/"+month+"/"+year;
                String _time = hour+":"+minute+":"+second+":"+millis;
                MySQL_Util.insert("Logs", new String[]{sw.toString(), _date, _time});
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}