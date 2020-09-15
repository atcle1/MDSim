package kr.ac.snu.cares.NotificationLog;

import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;

public class ParsedNotification implements Cloneable {
    public static final String TAG = ParsedNotification.class.getSimpleName();
    // 746555702	1	0|com.nhn.android.band|1|band|10063	10;64;64;
    
    public boolean bSmnEnabled;

    public long time = 0;
    public int type = 1;
    public String key = "";

    public int titleLen;
    public int textLen;
    public int bigTextLen;
    public int [] vib;

    public static ParsedNotification from(String str) {
        if (str == null) return null;
        //4: 0	1	0|com.kakao.talk|2|null|10066	12;5;5;
        //3: 16850491	3	0|com.facebook.katana|0|1443691323629286|10078
        ParsedNotification item = new ParsedNotification();
        try {
            String split[] = str.split("\t");

            item.time = Long.parseLong(split[0]); // 0
            item.type = Integer.parseInt(split[1]); // 1
            item.key = split[2];                    // 0|com.kakao.talk|2|null|10066

            if (item.type == 0) {
            	// some control type...
            } else if (split.length >= 4) {
                String split2[] = split[3].split(";");  // 12;5;5;
                item.titleLen = Integer.parseInt(split2[0]);
                item.textLen = Integer.parseInt(split2[1]);
                item.bigTextLen = Integer.parseInt(split2[2]);

                if (split2.length > 3) {
                    String split3[] = split2[3].split(",");
                    item.vib = new int[split3.length];
                    for (int i = 0; i < item.vib.length; i++) {
                        item.vib[i] = Integer.parseInt(split3[i]);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //Log.d(TAG, "ex " + str);
            return null;
        }
        postCleaning(item);
        return item;
    }

    public String getTitle() {
        return MyUtil.getRandomString(titleLen);
    }
    public String getText() {
        return MyUtil.getRandomString(textLen);
    }
    public String getBigText() {
        return MyUtil.getRandomString(bigTextLen);
    }

    public String toString() {
        String str = "";
        str = time + " " + type + " " + key + " " + titleLen + " " + textLen + " " + bigTextLen + " " + vib;
        return str;
    }

    
    public String toString(long offset) {
        String str = "";
        str = (offset) + " " + type + " " + key + " " + titleLen + " " + textLen + " " + bigTextLen + " " + vib;
        return str;
    }
    
    public static void postCleaning(ParsedNotification item)
    {
    	if (item.type != 3) {
	    	if (item.key.contains("com.android.mms")){
	    		    item.vib = new int[]{100,100,100,100};
	    	} else if (item.key.contains("com.kakao.talk")){
			    item.vib = new int[]{100,1000,0,0};
	    	}
    	}
    }
    
	public ParsedNotification clone()
	{
		ParsedNotification objReturn = null;
		try {
			objReturn = (ParsedNotification)super.clone();
			if (vib != null) {
				objReturn.vib = vib.clone();	
			}		
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objReturn;
	}
	
	public String getNotificationLogFormStr() {
		String msg = "";
		String vibStr = "";
		
		msg += type;
		
		if (vib != null) {
			for (int i = 0; i <vib.length - 1; i++) {
				vibStr+=vib[i]+",";
			}
			vibStr += vib[vib.length-1] + ";";
		}
		msg += "\t" + key + "\t";
		if (type != 3) {
			msg += titleLen + ";" + textLen + ";" + bigTextLen + ";" +
					vibStr;	
		}
		return msg;
	}
    
}
