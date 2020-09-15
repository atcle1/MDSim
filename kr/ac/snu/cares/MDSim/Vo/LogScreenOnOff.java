package kr.ac.snu.cares.MDSim.Vo;

public class LogScreenOnOff implements ParsedLogItem {
	public Boolean bScreenOn;
	
	@Override
	public boolean parse(LogItem logItem) {
		String msg = logItem.msg;
		if (msg.contains("SCR ON") || msg.contains("SCR : 1")) {
			bScreenOn = true;
			return true;
		} else if (msg.contains("SCR OFF") || msg.contains("SCR : 0")){
			bScreenOn = false;
			return true;
		} else {
			return false;
		}
	}
}
