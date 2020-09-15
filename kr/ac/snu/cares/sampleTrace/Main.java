package kr.ac.snu.cares.sampleTrace;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import kr.ac.snu.cares.MDSim.Log.LogReader;
import kr.ac.snu.cares.MDSim.Util.MyUtil;
import kr.ac.snu.cares.MDSim.Vo.LogItem;
import kr.ac.snu.cares.MDSim.Vo.LogItem.LogType;

public class Main {
	/**
	 * @param args
	 */
	
	public static String name = "kms";
	
	public static String dbPath = name + "/watch_phone/watch_phone.sqlite3";
	public static String notiPath = name + "_watch_phone_SNE.txt";
	public static String notiPath2 = name + "_watch_phone_SND.txt";
	
	public static void main2(String []args) {
		SampleTraceGenerator gen = new SampleTraceGenerator();
		gen.setDb(dbPath);
		gen.setNoti(true, notiPath);
		gen.setNoti(false, notiPath2);
		gen.readALlLogs();
		
		SampleTraceGenerator gen2 = gen.clone();
		gen2.prepare();
		gen2.sliceAll();
		gen2.compressAll();
		gen2.printResult();
		
		gen2 = gen.clone();
		gen2.prepare();
		gen2.sliceAll();
		gen2.compressAll();
		gen2.printResult();
	}
	
	public static void main(String []args) {
		LinkedList<SampleTraceGenerator> resultSet = new LinkedList<SampleTraceGenerator>();
		SampleTraceGenerator genp = new SampleTraceGenerator();
		genp.setDb(dbPath);
		genp.setNoti(true, notiPath);
		genp.setNoti(false, notiPath2);
		
		genp.readALlLogs();
		
		double sumEff = 0.0;
		for (int i = 0; i < 100; i++) {
			SampleTraceGenerator gen = genp.clone();
			gen.prepare();
			gen.sliceAll();
			gen.compressAll();
/*
			if (gen.dbTrace.compressedTrace.size() > 0 &&
					gen.notiTrace1.compressedTrace.size() * 0.9 > gen.notiTrace2.compressedTrace.size()) {
				gen.mergeTrace();
				gen.printResult();
				break;
			} */
			//System.out.println(gen.getSmnEfficient());
			if (gen.dbTrace.compressedTrace.size()>0 &&
					gen.dbTrace.compressedTrace.size()<3  &&
					gen.notiTrace2.compressedTrace.size()>40 &&
					gen.notiTrace1.compressedTrace.size()<60)
				resultSet.add(gen);
			else
				i--;
			//gen.init();
		}
		sort(resultSet);
		Iterator<SampleTraceGenerator> itr = resultSet.iterator();

		int cnt = 0;
		while (itr.hasNext()) {
			SampleTraceGenerator gen = itr.next();
			
			System.out.print(cnt++ + " " + gen.getSmnEfficient());
			System.out.println(" : " + gen.notiTrace1.compressedTrace.size() + " / " + gen.notiTrace2.compressedTrace.size() + " / " + gen.dbTrace.compressedTrace.size() );
			sumEff += gen.getSmnEfficient();
		}
		System.out.println("avg : " + sumEff/100);
		
		int targetIndex = findSampleWith(resultSet, 0.1f);
		
		System.out.println("target " + targetIndex + " " + resultSet.get(targetIndex).getSmnEfficient());
		resultSet.get(targetIndex).writeToFile("sampleWorkload/"+name+".txt");
		
		/*
		Scanner scan = new Scanner(System.in);
		int input = scan.nextInt();		
		resultSet.get(input).writeToFile("sampleWorkload/"+name+".txt");
		*/
		
		
	}
	
	public static void sort(LinkedList<SampleTraceGenerator> resultSet) {
		Collections.sort(resultSet, new SampleTraceGenerator.smnEffectComp());
	}
	
	public static int findSampleWith(LinkedList<SampleTraceGenerator> sampleTraceList, double targetEfficient)
	{
		Iterator<SampleTraceGenerator> itr = sampleTraceList.iterator();
		int cnt = 0;
		int r = 0;
		while (itr.hasNext()) {
			SampleTraceGenerator gen = itr.next();
			if (targetEfficient <= gen.getSmnEfficient()) {
				return cnt;
			}
			cnt++;
		}
		return 0;
	}

}
