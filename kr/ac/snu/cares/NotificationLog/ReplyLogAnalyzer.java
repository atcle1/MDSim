package kr.ac.snu.cares.NotificationLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by summer on 15. 10. 22.
 */
public class ReplyLogAnalyzer {
    private static final String TAG = LogReader.class.getSimpleName();
    private FileReader fileReader;
    private BufferedReader bfr;
    private String currentLine;
    private boolean bSuccess;


    public long jumpInterval = 60 * 1000;
    private long prevTime = 0;
    private long currentTime = 0;
    private int[] notificationCnt = new int[3];

    public int totalCnt = 0;
    public long expectedRunTime = 0;
    public boolean analysis(String path) {
        boolean bResult;
        bResult = open(path);
        long elapsedTime = 0;
        totalCnt = 0;
        prevTime = currentTime =0;
        notificationCnt[0]= notificationCnt[1]= notificationCnt[2] = 0;
        if (!bResult) return false;

        while ((currentLine = readLine())!= null) {
            ParsedNotification item =  ParsedNotification.from(currentLine);
            //Log.d(TAG, item.toString());
            totalCnt++;
            if (item.type <= 3 && item.type > 0)
                notificationCnt[item.type-1]++;
            elapsedTime = item.time - prevTime;
            if (elapsedTime > jumpInterval) {
                expectedRunTime += jumpInterval;
            } else {
                expectedRunTime += elapsedTime;
            }
            prevTime = item.time;
        }

        bSuccess = bResult;
        return bResult;
    }

    @Override
    public String toString() {
        String str = "";
        int min = (int)(expectedRunTime / (60 * 1000));
        int sec = (int)((expectedRunTime - (min * 60 * 1000))/1000);
        str = "success : " + bSuccess + " noti " + notificationCnt[0] + "/"+notificationCnt[1]+
                "/" + notificationCnt[2] + " expectedRunTime : " + min + "m " + sec + "s";
        return str;
    }

    public boolean open(String path) {
        boolean bReturn = false;
        currentLine = null;
        try {
            File f = new File(path);
           Log.i(TAG, "exist " + f.exists());
            if (f.canRead() == false){
                Log.i(TAG, path + " file canRead() false");
                return false;
            }
            fileReader = new FileReader(f);
            bfr = new BufferedReader(fileReader);
            bReturn = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bReturn;
    }

    public String readLine() {
        String line = "";
        try {
            line = bfr.readLine();
            currentLine = line;
        } catch (Exception ex) {
            ex.printStackTrace();
            line = null;
        }
        return line;
    }

    public String getCurrentLine() {
        return currentLine;
    }
}
