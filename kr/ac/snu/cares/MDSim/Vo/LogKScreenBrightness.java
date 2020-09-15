package kr.ac.snu.cares.MDSim.Vo;

public class LogKScreenBrightness implements ParsedLogItem{
	public int brightness;
	public boolean bScreenBrightnessZero;

	@Override
	public boolean parse(LogItem logItem) {
		// TODO Auto-generated method stub
		/* logItem.msg format example: " SBR 2"*/
		//System.out.println("logItem.msg: " + logItem.msg);
		if (logItem.msg.contains("SBR 0"))
			this.bScreenBrightnessZero = true;
		else
			this.bScreenBrightnessZero = false;
		
		String[] split = logItem.msg.split(" ");
		if (split == null || split.length != 2) {
			System.err.println("UNKNOWN BRIGHTNESS " + logItem.msg);
			return true;
		}
		
		brightness = Integer.parseInt(split[1]);
		
		return true;
	}
}