package kr.ac.snu.cares.MDSim.Vo;

import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;

import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class ScreenOnTerm {
	public long start = 0;
	public long end = 0;
	public boolean bUser;
	
	public LinkedList<Long> touchEvents;
	
	public ScreenOnTerm(long startMillis)
	{
		start = startMillis;
		bUser = false;
	}
	
	public ScreenOnTerm(long startMillis, long endMillis)
	{
		start = startMillis;
		end = endMillis;
	}
	
	public void setEndTime(long endMillis)
	{
		end = endMillis;
	}
	
	public void setstartTime(long startMillis)
	{
		start = startMillis;
	}
	
	public void addUserEvent(long timeMillis)
	{
		if (timeMillis < start) {
			System.err.println("err ScreenOnTerm, addUserEvent before start " + timeMillis + " < " + start);
			return;
		}
		
		if (end != 0) {
			setLastTouch(timeMillis);
			return;
		}
		
		Long millis = new Long(timeMillis);
		if (touchEvents == null) {
			touchEvents = new LinkedList<Long>();
		}
		touchEvents.add(millis);
		bUser = true;
	}
	
	public void setLastTouch(long timeMillis)
	{
		if (Math.abs(timeMillis - end) < 1000) {
			if (touchEvents == null) {
				touchEvents = new LinkedList<Long>();
			}
			touchEvents.add(end);
			bUser = true;
		} else {
			System.err.println(MyUtil.MillisToStr(timeMillis) + " touch event occured, but screen off is " + MyUtil.MillisToStr(end));
		}
	}
	
	public long getLength()
	{
		long length = end - start;
		if (length >= 0)
			return length;
		return 0;
	}
	
	
	public static class TimeComp implements Comparator<ScreenOnTerm> {
		@Override
		public int compare(ScreenOnTerm arg0, ScreenOnTerm arg1) {
			// TODO Auto-generated method stub
			return (int)(arg0.getLength() - arg1.getLength());
		}
	}
	
	@Override
	public String toString()
	{
		String r = "";
		r += MyUtil.MillisToStr(start) + "~" + MyUtil.MillisToStr(end) + 
				" : " + getLength()/1000;
		return r;
	}
	
	
}
