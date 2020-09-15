package kr.ac.snu.cares.sampleTrace;

import java.util.ArrayList;
import java.util.Random;

public class RandomPeriodGenerator {
	Random random = new Random();
	int minTerm;
	int slice;
	
	int startHour, startMin, startSeconds;
	int endHour, endMin, endSeconds;
	
	public static void main(String args[]) {
		RandomPeriodGenerator gen = new RandomPeriodGenerator();
		for (int i = 0; i <100; i++) {
			SamplePeriod parent = new SamplePeriod();
			
			parent.setTime(true, 1, 0, 0);
			parent.setTime(false, 3, 0, 0);
			SamplePeriod test = gen.getRandTerm(parent,  300000);
			//System.out.println(test);
		}
		SamplePeriod parent = new SamplePeriod();
		for (int i = 0; i < 24; i ++) {

			parent.setTime(true, i, 0, 0);
			parent.setTime(false, i + 1, 0, 0);
			SamplePeriod test = gen.getRandTerm(parent,  5 * 60 * 1000);
			//System.out.println(test);
		}
		
		parent.setTime(true, 0, 0, 0);
		parent.setTime(false, 24, 0, 0);
		ArrayList<SamplePeriod> testSet = gen.getRandTerms(parent, 12, 5 * 60 * 1000);
		/*
		for (SamplePeriod period : testSet) {
			System.out.println(period);
		}
		*/
	}

	/**
	 * parent shoud be start at 0
	 * @param parent   : start and end time
	 * @param sliceCnt  : slice count
	 * @param termLen  : pick length with in slice
	 * @return
	 */
	public ArrayList<SamplePeriod> getRandTerms(SamplePeriod parent, long sliceCnt, long termLen) {
		ArrayList<SamplePeriod> result = new ArrayList<SamplePeriod>();
		SamplePeriod slice = new SamplePeriod();
		SamplePeriod pickedSlice;
		long sliceSize = parent.end / sliceCnt;
		long endTime = 0;
		
		while (endTime + sliceSize <= parent.end) {
			slice.start = endTime;
			slice.end = endTime + sliceSize;
			//System.out.println(slice);
			
			pickedSlice = getRandTerm(slice, termLen);
			//System.out.println(pickedSlice);
			//System.out.println();
			result.add(pickedSlice);
			
			endTime += sliceSize;			
		}
		
		return result;
	}
	
	public SamplePeriod getRandTerm(SamplePeriod parent, long term) {
		SamplePeriod period = new SamplePeriod();
		long totalLength = parent.getPeriodMillis();
		
		long start = (Math.abs(random.nextLong())%(totalLength - term));
		period.start = start + parent.start;
		period.end = start + term + parent.start;
		return period;
	}

}