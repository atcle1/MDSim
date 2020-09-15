package kr.ac.snu.cares.MDSim.Report;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import kr.ac.snu.cares.MDSim.DataSet;
import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.ScreenOnTerm;
import kr.ac.snu.cares.MDSim.Vo.Term;
import kr.ac.snu.cares.MDSim.Vo.TermState;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class WatchSessionReport {
	public LinkedList<Term> terms;
	public TermState currentState;
	public Term currentTerm;
	/*
	public boolean bWatchScreenOn = false;
	public boolean bWatchSidOn = false;
	public int watchBrightness = 0;
	*/
	public long termStart = 0;
	
	public WatchSessionReport() {
		terms = new LinkedList<Term>();
		currentState = new TermState();
		currentTerm = new Term(0);
	}
	
	public void start() {
		termStart = MDSim.currentTimeMillis;
		// khj watch
		if (DataSet.selectedIndex == 1) {
			currentState.bPhoneCharging = false;
			currentState.bPhoneScreenOn = false;
			
			currentState.bWatchCharging = true;
			currentState.bWatchSid = true;
			currentState.bWatchBrightnessZero = true;
			currentState.bWatchScreenOn = false;
		} else if (DataSet.selectedIndex == 3) {
			currentState.bPhoneCharging = false;
			currentState.bPhoneScreenOn = false;
			
			currentState.bWatchCharging = false;
			currentState.bWatchSid = false;
			currentState.bWatchBrightnessZero = true;
			currentState.bWatchScreenOn = false;
		} else if (DataSet.selectedIndex == 5) {
			currentState.bPhoneCharging = true;
			currentState.bPhoneScreenOn = false;
			
			currentState.bWatchCharging = true;
			currentState.bWatchSid = false;
			currentState.bWatchBrightnessZero = true;
			currentState.bWatchScreenOn = false;
		} else if (DataSet.selectedIndex == 7) {
			currentState.bPhoneCharging = true;
			currentState.bPhoneScreenOn = false;
			
			currentState.bWatchCharging = true;
			currentState.bWatchSid = false;
			currentState.bWatchBrightnessZero = true;
			currentState.bWatchScreenOn = false;
		} else if (DataSet.selectedIndex == 9) {
			currentState.bPhoneCharging = false;
			currentState.bPhoneScreenOn = false;
			
			currentState.bWatchCharging = false;
			currentState.bWatchSid = false;
			currentState.bWatchBrightnessZero = true;
			currentState.bWatchScreenOn = false;
		} else if (DataSet.selectedIndex == 11) {
			currentState.bPhoneCharging = true;
			currentState.bPhoneScreenOn = false;
			
			currentState.bWatchCharging = true;
			currentState.bWatchSid = false;
			currentState.bWatchBrightnessZero = true;
			currentState.bWatchScreenOn = false;
		}
	}
	public void end() {
		
	}
	public void doReport() {
//		if (true)
//			return;
		updateLog(MDSim.currentTimeMillis);
		/* phone */
		long totalPScrOn = 0;
		long totalPScrOff = 0;
		
		long totalPScrOnCharging = 0;
		long totalPScrOnDischarging = 0;
		long totalPScrOffCharging = 0;
		long totalPScrOffDischarging = 0;
		
		/* watch */
		long totalWScrOn = 0;
		long totalWScrOff = 0;
		long totalWAmbient = 0;
		
		long totalWScrOnCharging = 0;
		long totalWScrOnDischarging = 0;
		long totalWAmbientCharging = 0;
		long totalWAmbientDischarging = 0;
		long totalWScrOffCharging = 0;
		long totalWScrOffDischarging = 0;
		
		System.out.println("\nWatchSessionReport Session report");
		//Collections.sort(terms, new Term.TimeComp());
		Iterator<Term> itr = terms.iterator();
		long totalLen = 0;
		while (itr.hasNext())
		{
			Term term = itr.next();
			TermState state = term.state;
			long termLen = term.getLength();
			totalLen += termLen;
			//System.out.println("term " + term);
			
			/* Watch */
			if (state.bWatchScreenOn && !state.bWatchBrightnessZero) {
				// SCR ON
				totalWScrOn += termLen;
				if (state.bWatchCharging) {
					totalWScrOnCharging += termLen;
				} else {
					totalWScrOnDischarging += termLen;
				}
				System.err.println("ON " + term);
			} else if (!state.bWatchScreenOn) {
				// SCR OFF
				totalWScrOff += termLen;
				if (state.bWatchCharging) {
					totalWScrOffCharging += termLen;
				} else {
					totalWScrOffDischarging += termLen;
				}
				System.err.println("OFF " + term);
			} else if (state.bWatchScreenOn && state.bWatchBrightnessZero && state.bWatchSid) {
				// AMBIENT
				totalWAmbient += termLen;
				// System.err.println("term " + term);
				if (state.bWatchCharging) {
					totalWAmbientCharging += termLen;
				} else {
					totalWAmbientDischarging += termLen;
				}
				System.err.println("OA " + term);
			} else if (state.bWatchScreenOn && !state.bWatchSid && state.bWatchBrightnessZero) {
				// WATCH : On SID Off Zero Dis, ambient to scroff
				totalWAmbient += termLen;
				//System.err.println("term " + term);
				if (state.bWatchCharging) {
					totalWAmbientCharging += termLen;
				} else {
					totalWAmbientDischarging += termLen;
				}
				System.err.println("OA " + term);
			} else {
				System.err.println("term " + term);
			}
			
			/* phone */
			if (state.bPhoneScreenOn) {
				totalPScrOn += termLen;
				if (state.bPhoneCharging) {
					totalPScrOnCharging += termLen;
				} else {
					totalPScrOnDischarging += termLen;
				}
			} else {
				totalPScrOff += termLen;
				if (state.bPhoneCharging) {
					totalPScrOffCharging += termLen;
				} else {
					totalPScrOffDischarging += termLen;
				}
			}
			
			
			
			
		}
		System.out.format("total : %.1f\n", (totalWScrOn + totalWAmbient +totalWScrOff)/1000.0);
		/* phone */
		System.out.format("PScrOn %.1f(%2.1f)\n" +
				"PScrOff %.1f(%2.1f)\n---\n", 
				totalPScrOn/1000.0,
				(float)100*totalPScrOn/totalLen,
				totalPScrOff/1000.0,
				(float)100*totalPScrOff/totalLen);
		System.out.format(
				"%15s %8.1f(%2.1f)" +
				"%15s %8.1f(%2.1f)\n" +
				"%15s %8.1f(%2.1f)" +
				"%15s %8.1f(%2.1f)\n---\n",
				"PScrOnChr",
				totalPScrOnCharging/1000.0,
				(float)100*totalPScrOnCharging/totalLen,
				"PScrOnDis",
				totalPScrOnDischarging/1000.0,
				(float)100*totalPScrOnDischarging/totalLen,
				"PScrOffChr",
				totalPScrOffCharging/1000.0,
				(float)100*totalPScrOffCharging/totalLen,
				"PScrOffDis",
				totalPScrOffDischarging/1000.0,
				(float)100*totalPScrOffDischarging/totalLen);
		
		/* watch */
		System.out.format("WScrOn %.1f(%2.1f)\n" +
				"WScrAmbient %.1f(%2.1f)\n" +
				"WScrOff %.1f(%2.1f)\n---\n", 
				totalWScrOn/1000.0,
				(float)100*totalWScrOn/totalLen,
				totalWAmbient/1000.0,
				(float)100*totalWAmbient/totalLen,
				totalWScrOff/1000.0,
				(float)100*totalWScrOff/totalLen);
		
		System.out.format(
				"%15s %8.1f(%2.1f)" +
				"%15s %8.1f(%2.1f)\n" +
				"%15s %8.1f(%2.1f)" +
				"%15s %8.1f(%2.1f)\n" +
				"%15s %8.1f(%2.1f)" +
				"%15s %8.1f(%2.1f)\n---\n",
				"WScrOnChr",
				totalWScrOnCharging/1000.0,
				(float)100*totalWScrOnCharging/totalLen,
				"WScrOnDis",
				totalWScrOnDischarging/1000.0,
				(float)100*totalWScrOnDischarging/totalLen,
				"WAmbientChr",
				totalWAmbientCharging/1000.0,
				(float)100*totalWAmbientCharging/totalLen,
				"WAmbientDis",
				totalWAmbientDischarging/1000.0,
				(float)100*totalWAmbientDischarging/totalLen,
				"WScrOffChr",
				totalWScrOffCharging/1000.0,
				(float)100*totalWScrOffCharging/totalLen,
				"WScrOffDis",
				totalWScrOffDischarging/1000.0,
				(float)100*totalWScrOffDischarging/totalLen);
		
		double days = MDSim.getInstance().elapsedTime/(float)(1000 * 60 * 60 * 24);
		System.out.format("total : %.2fs\n", totalLen/1000.0);
		System.out.format("total : %.2fdays\n", (totalLen/1000.0)/(60*60*24));
		System.out.println("\nDup Session report end");
	}
	
	
	/* update state */
	private TermState updateChargingState(boolean bMobile, boolean bCharging)
	{
		if (bMobile) {
			currentState.bPhoneCharging = bCharging;
		} else {
			currentState.bWatchCharging = bCharging;
		}
		return currentState;
	}
	private TermState updateMobileScreenState(boolean bScreenOn)
	{
		currentState.bPhoneScreenOn = bScreenOn;
		return currentState;
	}
	
	private TermState updateWatchScreenState(boolean bScreenOn)
	{
		currentState.bWatchScreenOn = bScreenOn;
		return currentState;
	}
	private TermState updateWatchSidState(boolean bSidOn)
	{
		currentState.bWatchSid = bSidOn;
		return currentState;
	}
	
	private TermState updateWatchBrightness(int brightness)
	{
		if (brightness == 0)
			currentState.bWatchBrightnessZero = true;
		else
			currentState.bWatchBrightnessZero = false;
		return currentState;
	}
	
	private void updateLog(long current) {
		Term term = new Term(termStart, current);
		term.state = currentState.clone();
		terms.add(term);
		termStart = current;
	}
	
	/* public */
	public void onMobileScreenOnOff(long time, boolean bScreenOn)
	{
		updateLog(time);
		updateMobileScreenState(bScreenOn);
	}
	
	public void onBattery(long time, Boolean bMobile, Boolean bCharging)
	{
		updateLog(time);
		updateChargingState(bMobile, bCharging);
	}
	
	public void onWatchScreenOnOff(long time, boolean bScreenOn)
	{
		updateLog(time);
		updateWatchScreenState(bScreenOn);
	}
	public void onWatchScreenSid(long time, boolean bScreenSid)
	{
		updateLog(time);
		updateWatchSidState(bScreenSid);
	}
	public void onWatchBrightness(long time, int brightness)
	{
		updateLog(time);
		updateWatchBrightness(brightness);
	}
}
