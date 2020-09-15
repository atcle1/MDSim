package kr.ac.snu.cares.MDSim.Vo;

import java.util.Calendar;
import java.util.Comparator;

import kr.ac.snu.cares.MDSim.MDSim;
import kr.ac.snu.cares.MDSim.Util.MyUtil;

public class NotificationItem implements Comparable<NotificationItem> {
	public enum EventType {NOTIFY, UPDATE, REMOVE};
	public EventType eventType;
	public String key;
	public String pkg;
	public String uIdentifier;
	public int id;
	public String ttag;
	public String uid;

	public boolean bOnGoing;
	public boolean bClearable;

	public String style;

	public boolean bEnc;

	public String title;
	public int titleLen;

	public String text;
	public int textLen;

	public String smText;
	public int smTextLen;

	public String bigText;
	public int bigTextLen;

	public Calendar postDate;
	public Calendar updateDate;
	public Calendar cancelDate;
	public int updateCnt;

	public boolean bSenderDelete;
	public int deleteReason;
	
	public boolean bwExt = false;
	
	public int [] vib;

	@Override
	public int compareTo(NotificationItem arg0) {
		// TODO Auto-generated method stub
		return this.key.compareTo(arg0.key);
	}

	@Override
	public String toString() {
		String str = "";
		switch (eventType) {
		case NOTIFY:
			str = "NOTIFY " + MyUtil.MillisToStr(postDate.getTimeInMillis());
			break;
		case UPDATE:
			str = "UPDATE " + MyUtil.MillisToStr(updateDate.getTimeInMillis());
			break;
		case REMOVE:
			str = "REMOVE " + MyUtil.MillisToStr(cancelDate.getTimeInMillis());
			str += " sd=" + bSenderDelete + " r=" + deleteReason;
			break;
		}

		return str + " KEY=" + key + "; PKG=" + pkg + "; id=" + id + "; style=" + style +"; bOn="
				+ bOnGoing + "; bClearable=" + bClearable + ";";
	}
	
	public String getStringVal(String val) {
		String split[] = val.split("=");
		if (split.length > 1) return split[1];
		else return "";
	}
	public int getLengthVal(String val) {
		String split[] = val.split("[|]");
		int r = -1;
		if (split.length > 1) {
			try {
			r=Integer.parseInt(split[0]);
			} catch (Exception ex ){
				r = 1;
			}
		}
		return r;
	}
	public int [] parseVib(String val) {
		String split[] = val.split("=");
		if (split.length != 2) return null;
		String vibStr[] = split[1].split(",");
		int [] vibArr = new int[vibStr.length];
		for (int i = 0; i < vibStr.length; i++) {
			vibArr[i] = Integer.parseInt(vibStr[i]);
		}
		return vibArr;
	}

	public void parse(String msg) {
		String split[] = msg.split(";");
		
//		 for (String t : split) { System.out.print("[" + t + "]"); }
//		 System.out.println();
		 
		key = split[0];
		parseTag(key);

		for (int i = 1; i <4; i++) {
			if (split[i].contains("ing=true")) {
				bOnGoing = true;
			} else if (split[i].contains("ing=false")) {
				bOnGoing = false;
			} else {
				//MDSim.logger.warning("parse err ing " + split[1]);
			}

			if (split[i].contains("cl=true")) {
				bClearable = true;
			} else if (split[i].contains("cl=false")) {
				bClearable = false;
			} else {
				//MDSim.logger.warning("parse err cl " + split[2]);
			}

			if (split[i].contains("bEnc=true")) {
				bEnc = true;
			} else if (split[i].contains("bEnc=false")) {
				bEnc = false;
			} else {
				//MDSim.logger.warning("parse err bEnc " + split[3]);
			}

			if (split[i].contains("style=")) {
				style = split[4].split("=")[1];
			} else {
				// MDSim.logger.warning("parse err style " + split[4] + " : " + msg);
			}
		}

		
		int i;
		Boolean bAd = false;
		if (msg.contains("광고")) {
			bAd = true;
		}
		for (i = 5; i < split.length; i++) {
			if (split[i].contains("wExt")) {
				bwExt = true;
				break;
			}
			if (split[i].contains("oldtime=")) {
				postDate = MyUtil.calStrToCalendar(split[i].split("=")[1]);
			} else if (split[i].startsWith("title")) {
				title = getStringVal(split[i]);
				if (bEnc == true && !bAd)
					titleLen = title.length();
				else
					titleLen = getLengthVal(title);
				
			} else if (split[i].startsWith("text")) {
				text = getStringVal(split[i]);
				if (bEnc == true && !bAd)
					textLen = getLengthVal(text);
				else
					textLen = text.length();
			} else if (split[i].startsWith("bigText")){
				bigText = getStringVal(split[i]);
				if (bEnc == true && !bAd)
					bigTextLen = getLengthVal(bigText);
				else
					bigTextLen = bigText.length();
			} else if (split[i].startsWith("smText")){
				smText = getStringVal(split[i]);
				if (bEnc == true && !bAd)
					smTextLen = getLengthVal(smText);
				else
					smTextLen = smText.length();
			} else if (split[i].startsWith("vib")) {
				vib = parseVib(split[i]);
			}
		}
		for ( ; i < split.length; i++) {
			if (split[i].startsWith("vib")) {
				vib = parseVib(split[i]);
			} else if (split[i].startsWith("wExt")){
				//System.out.println(split[i]);
				String wearBigText = getWearBigText(split[i]);
				if (wearBigText != null) {
					if (bEnc == true && !bAd)
						bigTextLen = getLengthVal(wearBigText);
					else
						bigTextLen = wearBigText.length();
				}
			}
		}
	}
	
	private String getWearBigText(String wExtStr) {
		int startIndex = wExtStr.indexOf("bigText");
		if (startIndex > 0) {
			int endIndex = wExtStr.indexOf(']', startIndex);
			//System.out.println(wExtStr.substring(startIndex+8, endIndex));
			return wExtStr.substring(startIndex+8, endIndex);
		} else {
			return null;
		}
		
	}

	// maybe... user.getIdentifier() + "|" + pkg + "|" + id + "|" + tag + "|" + uid;
	public void parseTag(String tag) {
		String[] tagSplit = tag.split("\\|");
		// for (String t : tagSplit) {
		// System.out.print("[" + t + "]");
		// }
		// System.out.println();
		uIdentifier = tagSplit[0];
		pkg = tagSplit[1];
		id = Integer.parseInt(tagSplit[2]);
		ttag = tagSplit[3];
		uid = tagSplit[4];
	}
	
	public String getKey() {
		return key;
	}
	
	public long getPostTime() {
		return postDate.getTimeInMillis();
	}
}
