package kr.ac.snu.cares.MDSim.Device.SmartNotification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.ac.snu.cares.MDSim.DataSet;
import kr.ac.snu.cares.MDSim.MDSim;

public class DelayedSendedLog {
	private File file;
	private BufferedWriter bufferWriter;
	private String dirStr = "./delayed/";
	private String fileName;
	
	public DelayedSendedLog() {
		init();
	}
	
	public void init() {
		MDSim sim = MDSim.getInstance();

		File dir = new File(dirStr);
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		fileName = sim.person_name + "_" + sim.dir + "_" + "dlog.txt";
		
		file = new File(dirStr, fileName);
		try {
			bufferWriter = new BufferedWriter(new FileWriter(file, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeLog(String log) {
		try {
			bufferWriter.write(log);
			bufferWriter.newLine();
			bufferWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
