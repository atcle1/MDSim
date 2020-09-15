package kr.ac.snu.cares.tracegen;

import kr.ac.snu.cares.tracegen.Vo.InitialStat;
import kr.ac.snu.cares.tracegen.Vo.TraceUtil;

public class Main {
	public static void main(String []args) {
		InitialStat istat = new InitialStat();
		istat.timeLength = 60 * 1000;
		istat.notifyCnt = 5;
		istat.touchCnt = 5;
		float stdAvg[] = new float[1000];
		for (int i = 0; i< 1000; i++) {
			Trace trace = Trace.get(istat);
			/*
			if (trace.stat.notiNotifyIntervalStd ) {
				System.out.println("i " + i);
				System.out.println(trace);
				System.out.println(trace.stat);
				
			}
			*/
			stdAvg[i] = trace.stat.notiNotifyIntervalStd;
		}
		
		double sum = 0;
		for (int i = 0; i < 1000; i++) {
			sum += stdAvg[i];
		}
		System.out.println(sum/1000);
	}
}
