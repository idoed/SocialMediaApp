package app.calcounterapplication.com.socialmediaapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by idoed on 07/05/2018.
 */

public class DateFormat {
    String saveCurrentDate;
    String saveCurrentTime;
    String postRandomName;

    public DateFormat() {
    }

    public String getCurrentStoryDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate =currentDate.format(calendar.getTime());
        Calendar calendartime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm");
        saveCurrentTime =currentTime.format(calendar.getTime());
        postRandomName=saveCurrentDate+saveCurrentTime;
        return postRandomName;
    }
    public String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm");
        saveCurrentTime =currentTime.format(calendar.getTime());
        return saveCurrentTime;
    }
    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate =currentDate.format(calendar.getTime());
        return saveCurrentDate;
    }

}
