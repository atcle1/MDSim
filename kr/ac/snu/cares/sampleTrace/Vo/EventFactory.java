package kr.ac.snu.cares.sampleTrace.Vo;

public class EventFactory {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static NotificationEvent getNotificationEvent(String str) {
		NotificationEvent event = new NotificationEvent();
		event.setNotication(str);
		return event;
	}
	
	public static TouchEvent getTouchEvent(long timeMillis) {
		TouchEvent event = new TouchEvent();
		event.setTime(timeMillis);
		return event;
	}
	
	
}
