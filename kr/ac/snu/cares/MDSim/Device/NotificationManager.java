package kr.ac.snu.cares.MDSim.Device;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.ac.snu.cares.MDSim.DataSet;
import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Main;
import kr.ac.snu.cares.MDSim.Device.SmartNotification.SmartNotificationManager;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.LogDynamicEvent;
import kr.ac.snu.cares.MDSim.Vo.LogItem;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationRemove;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationNotify;
import kr.ac.snu.cares.MDSim.Vo.LogNotificationUpdate;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;

public class NotificationManager {
    private static final int REASON_DELEGATE_CLICK = 1;
    private static final int REASON_DELEGATE_CANCEL = 2;
    private static final int REASON_DELEGATE_CANCEL_ALL = 3;
    private static final int REASON_DELEGATE_ERROR = 4;
    private static final int REASON_PACKAGE_CHANGED = 5;
    private static final int REASON_USER_STOPPED = 6;
    private static final int REASON_PACKAGE_BANNED = 7;
    private static final int REASON_NOMAN_CANCEL = 8;
    private static final int REASON_NOMAN_CANCEL_ALL = 9;
    private static final int REASON_LISTENER_CANCEL = 10;
    private static final int REASON_LISTENER_CANCEL_ALL = 11;
    private static final int REASON_GROUP_SUMMARY_CANCELED = 12;
    private static final int REASON_GROUP_OPTIMIZATION = 13;
    
	private static final Logger logger = Logger.getLogger(NotificationManager.class.getName());
	
	private MDSim mdSim;
	
	// Notification
	public int totalNotifiedNotifCnt;
	public int totalUpdateNotiCnt;
	public int totalRemovedNotiCnt;
	// SendedNotification
	public int totalSendedNotifiedNotiCnt;
	public int totalSendedUpdateNotiCnt;
	public int totalSendedRemovedNotiCnt;
	// SendedNotification during screen on
	public int totalSendedNotifiedNotiWhenScreenOnCnt;
	public int totalSendedUpdateNotiWhenScreenOnCnt;
	public int totalSendedRemovedNotiWhenScreenOnCnt;
		
	//
	public int totalBlockedNotiCnt;
	public int totalDelayedNotiCnt;
	public int avgNotiDelay;	
	
	public int totalSendedADNotificaitonCnt;
	
	public SmartNotificationManager snm;
	public WearWatch wearWatch;
	
	public ArrayList<NotificationItem> notificationList = new ArrayList<NotificationItem>();
	
	public NotificationManager(MDSim mdSim) {
		this.mdSim = mdSim;
		wearWatch = new WearWatch(mdSim);
		snm = new SmartNotificationManager(this, wearWatch);
	}
	
	public void init() {
		totalNotifiedNotifCnt = 0;
		totalUpdateNotiCnt = 0;
		totalRemovedNotiCnt = 0;
		
		totalSendedNotifiedNotiCnt = 0;
		totalSendedUpdateNotiCnt = 0;
		totalSendedRemovedNotiCnt = 0;
		
		totalSendedNotifiedNotiWhenScreenOnCnt = 0;
		totalSendedUpdateNotiWhenScreenOnCnt = 0;
		totalSendedRemovedNotiWhenScreenOnCnt = 0;
		
		totalBlockedNotiCnt = 0;
		totalDelayedNotiCnt = 0;
		avgNotiDelay = 0;	
		
		totalSendedADNotificaitonCnt = 0;
		bWearNotificationFound = false;
	}
	
	private boolean bUserPresented = false;
	public void setUserTouched() {
		bUserPresented = true;	
	}
	public void onScreenOnOff(long timeMillis, Boolean bScreenOn) {
		if (bScreenOn) {
			bUserPresented = true;
			snm.onScreenOn(timeMillis);
		} else {
			bUserPresented = false;
			snm.onScreenOff(timeMillis);
		}
	}
	
	public void onShutdown(long timeMillis) {
		System.out.println("onShutdown");
		for (int i = 0; i < notificationList.size(); i++) {
			NotificationItem item = notificationList.get(i);
			LogNotificationRemove litem = new LogNotificationRemove();
			litem.notification = item;
			litem.logItem = MDSim.currentLogItem;
			cancel(litem);
		}
	}
	public void onBootup(long timeMillis) {
		notificationList.clear();
		wearWatch.clear();
		System.out.println("onBootup");
		
	}
	
	public void onTouch(long timeMillis) {
		snm.onUserActivity(timeMillis, 2);
	}
	
	private boolean isContains(ArrayList<NotificationItem> list, String key)
	{
		if (list == null) {
			logger.log(Level.WARNING, "list is null");
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).key.equals(key)) return true;
		}
		return false;
	}
	public void enqueue(LogNotificationNotify notificationEnq) {
		enqueue(notificationEnq, true);
	}
	public void enqueue(LogNotificationNotify notificationEnq, boolean bGroupCheck) {
		//System.out.println("enq : " + notificationEnq.notification);
		SmartNotificationManager.breakNotification(notificationEnq.notification);
		if (notificationEnq.notification.pkg.equals("kr.ac.snu.cares.lsprofiler")) return;
		if (bGroupCheck && isGroupNotification(notificationEnq.logItem.timeMillis, 2, notificationEnq.notification)) return;
		totalNotifiedNotifCnt++;
		
		if (isSendedNotification(notificationEnq.notification)) {
			if (isContains(notificationList, notificationEnq.notification.key)) {
				// already contains...
				totalNotifiedNotifCnt--;
				//logger.log(Level.WARNING, "notification enq() but already enqued " + notificationEnq.notification.TAG + " " + MyUtil.MillisToCal(notificationEnq.notification.postDate.getTimeInMillis()));
				LogNotificationUpdate notificationUpdate = new LogNotificationUpdate();
				notificationUpdate.logItem = notificationEnq.logItem;
				notificationUpdate.notification = notificationEnq.notification;
				notificationUpdate.notification.updateDate = notificationEnq.notification.postDate;
				update(notificationUpdate, false);
				return;
			} else {
				totalSendedNotifiedNotiCnt++;
				//System.out.println("tsnn++ " + totalSendedNotifiedNotiCnt + " " + notificationEnq.logItem.msg);
				
				Calendar postCal = notificationEnq.notification.postDate;
				if (postCal == null)
					postCal = notificationEnq.notification.updateDate;
				if (bUserPresented && mdSim.mobileHwContext.lastScreenOn < postCal.getTimeInMillis()-1500) {
					totalSendedNotifiedNotiWhenScreenOnCnt++;
					//System.out.println(notificationEnq.notification);
				}
				if (notificationEnq.logItem.msg.contains("광고")) {
					totalSendedADNotificaitonCnt++;
				}
			}
			
			// 원래 아래코드는 if 밖에 있었으나 숫자이상으로 안으로 옮김, totalNotifiedNotiCnt, totalRemovedNotiCnt 않맞게되나 필요없으므로 무시
			int idx = findNotiIdx(notificationEnq.notification);
			if (idx != -1) {
				notificationList.set(idx, notificationEnq.notification);
			} else {
				notificationList.add(notificationEnq.notification);
			}
			
			// snm
			boolean bDelayed = false;
			if (snm.bEnabled) {
				bDelayed = snm.notifyPostedLockedToWear(notificationEnq.logItem.timeMillis, notificationEnq.notification);
				if (!bDelayed && isSendedNotification(notificationEnq.notification)) {
					wearWatch.notifyPosted(MDSim.currentTimeMillis, notificationEnq.notification);
				}
			} else {
				if (isSendedNotification(notificationEnq.notification))
					wearWatch.notifyPosted(MDSim.currentTimeMillis, notificationEnq.notification);
			}
		}else {

		}
		

		
	}
	public void update(LogNotificationUpdate notificationUpdate) {
		update(notificationUpdate, true);
	}
	public void update(LogNotificationUpdate notificationUpdate, boolean bGroupCheck) {
		//System.out.println("upd : " + notificationUpdate.notification);
		if (notificationUpdate.notification.pkg.equals("kr.ac.snu.cares.lsprofiler")) return;
		if (bGroupCheck && isGroupNotification(notificationUpdate.logItem.timeMillis, 2, notificationUpdate.notification)) return;
		int foundIdx = findNotiIdx(notificationUpdate.notification);
		
		
		if (foundIdx != -1) {
			notificationList.set(foundIdx, notificationUpdate.notification);
			totalUpdateNotiCnt++;
			
			if (isSendedNotification(notificationUpdate.notification)) {
				totalSendedUpdateNotiCnt++;
				
				if (bUserPresented &&
						mdSim.mobileHwContext.lastScreenOn < notificationUpdate.notification.updateDate.getTimeInMillis()-1500)
					totalSendedUpdateNotiWhenScreenOnCnt++;
				//System.out.println(notificationUpdate.notification);
				if (notificationUpdate.logItem.msg.contains("광고")) {
					totalSendedADNotificaitonCnt++;
				}
				

			}

			// snm
			if (snm.bEnabled) {
				if (isSendedNotification(notificationUpdate.notification)){
					boolean bDelayed = snm.notifyPostedLockedToWear(notificationUpdate.logItem.timeMillis, notificationUpdate.notification);
					if (!bDelayed )
						wearWatch.notifyPosted(MDSim.currentTimeMillis, notificationUpdate.notification); 
				}
			} else {
				if (isSendedNotification(notificationUpdate.notification))
					wearWatch.notifyPosted(MDSim.currentTimeMillis, notificationUpdate.notification);
				if (wearWatch.notifyNewNotificationCnt == 164) {
					System.out.println(notificationUpdate);
				}
				
			}
				
		} else {
			//logger.log(Level.WARNING, "notification update() but not founded " + notificationUpdate.notification.key);
			if (isSendedNotification(notificationUpdate.notification)) {
				LogNotificationNotify notificationEnq = new LogNotificationNotify();
				notificationEnq.logItem = notificationUpdate.logItem;
				notificationEnq.notification = notificationUpdate.notification;
				enqueue(notificationEnq, false);
			}
		}
	}
	
	public void cancel(LogNotificationRemove notificationCancel) {
		//System.out.println("cal : " + notificationCancel.logItem);
		if (notificationCancel.notification.pkg.equals("kr.ac.snu.cares.lsprofiler")) return;
		if (isGroupNotification(notificationCancel.logItem.timeMillis, 3, notificationCancel.notification)) return;
		int foundIdx = findNotiIdx(notificationCancel.notification);
		
		if (foundIdx != -1) {
			totalRemovedNotiCnt++;
			
			if (isCancelSendable(notificationList.get(foundIdx))) {
				//System.out.println("tsc " + totalSendedRemovedNotiCnt + " " + notificationCancel.notification);
				if (bUserPresented) {
					totalSendedRemovedNotiWhenScreenOnCnt++;
				} else {
					if (notificationCancel.notification.deleteReason != 8 && notificationCancel.notification.deleteReason != 10
							&& notificationCancel.notification.deleteReason != 12)
						System.out.println("noc when screen off "+notificationCancel.notification.deleteReason + " " +  MyUtil.MillisToStr(notificationCancel.logItem.timeMillis) + " " + notificationCancel.notification);
				}
			}
			
			

			if (wearWatch.findNotiIdx(notificationCancel.notification) == -1) {

				
			} else {
				totalSendedRemovedNotiCnt++;
				
				int idx = wearWatch.findNotiIdx(notificationCancel.notification);
				if (idx == -1) {
					System.err.println("=========??");
					//wearWatch.notificationList.remove(notificationCancel.notification);
				}
				// snm
				if (snm.bEnabled) {
					if ((notificationCancel.notification.deleteReason == 10 || notificationCancel.notification.deleteReason == 11 )&&
							wearWatch.findNotiIdx(notificationCancel.notification) == -1) {
						// if (notification removed by wear and watch have no such a notification
						
						// smartnotification delay-canceled notification, but log shows that notification removed by watch.
						System.err.println("!!!!!!!!!!!!!111");
					} else {
						boolean bDelayed = snm.notifyRemovedLockedToWear(notificationCancel.logItem.timeMillis, notificationCancel.notification, notificationCancel.notification.deleteReason);
						
						if (notificationCancel.notification.deleteReason == 10 || notificationCancel.notification.deleteReason == 11)
							wearWatch.notifyRemovedByWatch(MDSim.currentTimeMillis, notificationCancel.notification);
						else if (!bDelayed && wearWatch.findNotiIdx(notificationCancel.notification)!=-1 /* isCancelSendable(notificationList.get(foundIdx))*/) {
							wearWatch.notifyRemoved(MDSim.currentTimeMillis, notificationCancel.notification);
						}
					}
				} else {
					if (notificationCancel.notification.deleteReason == 10 || notificationCancel.notification.deleteReason == 11)
						wearWatch.notifyRemovedByWatch(MDSim.currentTimeMillis, notificationCancel.notification);
					else{
						if (isCancelSendable(notificationList.get(foundIdx)))
							wearWatch.notifyRemoved(MDSim.currentTimeMillis, notificationCancel.notification);
					}
				}
			}

			notificationList.remove(foundIdx);

		} else {
			//logger.log(Level.WARNING, "notification deleted() but not founded " + notificationCancel.logItem);
			int idx = wearWatch.findNotiIdx(notificationCancel.notification);
			if (idx != -1) {
				System.err.println("=========??");
				//wearWatch.notificationList.remove(idx);
			}
		}
	}
	
	public void onDynamicEvent(LogItem logItem, LogDynamicEvent parsedEvent) {
		if (parsedEvent.event.equals(SmartNotificationManager.MESSAGE_SMARTNOTIFICATION)){
			snm.handleMessage(parsedEvent);
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
	
	public NotificationItem getNotification(String key) {
		for (int i = 0; i < notificationList.size(); i++) {
			NotificationItem item = notificationList.get(i);
			if (item.key.equals(key)) {
				return item;
			}
		}
		return null;
	}
	
	private boolean isSendedNotification(NotificationItem item) {
		// TODO : check notification whether sended or not
		String key = item.key;
		String pkg = item.pkg;
		boolean bOnGoing = item.bOnGoing;
		boolean bClearable = item.bClearable;
		
		if (!bOnGoing && bClearable &&
				(pkg.equals("com.android.mms") ||
				pkg.equals("com.google.android.gm") ||
				pkg.equals("com.nhn.android.band") ||
				pkg.equals("com.kakao.talk"))
				) {
			return true;
		}
		
		if (bOnGoing|| bClearable == false ||
				key.contains("com.google.android.gms") ||
				key.contains("com.android.deskclock") ||
				key.contains("low_battery") ||
				pkg.equals("com.android.vending")||
				pkg.equals("com.android.providers.downloads") ||
				pkg.equals("android")||
				pkg.equals("com.android.dialer") ||
				pkg.equals("com.google.android.googlequicksearchbox") ||
				item.vib == null
								
				) {
			//System.err.println("not sendable " + item.key);
			return false;
		}
		
		return true;
	}
	
	public boolean isCancelSendable(NotificationItem item) {
		// TODO		
		String key = item.key;
		String pkg = item.pkg;
		boolean bOnGoing = item.bOnGoing;
		boolean bClearable = item.bClearable;
		if (bOnGoing|| bClearable == false ||
				key.contains("com.google.android.gms") ||
				key.contains("com.android.deskclock") ||
				key.contains("low_battery") ||
				pkg.equals("com.android.vending")||
				pkg.equals("com.android.providers.downloads") ||
				pkg.equals("android")||
				pkg.equals("com.android.dialer") ||
				pkg.equals("com.google.android.googlequicksearchbox")
				)
			return false;
		return true;
	}
	
	NotificationItem prevNotificationItem;
	long prevTimeMillis;
	long prevType;
	boolean bWearNotificationFound = false;
	public boolean isKakaotalkGroup(long timeMillis, int type, NotificationItem item) {
		boolean bGroup = false;
		if (type == 1 || type == 2) {
			if (item.id == 2 && DataSet.bWatchLog()) {
				bWearNotificationFound = true;
				bGroup = true;
			}
		} else {
			if (bWearNotificationFound) {
				if (item.id == 2) {
					bGroup = true;
				} else {
					bGroup = false;
				}
			} else {
				bGroup = false;
			}
		}
		
		
		if (bGroup == false) {
			//System.out.println("f " + MDSim.currentLogItem);
		} else {
			//System.out.println("t " + MDSim.currentLogItem);
		}
		
		return bGroup;
	}

	public boolean isGroupNotification(long timeMillis, int type, NotificationItem item) {
		boolean bGroup = false;
		
		if (item.pkg.contains("com.kakao.talk")) {
			return isKakaotalkGroup(timeMillis, type, item);
		}
		
		if (prevTimeMillis + 150 < timeMillis || prevTimeMillis == timeMillis) {
			// most of no-group notification case
			bGroup = false;
		} else {
			// within 0.3s ms		
			if (!prevNotificationItem.pkg.equals(item.pkg) ||
					prevNotificationItem.key.equals(item.key)) {
				// same key or different pkg 
				bGroup = false;
			} else if (prevType == 3 && (type == 2 || type == 1)) {
				bGroup = false;
			} else if ((prevType == 1 || prevType == 2) && type == 3) {
				bGroup = false;
			} else if (prevNotificationItem.pkg.equals(item.pkg) &&
					prevType != type) {
				bGroup = false;
			} else {
				// same type, equal pkg, within 300ms
				//System.out.println("\nprev  : " + prevTimeMillis + " " + prevNotificationItem);
				//System.out.println("group : " + timeMillis + " " + item );
				bGroup = true;
			}
		}
		
		prevNotificationItem = item;
		prevTimeMillis = timeMillis;
		prevType = type;

		return bGroup;
	}
	
	public void doReport() {
		System.out.println("\n===NotificationManager===");
		System.out.println("totalNotifiedNotifCnt : " + totalNotifiedNotifCnt);
		System.out.println("totalUpdateNotiCnt : " + totalUpdateNotiCnt);
		System.out.println("totalRemovedNotiCnt : " + totalRemovedNotiCnt);
		
		System.out.println("");
		System.out.println("totalSendedNotifiedNotiCnt : " + totalSendedNotifiedNotiCnt);
		System.out.println("totalSendedUpdateNotiCnt : " + totalSendedUpdateNotiCnt);
		System.out.println("totalSendedRemovedNotiCnt : " + totalSendedRemovedNotiCnt);
		System.out.println("");
		System.out.println("totalSendedNotifiedNotiWhenScreenOnCnt : " + totalSendedNotifiedNotiWhenScreenOnCnt);
		System.out.println("totalSendedUpdateNotiWhenScreenOnCnt : " + totalSendedUpdateNotiWhenScreenOnCnt);
		System.out.println("totalSendedRemovedNotiWhenScreenOnCnt : " + totalSendedRemovedNotiWhenScreenOnCnt);
		System.out.println("");
		System.out.println("totalADNotiCnt : " + totalSendedADNotificaitonCnt);
		System.out.println("");
		System.out.println("Left : ");
		for (int i = 0; i < notificationList.size(); i++)
		{
			System.out.println(notificationList.get(i));
		}
		
		snm.doReport();
		
		wearWatch.doReport();
	}
}