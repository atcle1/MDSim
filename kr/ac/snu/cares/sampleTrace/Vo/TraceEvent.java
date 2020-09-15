package kr.ac.snu.cares.sampleTrace.Vo;

import java.util.Comparator;

import kr.ac.snu.cares.MDSim.Util.MyUtil;


public class TraceEvent implements Cloneable {
	protected long time;
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public String getTimeStr() {
		if (time > 1000000000000L) {
			return MyUtil.MillisToStr(time) + " event";
		} else {
			long temp = time / 1000;
			int hour = (int) (temp / (60 * 60));
			temp = temp - (hour * 60 * 60);
			int min = (int) (temp) / (60);
			temp = temp - (min * 60);
			int sec = (int) temp;
			return hour +":" + min + ":" + sec;
		}
	}
	
	public String toString() {
		return getTimeStr() + " event";
		
	}
	
	public static class TimeComp implements Comparator<TraceEvent> {
		@Override
		public int compare(TraceEvent arg0, TraceEvent arg1) {
			// TODO Auto-generated method stub
			return (int)(arg0.time - arg1.time);
		}
	}
	
	public TraceEvent clone()
	{
		TraceEvent objReturn = null;
		try {
			objReturn = (TraceEvent)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objReturn;
	}
	
}
