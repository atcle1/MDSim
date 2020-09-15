package kr.ac.snu.cares.tracegen.Vo;

import java.util.Comparator;
import java.util.Random;

import kr.ac.snu.cares.NotificationLog.ParsedNotification;

public class TraceUtil {
	public static Random random;
	public static Random getRandom() {
		if (random == null)
			random = new Random();
		return random;
	}
	
	public float getAvg(int []arr) {
		long sum = 0;
		for (int number : arr) {
			sum += number;
		}
		return sum / arr.length;		
	}
	
	public static float getVariance(int []population) {
		float average = 0.0f;
		float variance = 0.0f;
		
		for (int p : population) {
			average += p;
		}
		average /= (float)population.length;
		
		for(int p : population) {
			variance += (p - average) * (p - average);
		}
		
		return variance / (float)(population.length - 1);
	}
	
	public static float[] getStat(int []population) {
		float r[] = new float[2];
		float average = 0.0f;
		float variance = 0.0f;
		
		for (int p : population) {
			average += p;
		}
		average /= (float)population.length;
		
		for(int p : population) {
			variance += ((p - average) * (p - average));
		}
		variance = variance / (float)(population.length - 1);
		r[0] = average;
		r[1] = (float)Math.sqrt(variance);
		return r;
	}
	
	static TimeComp comp;
	public static TimeComp getTimeComp() {
		if (comp == null)
			comp = new TimeComp();
		return comp;
	}
	static class TimeComp implements Comparator<TraceEvent> {
		@Override
		public int compare(TraceEvent arg0, TraceEvent arg1) {
			// TODO Auto-generated method stub
			return (int)(arg0.getTime() - arg1.getTime());
		}
	}
}
