package kr.ac.snu.cares.MDSim.Vo;

public class LogTextMessage implements ParsedLogItem {
	boolean bForce;
	String message;
	@Override
	public boolean parse(LogItem logItem) {
		if (logItem.msg.startsWith("TFC")) {
			bForce = true;
		} else if (logItem.msg.startsWith("TXT")) {
			bForce = false;
		} else {
			System.err.println("error");
			message = logItem.msg;
		}
		
		String split[] = logItem.msg.split(":");
		if (split != null && split.length > 1) {
			message = split[1];
		} else {
			message = "";
		}
		//System.out.println("text " + message);
		return false;
	}
	
	
}
