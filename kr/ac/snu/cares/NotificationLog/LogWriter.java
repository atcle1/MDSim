package kr.ac.snu.cares.NotificationLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;

public class LogWriter {
	BufferedWriter bufferWriter;
	
	public void open(String path) {
		File file = new File(path);

		
		try {
			bufferWriter = new BufferedWriter(new FileWriter(file, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(long time, ParsedNotification item) {
		String msg = "", vibStr = "";
		if (bufferWriter == null) {
			System.err.println(this + "bufferWriter is null");
			return;
		}
	    try {
	    	msg += item.bSmnEnabled ? "1\t" : "0\t";
	    	msg += time + "\t" + item.type;
	    	
			if (item.vib != null) {
				for (int i = 0; i <item.vib.length - 1; i++) {
					vibStr+=item.vib[i]+",";
				}
				vibStr += item.vib[item.vib.length-1] + ";";
			}
			msg += "\t" + item.key + "\t";
			if (item.type != 3) {
				msg += item.titleLen + ";" + item.textLen + ";" + item.bigTextLen + ";" +
						vibStr;	
			}
				    	
	    	bufferWriter.write(msg);
	    	bufferWriter.newLine();
	    }  catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	public void flush() {
		try {
		bufferWriter.flush();
		} catch (Exception ex) {
			
		}
	}
}
