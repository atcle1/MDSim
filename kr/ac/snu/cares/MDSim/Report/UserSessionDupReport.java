package kr.ac.snu.cares.MDSim.Report;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.ScreenOnTerm;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class UserSessionDupReport {

	public LinkedList<ScreenOnTerm> screenOnTerms;
	public UserSessionDupReport() {
		screenOnTerms = new LinkedList<ScreenOnTerm>();
	}
	
	public void doReport() {
		if (true)
			return;
		System.out.println("\nDup Session report");
		Collections.sort(screenOnTerms, new ScreenOnTerm.TimeComp());
		Iterator<ScreenOnTerm> itr = screenOnTerms.iterator();
		long totalLen = 0;
		while (itr.hasNext())
		{
			ScreenOnTerm term = itr.next();
			long termLen = term.getLength();
			totalLen += termLen;
			System.out.println("term " + MyUtil.MillisToStr(term.start) + " ~ " + MyUtil.MillisToStr(term.end) + " : " + termLen);
		}

		double days = MDSim.getInstance().elapsedTime/(float)(1000 * 60 * 60 * 24);
		System.out.format("total : %.2fs\n", totalLen/1000.0);
		System.out.format("avg : %.2fs\n", (totalLen/1000.0)/days);
		System.out.println("\nDup Session report end");
	}
	
	
	public boolean bMobileScreenOn = false;
	public boolean bWatchScreenOn = false;
	public boolean bMobilePlugged = false;
	public boolean bWatchPlugged = false;
	public boolean bDupStart = false;
	public long watchScreenDupStart = 0;
	public long totalDupTime = 0;
	public void onScreenOnOff(long time, boolean bMobile, boolean bScreenOn)
	{

		if (bMobile) {
			bMobileScreenOn = bScreenOn;
		} else {
			if (bWatchScreenOn && bScreenOn)
				return;
			bWatchScreenOn = bScreenOn;
			//System.out.println("watch " + MyUtil.MillisToStr(time) + " scr " + bScreenOn);
		}
		

		if (bWatchPlugged || bMobilePlugged) {
			bDupStart = false;
			return;
		}
		
		if (bWatchScreenOn && bMobileScreenOn) {
			
			bDupStart = true;
			watchScreenDupStart = time;
			//System.out.println("dup start");
		} else if (!bWatchScreenOn && bMobileScreenOn ||
				bWatchScreenOn && !bMobileScreenOn) {
			if (bDupStart) {
				bDupStart = false;
				screenOnTerms.add(new ScreenOnTerm(watchScreenDupStart, time));
				totalDupTime += time - watchScreenDupStart;
				//System.out.println("dup " + (time - watchScreenDupStart));
			} 
		} else if (!bWatchScreenOn && !bMobileScreenOn) {
			if (bDupStart) {
				//System.err.println("err dup?");
			}
			bDupStart = false;
		}
	}
	
	public void onBattery(long time, Boolean bMobile, Boolean bCharging)
	{
		if (bMobile) {
			bMobilePlugged = bCharging;
		} else {
			bWatchPlugged = bCharging;
		}
	}
}
