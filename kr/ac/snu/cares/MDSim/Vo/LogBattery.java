package kr.ac.snu.cares.MDSim.Vo;

public class LogBattery implements ParsedLogItem{
	public int batteryLevel;
	public boolean bCharging;
        @Override
        public boolean parse(LogItem logItem) {
		String[] split_list = logItem.msg.split(" ");
		batteryLevel = Integer.parseInt(split_list[2]);
		try {
		bCharging = split_list[3].contains("DIS") ? false : true;
		} catch(Exception ex) {
			ex.printStackTrace();
			System.out.println(logItem.msg);
		}
			return true;
        }
}