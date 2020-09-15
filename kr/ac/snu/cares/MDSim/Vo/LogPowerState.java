package kr.ac.snu.cares.MDSim.Vo;

public class LogPowerState implements ParsedLogItem {
	public boolean bBootOn;
	@Override
	public boolean parse(LogItem logItem) {
		// TODO Auto-generated method stub
		if (logItem.msg.contains("PST : 1")) {
			bBootOn = true;
		} else {
			bBootOn = false;
		}
		return true;
	}
}
