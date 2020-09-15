package kr.ac.snu.cares.MDSim.Device;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Device.SmartNotification.SmartNotificationManager;
import kr.ac.snu.cares.MDSim.Report.WatchNotificationClusterReport;
import kr.ac.snu.cares.MDSim.Report.NotificationPkgReport;
import kr.ac.snu.cares.MDSim.Report.NotificationReport;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Util.NotificationLogGenerator;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;

public class WearWatch {
	public int notifyNewNotificationCnt = 0;
	public int notifyUpdateNotificationCnt = 0;
	public int notifyRemovedNotificationCnt = 0;
	public int watchRemovedNotificationCnt = 0;
	private MDSim mdSim;
	private NotificationLogGenerator nlg;
	private NotificationReport report;
	private NotificationPkgReport pkgReport;
	private WatchNotificationClusterReport clusterReport;
	
	public WearWatch(MDSim mdSim) {
		this.mdSim = mdSim;
		nlg = mdSim.notificationLogGenerator;
		report = new NotificationReport();
		pkgReport = new NotificationPkgReport();
		clusterReport = new WatchNotificationClusterReport();
	}
	
	public ArrayList<NotificationItem> notificationList = new ArrayList<NotificationItem>();
	public void clear() {
		notificationList.clear();
	}
	int maxSize = 0;
	int currentSize = 0;
	public void notifyPosted(long timeMillis, NotificationItem item) {
		SmartNotificationManager.breakNotification(item);
		int foundIdx = findNotiIdx(item);
		if (foundIdx == -1) {
			notificationList.add(item);
			notifyNewNotificationCnt++;
			nlg.writeNewNotification(item);
			currentSize++;
			if (currentSize > maxSize) {
				System.out.println("MAXSIZE " + maxSize);
				maxSize = currentSize;
			}
			report.notiNotify(item);
			pkgReport.notiNotify(item);
		} else {
			notificationList.set(foundIdx, item);
			notifyUpdateNotificationCnt++;
			nlg.writeUpdateNotification(item);
			report.notiNotify(item);
			pkgReport.notiNotify(item);
		}

		//clusterReport.notiNotify(timeMillis, item);
	}
	public void notifyRemoved(long timeMillis, NotificationItem item)	{
		SmartNotificationManager.breakNotification(item);
		int foundIdx = findNotiIdx(item);
		if (foundIdx != -1) {
			notificationList.remove(foundIdx);
			notifyRemovedNotificationCnt++;
			nlg.writeRemoveNotification(item);
			clusterReport.notiRemove(timeMillis, item);
			currentSize--;
		} else {
			System.err.println(MyUtil.MillisToStr(MDSim.currentTimeMillis) + " watch notification deleted() but not founded " + item);
			// for consistency
			//notifyRemovedNotificationCnt++;
			//nlg.writeRemoveNotification(item);
			//clusterReport.notiRemove(timeMillis, item);
			// end
		}
	}
	
	public void notifyRemovedByWatch(long timeMillis, NotificationItem item)	{
		SmartNotificationManager.breakNotification(item);
		int foundIdx = findNotiIdx(item);
		if (foundIdx != -1) {
			notificationList.remove(foundIdx);
			watchRemovedNotificationCnt++;
			currentSize--;
		} else {
			//System.err.println(MyUtil.MillisToStr(MDSim.currentTimeMillis) + " watch notification deleted() but not founded " + item);
		}
	}
	
	public int findNotiIdx(NotificationItem findingItem) {
		for (int i = 0; i < notificationList.size(); i++) {
			NotificationItem item = notificationList.get(i);
			if (item.key.equals(findingItem.key)) {
				return i;
			}
		}
		return -1;
	}
	
	public void doReport() {
		System.out.println("\n===WearWatch===");
		System.out.println("notifyNewNotificationCnt : " + notifyNewNotificationCnt);
		System.out.println("notifyUpdateNotificationCnt : " + notifyUpdateNotificationCnt);
		System.out.println("notifyRemovedNotificationCnt : " + notifyRemovedNotificationCnt);
		System.out.println("watchRemovedNotificationCnt : " + watchRemovedNotificationCnt);
		
		nlg.writeLogEnd();
		nlg.flush();
		report.doReport();
		pkgReport.doReport();
		//clusterReport.doReport();
	}
}
