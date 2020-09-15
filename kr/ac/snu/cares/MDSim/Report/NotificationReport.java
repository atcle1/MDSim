package kr.ac.snu.cares.MDSim.Report;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;

public class NotificationReport {
	long []noti;
	int notiCnt;

	public NotificationReport() {
		noti = new long[50000];
	}
	
	private long prevTime;
	public void notiNotify(NotificationItem item) {
		if (notiCnt == 0) {
			prevTime = MDSim.currentTimeMillis;
		} else {
			noti[notiCnt - 1] = MDSim.currentTimeMillis - prevTime;	
			prevTime = MDSim.currentTimeMillis;
		}
		notiCnt++; 
	}
	
	public void doReport() {
		float []r = getStat(noti, notiCnt);
		MDSim sim = MDSim.getInstance();
		long runTime = sim.endTime.getTimeInMillis() - sim.startTime.getTimeInMillis();
		System.out.println("cnt : " + notiCnt + " avg : " + r[0] + " std : "+r[1]);
		System.out.println("noti 1/" + (runTime/60000.0f)/notiCnt + " min, "+ (double)(notiCnt*((60*60*1000)/(double)runTime)) +"/h");
	}
	
	public static float[] getStat(long []population, int length) {
		float r[] = new float[2];
		float average = 0.0f;
		float variance = 0.0f;
		
		for (int i = 0; i < length; i++) {
			average += population[i];
		}
		average /= (float)length;
		
		for(int i = 0; i < length; i++) {
			variance += ((population[i] - average) * (population[i] - average));
		}
		variance = variance / (float)(length - 1);
		r[0] = average;
		r[1] = (float)Math.sqrt(variance);
		return r;
	}
}
