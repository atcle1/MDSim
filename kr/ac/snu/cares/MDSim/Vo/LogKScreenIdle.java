package kr.ac.snu.cares.MDSim.Vo;

public class LogKScreenIdle  implements ParsedLogItem {
	public boolean bIdle = false;
	@Override
	public boolean parse(LogItem logItem) {
		// TODO Auto-generated method stub
		if (logItem.msg == null) {
			return false;
		}
		if (logItem.msg.contains("SID 0")) {
			bIdle = false;
		} else if (logItem.msg.contains("SID 1")) {
			bIdle = true;
		} else {
			System.err.println("unknown SID " + logItem.msg);
			return false;
		}
		return true;
	}
}
