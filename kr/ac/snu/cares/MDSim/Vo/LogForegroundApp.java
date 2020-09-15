package kr.ac.snu.cares.MDSim.Vo;

public class LogForegroundApp implements ParsedLogItem{
	public String resumedPackage;
	@Override
	public boolean parse(LogItem logItem) {
		String split[] = logItem.msg.split(" ");
		if (split.length == 3) {
			resumedPackage = split[2];
			return true;
		} else {
			System.err.println("parsing err");
		}
		return false;
	}
}
