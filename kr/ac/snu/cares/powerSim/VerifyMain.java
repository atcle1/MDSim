package kr.ac.snu.cares.powerSim;

import java.util.LinkedList;

import kr.ac.snu.cares.NotificationLog.ParsedNotification;
import kr.ac.snu.cares.sampleTrace.Vo.EventFactory;
import kr.ac.snu.cares.sampleTrace.Vo.NotificationEvent;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class VerifyMain {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "./verify/type1.txt";
		//path = "./verify/always_off_vib_500_1000_type2_v2_2.txt";
		//path = "./verify/type1_15s.txt";
		
		// always off, vib
		path = "./verify/type1_790_alwaysoff_vib_0.5_1s_1.txt";	//108431.2 - 106733.0 : 1.6
		//path = "./verify/type1_790_alwaysoff_vib_0.5_1s_6.txt";	//108719.8 - 106733.0 : 1.8
		//path = "./verify/type1_790_alwaysoff_vib_0.5_1s_7.txt";	//110607.9 - 106733.0 : 3.5
		// 6, 7 txt는 실제 존재아님, 1과 동일, 측정실험만 3번
		
		//path = "./verify/type2_10min_5v_alwyasoff_vib_0.5_1s_1.txt";	//23202 - 23795.0 : -2.5
		//path = "./verify/type2_10min_5v_alwyasoff_vib_0.5_1s_6.txt";	//22392 - 21915.0 : 2.1
		//path = "./verify/type2_10min_5v_alwyasoff_vib_0.5_1s_7.txt";	//26088 - 23795.00 : 8.8
		
		//path = "./verify/type2_10min_10v_alwyasoff_vib_0.5_1s_4.txt";	//33570 - 33,332.9 : 0.7
		//path = "./verify/type2_10min_10v_alwyasoff_vib_0.5_1s_6.txt";	//38310 - 35,988.4 : 6.0
		//path = "./verify/type2_10min_10v_alwyasoff_vib_0.5_1s_7.txt";	//37164 - 34075.30 : 8.3
		
		//path = "./verify/type2_10min_20v_alwyasoff_vib_0.5_1s_1.txt";	//61068	 - 56621.80 : 7.2
		//path = "./verify/type2_10min_20v_alwyasoff_vib_0.5_1s_6.txt";	//62796 - 57500.20 : 8.4
		//path = "./verify/type2_10min_20v_alwyasoff_vib_0.5_1s_7.txt";	
		//path = "./verify/type2_10min_20v_alwyasoff_vib_0.5_1s_8.txt";	//67278 - 62915.00 : 6.5

		
		// always on, vib
		//path = "./verify/type1_790_alwayson_vib0.5_1s_v1.txt"; // 124148.5 - 113,613.1 8.4%S
		//path = "./verify/type1_790_alwayson_vib0.5_1s_v2.txt"; // 119764.0 - 113,613.1 8.4%S
		//path = "./verify/type1_790_alwayson_vib0.5_1s_v3.txt"; // 117994.4 - 113,613.1 8.4%S
		// 2, 3 실제 존재 아님, 1과 동일, 측정 실험만 3번

		//path = "./verify/type2_10min_5v_alwyason_vib_0.5_1s_4.txt";	//30930 - 29,292.2 : 5.3
		//path = "./verify/type2_10min_5v_alwyason_vib_0.5_1s_5.txt";	//30210 - 29,292.2 : 3.0
		//path = "./verify/type2_10min_5v_alwyason_vib_0.5_1s_6.txt";	//30660 - 28,042.4 : 8.5
				
		//path = "./verify/type2_10min_10v_alwyason_vib_0.5_1s_2.txt";	//46236 - 42,865.8 : 7.3
		//path = "./verify/type2_10min_10v_alwyason_vib_0.5_1s_3.txt";	//46794 - 41,915.4 : 10.4
		//path = "./verify/type2_10min_10v_alwyason_vib_0.5_1s_4.txt";	//43368 - 40.197.6 : 7.3
		
		//path = "./verify/type2_10min_20v_alwyason_vib_0.5_1s_1.txt";	//69276 - 63,041.1 : 9.0
		//path = "./verify/type2_10min_20v_alwyason_vib_0.5_1s_2.txt";	//70206 - 64,533.4 : 8.1
		//path = "./verify/type2_10min_20v_alwyason_vib_0.5_1s_3.txt";	//64938 - 62,380.0 : 4.0
		
				
		
		analysis(path);
		
//		System.out.println(parseLog("   0.000 NOTI 1 len=2 500,1000"));
//		System.out.println(parseLog(" 328.000 CANCEL 5"));
//		System.out.println(parseLog("  64.500 WAIT 10000"));
//		System.out.println(parseLog(" 405.000 LOG END"));
		
	}
	
	public static void analysis(String notiPath) {
		LinkedList<TraceEvent> trace = VerifyMain.readLog(notiPath);
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
				NotificationEvent notiEvent = parseLog(notiString);
				if (notiEvent != null)
					trace.add(notiEvent);
			}
		} while (notiString != null);
		return trace;
	}
	
	public static NotificationEvent parseLog(String str) {
		long time_ms;
		NotificationEvent notificationEvent = null;
		str = str.trim();
		String[] splitStr = str.split(" ");
		try {
			time_ms = (long)Float.parseFloat(splitStr[0]) * 1000;
			switch (splitStr[1]) {
			case "NOTI":
				notificationEvent = new NotificationEvent();
				notificationEvent.setTime(time_ms);
				notificationEvent.notificaiton = new ParsedNotification();
				notificationEvent.notificaiton.time = time_ms;
				notificationEvent.notificaiton.key = "NOTI|" + splitStr[2];
				if (splitStr.length == 4)
					notificationEvent.notificaiton.vib = parseVib(splitStr[3], null);
				else
					notificationEvent.notificaiton.vib = parseVib(splitStr[3], splitStr[4]);
				notificationEvent.notificaiton.type = 1;
				break;
			case "CANCEL":
				notificationEvent = new NotificationEvent();
				notificationEvent.setTime(time_ms);
				notificationEvent.notificaiton = new ParsedNotification();
				notificationEvent.notificaiton.time = time_ms;
				notificationEvent.notificaiton.key = "CANCEL|" + splitStr[2];
				notificationEvent.notificaiton.type = 3;
				break;
			case "WAIT":
				// ignore
				break;
			case "LOG":
				notificationEvent = new NotificationEvent();
				notificationEvent.setTime(time_ms);
				notificationEvent.notificaiton = new ParsedNotification();
				notificationEvent.notificaiton.time = time_ms;
				notificationEvent.notificaiton.type = 0;		// end
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return notificationEvent;
	}
	public static int[] parseVib(String lenStr, String vibStr)
	{
		if (lenStr.equals("null"))
			return null;
		if (lenStr.equals("len=0"))
			return new int[0];
		
		String[] lenSplit = lenStr.split("=");
		String[] vibSplit = vibStr.split(",");
		int length = Integer.parseInt(lenSplit[1]);
		int []vib = new int[length];
		for (int i = 0; i < length; i++) {
			vib[i] = Integer.parseInt(vibSplit[i]);
		}
		return vib;
	}
}
