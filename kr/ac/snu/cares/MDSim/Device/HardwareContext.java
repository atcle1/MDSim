package kr.ac.snu.cares.MDSim.Device;

import java.util.Calendar;

import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.LogKSuspend;
import kr.ac.snu.cares.MDSim.Vo.LogKWakeup;


public class HardwareContext {
	// Context
	public int bWakeup;
	public int screenBrightness;
	public boolean bScreenOn;
	public boolean bPlugedIn;
	
	public long lastScreenOn;
	public long lastScreenOff;
	
	// Statistics
	// Hardware
	public int wakeupTimeMillis;
	public int suspedTimeMillis;
	public int wakeupCnt;
	
	// Screen
	public int screenOnTimeMillis;
	public int screenOffTimeMillis;
	
	// Power
	public int plugedInTimeMillis;
	public int unplgedTimeMillis;
	public int plugedInCnt;
	
	public void onScreenOnOff(long time, Boolean bScreenOn){
		this.bScreenOn = bScreenOn;
		//System.out.println(MyUtil.MillisToStr(time) + " Screen " + bScreenOn);
		if (bScreenOn) {
			lastScreenOn = time;
			if (lastScreenOff != 0) {
				if (time - lastScreenOff > 0)
					screenOffTimeMillis += time - lastScreenOff;
			}
		} else {
			lastScreenOff = time;
			if (lastScreenOn != 0) {
				if (time - lastScreenOn > 0) {
					screenOnTimeMillis += time - lastScreenOn;
				}
			}
		}
	}
	
	public void onSuspend(long time, LogKSuspend suspend) {
		
	}
	
	public void onWakeup(long time, LogKWakeup wakeup) {
		
	}
	
	public void doReport() {
		System.out.println("print result...");		
	}
}
