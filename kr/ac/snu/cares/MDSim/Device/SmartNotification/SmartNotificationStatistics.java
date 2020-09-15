package kr.ac.snu.cares.MDSim.Device.SmartNotification;

import java.util.ArrayList;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Util.Slog;

public class SmartNotificationStatistics {
    public static final String TAG = SmartNotificationStatistics.class.getSimpleName();
    public ArrayList<String> mLog;
    // notify
    int postedNotificationCnt;
    int postedSendableNotificationCnt;

    // notify delay
    int delayedNotificationCnt; // sended + updated + cancled

    int delayedUpdatedNotificationCnt;
    int delayedSendedNotificationCnt;
    int delayedCanceledNotificationCnt; // delayedCanceledByUser + App + other

    int delayedCanceledByUserPerceived;
    int delayedCanceledByUserConfirm;
    int delayedCanceledByApp;
    int delayedCanceledByOther;
    
    int perceivedCanceled;
    int perceivedCanceledByUserConfirm;
    int perceivedCanceledByApp;
    int perceivedCanceledByWear;
    int perceivedCanceledByOther;

    // cancel
    int canceledNotificationCnt;    // total
    int canceledSendableCancelCnt;  // sendable total

    // cancel reason
    int canceledByUser;
    int canceledByUserClick;
    int canceledByApp;
    int canceledByWear;
    int canceledByOther;

    // cancel delay info
    int canceledNotDelayed;
    int canceledDelayed;
    int canceledRemoved;
    
    int canceledDelayedSended;
    double canceledDelayedSendedAvgCnt;

    // canceledSendableCancelCnt = canceledNotDelayed +
    //									canceledDelayed +
    //									canceledRemoved
    // delayedNotificationCnt = delayedSendedNotificationCnt +
    //									delayedUpdatedNotificationCnt +
    //									delayedCanceledNotificationCnt
    //
    // postedSendableNotificationCnt - delayedUpdatedNotificationCnt - delayedCanceledNotificaitonCnt
    //							= notifyNewNotificaitonCnt + notifyUpdateNotificationCnt
    // canceledSendableCancelCnt - canceledRemoved = notifyRemovedNotificationCnt
    public SmartNotificationStatistics() {
        mLog = new ArrayList<String>();
    }

    /*
    public SmartNotificationStatistics(Bundle bundle) {
        fromBundle(bundle);
    }
    */

    public void init() {
        postedNotificationCnt = 0;
        postedSendableNotificationCnt = 0;

        delayedNotificationCnt = 0;

        delayedUpdatedNotificationCnt = 0;
        delayedSendedNotificationCnt = 0;
        delayedCanceledNotificationCnt = 0;

        delayedCanceledByUserPerceived = 0;

        delayedCanceledByUserConfirm = 0;   //naver occured...
        delayedCanceledByApp = 0;
        delayedCanceledByOther = 0;
        
        perceivedCanceled = 0;
        perceivedCanceledByUserConfirm = 0;
        perceivedCanceledByApp = 0;
        perceivedCanceledByWear = 0;
        perceivedCanceledByOther = 0;

        canceledNotificationCnt = 0;
        canceledSendableCancelCnt = 0;
        canceledByUser = 0;
        canceledByUserClick = 0;
        canceledByApp = 0;
        canceledByWear = 0;
        canceledByOther = 0;

        canceledNotDelayed = 0;
        canceledDelayed = 0;
        canceledRemoved = 0;
        canceledDelayedSended = 0;
        canceledDelayedSendedAvgCnt = 0;

        mLog.clear();
    }

    @Override
    public String toString() {
    	int notifyCancledCnt = delayedUpdatedNotificationCnt + delayedCanceledNotificationCnt;
    	String t = "" + (float)notifyCancledCnt / postedSendableNotificationCnt;
        return "\n" +
                "postedNotificationCnt : " + postedNotificationCnt +
                "\npostedSendableNotificationCnt : " + postedSendableNotificationCnt +
                "\ndelayedNotificationCnt : " + delayedNotificationCnt +
                "\ndelayedSendedNotificationCnt : " + delayedSendedNotificationCnt +
                "\ndelayedUpdatedNotificationCnt : " + delayedUpdatedNotificationCnt +
                "\ndelayedCanceledNotificationCnt : " + delayedCanceledNotificationCnt +
                "\n   delayedCanceledByUserPerceived : " + delayedCanceledByUserPerceived +
                "\n   delayedCanceledByUserConfirm : " + delayedCanceledByUserConfirm +
                "\n   delayedCanceledByApp : " + delayedCanceledByApp +
                "\n   delayedCanceledByOther : " + delayedCanceledByOther +
                
                "\n\nperceivedCanceled : " + perceivedCanceled +
                "\n   perceivedCanceledByUserConfirm : "+ perceivedCanceledByUserConfirm +
                "\n   perceivedCanceledByApp : " + perceivedCanceledByApp+ 
                "\n   perceivedCanceledByWear : "+ perceivedCanceledByWear +
                "\n   perceivedCanceledByOther : "+ perceivedCanceledByOther+

                "\n\ncanceledNotificationCnt : " + canceledNotificationCnt +
                "\ncanceledSendableCancelCnt : " + canceledSendableCancelCnt +
                "\n   canceledByUser : " + canceledByUser +
                "\n   canceledByUserClick : " + canceledByUserClick +
                "\n   canceledByApp : " + canceledByApp +
                "\n   canceledByOther : "+ canceledByOther +
                "\ncanceledByWear : "+ canceledByWear +

                "\n\ncanceledNotDelayed : " + canceledNotDelayed +
                "\ncanceledDelayed : " + canceledDelayed +
                "\ncanceledRemoved : " + canceledRemoved +
                "\ncanceledDelayedSended(GroupCnt) : " + canceledDelayedSended +
                "\ncanceledDelayedSendedAvgCnt : " + canceledDelayedSendedAvgCnt;
    }


    public void writeLog(long timeMillis, String msg) {
        synchronized(mLog) {
            String t = MyUtil.MillisToStr(timeMillis) + " : "+ msg;
            mLog.add(t);
            Slog.i(TAG, t);
        }
    }

    static SimpleDateFormat timeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static String getTimeStringFromSystemMillis(long timeMillis) {
        return timeSDF.format(new Date(timeMillis));
    }
    public static String getTimeStringFromSystemMillis() {
        return timeSDF.format(new Date());
    }

    public boolean writeLogToFile(String path) {
        File logFile;
        FileWriter fw;
        BufferedWriter bufferWritter;
        try {
            logFile = new File(path);
            File directory = new File(logFile.getParentFile().getAbsolutePath());
            directory.mkdirs();
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            bufferWritter = new BufferedWriter(fw);
            //Slog.i(TAG, "log size : " + mLog.size());
            for (int i = 0; i < mLog.size(); i++) {
                bufferWritter.write(mLog.get(i) + "\n");
            }
            bufferWritter.write("statistics start");
            bufferWritter.write(toString() + "\n");
            bufferWritter.write("statistics end\n");
            bufferWritter.close();
            //FileUtils.setPermissions(logFile.getPath(), 0777, -1, -1); // drwxrwxr-x
            //FileUtils.setPermissions(directory.getPath(), 0777, -1, -1); // drwxrwxr-x
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        //Slog.i(TAG, "writeLogToFile " + path + " success");
        return true;
    }
    
    public boolean writeLogToStdout() {
        BufferedWriter bufferWritter;
        try {
            bufferWritter = new BufferedWriter(new OutputStreamWriter(System.out));
            //Slog.i(TAG, "log size : " + mLog.size());
            for (int i = 0; i < mLog.size(); i++) {
                bufferWritter.write(mLog.get(i) + "\n");
            }
            bufferWritter.write("statistics start");
            bufferWritter.write(toString() + "\n");
            bufferWritter.write("statistics end\n"+mLog.size()+"\n");
            bufferWritter.flush();
            //FileUtils.setPermissions(logFile.getPath(), 0777, -1, -1); // drwxrwxr-x
            //FileUtils.setPermissions(directory.getPath(), 0777, -1, -1); // drwxrwxr-x
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        //Slog.i(TAG, "writeLogToFile " + path + " success");
        return true;
    }
}