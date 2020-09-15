package kr.ac.snu.cares.MDSim.Vo;

public class LogKSuspend implements ParsedLogItem{
	public boolean bScreenIdle;
	@Override
	public boolean parse(LogItem logItem) {
		if (logItem.msg.contains("1")) {
			bScreenIdle = true;
		} else {
			bScreenIdle = false;
		}
		return true;
	}
}
