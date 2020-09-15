package kr.ac.snu.cares.MDSim.Device.SmartNotification;

import kr.ac.snu.cares.MDSim.Vo.NotificationItem;

public class SmartNotificationItem {
	public static final int STATE_DELAYED = 1;
	public static final int STATE_MAY_PERCEIVED = 2;
	public static final int STATE_PERCEIVED = 3;
	public static String [] STATE_STR = {"STATE_UNKNOWN", "STATE_DELAYED", "STATE_MAY_PERCEIVED", "STATE_PERCEIVED"};  

	// state is not object identifier, only sbn !
	public NotificationItem sbn;
	public int state;
	public boolean mustSendCancelIfNotNotify;

	public SmartNotificationItem(NotificationItem sbn, int state) {
		this.sbn = sbn;
		this.state = state;
		mustSendCancelIfNotNotify = false;
	}
	public String getKey() {
		return sbn.key;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmartNotificationItem other = (SmartNotificationItem) obj;
		return sbn.key.equals(other.sbn.key);
	}

	@Override
	public int hashCode() {
		return sbn.key.hashCode();
    }
	
	@Override
	public String toString() {
		String str = "";
		str = sbn.getKey() + " " + STATE_STR[state];
		return str;
	}
}