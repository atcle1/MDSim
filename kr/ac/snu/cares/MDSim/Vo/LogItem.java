package kr.ac.snu.cares.MDSim.Vo;

import java.sql.ResultSet;
import java.util.Calendar;

import javax.rmi.CORBA.Util;

import kr.ac.snu.cares.MDSim.Util.MyUtil;

public class LogItem {
	public static String [] device_str = {"ER", "MK", "MP", "WK", "WP", "BT"};
	public static final int SOURCE_ERROR = 0;
	public static final int SOURCE_MOBILE_MDK = 1;
	public static final int SOURCE_MOBILE_MDP = 2;
	public static final int SOURCE_WEAR_MDK = 3;
	public static final int SORUCE_WEAR_MDP = 4;
	public static final int SOURCE_BT_LOG = 5;
	
	public enum LogType {UNKNOWN, TEXTMSG, KSUSPEND, KWAKEUP, KTOUCH,
		NOTIFICATION_ENQ, NOTIFICATION_UPDATE, NOTIFICATION_CANCEL,
		KSCREEN_ONOFF, SCREEN_BRIGHTNESS, SCREEN_ONOFF, SCREEN_IDLE, SCREEN_TIMEOUT_SET,
		CALLSTATE, BATTERY, FOREGROUNDAPP, FPAN, KEY_PRESSED, STATUSBAR_TOUCH,
		SMS_RECEIVED, PACKAGE_INSTALL, PACKAGE_REPLACE, PACKAGE_REMOVE,
		MDPSTATE, POWERSTATE,
		DYNAMICEVENT}
	
	// LOGTYPE
	public int deviceLogType;
	
	// raw log data
	public int idx;
	public long timeMillis;
	public String msg;
	
	// basic log type
	public String prefix;
		
	// parsedData
	public LogType logType;
	public ParsedLogItem parsedItem;
	
	public String toString()
	{
		return MyUtil.MillisToStr(timeMillis) + " " + device_str[deviceLogType] + " " + msg;
	}
}
