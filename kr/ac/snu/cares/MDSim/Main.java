package kr.ac.snu.cares.MDSim;

import kr.ac.snu.cares.MDSim.Log.LogReader;

public class Main {

	
	public static int select;
	public static void main(String args[])
	{
        String logPath = "1/phone/phone.sqlite3";
        for (int i = 3; i < 4; i++) {
	        //DataSet.selectedIndex = 15;
	        
	        logPath = DataSet.getSelectedData();
	        String split[] = logPath.split("[/]");
	        System.err.println("DATASET : " + logPath);
	        LogReader logReader = LogReader.getInstance(logPath);
	        logReader.init();
	        MDSim mdSim = MDSim.getInstance(logReader);
	        mdSim.person_name = split[0];
	        mdSim.dir = split[1];
	        mdSim.bSnmEnable = true;
	        //mdSim.bSnmEnable = false;
	        mdSim.init();
	
			mdSim.notificationLogGenerator.init();
			
			System.err.println();
			System.err.println(logPath);
			mdSim.run();
        }
	}
}
