package kr.ac.snu.cares.sampleTrace;

public class SamplePeriod {
	public static final long HOUR_MILLIS = 60 * 60 * 1000;
	public static final long MIN_MILLIS = 60 *1000;
	public long start;
	public long end;
	
	public SamplePeriod() {
		
	}
	
	public SamplePeriod(long start, long end) {
		this.start = start;
		this.end = end;
	}
	
	public long getPeriodMillis() {
		return end - start;
	}
	
	public boolean contains(long millis) {
		if (millis >= start && millis <= end)
			return true;
		return false;
	}
	
	public void setTime(boolean bStart, int hour, int min, int seconds) {
		if (bStart) {
			start = hour * HOUR_MILLIS + min * MIN_MILLIS + seconds * 1000;
		} else {
			end = hour * HOUR_MILLIS + min * MIN_MILLIS + seconds * 1000;
		}
	}
	
	public void setTime(boolean bStart, long time) {
		if (bStart) {
			start = time;
		} else {
			end = time;
		}
	}
	
	@Override
	public String toString() {
		int []s_hms = SamplePeriod.convertToHMS(start);
		int []e_hms = SamplePeriod.convertToHMS(end);
		return HMStoStr(s_hms) + "~" + HMStoStr(e_hms); 
	}
	
	public static int[] convertToHMS(long millis)
	{
		int []hms = new int[3];
		long temp;
		hms[0] = (int)(millis/HOUR_MILLIS);
		temp = millis - (hms[0] * HOUR_MILLIS);
		hms[1] = (int)(temp/MIN_MILLIS);
		temp = temp - (hms[1] * MIN_MILLIS);
		hms[2] = (int)(temp/1000);
		return hms;
	}
	public String HMStoStr(int []hms)
	{
		return String.format("%02d:%02d:%02d", hms[0], hms[1], hms[2]);
	}
}
