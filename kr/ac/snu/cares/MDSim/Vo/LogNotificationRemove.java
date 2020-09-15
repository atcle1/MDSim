package kr.ac.snu.cares.MDSim.Vo;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem.EventType;

public class LogNotificationRemove implements ParsedLogItem {
	public NotificationItem notification = new NotificationItem();
	public LogItem logItem;
	
	@Override
	public boolean parse(LogItem logItem) {
		notification.eventType= EventType.REMOVE;
		String log = logItem.msg.substring(6);
		String split[] = log.split(";");
		
		notification.key = split[0];
		notification.parseTag(notification.key);
		
		if (split[1].contains("sd=true")) {
			notification.bSenderDelete = true;
		} else if (split[1].contains("sd=false")) {
			notification.bSenderDelete = false;
		} else {
			MDSim.logger.warning("parse err sd " + split[1]);
		}
		
		if (split[2].contains("reason=")) {
			notification.deleteReason = Integer.parseInt(split[2].substring(7));
		} else {
			MDSim.logger.warning("parse err reason " + split[2]);
		}
		
		if (split[3].contains("post=")) {
			notification.cancelDate = MyUtil.calStrToCalendar(split[3].substring(5));
		} else {
			MDSim.logger.warning("parse err post " + split[3]);
		}
		this.logItem = logItem;
		return true;
	}
}