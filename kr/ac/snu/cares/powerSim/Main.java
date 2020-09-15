package kr.ac.snu.cares.powerSim;

import java.util.Iterator;
import java.util.LinkedList;

import kr.ac.snu.cares.sampleTrace.Vo.EventFactory;
import kr.ac.snu.cares.sampleTrace.Vo.NotificationEvent;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class Main {
	public static String names[] = {"1", "2", "3", "4", "5"};
	//public static String notiPath = names[0] + "_watch_phone_SNE.txt";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "";
		for (int i = 0; i < 5; i++) {
			path = "./dataset_txt/" + names[i] + "_watch_phone_SND.txt";
			System.out.println("path : " + path);
			analysis(path);
			System.out.println("path : " + path + " end\n");
		}
		
	}
	
	public static void analysis(String notiPath) {
		LinkedList<TraceEvent> trace = Main.readLog(notiPath);
		PowerSim sim = new PowerSim();
		sim.initParm();
		sim.runSim(trace);
	}
	
	public static LinkedList<TraceEvent> readLog(String sndNotiPath) {
		kr.ac.snu.cares.NotificationLog.LogReader reader = new kr.ac.snu.cares.NotificationLog.LogReader();
		LinkedList<TraceEvent> trace = new LinkedList<TraceEvent>();
		reader.open(sndNotiPath);
		String notiString;
		do {
			notiString = reader.readLine();
			if (notiString != null) {
				NotificationEvent notiEvent = EventFactory
						.getNotificationEvent(notiString);
				trace.add(notiEvent);
			}
		} while (notiString != null);
		return trace;
	}
	

}