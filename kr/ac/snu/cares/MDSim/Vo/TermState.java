package kr.ac.snu.cares.MDSim.Vo;

public class TermState implements Cloneable {
	public boolean bPhoneScreenOn;
	public boolean bPhoneCharging;
	
	public boolean bWatchScreenOn;
	public boolean bWatchSid;
	public boolean bWatchCharging;
	public boolean bWatchBrightnessZero;
	
	@Override
	public String toString()
	{
		String phoneScreenStr = bPhoneScreenOn ? "On" : "Off";
		String phoneChargingStr = bPhoneCharging ? "Chr" : "Dis";
		
		String watchScreenStr = bWatchScreenOn ? "On" : "Off";
		String watchSidStr = bWatchSid ? "On" : "Off";
		String watchChargingStr = bWatchCharging ? "Chr" : "Dis";
		
		String watchBrightnessStr = bWatchBrightnessZero ? "Zero" : "BRI";
		
		return "PHONE : " + phoneScreenStr + " " + phoneChargingStr + " WATCH : "
			+ watchScreenStr + " SID " + watchSidStr + " " + watchBrightnessStr+" "+watchChargingStr; 
	}
	
	public TermState() {
		
	}
	
	public TermState clone() {
		TermState newTerm = new TermState();
		newTerm.bPhoneScreenOn = bPhoneScreenOn;
		newTerm.bPhoneCharging = bPhoneCharging;
		newTerm.bWatchScreenOn = bWatchScreenOn;
		newTerm.bWatchSid = bWatchSid;
		newTerm.bWatchCharging = bWatchCharging;
		newTerm.bWatchBrightnessZero = bWatchBrightnessZero;
		return newTerm;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TermState))
			return false;
		TermState rhs = (TermState)obj;
		
		if (bPhoneScreenOn != rhs.bPhoneScreenOn ||
				bPhoneCharging != rhs.bPhoneCharging ||
				bWatchScreenOn != rhs.bWatchScreenOn ||
				bWatchSid != rhs.bWatchSid ||
				bWatchCharging != rhs.bWatchCharging ||
				bWatchBrightnessZero != rhs.bWatchBrightnessZero)
			return false;
			
		
		return true;	
	}
}
