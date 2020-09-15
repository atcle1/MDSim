package kr.ac.snu.cares.MDSim.Vo;

import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem.EventType;

public class LogNotificationUpdate implements ParsedLogItem {
	public NotificationItem notification = new NotificationItem();
	public LogItem logItem;
	@Override
	public boolean parse(LogItem logItem) {
		String log = logItem.msg.substring(6);
		notification.eventType= EventType.UPDATE;
		notification.updateDate = MyUtil.MillisToCal(logItem.timeMillis);
		notification.parse(log);
		this.logItem = logItem;
		return true;
	}
}
