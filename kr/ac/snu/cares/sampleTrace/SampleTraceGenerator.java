package kr.ac.snu.cares.sampleTrace;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.management.Notification;

import kr.ac.snu.cares.MDSim.Log.LogReader;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Util.NotificationLogGenerator;
import kr.ac.snu.cares.MDSim.Vo.LogItem;
import kr.ac.snu.cares.MDSim.Vo.LogItem.LogType;
import kr.ac.snu.cares.sampleTrace.Vo.EventFactory;
import kr.ac.snu.cares.sampleTrace.Vo.NotificationEvent;
import kr.ac.snu.cares.sampleTrace.Vo.TouchEvent;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class SampleTraceGenerator implements Cloneable {
	public String dbPath = "kms/watch_phone/watch_phone.sqlite3";
	public String sneNotiPath = "kms_watch_phone_SNE.txt";
	public String sndNotiPath = "kms_watch_phone_SND.txt";
	
	public long logStart;
	public long logEnd;
	
	public Trace dbTrace = new Trace();
	public Trace notiTrace1 = new Trace();
	public Trace notiTrace2 = new Trace();
		
	ArrayList<SamplePeriod> samplingPeriods;
	
	public long splitStart;
	public long splitEnd;
	public long sliceCnt = 12;
	public long sliceLen = 5 * 60 * 1000;	// 5min
	public long targetTraceLen = 60 * 60 * 1000;	// 1 hour
	public long targetTotalSamplingLen = 24 * 60 * 60 * 1000;	// 24 hour
	
	public void init() {
		dbTrace = new Trace();
		notiTrace1 = new Trace();
		notiTrace2 = new Trace();
		logStart = logEnd = 0;
		splitStart = splitEnd = 0;
	}
	
	protected SampleTraceGenerator clone() {
		SampleTraceGenerator obj = null;
		try {
			obj = (SampleTraceGenerator)super.clone();
			obj.dbTrace = dbTrace.clone();
			obj.notiTrace1 = notiTrace1.clone();
			obj.notiTrace2 = notiTrace2.clone();
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//obj.dbTrace = dbTrace.
		return obj;
	}
	
	
	public void setDb(String path) {
		dbPath = path;
	}
	
	public void setNoti(boolean bSne, String path) {
		if (bSne)
			this.sneNotiPath = path;
		else
			this.sndNotiPath = path;
	}
	
	public void readALlLogs() {
		// TODO Auto-generated method stub
		LogItem item = null;
		
		LogReader logReader = LogReader.getInstance(dbPath);
       logReader.init();       
       boolean bFirst = true;
       int cnt = 0;
       while ((item = logReader.nextLogItem()) !=null)
       {
    	   if (item.deviceLogType == LogItem.SOURCE_WEAR_MDK) {
    		   if (bFirst) {
    			   logStart = item.timeMillis;
    			   bFirst = false;
    		   }
    		   logEnd = item.timeMillis;
    		   cnt++;
    		   switch (item.logType)
    		   {
    		   /*
    		   case KSCREEN_ONOFF:
    			   System.out.println(MyUtil.MillisToStr(item.timeMillis) + " " + item.msg);
    			   break;
    		   case SCREEN_IDLE:
    			   System.out.println(MyUtil.MillisToStr(item.timeMillis) + " " + item.msg);
    			   break;
    		   case SCREEN_BRIGHTNESS:
    			   System.out.println(MyUtil.MillisToStr(item.timeMillis) + " " + item.msg);
    			   break;
    			   */
    		   case KTOUCH:
    			   //System.out.println(MyUtil.MillisToStr(item.timeMillis) + " " + item.msg);
    			   TouchEvent touchEvent = EventFactory.getTouchEvent(item.timeMillis);
    			   dbTrace.trace.add(touchEvent);
    			   break;
    		   }
    	   }
       }
       
       // SND noti
       kr.ac.snu.cares.NotificationLog.LogReader reader = new kr.ac.snu.cares.NotificationLog.LogReader();
       reader.open(sndNotiPath);
       String notiString;
       do {
    	   notiString = reader.readLine();
    	   if (notiString != null) {
	    	   NotificationEvent notiEvent = EventFactory.getNotificationEvent(notiString);
	    	   notiTrace1.trace.add(notiEvent);
    	   }
       } while( notiString != null);
       System.out.println("read SND " + notiTrace1.trace.size());
       
       // SNE noti
       reader = new kr.ac.snu.cares.NotificationLog.LogReader();
       reader.open(sneNotiPath);
       do {
    	   notiString = reader.readLine();
    	   if (notiString != null) {
	    	   NotificationEvent notiEvent = EventFactory.getNotificationEvent(notiString);
	    	   notiTrace2.trace.add(notiEvent);
    	   }
       } while( notiString != null);
       
       System.out.println("readLog start " + MyUtil.MillisToStr(logStart) + " to " + MyUtil.MillisToStr(logEnd));
	}
	
	public boolean prepare() {
		RandomPeriodGenerator gen = new RandomPeriodGenerator();
		Random random = new Random();
		
		splitStart = (long)(random.nextDouble() * (logEnd - logStart - targetTotalSamplingLen))
				+ logStart;
		splitEnd = splitStart + targetTotalSamplingLen;
		
		if (splitStart <= 0) {
			System.err.println("analysis() err traceSliceStart < 0");
			return false;
		}
		
		SamplePeriod parent = new SamplePeriod();
		parent.setTime(true, 0, 0, 0);
		parent.setTime(false, targetTotalSamplingLen);
		//ArrayList<SamplePeriod> periodSet = gen.getRandTerms(parent, 12, 5 * 60 * 1000);
		//targetTotalSamplingLen = 24 * 60 * 60 * 1000;
		samplingPeriods = gen.getRandTerms(parent, sliceCnt, sliceLen);
		
		return true;
	}
	
	public boolean sliceAll() {
		System.out.println("split start " + MyUtil.MillisToStr(splitStart) + " splitEnd " + MyUtil.MillisToStr(splitEnd));
		System.out.println(samplingPeriods);
		dbTrace.sampling(splitStart, splitEnd, samplingPeriods);
		
		notiTrace1.sampling(splitStart, splitEnd, samplingPeriods);
		//notiTrace1.print("noti1");
		
		notiTrace2.sampling(splitStart, splitEnd, samplingPeriods);
		//notiTrace2.print("noti2");
		
		//notiTrace2.printCompress();
		
		return true;		
	}
	
	public boolean compressAll() {
		dbTrace.compress(splitStart, splitEnd, targetTotalSamplingLen/sliceCnt, sliceLen, samplingPeriods);
		notiTrace1.compress(splitStart, splitEnd, targetTotalSamplingLen/sliceCnt, sliceLen, samplingPeriods);
		notiTrace2.compress(splitStart, splitEnd, targetTotalSamplingLen/sliceCnt, sliceLen, samplingPeriods);
		
		return true;
	}
	
	public boolean mergeTrace() {
		notiTrace1.merge(dbTrace.compressedTrace);
		notiTrace2.merge(dbTrace.compressedTrace);
		return true;
	}
	
	
	
	int calMillisToTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		int milisec = calendar.get(Calendar.MILLISECOND);
		
		return hour * 60 * 60 * 1000 +
				min * 60 * 1000 +
				sec *1000 +
				milisec;
	}
	
	public void printResult() {
		dbTrace.printCompress();
		notiTrace1.printCompress();
		notiTrace2.printCompress();
	}
	/*
	public void printTrace() {
		Iterator<TraceEvent> itr = dbTrace.trace.iterator();
		while (itr.hasNext()) {
			TraceEvent event = itr.next();
			System.out.println(event);
		}
	}
	
	
	public LinkedList<TraceEvent> analysis() {
		long traceStartTime = dbTrace.trace.getFirst().getTime();
		long traceEndTime = dbTrace.trace.getLast().getTime();
		if (traceStartTime == 0) {
			System.err.println("analysis() startTime is 0");
		}
		
		Random random = new Random();
		splitStart = (long)(random.nextDouble() * (traceEndTime - traceStartTime - targetTotalSamplingLen))
				+ traceStartTime;
		splitEnd = splitStart + targetTotalSamplingLen;
		
		System.out.println("ts " + splitStart + " ts " + targetTotalSamplingLen );
		if (splitStart <= 0) {
			System.err.println("analysis() err traceSliceStart < 0");
			return null;
		}
		
		samplingTargetSet1 = new LinkedList<TraceEvent>();
		Iterator<TraceEvent> itr = dbTrace.trace.iterator();
		while(itr.hasNext()) {
			TraceEvent event = itr.next();
			long eventTime = event.getTime();
			if (eventTime >= splitStart &&
					eventTime <= splitEnd) {
				//System.out.println(event);
				event.setTime(event.getTime() -  splitStart);
				//System.out.println(event);
				samplingTargetSet1.add(event);
			} else if (eventTime > splitEnd) {
				break;
			}
		}
		System.out.println(samplingTargetSet1.size());
		itr = samplingTargetSet1.iterator();
		
		return samplingTargetSet1;		
	}
	
	public void generate() {
		generatedTrace1 = new LinkedList<TraceEvent>();
		RandomPeriodGenerator gen = new RandomPeriodGenerator();
		SamplePeriod parent = new SamplePeriod();
		parent.setTime(true, 0, 0, 0);
		parent.setTime(false, 24, 0, 0);
		ArrayList<SamplePeriod> periodSet = gen.getRandTerms(parent, 12, 5 * 60 * 1000);
		
		Iterator<TraceEvent> itr = samplingTargetSet1.iterator();
		while(itr.hasNext()) {
			TraceEvent event = itr.next();
			for (int i = 0; i < periodSet.size(); i++) {
				SamplePeriod period = periodSet.get(i);
				if (period.contains(event.getTime())) {
					//event.setTime(event.getTime());
					long periodStart = period.start;
					
					long sliceStart = i * 2 * 60 * 60 * 1000; // 2 hour
					long termStart = i * 5 * 60 * 1000; // 5 min
					
					event.setTime(event.getTime() - periodStart + termStart);

					generatedTrace1.add(event);
				}
			}
		}
		
		itr = generatedTrace1.iterator();
		while(itr.hasNext()) {
			TraceEvent event = itr.next();
			System.out.println(event);
		}
	}
	
	
	public void sort() {
		Collections.sort(dbTrace.trace, new TraceEvent.TimeComp());
	}
	*/
	public double getSmnEfficient() {
		int noti1Cnt = 0;
		int noti2Cnt = 0;
		/*
		noti1Cnt = notiTrace1.compressedTrace.size();
		noti2Cnt = notiTrace2.compressedTrace.size();
		*/
		Iterator<TraceEvent> itr1 = notiTrace1.compressedTrace.iterator();
		Iterator<TraceEvent> itr2 = notiTrace2.compressedTrace.iterator();
		while (itr1.hasNext()) {
			NotificationEvent event = (NotificationEvent)itr1.next();
			if (event.notificaiton.type != 3) {
				noti1Cnt++;
			}
		}
		while (itr2.hasNext()) {
			NotificationEvent event = (NotificationEvent)itr2.next();
			if (event.notificaiton.type != 3) {
				noti2Cnt++;
			}
		}
		//System.out.println("notiCnt " + noti1Cnt + " " + noti2Cnt);
		if (noti1Cnt == 0)
			return 0.0;
		return (double)(noti1Cnt-noti2Cnt)/(double)noti1Cnt;
	}
	public static class smnEffectComp implements Comparator<SampleTraceGenerator> {
		@Override
		public int compare(SampleTraceGenerator arg0, SampleTraceGenerator arg1) {
			// TODO Auto-generated method stub
			if (arg0.getSmnEfficient() - arg1.getSmnEfficient() > 0)
				return 1;
			else if (arg0.getSmnEfficient() == arg1.getSmnEfficient())
				return 0;
			else
				return -1;
		}
	}
	
	public void writeToFile(String path) {
		Iterator<TraceEvent> itr1, itr2, itr3;
		itr1 = notiTrace1.compressedTrace.iterator();
		itr2 = notiTrace2.compressedTrace.iterator();
		itr3 = dbTrace.compressedTrace.iterator();
		System.out.println("\nefficient : " + getSmnEfficient());
		while (itr1.hasNext()) {
			NotificationEvent event = (NotificationEvent)itr1.next();
		
			System.out.println("0\t" + event.getTime() + "\t" + event.notificaiton.getNotificationLogFormStr());
		}
		System.out.println("----------------");
		while (itr2.hasNext()) {
			NotificationEvent event = (NotificationEvent)itr2.next();
		
			System.out.println("1\t" + event.getTime() + "\t" + event.notificaiton.getNotificationLogFormStr());
		}
		System.out.println("----------------");
		while (itr3.hasNext()) {
			TraceEvent event = itr3.next();
		
			System.out.println("1\t" + event.getTime() + " " + event);
		}
		
		System.out.println(notiTrace1.compressedTrace.size() + " / " + notiTrace2.compressedTrace.size() + "/" +
		dbTrace.compressedTrace.size());
				
	}
	
}
