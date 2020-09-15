package kr.ac.snu.cares.MDSim.Vo;

import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem.EventType;

public class LogNotificationNotify implements ParsedLogItem {
	public NotificationItem notification = new NotificationItem();
	public LogItem logItem;

	@Override
	public boolean parse(LogItem logItem) {
		String log = logItem.msg.substring(6);
		notification.parse(log);
		notification.eventType= EventType.NOTIFY; 
		notification.postDate = MyUtil.MillisToCal(logItem.timeMillis);
		this.logItem = logItem;
		return true;
	}
}
