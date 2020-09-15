package kr.ac.snu.cares.MDSim.Report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;
import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class NotificationPkgReport {
	private HashMap<String, Integer> notificationStat = new HashMap<String, Integer>();

	public void notiNotify(NotificationItem item) {
		if (notificationStat.containsKey(item.pkg)) {
			int totalCnt = notificationStat.get(item.pkg);
			notificationStat.put(item.pkg, totalCnt + 1);
		} else {
			notificationStat.put(item.pkg, 1);
		}
	}
	
	public void doReport() {
		HashMap<String, Integer> runStat = notificationStat;
		Iterator<String> iteratorKey = sortByValue(runStat).iterator();
		System.out.println("===Notification cnt===");
		while(iteratorKey.hasNext()){
			   String key = iteratorKey.next();
			   System.out.printf("%30s %d\n", key, runStat.get(key));
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
