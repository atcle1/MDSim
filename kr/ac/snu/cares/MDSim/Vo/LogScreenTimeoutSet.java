package kr.ac.snu.cares.MDSim.Vo;

public class LogScreenTimeoutSet implements ParsedLogItem {
	public int screenTimeout = 30 * 1000;
	
	@Override
	public boolean parse(LogItem logItem) {
		// TODO
		String msg = logItem.msg;
		System.err.println(msg);
		return true;
	}
}
