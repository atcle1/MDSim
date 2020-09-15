package kr.ac.snu.cares.MDSim.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyUtil {
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
