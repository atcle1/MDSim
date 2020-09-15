package kr.ac.snu.cares.NotificationLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main {
	public static void main(String[] args) {
		String name = "kms";
		String file1 = "./" + name + "_watch_phone_SND.txt";
		String file2= "./" + name + "_watch_phone_SNE.txt";
		
		String ofile= "merge_" + name + "_watch_phone.txt";
		
		LogReader reader1= new LogReader();
		LogReader reader2= new LogReader();
		LogWriter writer = new LogWriter();
		
		reader1.open(file1);
		reader2.open(file2);
		writer.open(ofile);
		
		ArrayList<ParsedNotification> merge = new ArrayList<ParsedNotification>(20000);
		String line;
		
		while (true) {
			line = reader1.readLine();
			if (line == null) break;
			ParsedNotification item = ParsedNotification.from(line);
			item.bSmnEnabled = false;
			merge.add(item);
		}
		
		while (true) {
			line = reader2.readLine();
			if (line == null) break;
			ParsedNotification item = ParsedNotification.from(line);
			item.bSmnEnabled = true;
			merge.add(item);
		}
		
		Collections.sort(merge, new TimeComp());
		
		for (int i = 0; i < merge.size(); i++) {
			ParsedNotification item = merge.get(i);
		}
		
		long minJumpTime = 60 * 1000;
		long currentTime = 0;
		long prevTime = merge.get(0).time;
		long totalJumpTime = 0;
		long term = 0;
		
		for (int i = 0; i < merge.size(); i++) {
			ParsedNotification item = merge.get(i);
			
			long interval = item.time - prevTime;
			if (interval > minJumpTime) {
				currentTime += minJumpTime;
			} else {
				currentTime += interval;
			}
			
			prevTime = item.time;
			writer.write(currentTime, item);
		}
		writer.flush();
		
	
		
	}	

	
	static class TimeComp implements Comparator<ParsedNotification> {
		@Override
		public int compare(ParsedNotification arg0, ParsedNotification arg1) {
			// TODO Auto-generated method stub
			return (int)(arg0.time - arg1.time);
		}
	}
}
