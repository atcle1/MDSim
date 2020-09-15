package kr.ac.snu.cares.MDSim.Report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;

public class WatchNotificationClusterReport {

	private LinkedList<NotificationInfo> history = new LinkedList<NotificationInfo>();
	private LinkedList<NotificationInfoSet> clusterList = new LinkedList<NotificationInfoSet>();
	private int notifyCnt = 0;
	private int removeCnt = 0;
	public void notiNotify(long current, NotificationItem item) {
		NotificationInfo info = new NotificationInfo();
		info.time = current;
		info.bNotify = true;
		info.item = item;
		history.add(info);
		notifyCnt++;
	}
	
	public void notiRemove(long current, NotificationItem item) {
		NotificationInfo info = new NotificationInfo();
		info.time = current;
		info.bNotify = false;
		info.item = item;
		history.add(info);
		removeCnt++;
	}
	
	public void doReport() {
		System.out.println("===NotificationClusterReport===");
		int clusterSizeArr[] = new int[11];
		doClustering();
		int clusterTotalSize = clusterList.size();
		Iterator<NotificationInfoSet> iter = clusterList.iterator();
		while (iter.hasNext()) {
			NotificationInfoSet cluster = iter.next();
			//System.out.println(cluster);
			int clusterSize = cluster.cluster.size();
			if (clusterSize < 5) {
				clusterSizeArr[clusterSize]++;
			} else {
				clusterSizeArr[5]++;
			}
		}
		int sum = notifyCnt + removeCnt;
		for (int i = 1; i < 5; i++) {
			System.out.format("%3d : %4d(%2.1f)\n", i, clusterSizeArr[i], 100.0 * (float)clusterSizeArr[i]/clusterTotalSize);
		}
		System.out.format("%3s : %4d(%2.1f)\n", "5+", clusterSizeArr[5], 100.0 * (float)clusterSizeArr[5]/clusterTotalSize);
		
		System.out.println(" notify : " + notifyCnt + " removeCnt " + removeCnt);
		System.out.println(" history : " + history.size());
		System.out.println("cluster cnt : " + clusterList.size());
		System.out.println("===NotificationClusterReport end===");
	}
	
	private static final long MERGE_THR = 100;
	private void doClustering() {
		Iterator<NotificationInfo> iter = history.iterator();
		NotificationInfo prevInfo;
		NotificationInfoSet currentCluster = new NotificationInfoSet();
		
		if (iter.hasNext()) {
			prevInfo = iter.next();
			currentCluster.cluster.add(prevInfo);
		} else
			return;
		
		while (iter.hasNext()) {
			NotificationInfo info = iter.next();
			if (prevInfo.time + MERGE_THR > info.time) {
				currentCluster.cluster.add(info);
			} else {
				clusterList.add(currentCluster);
				currentCluster = new NotificationInfoSet();
				currentCluster.cluster.add(info);
			}
			prevInfo = info;
		}
		// last cluster
		clusterList.add(currentCluster);
	}
	
	class NotificationInfoSet {
		public LinkedList<NotificationInfo> cluster;
		public NotificationInfoSet() {
			cluster = new LinkedList<NotificationInfo>();
		}
		@Override
		public String toString() {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("---------------\n");
			Iterator<NotificationInfo> iter = cluster.iterator();
			while(iter.hasNext()) {
				NotificationInfo info = iter.next();
				stringBuffer.append(info);
			}
			return stringBuffer.toString();
		}
	}
	
    class NotificationInfo {
    	public long time;
    	public boolean bNotify;
    	public NotificationItem item;
    	
    	@Override
    	public String toString() {
    		return MyUtil.MillisToStr(time) +" " + bNotify +"\n";
    	}
    }
}
