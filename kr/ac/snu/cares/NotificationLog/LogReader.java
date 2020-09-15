package kr.ac.snu.cares.NotificationLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class LogReader {
    private static final String TAG = LogReader.class.getSimpleName();
    private FileReader fileReader;
    private BufferedReader bfr;
    private String currentLine;

    public boolean open(String path) {
        boolean bReturn = false;
        currentLine = null;
        try {
            File f = new File(path);
            
            //Log.i(TAG, "exist " + f.exists());
            if (f.canRead() == false){
                //Log.i(TAG, path + " file canRead() false");
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
