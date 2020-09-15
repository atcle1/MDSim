package kr.ac.snu.cares.MDSim.Device;

import java.util.Iterator;
import java.util.LinkedList;

import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.LogDynamicEvent;
import kr.ac.snu.cares.MDSim.Vo.LogItem;

public class DynamicEventQueue {
	private LinkedList<LogItem> dynamicEventLogList;
	private static DynamicEventQueue instance;
	private DynamicEventQueue() {
		 dynamicEventLogList = new LinkedList<LogItem>();
	}
	
	public static DynamicEventQueue getInstance() {
		if (instance != null) return instance;
		instance = new DynamicEventQueue();
		return instance;
	}
	
	public void init() {
		dynamicEventLogList.clear();
	}
	
	public void addDynamicEvent(LogItem item) {
		dynamicEventLogList.offer(item);
		// System.out.println("d queue add " + item);
	}
	
	public void removeMessages() {
		dynamicEventLogList.clear();
	}
	
	public boolean removeMessages(String event) {
		boolean bRemoved = false;
		Iterator<LogItem> iter = dynamicEventLogList.iterator();
		while (iter.hasNext()) {
			LogItem item = iter.next();
			LogDynamicEvent pitem = (LogDynamicEvent)item.parsedItem;
			if (pitem.event.equals(event)) {
				iter.remove();
				bRemoved = true;
			}
		}
		return bRemoved;
	}
	
	public boolean removeMessages(String event, Object obj) {
		Iterator<LogItem> iter = dynamicEventLogList.iterator();
		while (iter.hasNext()) {
			LogItem item = iter.next();
			LogDynamicEvent pitem = (LogDynamicEvent)item.parsedItem;
			if (pitem.event.equals(event) && pitem.obj.equals(obj)) {
				iter.remove();
				return true;
			}
		}
		return false;
	}
	
	public LogItem nextDynamicEventLogItem() {
		LogItem dynamicEventLog = null;
		dynamicEventLog = dynamicEventLogList.poll();
		return dynamicEventLog;
	}
		
	public LogItem peekDynamicEventLogItem() {
		LogItem dynamicEventLog = null;
		dynamicEventLog = dynamicEventLogList.peek();
		return dynamicEventLog;
	}
	
	public void removes(Object obj) {
		
	}
	
	public int size() {
		return dynamicEventLogList.size();
	}
	
	@ Override
	public String toString() {
		String str = "";
		Iterator<LogItem> iter = dynamicEventLogList.iterator();
		while (iter.hasNext()) {
			LogItem item = iter.next();
			str += MyUtil.MillisToStr(item.timeMillis) + " " + item.msg + " remained\n";
		}
		return str;
	}
}
