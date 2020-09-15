package kr.ac.snu.cares.MDSim.Vo;

public class LogKTouch implements ParsedLogItem {
	public boolean bFirst;
	public boolean bLast;
	@Override
	public boolean parse(LogItem logItem) {
		// TODO Auto-generated method stub
		if (logItem.msg.contains("LT")) {
			bLast = true;
		}
		else if (logItem.msg.contains("FT")) {
			bFirst = true;
		}
		return true;
	}
}
