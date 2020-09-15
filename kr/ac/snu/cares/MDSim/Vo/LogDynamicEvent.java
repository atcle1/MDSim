package kr.ac.snu.cares.MDSim.Vo;

public class LogDynamicEvent implements ParsedLogItem {
	public LogItem logItem;
	public String event;
	public Object obj;
	@Override
	public boolean parse(LogItem logItem) {
		// TODO Auto-generated method stub
		this.logItem = logItem;
		return true;
	}

}
