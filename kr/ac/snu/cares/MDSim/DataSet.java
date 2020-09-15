package kr.ac.snu.cares.MDSim;

public class DataSet {
	public static int selectedIndex = 0;
	public static final String []dataSet = {
		"1/phone/phone.sqlite3",				// 0 light
    	"1/watch_phone/watch_phone.sqlite3", // (User 1)
    	
    	"2/phone/phone.sqlite3",				// 2 heavy
    	"2/watch_phone/watch_phone.sqlite3", // (User 2)
    	
    	"3/phone/phone.sqlite3",				// 4
    	"3/watch_phone/watch_phone.sqlite3", // (User 3)
    	
    	"4/phone/phone.sqlite3",				// 6 heavy
    	"4/watch_phone/watch_phone.sqlite3", // (User 4)
    	
    	"5/phone/phone.sqlite3",				// 8 heavy
    	"5/watch_phone/watch_phone.sqlite3",	// (User 5)
	};
	
	public static String getSelectedData() {
		return dataSet[selectedIndex];
	}
	public static boolean bWatchLog() {
		return selectedIndex%2 != 0;
	}
}