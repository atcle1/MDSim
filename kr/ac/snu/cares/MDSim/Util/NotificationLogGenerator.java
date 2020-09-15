package kr.ac.snu.cares.MDSim.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.management.Notification;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Vo.LogItem;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationNotify;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationRemove;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationUpdate;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;
import kr.ac.snu.cares.NotificationLog.ParsedNotification;

public class NotificationLogGenerator {
	private String pathStr = "./dataset_txt/";
	private String fileName = "DefaultNotifcationLog.txt";
	private File file;
	private BufferedWriter bufferWriter;
	private long startTime = 0;
	private long prevTime = 0;
	private boolean bAbsoluteTime = false;
	
	public NotificationLogGenerator() {
		
	}
	
	public void setAbsoluteTime(boolean bTrue) {
		bAbsoluteTime = bTrue;
		bAbsoluteTime = false;
		// 파워모델을 위해 바꿈, sampleTrace 시 윗라인 사용
	}
	
	public void init() {
		MDSim sim = MDSim.getInstance();
		String snmEnable = sim.notificationManager.snm.bEnabled ? "SNE" : "SND";
		fileName = sim.person_name + "_" + sim.dir + "_" + snmEnable + ".txt";
		file = new File(pathStr, fileName);
		try {
			bufferWriter = new BufferedWriter(new FileWriter(file, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void writeLogEnd() {
		// 파워모델을 위해 바꿈, sampleTrace 시 리턴
		if (bufferWriter == null) {
			System.err.println(this + "bufferWriter is null");
			return;
		}
    	try {
			bufferWriter.write(MDSim.getInstance().endTime.getTimeInMillis()-startTime + "\t0\tEND");
			bufferWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}
	
	public void writeNewNotification(NotificationItem notification) {
		if (bufferWriter == null) {
			System.err.println(this + "bufferWriter is null");
			return;
		}
	    try {
	    	String msg = getNotificationLogForm(MDSim.currentTimeMillis, notification, 1);
	    	bufferWriter.write(msg);
	    	bufferWriter.newLine();
	    }  catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	public void writeUpdateNotification(NotificationItem notification) {
		if (bufferWriter == null) {
			System.err.println(this + "bufferWriter is null");
			return;
		}
	    try {
	    	String msg = getNotificationLogForm(MDSim.currentTimeMillis, notification, 2);
	    	bufferWriter.write(msg);
	    	bufferWriter.newLine();
	    }  catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	
	public void writeRemoveNotification(NotificationItem notification) {
		if (bufferWriter == null) {
			System.err.println(this + "bufferWriter is null");
			return;
		}
	    try {
	    	String msg = getNotificationLogForm(MDSim.currentTimeMillis, notification, 3);
	    	bufferWriter.write(msg);
	    	bufferWriter.newLine();
	    }  catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}
	public void flush() {
		if (bufferWriter == null) {
			System.err.println(this + "bufferWriter is null");
			return;
		}
		try {
			bufferWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getNotificationLogForm(long timeMillis, NotificationItem item, int type) {
		String msg = "";
		String vibStr = "";
		long elapsedTime = 0;
		if (startTime == 0) {
			startTime = timeMillis;
			startTime = MDSim.getInstance().startTime.getTimeInMillis();
			// 파워모델을 위해 바꿈, sampleTrace 시 윗라인 사용
			prevTime = startTime;
			
		}
		if (timeMillis < prevTime) {
			System.out.println("err");
		} else {
			if (bAbsoluteTime)
				elapsedTime = timeMillis;
			else
				elapsedTime = timeMillis - startTime;
			prevTime = timeMillis;
		}
		
		msg += elapsedTime;
		
		msg += "\t" + getNotificationLogFormStr(item, type);
		
		return msg;
	}
	
	public static String getNotificationLogFormStr(NotificationItem item, int type) {
		String msg = "";
		String vibStr = "";

		msg += type;
		
		if (item.vib != null) {
			for (int i = 0; i <item.vib.length - 1; i++) {
				vibStr+=item.vib[i]+",";
			}
			vibStr += item.vib[item.vib.length-1] + ";";
		} else {
			if (item.pkg.equals("com.android.mms")) {
				vibStr += "100,100,100,100";
			} else if (item.pkg.equals("com.kakao.talk")) {
				vibStr += "100,1000,0,0";				
			}
		}
		msg += "\t" + item.key + "\t";
		if (type != 3) {
			msg += item.titleLen + ";" + item.textLen + ";" + item.bigTextLen + ";" +
					vibStr;	
		}
		return msg;
	}
}
