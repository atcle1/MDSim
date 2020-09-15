package kr.ac.snu.cares.MDSim.Report;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.ScreenOnTerm;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class UserSessionReport {
	public boolean bScreenOn;
	
	public long lastScreenOn;
	public long lastScreenOff;
	
	// Screen
	public int screenOnTimeMillis;
	public int screenOffTimeMillis;
	
	// smart screen
	public static final double idle_power_amb = 29.97;		// power consumption in ambition mode
	public static final double idle_power_scroff = 12.21;	// power consumption in scr off
	public static final double bt_energy = 371.6351;		// energy for bt send 1 time, 371.6351 mJ
	public static final long turnOffThreshold = 30 * 1000;	// 30 s
	//public static final long turnOffThreshold = 1;	// Oracle
	
	public long breakEvenTime;
	public int powerNoActCnt;
	public double energyNoAct;
	public long powerNoActTime;
	
	public int powerLossCnt;
	public double energyLoss;
	public long powerLossTime;
	
	public int powerBenefitCnt;
	public double energyBenefit;
	public long powerBenefitTime;
	
	public double powerSum;
		
	public LinkedList<ScreenOnTerm> screenOnTerms;
	public ScreenOnTerm currentScreenOnTerm;
	public UserSessionReport() {
		screenOnTerms = new LinkedList<ScreenOnTerm>();
		
		breakEvenTime = (long) ((bt_energy * 2) / (idle_power_amb - idle_power_scroff) * 1000);
		System.out.println("sms : breakEvenTime " + breakEvenTime);		
	}
	
	public double calcSessionBenefitEnergy(long length) {
		double scroff_energy = 0;
		if (length > turnOffThreshold) {
			scroff_energy += turnOffThreshold * idle_power_amb;
			scroff_energy += ((length - turnOffThreshold) * idle_power_scroff)
					+ ( 2 * bt_energy * 1000);
		}	
		double amb_energy = length * idle_power_amb;
		System.out.format("len %d energy %f\n", length, (amb_energy - scroff_energy)/1000.0);
		return (amb_energy - scroff_energy)/1000.0;		
	}
	
	public void doReport() {
		//if (true)
		//	return;
		System.out.println("===SmartAmbient Report===");
		Collections.sort(screenOnTerms, new ScreenOnTerm.TimeComp());
		Iterator<ScreenOnTerm> itr = screenOnTerms.iterator();
		while (itr.hasNext())
		{
			ScreenOnTerm term = itr.next();
			long termLen = term.getLength();
			if (termLen > 2 * 60 * 60 * 1000) continue;
			if (term.bUser) {
				System.out.println(term);
				if (termLen < turnOffThreshold) {
					// do nothing
					powerNoActCnt++;
					powerNoActTime += termLen;
					energyNoAct += termLen * idle_power_amb / 1000.0;
				} else if (termLen < turnOffThreshold + breakEvenTime) {
					powerLossCnt++;
					powerLossTime += termLen;
					energyLoss += calcSessionBenefitEnergy(termLen);
				} else {
					powerBenefitCnt++;
					powerBenefitTime += termLen;
					energyBenefit += calcSessionBenefitEnergy(termLen);
				}
			}
		}
		System.out.println("breakeven time " + breakEvenTime/1000.0);
		System.out.println("total");
		System.out.format("NoAct cnt : %d time : %.2f energy : %.2f\n", powerNoActCnt,
				powerNoActTime/1000.0, energyNoAct);
		System.out.format("loss cnt : %d time : %.2f energy : %.2f\n", powerLossCnt,
				powerLossTime/1000.0, energyLoss);
		System.out.format("benefit cnt : %d time : %.2f energy : %.2f\n", powerBenefitCnt,
				powerBenefitTime/1000.0, energyBenefit);
		System.out.format("sum : %.2fmJ (%.2fm, %.2fm)\n", (energyBenefit + energyLoss),
				(((energyBenefit + energyLoss)) / idle_power_amb)/60.0,
				(((energyBenefit + energyLoss)) / idle_power_scroff)/60.0);
		
		System.out.format("oracle : %.2fmJ (%.2fm, %.2fm)\n", (energyBenefit),
				(((energyBenefit)) / idle_power_amb)/60.0,
				(((energyBenefit)) / idle_power_scroff)/60.0);
		
		System.out.println("per day");
		double days = MDSim.getInstance().elapsedTime/(float)(1000 * 60 * 60 * 24);
		System.out.format("NoAct cnt : %.2f time : %.2f energy : %.2f\n", powerNoActCnt/days,
				powerNoActTime/days/1000.0, energyNoAct/days);
		System.out.format("loss cnt : %.2f time : %.2f energy : %.2f\n", powerLossCnt/days,
				powerLossTime/days/1000.0, energyLoss/days);
		System.out.format("benefit cnt : %.2f time : %.2f energy : %.2f\n", powerBenefitCnt/days,
				powerBenefitTime/days/1000.0, energyBenefit/days);
		System.out.format("sum : %.2fmJ (%.2fm, %.2fm)\n", (energyBenefit + energyLoss)/days,
				(((energyBenefit + energyLoss)/days) / idle_power_amb)/60.0,
				(((energyBenefit + energyLoss)/days) / idle_power_scroff)/60.0);
		
		System.out.format("oracle : %.2fmJ (%.2fm, %.2fm)\n", (energyBenefit)/days,
				(((energyBenefit)/days) / idle_power_amb)/60.0,
				(((energyBenefit)/days) / idle_power_scroff)/60.0);
	}
	
	public void onTouch(long time) {
		//System.out.println(MDSim.currentLogItem.msg);
		if (currentScreenOnTerm != null) {
			currentScreenOnTerm.addUserEvent(time);
		} else {
			System.err.println(MyUtil.MillisToStr(time) + " touch event occured, but currentScreenOnTerm is null");
		}
	}
	
	public void onScreenOnOff(long time, Boolean bScreenOn){
		this.bScreenOn = bScreenOn;
		//System.out.println(MyUtil.MillisToStr(time) + " Screen " + bScreenOn);
		if (bScreenOn) {
			lastScreenOn = time;
			if (lastScreenOff != 0) {
				if (time - lastScreenOff > 0) {
					screenOffTimeMillis += time - lastScreenOff;
					currentScreenOnTerm = new ScreenOnTerm(time);
				}					
			}
		} else {
			lastScreenOff = time;
			if (lastScreenOn != 0) {
				if (time - lastScreenOn > 0) {
					screenOnTimeMillis += time - lastScreenOn;
					//System.out.println(MyUtil.MillisToStr(MDSim.currentTimeMillis) + "SCR ON " + (time - lastScreenOn)/1000);
					if (currentScreenOnTerm != null) {
						currentScreenOnTerm.setEndTime(time);
						screenOnTerms.add(currentScreenOnTerm);
					}
				}
			}
		}
	}
}
