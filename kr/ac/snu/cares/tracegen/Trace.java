package kr.ac.snu.cares.tracegen;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import kr.ac.snu.cares.tracegen.Vo.InitialStat;
import kr.ac.snu.cares.tracegen.Vo.NotificationEvent;
import kr.ac.snu.cares.tracegen.Vo.TouchEvent;
import kr.ac.snu.cares.tracegen.Vo.TraceEvent;
import kr.ac.snu.cares.tracegen.Vo.TraceStat;
import kr.ac.snu.cares.tracegen.Vo.TraceUtil;

public class Trace {
	public TraceStat stat;	
	public LinkedList<TraceEvent> trace;
	
	public static Trace get(InitialStat initialStat) {
		Trace instance = new Trace();
		instance.trace = new LinkedList<TraceEvent>();
		int timeMillis;
		for (int i = 0; i < initialStat.notifyCnt; i++) {
			timeMillis = TraceUtil.getRandom().nextInt(initialStat.timeLength);
			NotificationEvent notificationEvent = new NotificationEvent();
			notificationEvent.setTime(timeMillis);
			instance.trace.add(notificationEvent);
		}
		
		for (int i = 0; i < initialStat.touchCnt; i++) {
			timeMillis = TraceUtil.getRandom().nextInt(initialStat.timeLength);
			TouchEvent touchEvent = new TouchEvent();
			touchEvent.setTime(timeMillis);
			instance.trace.add(touchEvent);
		}
		Collections.sort(instance.trace, TraceUtil.getTimeComp());
		instance.stat = TraceStat.from(initialStat);
		instance.calcStat();
		return instance;
	}
	
	public void calcStat() {
		int prevCnt = 0;
		int prevEventTime = 0;
		
		if (stat.initialStat.notifyCnt > 2) { 
			int[] notificationTerm = new int[stat.initialStat.notifyCnt - 1];
			prevCnt = prevEventTime = 0;
			Iterator<TraceEvent> itr = trace.iterator();
			while (itr.hasNext()) {
				TraceEvent event = itr.next();
				if (NotificationEvent.class == event.getClass()) {
					if (prevCnt == 0) {
						prevEventTime = event.getTime();
					} else {
						notificationTerm[prevCnt - 1] = event.getTime() - prevEventTime;
						prevEventTime = event.getTime(); 
					}
					prevCnt++;
				}
			}
			float calcStat[] = TraceUtil.getStat(notificationTerm);
			stat.notiNotifyIntervalAvg = calcStat[0];
			stat.notiNotifyIntervalStd = calcStat[1];
		} else {
			stat.notiNotifyIntervalAvg = Float.NaN;
			stat.notiNotifyIntervalStd = Float.NaN;
		}
		
		if (stat.initialStat.touchCnt > 2) { 
			int[] term = new int[stat.initialStat.touchCnt - 1];
			prevCnt = prevEventTime = 0;
			Iterator<TraceEvent> itr = trace.iterator();
			while (itr.hasNext()) {
				TraceEvent event = itr.next();
				if (TouchEvent.class == event.getClass()) {
					if (prevCnt == 0) {
						prevEventTime = event.getTime();
					} else {
						term[prevCnt - 1] = event.getTime() - prevEventTime;	
						prevEventTime = event.getTime(); 
					}
					prevCnt++;
				}
			}
			float calcStat[] = TraceUtil.getStat(term);
			stat.touchIntervalAvg = calcStat[0];
			stat.touchIntervalStd = calcStat[1];
		} else {
			stat.touchIntervalAvg = Float.NaN;
			stat.touchIntervalStd = Float.NaN;
		}
	}
	
	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		Iterator<TraceEvent> itr = trace.iterator();
		while (itr.hasNext()) {
			TraceEvent event = itr.next();
			buffer.append(event.getTime() + " " + event.getClass() + "\n");
		}
		return buffer.toString();
	}
}
