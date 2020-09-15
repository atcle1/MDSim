package kr.ac.snu.cares.MDSim.Device.SmartNotification;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Device.DynamicEventQueue;
import kr.ac.snu.cares.MDSim.Device.NotificationManager;
import kr.ac.snu.cares.MDSim.Device.WearWatch;
import kr.ac.snu.cares.MDSim.Log.LogItemFactory;
import kr.ac.snu.cares.MDSim.Report.NotificationReport;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Util.Slog;
import kr.ac.snu.cares.MDSim.Vo.LogDynamicEvent;
import kr.ac.snu.cares.MDSim.Vo.LogItem;
import kr.ac.snu.cares.MDSim.Vo.NotificationItem;

public class SmartNotificationManager {
	// private final Object mLock = new Object();
	static private String[] DELETE_REASON = {
	"", //0
    "REASON_DELEGATE_CLICK", // = 1;
    "REASON_DELEGATE_CANCEL",  // 2;
    "REASON_DELEGATE_CANCEL_ALL",  // 3;
    "REASON_DELEGATE_ERROR",  // 4;
    "REASON_PACKAGE_CHANGED",  // 5;
    "REASON_USER_STOPPED",  // 6;
    "REASON_PACKAGE_BANNED",  // 7;
    "REASON_NOMAN_CANCEL",  // 8;
    "REASON_NOMAN_CANCEL_ALL",  // 9;
    "REASON_LISTENER_CANCEL",  // 10;
    "REASON_LISTENER_CANCEL_ALL",  // 11;
    "REASON_GROUP_SUMMARY_CANCELED",  // 12;
    "REASON_GROUP_OPTIMIZATION",  // 13;
	};
	private static int screen_timeout = 30 * 1000;
	
	// enable
	public boolean bEnabled = true;
	
	private boolean bScreenOn = true;
	private boolean pendingDirty = false;
	private long screenOnTimeMillis;
	public long userActivityTimeMillis;			// for debug
	private boolean bUserActivityEnabled = true;

	private static int PERCEIVED_THRESHOLD = 15 * 1000;
	private static int NOTIFICATION_DELAY_TIMEOUT = 15 * 1000;
	
	private DynamicEventQueue dynamicEventQueue;
	public static String MESSAGE_SMARTNOTIFICATION = "msg_smartnotification";

	public SmartNotificationStatistics st = new SmartNotificationStatistics();
	public NotificationManager nm;
	private WearWatch wearWatch;
	
	private DelayedSendedLog dsl;
	
	public SmartNotificationManager(NotificationManager nm, WearWatch watch) {
		dynamicEventQueue = DynamicEventQueue.getInstance();
		this.nm = nm;
		wearWatch = watch;
		dsl = new DelayedSendedLog();
		
	}

	public static final String TAG = SmartNotificationManager.class.getSimpleName();
	LinkedList<SmartNotificationItem> postedDelayingNotification = new LinkedList<>();
	LinkedList<SmartNotificationItem> removedPendingNotification = new LinkedList<>();
	
	public boolean isExist(LinkedList<SmartNotificationItem> list, String key)
	{
		Iterator<SmartNotificationItem> itr = list.iterator();
		SmartNotificationItem item;
		while(itr.hasNext()) {
			item = itr.next();
			if (item.sbn.key.equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteIfExist(LinkedList<SmartNotificationItem> list, String key)
	{
		ListIterator<SmartNotificationItem> itr = list.listIterator();
		SmartNotificationItem item;
		while(itr.hasNext()) {
			item = itr.next();
			if (item.sbn.key.equals(key)) {
				itr.remove();
				return true;
			}
		}
		return false;
	}
	
	public boolean isExistDelayedNotification(LinkedList<SmartNotificationItem> list)
	{
		ListIterator<SmartNotificationItem> itr = list.listIterator();
		SmartNotificationItem item;
		while(itr.hasNext()) {
			item = itr.next();
			if (item.state == SmartNotificationItem.STATE_DELAYED ||
					item.state == SmartNotificationItem.STATE_MAY_PERCEIVED) {
				return true;
			}
		}
		return false;
	}
	
	public int findIdx(List<SmartNotificationItem> list, String key) {
		if (list == null) return -1;
		for (int i = 0; i < list.size(); i++) {
			NotificationItem item = list.get(i).sbn;
			if (item.key.equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	public SmartNotificationItem findItem(List<SmartNotificationItem> list, String key) {
		if (list == null) return null;
		for (int i = 0; i < list.size(); i++) {
			NotificationItem item = list.get(i).sbn;
			if (item.key.equals(key)) {
				return list.get(i);
			}
		}
		return null;
	}
	
	public void onUserActivity(long time, int event) {
		userActivityTimeMillis = time;
		st.writeLog(time, "oUA " + pendingDirty + " dqsize = " + dynamicEventQueue.size());
		st.writeLog(MDSim.currentTimeMillis, debug());
		
		/*
		if (dynamicEventQueue.size() != 0) {
			st.writeLog(time, dynamicEventQueue.toString());
		}
		*/
		//if (!pendingDirty) return;
		if (bUserActivityEnabled == false) return;
		
		//System.out.println(MyUtil.MillisToStr(time) + " :touch");
		
		ListIterator<SmartNotificationItem> itr = postedDelayingNotification.listIterator();
		while(itr.hasNext()) {
			SmartNotificationItem sbnItem = itr.next();
			//st.writeLog(time, sbnItem.getKey() + " list " + sbnItem.state);
			if (sbnItem.state != SmartNotificationItem.STATE_PERCEIVED) {
				//Slog.i(TAG, "onUserActivity() : STATE_PERCEIVED " + sbnItem.getKey());
				sbnItem.state = SmartNotificationItem.STATE_PERCEIVED;
				st.delayedCanceledNotificationCnt++;
				st.delayedCanceledByUserPerceived++;
				st.writeLog(time, sbnItem.getKey() + " UA PERCEIVED delayedCanceledNotificationCnt : " + st.delayedCanceledNotificationCnt);
				st.writeLog(time, debug());
				SmartNotificationManager.breakNotification(sbnItem.sbn);
				//itr.set(sbnItem);
				//st.writeLog(time, "after remove deq " + dynamicEventQueue.toString());
			}
		}

		// actually, just removeMessages is enough. just for validating
		if (isExistDelayedNotification(postedDelayingNotification)) {
			boolean bRemoved = dynamicEventQueue.removeMessages(MESSAGE_SMARTNOTIFICATION);
			if (!bRemoved) st.writeLog(time, "cannot find rm!");
			if (dynamicEventQueue.size() != 0) {
				st.writeLog(time, "dynamicEvent exist! sz=" + dynamicEventQueue.size());
				st.writeLog(time, dynamicEventQueue.toString());
			}
		}
		pendingDirty = false;
	}

	public void onScreenOn(long timeMillis) {
		//Slog.i(TAG, MyUtil.MillisToStr(timeMillis) + " onScreenOn()");
		bScreenOn = true;
		screenOnTimeMillis = timeMillis;
		st.writeLog(timeMillis, "SCR ON");
	}

	public void onScreenOff(long timeMillis) {
		//Slog.i(TAG, MyUtil.MillisToStr(timeMillis) + " onScreenOff()");
		bScreenOn = false;
		userActivityTimeMillis = 0;
		if (bEnabled) {
			notifyAllpendingNotification(timeMillis, "SCROFF");
		} else
			st.writeLog(timeMillis, "SCR OFF");
	}


	public void reset(long timeMillis) {
		//Slog.i(TAG, "reset()");
		bEnabled = false;
		notifyAllpendingNotification(timeMillis, "RESET");
		st.init();
	}

	public void setScreenTimeout(int timeout) {
		System.err.println("set Screen Timeout " + timeout);
		screen_timeout = timeout;
		PERCEIVED_THRESHOLD = timeout / 2;
		NOTIFICATION_DELAY_TIMEOUT = timeout / 2;
		st.writeLog(MDSim.currentTimeMillis, "STO " + timeout);
	}

	public boolean notifyPostedLockedToWear(long timeMillis, NotificationItem sbn) {
		//Slog.i(TAG, "notifyPostedLockedToWear " + sbn.getKey());
		boolean bDelayed = false;
		int state = 0;
		long postedTime = timeMillis;
		if (sbn.updateDate != null)
			postedTime = sbn.updateDate.getTimeInMillis();
		else
			postedTime = sbn.postDate.getTimeInMillis();
		
		SmartNotificationItem sni = null;

		if (!bEnabled) return false;
		breakNotification(sbn);
		st.postedNotificationCnt++;
		/*
		if (!isSendable(sbn)) {
			//st.writeLog(timeMillis, sbn.getKey() + " NP S NOTSENDABLE");
			//deleteIfExist(removedPendingNotification, sbn.getKey());
			return false;
		}
		*/
		
		st.postedSendableNotificationCnt++;
		if (!bScreenOn) {
			//deleteIfExist(removedPendingNotification, sbn.key);
			st.writeLog(timeMillis, sbn.getKey() + " NP S SCROFF");
			return false;
		}
		
		// now, screen on state
		// check the delaying condition
		/*if(userActivityTimeMillis < screenOnTimeMillis &&
				postedTime < screenOnTimeMillis + 10 * 1000) {
			bDelayed = true;
			state = SmartNotificationItem.STATE_DELAYED;
			st.writeLog(timeMillis, sbn.getKey() + " NP D NOTIWAKEUP");
		} else*/	if (postedTime < screenOnTimeMillis + 10* 1000 && userActivityTimeMillis < screenOnTimeMillis) {
			// screen is recently turn on and recently screen is turned on
			// maybe notification app wakeup the display
			bDelayed = true;
			state = SmartNotificationItem.STATE_DELAYED;
			st.writeLog(timeMillis, sbn.getKey() + " NP D NOTIWAKEUP");
		} else if (pendingDirty) {
			bDelayed = true;
			state = SmartNotificationItem.STATE_MAY_PERCEIVED;
			st.writeLog(timeMillis, sbn.getKey() + " NP D PENDINGD");
		} else if (postedTime < userActivityTimeMillis + PERCEIVED_THRESHOLD) {
			// recently, user used or pending notification exsist
			bDelayed = true;
			state = SmartNotificationItem.STATE_MAY_PERCEIVED;
			st.writeLog(timeMillis, sbn.getKey() + " NP D UACT " + pendingDirty);
		} else {
			bDelayed = false;
			st.writeLog(timeMillis, sbn.getKey() + " NP S NUA " + pendingDirty);
		}

		if (bDelayed) {
			pendingDirty = true;
			sni = new SmartNotificationItem(sbn, state);
			st.delayedNotificationCnt++;
			
			// if NR is pending, remove it.
			/*
			boolean bRemovedPendingDeleted = deleteIfExist(removedPendingNotification, sbn.getKey());
			if (bRemovedPendingDeleted)
				st.writeLog(timeMillis, "remove cancel delay " + sbn.getKey() + " " + bRemovedPendingDeleted);
			*/
			int idx = findIdx(postedDelayingNotification, sni.getKey());
			
			if (idx == -1) {
				// new notification
				postedDelayingNotification.offer(sni);
				
				notifyDelayedToWear(sni);
				st.writeLog(timeMillis, "new notification delay delayedNotificationCnt : " + (st.delayedNotificationCnt));
			} else {
				// update notificaiton (already same notification is pending state, update)
				//dynamicEventQueue.removeMessages(MESSAGE_SMARTNOTIFICATION, sni);
				//Slog.i(TAG, "notifyPostedLockedToWear : " + sbn.getKey() + " update");
				// 기존것 may perceived / perceived 나눠서 봐야함?
				// may perceived -> 기존 timer
				// perceived(timer canceled) -> 새타이머
				SmartNotificationItem item = postedDelayingNotification.get(idx);
				st.writeLog(timeMillis, "NP already exist state " + item.state);
				
				if (item.state == SmartNotificationItem.STATE_PERCEIVED) {
					// already perceived notification update, new NP delay
					// already counted to delayedCanceledNotification
					
					notifyDelayedToWear(sni);
					st.writeLog(timeMillis, "update notification (perceived) delay delayedNotificationCnt : " + (st.delayedNotificationCnt));
				} else {
					// not perceived delaying notification update
					// do nothing, just update.
					st.delayedUpdatedNotificationCnt++;		
					
				}
				// update postedDelayingNotification list
				postedDelayingNotification.set(idx, sni);
				
				st.writeLog(timeMillis, "update notification delay " + sni + " delayedUpdatedNotificationCnt : " + (st. delayedUpdatedNotificationCnt));
			}
			st.writeLog(MDSim.currentTimeMillis, debug());
			
			SmartNotificationItem removedItem = findItem(removedPendingNotification, sbn.getKey());
			if (removedItem != null) {
				// mustsendcancelifnotify 는 이미 전송된 알림에 대하여 cancel이 pending된것임, 한번은 가야함
				// 동일 알림이 발생해도 pendingCancel을 지우면 안됨
				if (removedItem.mustSendCancelIfNotNotify == false) {
					deleteIfExist(removedPendingNotification, sbn.key);
					st.canceledRemoved++; // ?
				}
				
			}
			return true;		// delaying
		} 
		
		deleteIfExist(removedPendingNotification, sbn.getKey());
		return false;			// not delaying, send to wear
	}
/*
	public boolean notifyRemovedLockedToWear(NotificationItem sbn) {
		return notifyRemovedLockedToWear(sbn, -2);
	}
*/
	public static boolean breakNotification(NotificationItem item) {
		if (
			MDSim.currentTimeMillis() > MyUtil.calStrToMillis("2015-09-17 20:59:00.952")) {
			return true;
		}
		return false;
	}
	public boolean notifyRemovedLockedToWear(long timeMillis, NotificationItem sbn, int reason) {
		//Slog.i(TAG, "notifyRemovedLockedToWear " + sbn.getKey() + " reason " + reason);
		SmartNotificationItem sbnItem;
		boolean bWearRemove = false;
		if (!bEnabled) return false;
		st.canceledNotificationCnt++;
/*
		if (!isCancelSendable(sbn) || reason == -2) {
			//st.writeLog(timeMillis, sbn.getKey() + " NR S NOTSENDABLE");
			return false;
		}
		*/
		breakNotification(sbn);
		st.canceledSendableCancelCnt++;
		//System.out.println("sc++ " + st.canceledSendableCancelCnt + " " + sbn);

		if (reason == 10 || reason == 11) {	// wear remove
			st.canceledByWear++;
			st.canceledSendableCancelCnt--;
			bWearRemove = true;			// only sim
		} else if (reason == 1) {
			st.canceledByUserClick++;
		} else if (reason == 2 || reason == 3) {
			st.canceledByUser++;
		} else if (reason == 8 || reason == 9) {
			st.canceledByApp++;
		} else {
			st.canceledByOther++; 
		}
		
		SmartNotificationItem prevItem = findItem(removedPendingNotification, sbn.getKey());
		
		// check delaying(may perceived or perceived) notification to remove
		Iterator<SmartNotificationItem> itr = postedDelayingNotification.iterator();
		while(itr.hasNext()) {
			sbnItem = itr.next();
			breakNotification(sbnItem.sbn);
			if (sbnItem.getKey().equals(sbn.getKey())) {
				//mHandler.removeMessages(MESSAGE_SMARTNOTIFICATION, sbnItem);
				
				
				//Slog.i(TAG, "notifyRemovedLockedToWear : postedQueue removed " + sbnItem.getKey());
				if (sbnItem.state == SmartNotificationItem.STATE_DELAYED || sbnItem.state == SmartNotificationItem.STATE_MAY_PERCEIVED) {
					// delayed notification in smart notification manager
					st.delayedCanceledNotificationCnt++;
					
					if (reason == 1 || reason == 2 || reason == 3) {
						// naver occure if sm enabled real load...,
						// (if user perceived a notification, it accounted in other perceiving routine.)
						// only counted in sm not enabled log
						st.delayedCanceledByUserConfirm++;
					} else if (reason == 8 || reason == 9) {
						st.delayedCanceledByApp++;
					} else {
						//System.out.println("rm other reason delayed " + reason);
						st.delayedCanceledByOther++;
					}
					st.writeLog(timeMillis, sbn.getKey() + " NR DC QRMV R=" + reason + " delayedCanceledNotificationCnt : " + st.delayedCanceledNotificationCnt);
					st.writeLog(MDSim.currentTimeMillis, debug());
				} else if ( sbnItem.state == SmartNotificationItem.STATE_PERCEIVED){
					st.perceivedCanceled++;
					if (reason == 1 || reason == 2 || reason == 3) {
						st.perceivedCanceledByUserConfirm++;
					} else if (reason == 8 || reason == 9) {
						st.perceivedCanceledByApp++;
					} else if (reason == 10 || reason == 11 ) {
						st.perceivedCanceledByWear++;
						// not counted... actually, counted to st.canceledByWear
					} else {
						//System.out.println("rm other reason pc " + reason);
						st.perceivedCanceledByOther++;
					}
					st.writeLog(timeMillis, sbn.getKey() + " NR PC ALREADY R=" + reason + " perceivedCanceled : " + st.perceivedCanceled);
					st.writeLog(MDSim.currentTimeMillis, debug());
				} else {
					st.writeLog(timeMillis, sbn.getKey() + " err?? state=" + sbnItem.state);
					System.exit(0);
				}
				
				if (sbnItem.state != SmartNotificationItem.STATE_PERCEIVED) {
					// NP delaying but not perceived notification should be remove the pending event ?
					Boolean bRemoved = dynamicEventQueue.removeMessages(MESSAGE_SMARTNOTIFICATION, sbnItem);
					if (!bRemoved) {
						st.writeLog(timeMillis, sbn.getKey() + " should removed but, not removed=" + reason);
					}
				}
				// perceived or not, anyway delayed notification is removed
				
				if (bWearRemove && sbnItem.state != SmartNotificationItem.STATE_PERCEIVED) {
					// this sbnItem actually user can't see the right notification and remove 
				} else
					itr.remove();
				
				if (!isExistDelayedNotification(postedDelayingNotification))
					pendingDirty = false;
				
				if (bWearRemove) {
					return true;
				} else {
					st.canceledRemoved++;
				}
				return true;		// pending notification is canceled, do not send NP_cancel
			}
		}

		// 스크린이 꺼진 상태에서도 워치로 인하여 notificaiton delete 들어올 수 있음

		if (!bScreenOn && !bWearRemove) {
			// While screen is off, do not delay
			st.writeLog(timeMillis, sbn.getKey() + " NR S SCROFF R=" + reason);
			st.canceledNotDelayed++;
			return false;
		} else if (bWearRemove) {
			// wear remove, do not delay
			return false;
		}

		// make pending NC...
		SmartNotificationItem item = new SmartNotificationItem(sbn, SmartNotificationItem.STATE_DELAYED);
		item.mustSendCancelIfNotNotify = true;
		

		if (prevItem != null) {
			int idx = findIdx(removedPendingNotification, prevItem.getKey());
			removedPendingNotification.set(idx, item);
		} else
			removedPendingNotification.offer(item);
		//Slog.i(TAG, "notifyRemovedLockedToWear : removedQueue add " + sbn.getKey());
		//pendingDirty = true;


		st.writeLog(timeMillis, sbn.getKey() + " NR D SCRON R=" + reason);
		st.canceledDelayed++;
		
		return true;
	}

	private void notifyDelayedToWear(SmartNotificationItem sni) {
		/*
		Slog.i(TAG, "notifyDelayedToWear : " + sni.getKey());
		Message msg = Message.obtain(mHandler, MESSAGE_SMARTNOTIFICATION);
		msg.arg1 = ARG1_SEND_NOTIFICATION;
		msg.obj = sni;
		mHandler.sendMessageDelayed(msg, NOTIFICATION_DELAY_TIMEOUT);
		*/

		pendingDirty = true;
		LogItem item = LogItemFactory.getDynamicEventLogItem(MDSim.currentTimeMillis + NOTIFICATION_DELAY_TIMEOUT,
				LogItem.SOURCE_MOBILE_MDP, "devent "  + sni.toString() + " created at = " + MyUtil.MillisToStr(MDSim.currentTimeMillis), MESSAGE_SMARTNOTIFICATION, sni);
		dynamicEventQueue.addDynamicEvent(item);	
	}

	private void notifyAllpendingNotification(long timeMillis, String reasonStr) {
		// SCR OFF or Timeout
		Iterator<SmartNotificationItem> itr, itr2;
		SmartNotificationItem sbnItem;
		//Slog.i(TAG, "notifyAllpostedDelayingNotification() called");

		//mHandler.removeMessages(MESSAGE_SMARTNOTIFICATION);	// cancel all delayed notificaiton works...
		dynamicEventQueue.removeMessages(MESSAGE_SMARTNOTIFICATION);
		
		itr = removedPendingNotification.iterator();
		while(itr.hasNext()) {
			SmartNotificationItem removeItem = itr.next();
			if (removeItem.mustSendCancelIfNotNotify) {
				SmartNotificationItem notifyItem = findItem(postedDelayingNotification, removeItem.getKey());
				if (notifyItem != null &&
						notifyItem.state != SmartNotificationItem.STATE_PERCEIVED) {
					// 보낼 노티피케이션 중 이전 cancel이 있으면 cancel은 안보내도 됨
					// NC ND : 이런 케이스 없음
					itr.remove();
				}
			}
		}
		

		int postedDelayingNotificationSize = postedDelayingNotification.size();
		if (postedDelayingNotificationSize != 0) {
			st.writeLog(timeMillis, "NAPN PSQS CNT=" + postedDelayingNotificationSize);
			itr = postedDelayingNotification.iterator();
			while(itr.hasNext()) {
				try {
					sbnItem = itr.next();
					if (sbnItem.state == SmartNotificationItem.STATE_DELAYED ||
						sbnItem.state == SmartNotificationItem.STATE_MAY_PERCEIVED) {
						//Slog.i(TAG, "      notifyAllpendingNotification() : notify Post " + sbnItem.getKey());
						st.writeLog(timeMillis, sbnItem.getKey() + " NAPN PSQS R=" + reasonStr);
						//nms.notifyPostedToWearLocked(sbnItem.sbn);
						wearWatch.notifyPosted(timeMillis, sbnItem.sbn);

						// delayed send
						if (sbnItem.sbn.updateDate != null)
							dsl.writeLog(sbnItem.sbn.updateDate.getTimeInMillis() + " " + timeMillis + " " + (timeMillis - sbnItem.sbn.updateDate.getTimeInMillis()));
						else if (sbnItem.sbn.postDate != null)
							dsl.writeLog(sbnItem.sbn.getPostTime() + " " + timeMillis + " " + (timeMillis - sbnItem.sbn.getPostTime()));
						else
							dsl.writeLog("!!!!!!!!!!!!1 post or update is null");
					
						
						st.delayedSendedNotificationCnt++;
						itr.remove();
						// perceived 된것은 전달하지 않기로 결정된것, 이것은 리스트에 계속 가지고 있어야 함
						// 이후 취소가 올 때, 없으면 이미 보낸것으로 보게되어 오작동함
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		int removedPendingNotificationSize = removedPendingNotification.size();
		if (removedPendingNotificationSize != 0) {
			st.canceledDelayedSendedAvgCnt = ((st.canceledDelayedSendedAvgCnt * st.canceledDelayedSended)
			 							+ removedPendingNotificationSize ) /
			 						 	(st.canceledDelayedSended + 1);
			st.canceledDelayedSended++;
			st.writeLog(timeMillis, "NAPN PRQS CNT=" + removedPendingNotificationSize);
			itr = removedPendingNotification.iterator();
			while(itr.hasNext()) {
				try {
					
					sbnItem = itr.next();
					//Slog.i(TAG, "      notifyAllpendingNotification() : notify Remove " + sbnItem.getKey());
					st.writeLog(timeMillis, sbnItem.getKey() + " NAPN PRQS R=" + reasonStr);
					//nms.notifyRemovedToWearLocked(sbnItem.sbn);
					wearWatch.notifyRemoved(timeMillis, sbnItem.sbn);
					/*
					if (sbnItem.sbn.cancelDate != null)
						System.out.println(sbnItem.sbn.getPostTime() + " " + timeMillis + " " + (timeMillis - sbnItem.sbn.getPostTime()));
					else if (sbnItem.sbn.updateDate != null)
						System.out.println(sbnItem.sbn.updateDate.getTimeInMillis() + " " + timeMillis + " " + (timeMillis - sbnItem.sbn.updateDate.getTimeInMillis()));
					else
						System.err.println("!!!!!!!!!!!!1 post or update is null");
					*/
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			removedPendingNotification.clear();	
		}
		pendingDirty = false;
		if (reasonStr.equals("SCROFF")) {
			postedDelayingNotification.clear();
		}
	}
	/*
	private void sendNotification(SmartNotificationItem sni) {
		// deprecated, not used
		//Slog.i(TAG, "      sendNotification : " + sni.getKey());
		//nms.notifyPostedToWearLocked(sni.sbn);
		//wearWatch.notifyPosted(sni.sbn);
		postedDelayingNotification.remove(sni);
		st.delayedSendedNotificationCnt++;
	}
	*/

	public void handleMessage(LogDynamicEvent event) {
			/*
			// send only delayed timeout notification
			SmartNotificationItem sni = (SmartNotificationItem)msg.obj;
			Slog.i(TAG, "   ARG1_SEND_NOTIFICATION : " + sni.getKey());
			st.writeLog(sni.getKey() + " H PSQS R=TIMEOUT");
			sendNotification((SmartNotificationItem)msg.obj);
			*/
			// send all of delayed notification
			//SmartNotificationItem sni = (SmartNotificationItem)msg.obj;
			//Slog.i(TAG, "   ARG1_SEND_NOTIFICATION : " + sni.getKey());
			st.writeLog(MDSim.currentTimeMillis, "TIMEOUT MSG " + event.logItem.msg);
			notifyAllpendingNotification(event.logItem.timeMillis, "SND_TMOUT");
		return;
	}
/*
	private boolean isSendable(NotificationItem item) {
		
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
	
*/
	
	private boolean isCancelSendable(NotificationItem item) {
		// notification lookup 해서 원래 notification 찾은다음, 그것이
		// sendable 인지 검사
		int idx = nm.findNotiIdx(item);
		return nm.isCancelSendable(nm.notificationList.get(idx));
	}

	public SmartNotificationStatistics getStatistics() {
		return st;
	}
	
	public void doReport() {
		System.out.println("\n===SmartNotificationManager===");
		System.out.println(st);
		System.out.println("\n===SmartNotificationLog===");
		//st.writeLogToStdout();
	}
	
	public String debug() {
		return "DN : " + st.delayedNotificationCnt + "DS : " + st.delayedSendedNotificationCnt + " DU : " + st.delayedUpdatedNotificationCnt + " " + " DC " + st.delayedCanceledNotificationCnt + " DPC " + st.perceivedCanceled;
	}
}