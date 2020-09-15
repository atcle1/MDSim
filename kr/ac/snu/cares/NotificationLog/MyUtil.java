package kr.ac.snu.cares.NotificationLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by summer on 15. 10. 22.
 */
public class MyUtil {
    private static String chars[] = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n",
            "o","p","q","r","s","t","u","v","w","x","y","z"};
    public static String getRandomString(int length)
    {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i=0 ; i<length ; i++)
        {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }

    public static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");

    public static Calendar calStrToCalendar(String calStr) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(calStr.substring(0, 23)));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.err.println("ex " + calStr);
            e.printStackTrace();
            return null;
        }
        return calendar;
    }

    public static long calStrToMillis(String calStr) {
        Calendar cal = MyUtil.calStrToCalendar(calStr);
        if (cal == null)
            return -1;
        return cal.getTimeInMillis();
    }

    public static Calendar MillisToCal(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    public static String MillisToStr(long millis) {
        return sdf.format(MyUtil.MillisToCal(millis).getTime());
    }
}
