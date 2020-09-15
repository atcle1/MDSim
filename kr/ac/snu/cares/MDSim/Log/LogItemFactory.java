package kr.ac.snu.cares.MDSim.Log;

import java.sql.ResultSet;
import java.util.logging.Logger;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.LogBattery;
import kr.ac.snu.cares.MDSim.Vo.LogDynamicEvent;
import kr.ac.snu.cares.MDSim.Vo.LogForegroundApp;
import kr.ac.snu.cares.MDSim.Vo.LogItem;
import kr.ac.snu.cares.MDSim.Vo.LogItem.LogType;
import kr.ac.snu.cares.MDSim.Vo.LogKScreenBrightness;
import kr.ac.snu.cares.MDSim.Vo.LogKScreenIdle;
import kr.ac.snu.cares.MDSim.Vo.LogKSuspend;
import kr.ac.snu.cares.MDSim.Vo.LogKTouch;
import kr.ac.snu.cares.MDSim.Vo.LogKWakeup;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationRemove;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationNotify;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationUpdate;
import kr.ac.snu.cares.MDSim.Vo.LogPowerState;
import kr.ac.snu.cares.MDSim.Vo.LogScreenOnOff;
import kr.ac.snu.cares.MDSim.Vo.LogScreenTimeoutSet;
import kr.ac.snu.cares.MDSim.Vo.LogTextMessage;
import kr.ac.snu.cares.MDSim.Vo.ParsedLogItem;
;
public class LogItemFactory {
	private static final Logger logger = Logger.getLogger(LogItemFactory.class.getName());
	
	public static LogItem get(ResultSet rs, int columnSize) {
		LogItem logItem = new LogItem();
		try {
			logItem.idx = rs.getInt(1);
			logItem.timeMillis = MyUtil.calStrToMillis(rs.getString(2));
			logItem.msg = rs.getString(3);
			logItem.msg = logItem.msg.trim();
			if (columnSize == 4) {
				logItem.deviceLogType = rs.getInt(4);
			}
			logItem.prefix = logItem.msg.split("[ \r\n]+", 0)[0].trim();
			//System.out.println("prefix [" + logItem.prefix + "]");
			
			// detect logtype
			detectLogType(logItem);
			
			switch (logItem.deviceLogType) {
			case LogItem.SOURCE_MOBILE_MDK:
				logItem = getMobileMDKLogItem(logItem);
				break;
			case LogItem.SOURCE_MOBILE_MDP:
				logItem = getMobileMDPLogItem(logItem);
				break;
			case LogItem.SOURCE_WEAR_MDK:
				logItem = getWearMDKLogItem(logItem);
				break;
			case LogItem.SORUCE_WEAR_MDP:
				logItem = getWearMDPLogItem(logItem);
				break;
			case LogItem.SOURCE_BT_LOG:
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		return logItem;
	}
	
	private static void detectLogType(LogItem item) {
		if (item.prefix == null || item.prefix.equals("")) {
			item.logType = LogType.UNKNOWN;
			return;
		}
		
		switch (item.prefix) {
		case "SCR":
			if (item.deviceLogType == LogItem.SOURCE_MOBILE_MDK || item.deviceLogType == LogItem.SOURCE_WEAR_MDK)
				item.logType = LogType.KSCREEN_ONOFF;
			else
				item.logType = LogType.SCREEN_ONOFF;
			break;
		case "WKU":
			item.logType = LogType.KWAKEUP;
			break;
		case "SUS":
			item.logType = LogType.KSUSPEND;
			break;
		case "FFGA":
			item.logType = LogType.FOREGROUNDAPP;
			break;
		case "BAT":
			item.logType = LogType.BATTERY;
			break;
		case "SBR":
			item.logType = LogType.SCREEN_BRIGHTNESS;
			break;
		case "NOP":
			item.logType = LogType.NOTIFICATION_ENQ;
			break;
		case "NOS":	// foreground notificaiton removed (update)
			item.logType = LogType.NOTIFICATION_UPDATE;
			break;
		case "NOU":
			item.logType = LogType.NOTIFICATION_UPDATE;
			break;
		case "NOC":
			item.logType = LogType.NOTIFICATION_CANCEL;
			break;
		case "TXT":
			item.logType = LogType.TEXTMSG;
			break;
		case "TFC":
			item.logType = LogType.TEXTMSG;
			break;
		case "KP": case "KS":
			item.logType = LogType.KEY_PRESSED;
			break;
		case "FPAN":
			item.logType = LogType.FPAN;
			break;
		case "FSTB":
			item.logType = LogType.STATUSBAR_TOUCH;
			break;
		case "PAD":
			item.logType = LogType.PACKAGE_INSTALL;
			break;
		case "PRP":
			item.logType = LogType.PACKAGE_REPLACE;
			break;
		case "SMSR":
			item.logType = LogType.SMS_RECEIVED;
			break;
		case "CST":
			item.logType = LogType.CALLSTATE;
			break;
		case "PRM":
			item.logType = LogType.PACKAGE_REMOVE;
			break;
		case "PAUSE": case "START": case "RESUME":
			item.logType = LogType.MDPSTATE;
			break;
		case "PST":
			item.logType = LogType.POWERSTATE;
			break;
		case "SID":
			item.logType = LogType.SCREEN_IDLE;
			break;
		case "STO":
			item.logType = LogType.SCREEN_TIMEOUT_SET;
			break;
		case "DT":
			item.logType = LogType.KTOUCH;
			break;
		default:
			logger.warning("unknown prefix [" + item.prefix + "] / " + item);
			item.logType = LogType.UNKNOWN;
			break;
		}
	}

	public static LogItem getMobileMDKLogItem(LogItem item) {
		ParsedLogItem parsedLogItem = null;
		switch (item.logType) {
		case KSCREEN_ONOFF:
			parsedLogItem = new LogScreenOnOff();
			break;
		case KSUSPEND:
			parsedLogItem = new LogKSuspend();
			break;
		case KWAKEUP:
			parsedLogItem = new LogKWakeup();
			break;
		case SCREEN_BRIGHTNESS:
			parsedLogItem = new LogKScreenBrightness();
			break;
		case KTOUCH:
			parsedLogItem = new LogKTouch();
			break;
		default:
			break;	
		}
		if (parsedLogItem != null && parsedLogItem.parse(item)){
			item.parsedItem = parsedLogItem;
		}
		return item;
	}
	
	public static LogItem getMobileMDPLogItem(LogItem item) {
		ParsedLogItem parsedLogItem = null;
		switch (item.logType) 
		{
			case FOREGROUNDAPP:
				parsedLogItem = new LogForegroundApp();
				break;
			case NOTIFICATION_ENQ:
				parsedLogItem = new LogNotificationNotify();
				break;
			case NOTIFICATION_UPDATE:
				parsedLogItem = new LogNotificationUpdate();
				break;
			case NOTIFICATION_CANCEL:
				parsedLogItem = new LogNotificationRemove();
				break;
			case SCREEN_ONOFF:
				parsedLogItem = new LogScreenOnOff();
				break;
			case TEXTMSG:
				parsedLogItem = new LogTextMessage();
				break;
			case POWERSTATE:
				parsedLogItem = new LogPowerState();
				break;
			case SCREEN_TIMEOUT_SET:
				parsedLogItem = new LogScreenTimeoutSet();
				break;
			case BATTERY:
				parsedLogItem = new LogBattery();
			default:
				break;
			}
		if (parsedLogItem != null && parsedLogItem.parse(item)){
			item.parsedItem = parsedLogItem;
		}
		return item;
	}
	public static LogItem getWearMDKLogItem(LogItem item) {
		ParsedLogItem parsedLogItem = null;
		switch (item.logType) 
		{
			case KSCREEN_ONOFF:
				parsedLogItem = new LogScreenOnOff();
				break;
			case KSUSPEND:
				parsedLogItem = new LogKSuspend();
				break;
			case KWAKEUP:
				parsedLogItem = new LogKWakeup();
				break;
			case SCREEN_BRIGHTNESS:
				parsedLogItem = new LogKScreenBrightness();
				break;
			case SCREEN_IDLE:
				parsedLogItem = new LogKScreenIdle();
				break;
			default:
				break;	
		}
		if (parsedLogItem != null && parsedLogItem.parse(item)){
			item.parsedItem = parsedLogItem;
		}
		return item;
	}
	
	public static LogItem getWearMDPLogItem(LogItem item) {
		ParsedLogItem parsedLogItem = null;
		switch (item.logType) 
		{
			case SCREEN_ONOFF:
				parsedLogItem = new LogScreenOnOff();
				break;
			case TEXTMSG:
				parsedLogItem = new LogTextMessage();
				break;
			case POWERSTATE:
				parsedLogItem = new LogPowerState();
				break;
			case BATTERY:
				parsedLogItem = new LogBattery();
			default:
				break;
		}
		if (parsedLogItem != null && parsedLogItem.parse(item)){
			item.parsedItem = parsedLogItem;
		}
		return item;
	}		
	
	public static LogItem getDynamicEventLogItem(long timemillis, int deviceType, String msg, String event, Object obj) {
		LogItem logItem = new LogItem();
		logItem.timeMillis = timemillis;
		logItem.deviceLogType = deviceType;
		logItem.prefix = "DYEVENT";
		logItem.logType = LogType.DYNAMICEVENT;
		logItem.msg = msg;
		LogDynamicEvent parsedDynamicEvent = new LogDynamicEvent();
		parsedDynamicEvent.event = event;
		parsedDynamicEvent.obj = obj;
		parsedDynamicEvent.logItem = logItem;
		logItem.parsedItem = (ParsedLogItem) parsedDynamicEvent;
		return logItem;
	}
}