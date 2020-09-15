package kr.ac.snu.cares.MDSim;

import java.util.Calendar;
import java.util.logging.Logger;

import kr.ac.snu.cares.MDSim.Device.DynamicEventQueue;
import kr.ac.snu.cares.MDSim.Device.HardwareContext;
import kr.ac.snu.cares.MDSim.Device.NotificationManager;
import kr.ac.snu.cares.MDSim.Device.WindowManager;
import kr.ac.snu.cares.MDSim.Log.LogReader;
import kr.ac.snu.cares.MDSim.Report.UserSessionDupReport;
import kr.ac.snu.cares.MDSim.Report.UserSessionReport;
import kr.ac.snu.cares.MDSim.Report.WatchSessionReport;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Util.NotificationLogGenerator;
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
import kr.ac.snu.cares.MDSim.Vo.LogNotificationNotify;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationRemove;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationUpdate;
import kr.ac.snu.cares.MDSim.Vo.LogPowerState;
import kr.ac.snu.cares.MDSim.Vo.LogScreenOnOff;

public class MDSim {
	public static final Logger logger = Logger.getLogger(MDSim.class.getName());
	
	public String dataset = "";
	public String person_name;
	public String dir;
	public boolean bSnmEnable;
	
	private LogReader logReader;
	private DynamicEventQueue dynamicEventQueue;
	private static MDSim instance;
	
	public HardwareContext mobileHwContext = new HardwareContext();
	public HardwareContext wearHwContext = new HardwareContext();
	public WindowManager windowManager;
	public NotificationManager notificationManager;
	public NotificationLogGenerator notificationLogGenerator;
	public UserSessionReport userSessionReport = new UserSessionReport();
	public UserSessionDupReport userSessionDupReport = new UserSessionDupReport();
	public WatchSessionReport watchSessionReport = new WatchSessionReport();
	
	private MDSim() {
	}
	
	public void init() {
		windowManager = new WindowManager();
		notificationLogGenerator = new NotificationLogGenerator();
		notificationLogGenerator.setAbsoluteTime(true);
		
		notificationManager = new NotificationManager(this);
		notificationManager.snm.bEnabled = bSnmEnable;
		
		dynamicEventQueue = DynamicEventQueue.getInstance();
		dynamicEventLogItem = null;
		dbLogItem = null;
		
		
	}
	
	public static MDSim getInstance(LogReader logReader)
	{
		if (logReader == null) return null;
		instance = new MDSim();
		instance.logReader = logReader;
		return instance;
	}
	
	public static MDSim getInstance() {
		return instance;
	}
	
	boolean bLogStart = false;
	boolean bMKLogStart = false;
	boolean bMPLogStart = false;
	boolean bWKLogStart = false;
	boolean bWPLogStart = false;
	public Calendar startTime;
	public Calendar endTime;
	public long elapsedTime;
	
	LogItem dynamicEventLogItem = null;
	LogItem dbLogItem = null;
	public LogItem nextLogItem() {
		LogItem item = null;
		
		if (dbLogItem == null)
			dbLogItem = logReader.nextLogItem();
		dynamicEventLogItem = dynamicEventQueue.peekDynamicEventLogItem();

		if (dbLogItem == null) return null;
		
		if (dynamicEventLogItem != null && dbLogItem.timeMillis > dynamicEventLogItem.timeMillis) {
			item = dynamicEventLogItem;
			dynamicEventQueue.nextDynamicEventLogItem();
		} else {
			item = dbLogItem;
			dbLogItem = null;
		}
		
		return item;
	}
	
	public static long currentTimeMillis;
	public static LogItem currentLogItem;
	public static LogItem currentLogItem() {
		return currentLogItem;
	}
	public static long currentTimeMillis() {
		return currentTimeMillis;
	}
	
	public int run() {
		logger.info("MDSim run()");
		//logReader.test();
		LogItem item = null, lastItem = null;
		startTime = null;
		endTime = Calendar.getInstance();
		
		boolean bAnalysisStopEnabled = false;		// flag for analysis stop time enabled
		long analysisStopTime = MyUtil.calStrToMillis("2015-10-02 04:15:00.000");
		
		while ((item = nextLogItem()) != null) {
			// System.out.println(item.msg);
			currentLogItem = item;
			currentTimeMillis = item.timeMillis;
			
			if (MyUtil.MillisToCal(item.timeMillis).get(Calendar.YEAR)<2000) {
				System.out.println("before 2000 : " + item);
				continue;
			}
			if (startTime == null) {
				startTime = Calendar.getInstance();
				startTime.setTimeInMillis(item.timeMillis);
				System.err.println("START : " + MyUtil.MillisToStr(startTime.getTimeInMillis()));
				start();
			}
			
			if (item.logType == LogType.TEXTMSG && !item.msg.equals("TXT : USP")) {
				// System.out.println(item  + " " + item.deviceLogType);
			}
			
			if (bAnalysisStopEnabled && item.timeMillis > analysisStopTime){
				System.err.println("Anasysis stop");
				break;
			}
			
			switch (item.deviceLogType) {
			case LogItem.SOURCE_MOBILE_MDK:
				onMobileKlog(item);
				break;
			case LogItem.SOURCE_MOBILE_MDP:
				onMobilePlog(item);
				break;
			case LogItem.SOURCE_WEAR_MDK:
				onWearKlog(item);
				break;
			case LogItem.SORUCE_WEAR_MDP:
				onWearPlog(item);
				break;
			case LogItem.SOURCE_ERROR:
				break;
			}
			
			lastItem = item;
		}
		endTime.setTimeInMillis(lastItem.timeMillis);
		elapsedTime = endTime.getTimeInMillis() - startTime.getTimeInMillis();
		end();
		
		System.err.println("START : " + MyUtil.MillisToStr(startTime.getTimeInMillis()));
		System.err.println("END   : " + MyUtil.MillisToStr(endTime.getTimeInMillis()));
		System.err.println("ELASPED : " + elapsedTime/(float)(1000 * 60 * 60 * 24));
		long tmp = elapsedTime;
		int day = (int) (tmp / (1000 * 60 * 60 * 24));
		tmp = tmp - (day * (1000 * 60 * 60 * 24));
		int hour = (int) (tmp / (1000 * 60 * 60));
		tmp = tmp - (hour * (1000 * 60 * 60));
		int min = (int) (tmp / (1000 * 60));
		tmp = tmp - (min * (1000 * 60));
		int second = (int) (tmp / (1000));
		tmp = tmp - (min * (1000));
		System.err.println("ELASPED : " + day + " d " + hour + " h " + min + "m " + second + "s");

		return 0;
	}
	
	private void start(){
		watchSessionReport.start();
	}
	
	private void end() {
		System.out.println("MDSim run() end");
		//windowManager.doReport();
		notificationManager.doReport();
		//mobileHwContext.doReport();
		//notificationLogGenerator.flush();
		//userSessionReport.doReport();
		//userSessionDupReport.doReport();
		//watchSessionReport.doReport();
	}
	
	private void onMobileKlog(LogItem logItem) {
		if (bMKLogStart == false && logItem.logType == LogType.KSCREEN_ONOFF) {
			LogScreenOnOff logScreenOnOff = (LogScreenOnOff)logItem.parsedItem;
			bMKLogStart = true;
			windowManager.init(logItem.timeMillis, logScreenOnOff.bScreenOn);
		}
		if (bMKLogStart == false)
			return;
		
		// start log
		switch(logItem.logType) {
		case KSCREEN_ONOFF:
			if (logItem.parsedItem == null) {
				System.err.println("parsedItem is null " + logItem.msg);
				System.exit(1);
			}
			LogScreenOnOff logScreenOnOff = (LogScreenOnOff)logItem.parsedItem;
			//windowManager.setScreen(logItem.timeMillis, logScreenOnOff.bScreenOn);
			//notificationManager.onScreenOnOff(logItem.timeMillis, logScreenOnOff.bScreenOn);
			mobileHwContext.onScreenOnOff(logItem.timeMillis, logScreenOnOff.bScreenOn);
			userSessionReport.onScreenOnOff(logItem.timeMillis, logScreenOnOff.bScreenOn);
			userSessionDupReport.onScreenOnOff(logItem.timeMillis, true, logScreenOnOff.bScreenOn);
			watchSessionReport.onMobileScreenOnOff(logItem.timeMillis, logScreenOnOff.bScreenOn);
			break;
		case KSUSPEND:
			if (logItem.parsedItem == null) {
				System.err.println("parsedItem is null " + logItem.msg);
				System.exit(1);
			}
			LogKSuspend logKsuspend = (LogKSuspend)logItem.parsedItem;
			mobileHwContext.onSuspend(logItem.timeMillis, logKsuspend);
			break;
		case KWAKEUP:
			if (logItem.parsedItem == null) {
				System.err.println("parsedItem is null " + logItem.msg);
				System.exit(1);
			}
			LogKWakeup logKwakeup = (LogKWakeup)logItem.parsedItem;
			mobileHwContext.onWakeup(logItem.timeMillis, logKwakeup);
			break;
		case KTOUCH:
			LogKTouch logKtouch = (LogKTouch)logItem.parsedItem;
			notificationManager.onTouch(logItem.timeMillis);
			userSessionReport.onTouch(logItem.timeMillis);
			break;
			
		}
	}
	private void onWearPlog(LogItem logItem) {
		if (bWPLogStart == false && logItem.logType != LogItem.LogType.TEXTMSG) {
			// resumeLogging 부터 시작하기위함
			return;
		}
		
		switch(logItem.logType) {
		case TEXTMSG:
			if (logItem.msg.contains("resumeLogging")) {
				onWearResumeLogging(logItem);
			}
			break;
		case BATTERY:
			LogBattery batteryState = (LogBattery)logItem.parsedItem;
			userSessionDupReport.onBattery(logItem.timeMillis, false, batteryState.bCharging);
			watchSessionReport.onBattery(logItem.timeMillis, false, batteryState.bCharging);
			break;
		default:
			break;
		}
	}
	
	private void onMobilePlog(LogItem logItem) {
		/*
		if (bMPLogStart == false && logItem.logType == LogType.SCREEN_ONOFF) {
			LogScreenOnOff logScreenOnOff = (LogScreenOnOff)logItem.parsedItem;
			if (logScreenOnOff.bScreenOn) {
				bMPLogStart = true;
			}
		}
		if (bMPLogStart == false)
			return;
		*/
		if (bMPLogStart == false && logItem.logType != LogItem.LogType.TEXTMSG) {
			// resumeLogging 부터 시작하기위함
			return;
		}
		
		switch(logItem.logType) {
		case FOREGROUNDAPP:
			LogForegroundApp logForegroundApp = (LogForegroundApp)logItem.parsedItem;
			windowManager.setTopActivity(logItem.timeMillis, logForegroundApp.resumedPackage);
			break;
		case NOTIFICATION_ENQ:
			LogNotificationNotify notificationEnq = (LogNotificationNotify)logItem.parsedItem;
			notificationManager.enqueue(notificationEnq);
			break;
		case NOTIFICATION_UPDATE:
			LogNotificationUpdate notificationUpdate = (LogNotificationUpdate)logItem.parsedItem;
			notificationManager.update(notificationUpdate);
			break;
		case NOTIFICATION_CANCEL:
			LogNotificationRemove notificationCancel = (LogNotificationRemove)logItem.parsedItem;
			notificationManager.cancel(notificationCancel);
			break;
		case SCREEN_ONOFF:
			LogScreenOnOff logScreenOnOff = (LogScreenOnOff)logItem.parsedItem;
			windowManager.setScreen(logItem.timeMillis, logScreenOnOff.bScreenOn);
			notificationManager.onScreenOnOff(logItem.timeMillis, logScreenOnOff.bScreenOn);
			break;
		case POWERSTATE:
			LogPowerState logPowerState = (LogPowerState)logItem.parsedItem;
			logger.info(MyUtil.MillisToStr(logItem.timeMillis) + "onMobilePower state = " + logPowerState.bBootOn);
			if (logPowerState.bBootOn) {
				windowManager.onBootup(logItem.timeMillis);
				notificationManager.onBootup(logItem.timeMillis);
			} else {
				windowManager.onShutdown(logItem.timeMillis);
				notificationManager.onShutdown(logItem.timeMillis);
			}
			
			break;
		case DYNAMICEVENT:
			LogDynamicEvent parsedEvent = (LogDynamicEvent)logItem.parsedItem;
			notificationManager.onDynamicEvent(logItem, parsedEvent);
			break;
		case BATTERY:
			LogBattery batteryState = (LogBattery)logItem.parsedItem;
			userSessionDupReport.onBattery(logItem.timeMillis, true, batteryState.bCharging);
			watchSessionReport.onBattery(logItem.timeMillis, true, batteryState.bCharging);
			break;
		case TEXTMSG:
			if (logItem.msg.contains("resumeLogging")) {
				onMobileResumeLogging(logItem);
			}
			break;
		default:
			//System.out.println(logItem.prefix);
			break;
		}
	}
	
	private void onMobileResumeLogging(LogItem item) {
		bMPLogStart = true;
		if (item.msg.contains("ds=")) {
			String[] splitMsg = item.msg.split("ds=");
			// state_off 1, state_on 2, doze : 3
			int screenState = Integer.parseInt(splitMsg[1]);
			// logger.info(MyUtil.MillisToStr(item.timeMillis) + "onMobileResume ds = " + screenState);
			if (screenState != 1) {
				windowManager.setScreen(item.timeMillis, true);
				notificationManager.onScreenOnOff(item.timeMillis, true);
			} else {
				windowManager.setScreen(item.timeMillis, false);
				notificationManager.onScreenOnOff(item.timeMillis, false);
			}
			
		}
	}
	
	private void onWearResumeLogging(LogItem item) {
		bWPLogStart = true;
		if (item.msg.contains("ds=")) {
			String[] splitMsg = item.msg.split("ds=");
			int screenState = Integer.parseInt(splitMsg[1]);
			System.out.println("ds = " + screenState);
			if (screenState == 2) {
				// wear screen on
			} else {
				// wear screen off
			}
		}
	}

	private void onWearKlog(LogItem logItem) {
		if (bWKLogStart == false && logItem.logType == LogType.KSCREEN_ONOFF) {
			LogScreenOnOff logScreenOnOff = (LogScreenOnOff)logItem.parsedItem;
			bWKLogStart = true;
			windowManager.init(logItem.timeMillis, logScreenOnOff.bScreenOn);
		}
		if (bWKLogStart == false){
			//System.out.println("HMMMM....");
			//System.exit(1);
			return;
		}
		
		switch(logItem.logType) {
		case KSCREEN_ONOFF:
			if (logItem.parsedItem == null) {
				System.err.println("parsedItem is null " + logItem.msg);
				System.exit(1);
			}
			LogScreenOnOff logScreenOnOff = (LogScreenOnOff)logItem.parsedItem;
			watchSessionReport.onWatchScreenOnOff(logItem.timeMillis, logScreenOnOff.bScreenOn);
			break;
		case KSUSPEND:
			if (logItem.parsedItem == null) {
				System.err.println("parsedItem is null " + logItem.msg);
				System.exit(1);
			}
			LogKSuspend logKsuspend = (LogKSuspend)logItem.parsedItem;
			mobileHwContext.onSuspend(logItem.timeMillis, logKsuspend);
			break;
		case KWAKEUP:
			if (logItem.parsedItem == null) {
				System.err.println("parsedItem is null " + logItem.msg);
				System.exit(1);
			}
			LogKWakeup logKwakeup = (LogKWakeup)logItem.parsedItem;
			mobileHwContext.onWakeup(logItem.timeMillis, logKwakeup);
			break;
		case SCREEN_BRIGHTNESS:
			LogKScreenBrightness logScreenBrightness = (LogKScreenBrightness)logItem.parsedItem;
			userSessionDupReport.onScreenOnOff(logItem.timeMillis, false, !logScreenBrightness.bScreenBrightnessZero);
			watchSessionReport.onWatchBrightness(logItem.timeMillis, logScreenBrightness.brightness);
			break;
		case SCREEN_IDLE:
			LogKScreenIdle logScreenIdle = (LogKScreenIdle)logItem.parsedItem;
			watchSessionReport.onWatchScreenSid(logItem.timeMillis, logScreenIdle.bIdle);
			break;
		}
	}
}
