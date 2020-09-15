package kr.ac.snu.cares.powerSim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import kr.ac.snu.cares.sampleTrace.Vo.NotificationEvent;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class PowerSim {
	/**
	 * @param args
	 */
	public LinkedList<TraceEvent> trace;
		
	public void setTrace(LinkedList<TraceEvent> trace) {
		this.trace = trace;
	}
	
	public void initParm() {
		current_ms = 0;
		energy_mJ = 0.0f;
		sleep_ms = 0;
		bAlwaysOff = true;
		idlePower_mW = PowerParm.IDLE_SCREENOFF.power;
		effectiveStack = new LinkedList<NotificationEvent>();
		powerConsumed = new LinkedList<PowerParm>();
		powerConsumed_dedup = new ArrayList<PowerParm>();
		keyFrame = new LinkedList<Long>();
		effectivePowerParm = new ArrayList<PowerParm>();
	}
	
	public void runSim(LinkedList<TraceEvent> trace)
	{
		Iterator<TraceEvent> itr = trace.iterator();
		TraceEvent event = null;
		while(itr.hasNext()) {
			event = itr.next();
			next(event);
			//System.out.println(event.toString());
		}
		
		dedupPowerParm();
		PowerParmsCalculator calc = new PowerParmsCalculator();
		calc.calc(powerConsumed_dedup);
		System.out.println(calc);
		
		//System.out.println("energy " + energy_mJ);
	}
	
	public boolean bAlwaysOff;
	public TraceEvent prevEvent;
	public TraceEvent currentEvent;
	public long end_ms;
	public float idlePower_mW;
	public long current_ms;
	public float energy_mJ;
	public long sleep_ms;
	public LinkedList<NotificationEvent> effectiveStack;
	public LinkedList<PowerParm> powerConsumed;
	public ArrayList<PowerParm> powerConsumed_dedup;
	public LinkedList<Long> keyFrame;
	public ArrayList<PowerParm> effectivePowerParm;
	
	private void next(TraceEvent event) {
		NotificationEvent current_ne = (NotificationEvent)event;
		NotificationEvent prev_ne = (NotificationEvent)prevEvent;
		if (prevEvent == null) {
			//addEnergy(event.getTime(), idlePower_mW);
			prevEvent = event;
			addPower(PowerParm.IDLE_SCREENOFF, 0l, event.getTime());
		} else if (current_ne.notificaiton.type == 0) {
			
			//System.out.println("end");
			end_ms = current_ne.getTime();
			System.out.println("end " + end_ms);
		} else {
			addPower(current_ne);
			/*
			if (current_ne.notificaiton.type == 1 ||
					current_ne.notificaiton.type == 2) {
				effectiveStack.add(current_ne);
			}else if (current_ne.notificaiton.type == 3) {
				effectiveStack.add(current_ne);;
			}
			*/
		}

	}
	private void addPower(PowerParm parm, Long start, Long end)
	{
		PowerParm newParm = new PowerParm(parm);
		newParm.setStartEnd(start, end);
		powerConsumed.add(newParm);
	}
	
	private long addPower(NotificationEvent ne) {
		PowerParm s0, s1, s2, s3, s4;
		switch (ne.notificaiton.type) {
		case 1:
		case 2:
			s0 = new PowerParm(PowerParm.NOTI_NOTIFY_WAKEUP);
			s0.start_ms = ne.getTime();
			s0.end_ms = ne.getTime() + s0.length_ms;
			s1 = new PowerParm(PowerParm.NOTI_NOTIFY_PROC);
			s1.start_ms = s0.end_ms;
			s1.end_ms = s1.start_ms + s1.length_ms;
			/*
			s2 = new PowerParm(PowerParm.NOTI_NOTIFY_VIB);
			s2.start_ms = s1.end_ms;
			s2.end_ms = s2.start_ms + s2.length_ms;
			s3 = new PowerParm(PowerParm.NOTI_NOTIFY_IDLE);
			s3.start_ms = s2.end_ms;
			s3.end_ms = s3.start_ms + s3.length_ms;
			s4 = new PowerParm(PowerParm.NOTI_NOTIFY_SUSPEND);
			s4.start_ms = s3.end_ms;
			s4.end_ms = s4.start_ms + s4.length_ms;
			*/
			s2 = new PowerParm(PowerParm.NOTI_NOTIFY_SUSPEND);
			s2.start_ms = s1.end_ms;
			s2.end_ms = s2.start_ms + s2.length_ms;
			
			powerConsumed.add(s0);
			powerConsumed.add(s1);
			powerConsumed.add(s2);
			//powerConsumed.add(s3);
			//powerConsumed.add(s4);
			break;
		case 3:
			s0 = new PowerParm(PowerParm.NOTI_CANCEL_WAKEUP);
			s0.start_ms = ne.getTime();
			s0.end_ms = ne.getTime() + s0.length_ms;
			s1 = new PowerParm(PowerParm.NOTI_CANCEL_PROC);
			s1.start_ms = s0.end_ms;
			s1.end_ms = s1.start_ms + s1.length_ms;
			s2 = new PowerParm(PowerParm.NOTI_CANCEL_SUSPEND);
			s2.start_ms = s1.end_ms;
			s2.end_ms = s2.start_ms + s2.length_ms;
			powerConsumed.add(s0);
			powerConsumed.add(s1);
			powerConsumed.add(s2);
			break;
		}
		return 0;
	}
	
	private void dedupPowerParm()
	{
		PowerParm prev_parm = null;
		PowerParm parm = null;
		long prev_start = 0;
		long prev_length = 0;
		Collections.sort(powerConsumed);
		Iterator<PowerParm> itr = powerConsumed.iterator();
		int conflict = 0;
/*	
		itr = powerConsumed.iterator();
		while(itr.hasNext()) {
			parm = itr.next();
			if (prev_parm != null &&
					prev_parm.start_ms + prev_parm.length_ms > parm.start_ms) {
				conflict++;
			}
			//System.out.println(parm);
			prev_parm = parm;
		}
		System.out.println("conflict " + conflict);

		itr = powerConsumed.iterator();
		prev_parm = itr.next();
		while(itr.hasNext()) {
			parm = itr.next();
			 if (parm.getTypeStepTag() == prev_parm.getTypeStepTag() &&
						parm.start_ms == prev_parm.start_ms) {
				 // complitly duplicated
					itr.remove();
					conflict--;
					continue;
			}
			 prev_parm = parm;
		}
		System.out.println("cd dedup, conflict " + conflict);
*/
		
		itr = powerConsumed.iterator();
		while(itr.hasNext()) {
			parm = itr.next();
			keyFrame.add(parm.start_ms);
			keyFrame.add(parm.end_ms);
		}
		keyFrame.add(end_ms);
		
		// add elements to al, including duplicates
		Set<Long> hs = new HashSet<>();
		hs.addAll(keyFrame);
		keyFrame.clear();
		keyFrame.addAll(hs);
		
		Collections.sort(keyFrame);
		
		Long frame, prev_frame = 0l;
		Iterator<Long> frame_itr = keyFrame.iterator();
		while(frame_itr.hasNext()) {
			frame = frame_itr.next();
			//System.out.println("frame : " + frame);
			
				// calc
				//System.out.println("frame : " + prev_frame + " " + frame);
				setupStack(prev_frame, frame);
				//printStack();
				PowerParm calcParm = calcStack(prev_frame, frame);
				if (calcParm != null)
					powerConsumed_dedup.add(calcParm);
			
			prev_frame = frame;
		}
		

		
		dedupCheck(powerConsumed_dedup);
	}
	
	private void dedupCheck(List<PowerParm> list)
	{
		PowerParm parm = null, prev_parm = null;
		int conflict = 0;
		Iterator<PowerParm> itr = list.iterator();
		while(itr.hasNext()) {
			parm = itr.next();
			if (parm == null) {
				System.out.println("parm is null");
			} else	if (prev_parm != null &&
					prev_parm.start_ms + prev_parm.length_ms > parm.start_ms) {
				conflict++;
			}
			//System.out.println(parm);
			prev_parm = parm;
		}
		System.out.println("conflict " + conflict);
	}
	
	private void setupStack(long start_ms, long end_ms)
	{
		PowerParm parm = null;
		Iterator<PowerParm> itr = powerConsumed.iterator();
		effectivePowerParm.clear();
		while(itr.hasNext()) {
			parm = itr.next();
			if (parm.start_ms <= start_ms &&
					parm.end_ms > start_ms) {
				effectivePowerParm.add(parm);
			} else if (parm.start_ms > start_ms &&
					parm.start_ms < end_ms) {
				effectivePowerParm.add(parm);
			} else if (parm.start_ms > end_ms) {
				break;
			}
		}
	}
	private void printStack() {
		if (effectivePowerParm.size() > 0) {
		for (int i =0; i < effectivePowerParm.size(); i++) {
			
			System.out.println("ST " + effectivePowerParm.get(i));
		}
		}
	}
	
	private PowerParm calcStack(long start_ms, long end_ms) {
		PowerParm selectedPowerParm = null;
		PowerParm newPowerParm = null;
		boolean bDup = false;
		if (effectivePowerParm.size() == 0) {
			// sleep state
			sleep_ms += end_ms + start_ms;
			newPowerParm = new PowerParm(PowerParm.IDLE_SCREENOFF);
			newPowerParm.setStartEnd(start_ms, end_ms);
			return newPowerParm;
		}
		
		bDup = false;
		for (int i =0; i < effectivePowerParm.size(); i++) {
			PowerParm tempParm = effectivePowerParm.get(i);
			if (selectedPowerParm == null) {
				selectedPowerParm = tempParm;
			} else {
				if (
						(selectedPowerParm.step == 0 ||
						selectedPowerParm.step == 2 )&&
						tempParm.step == 1 ) {
					selectedPowerParm = tempParm;
				} else if (selectedPowerParm.step == 2 &&
						tempParm.step == 0) {
					//selectedPowerParm = tempParm;
				} else if (selectedPowerParm.step == 0 &&
						tempParm.step == 2) {
					// suspend 중 wake up 있는 경우 wakeup을 봄
					//selectedPowerParm = tempParm;
				}
				bDup = true;
			}
		}
		newPowerParm = new PowerParm(selectedPowerParm);
		newPowerParm.setStartEnd(start_ms, end_ms);
		if (bDup) {
			newPowerParm.power = newPowerParm.power * 1.0f;	// 10% adj
		}
		return newPowerParm;
	}

}