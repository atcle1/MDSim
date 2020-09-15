package kr.ac.snu.cares.sampleTrace.Vo;

import javax.management.Notification;

import kr.ac.snu.cares.NotificationLog.MyUtil;
import kr.ac.snu.cares.NotificationLog.ParsedNotification;

public class NotificationEvent extends TraceEvent implements Cloneable{
	public ParsedNotification notificaiton = null;
	
	public void setNotication(String str) {
		notificaiton = ParsedNotification.from(str);
		time = notificaiton.time;
	}
	
	public String toString() {
		return getTimeStr() + " " + notificaiton.toString();
	}
	
	public String toString(long offset) {
		return (time+offset) + " " + notificaiton.toString();
	}
	
	
	public TraceEvent clone()
	{
		NotificationEvent objReturn = null;
		objReturn = (NotificationEvent)super.clone();
		if (notificaiton != null)
			objReturn.notificaiton = notificaiton.clone();
		return objReturn;
	}
}
