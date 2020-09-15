package kr.ac.snu.cares.MDSim.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Util.MyUtil;

public class WindowManager {
	private String topPackage = "null";
	private long prevPackageResumed;
	private Boolean bScreenOn = false;
	
	private HashMap<String, Integer> runtimeStat = new HashMap<String, Integer>();
	
	public void init(long resumedMillis, boolean bScreenOn) {
		runtimeStat.clear();
		topPackage = "kr.ac.snu.cares.lsprofiler";
		prevPackageResumed = resumedMillis;
		this.bScreenOn = bScreenOn;
	}
	
	public void setScreen(long timeMillis, boolean bOn) {
		//System.err.println(MyUtil.MillisToStr(timeMillis) + " setScreen " + bOn);
		if (bOn) {
			prevPackageResumed = timeMillis;
		} else {
			if (bScreenOn)
				addRuntime(timeMillis - prevPackageResumed, topPackage);
		}
		bScreenOn = bOn;
	}
	
	public void onShutdown(long timeMillis) {
		// accumulate
		setScreen(timeMillis, false);
	}
	public void onBootup(long timeMillis) {
		topPackage = "launcher(boot)";
		prevPackageResumed = timeMillis;
		this.bScreenOn = true;
	}
	
	public void setTopActivity(long timeMillis, String packageName) {
		//System.err.println(MyUtil.MillisToStr(timeMillis) + " ffga : " + packageName);
		if (packageName == null)
			System.out.println("null");
		if (bScreenOn) {
			// accumulate previous resumed package
			addRuntime(timeMillis - prevPackageResumed, topPackage);
		} else {
			// ignore, not a real user behavior		
			// addRuntime(timeMillis - prevPackageResumed, topPackage);
			/*
			System.err.println("Screen offed, but top activity is changed");
			System.err.println(MyUtil.MillisToStr(prevPackageResumed) + " " + topPackage + " to " +
					MyUtil.MillisToStr(timeMillis) + " " + packageName); 
			*/
		}
		topPackage = packageName;
		prevPackageResumed = timeMillis;
	}
	
	private void addRuntime(long runTimeMillis, String packageName) {
	    /*if(packageName == null){
		System.out.println("packagename is null");
		return;
	    }*/
		if (packageName.equals("kr.ac.snu.cares.lsprofiler")) return;
		//if (packageName.equals("com.initialcoms.ridi"))
		//	MDSim.logger.warning("runTimeMillis " + runTimeMillis/1000 + " " + packageName);
		if (packageName.equals("droom.sleepIfUCan") && runTimeMillis > 10 * 1000) {
			System.out.println(MyUtil.MillisToStr(MDSim.currentTimeMillis) + "addRuntime " + runTimeMillis/1000 + " " + packageName);
		}
		if (runTimeMillis < 0) {
			MDSim.logger.warning("runTimeMillis " + runTimeMillis + " " + packageName);
			return;
		}
		// System.out.println("addRuntime " + runTimeMillis + " " + packageName);
		if (runtimeStat.containsKey(packageName)) {
			int accumulateTime = (int)(runtimeStat.get(packageName) + runTimeMillis);
			runtimeStat.put(packageName, accumulateTime);
		} else {
			runtimeStat.put(packageName, (int)runTimeMillis);
		}
	}
	
	public HashMap<String, Integer> getRuntimeStat() {
		return runtimeStat;
	}
	
	public void doReport() {
		System.out.println("\n===WindowManager===");
		HashMap<String, Integer> runStat = getRuntimeStat();
		Iterator<String> iteratorKey = sortByValue(runStat).iterator();
		while(iteratorKey.hasNext()){
			   String key = iteratorKey.next();
			   System.out.printf("%10.2f %s\n", runStat.get(key)/1000.0, key);
		}
	}
	
    public static List sortByValue(final Map map){
        List<String> list = new ArrayList();
        list.addAll(map.keySet());
         
        Collections.sort(list,new Comparator(){
             
            public int compare(Object o1,Object o2){
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                 
                return ((Comparable) v1).compareTo(v2);
            }
             
        });
        Collections.reverse(list); // 주석시 오름차순
        return list;
    }
}
