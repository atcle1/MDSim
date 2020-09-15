package kr.ac.snu.cares.powerSim;

import java.util.Iterator;
import java.util.List;

public class PowerParmsCalculator {
	public long idle_ms = 0;
	public long work_ms = 0;
	public long total_ms = 0;
	public double idle_energy = 0;
	public double work_energy = 0f;
	public double total_energy;
	public int wakeup_cnt;
	
	private int state = 0;
	
	public void calc(List<PowerParm> list)
	{
		Iterator<PowerParm> itr = list.iterator();
		PowerParm parm = null, prevParm = null;
		while (itr.hasNext()) {
			parm = itr.next();
			//System.out.println(parm);
			if (prevParm == null) {
				if (parm.start_ms != 0) {
					System.err.println("start is not 0, " + parm.start_ms);
				}
			} else if (prevParm.end_ms != parm.start_ms) {
				System.err.println("end and start isn't continuous, ");
				System.err.println(prevParm);
				System.err.println(parm);
			}
			
			if (parm.type == 0) {
				idle_ms += parm.length_ms;
				idle_energy += parm.getEnergy();
			} else {
				work_ms += parm.length_ms;
				work_energy += parm.getEnergy();
				
				if (parm.type != 0 && (parm.step == 0 && state == -1)){
					wakeup_cnt++;
					state = 1;
				} else 	if (parm.type == 0) {
					state = -1;
				} else if (parm.step == 0) {
					state =0;
				} else if (parm.step == 1) {
					state = 1;
				} else if (parm.step == 2) {
					state = 2;
				}
			}
			prevParm = parm;
		}
		total_energy = idle_energy + work_energy;
		total_ms = idle_ms + work_ms;
	}
	
	public String toString() {
		String result = "";
		result += String.format("IDLE : %,12d ms : %,15.1f mJ\n", idle_ms, idle_energy);
		result += String.format("WORK : %,12d ms : %,15.1f mJ\n", work_ms, work_energy);
		result += String.format("TOTL : %,12d ms : %,15.1f mJ", total_ms, total_energy);
		return result;
	}
}
