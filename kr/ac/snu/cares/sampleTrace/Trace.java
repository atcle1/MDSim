package kr.ac.snu.cares.sampleTrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import kr.ac.snu.cares.sampleTrace.Vo.TraceEvent;


public class Trace implements Cloneable {
	public LinkedList<TraceEvent> trace = new LinkedList<TraceEvent>();
	public LinkedList<TraceEvent> samplingedTrace = new LinkedList<TraceEvent>();
	public LinkedList<TraceEvent> compressedTrace = new LinkedList<TraceEvent>();
	
	public void sampling(long start, long end, ArrayList<SamplePeriod> periods)
	{
		Iterator<TraceEvent> itr = trace.iterator();
		while (itr.hasNext()) {
			TraceEvent event = itr.next().clone();
			//if (trace.size()<1000)
			//	System.out.println(event);
			long eventTime = event.getTime();
			if (eventTime >= start && eventTime < end) {
				for (int i = 0; i < periods.size(); i++) {
					SamplePeriod period = periods.get(i);
					if (period.contains(eventTime - start)) {
						samplingedTrace.add(event);
						//System.out.println("add " + event);
					}
				}
			}
		}
	}
	
	public void compress(long start, long end, long samplingSliceSize, long slotSize, ArrayList<SamplePeriod> periods)
	{
		Iterator<TraceEvent> itr = trace.iterator();
		while (itr.hasNext()) {
			TraceEvent event = itr.next().clone();
			long eventTime = event.getTime();
			if (eventTime >= start && eventTime <= end) {
				for (int i = 0; i < periods.size(); i++) {
					SamplePeriod period = periods.get(i);
					if (period.contains(eventTime - start)) {
						long timeAfterStart = eventTime - start;
						long timeAfterSlice = timeAfterStart - period.start;
						long timeInTrace = timeAfterSlice + (i * slotSize);
						event.setTime(timeInTrace);
						compressedTrace.add(event);
					}
				}
			}
		}
	}
	
	/*
	public void strip(long startMillis, long endMillis)
	{
		Iterator<TraceEvent> itr = trace.iterator();
		while (itr.hasNext()) {
			TraceEvent event = itr.next();
			long eventTime = event.getTime();
			if (eventTime < startMillis ||
					eventTime > endMillis) {
				itr.remove();
			}				
		}
	}
	*/
	public void addAll(long millis)
	{
		Iterator<TraceEvent> itr = trace.iterator();
		while (itr.hasNext()) {
			TraceEvent event = itr.next();
			event.setTime(event.getTime() + millis);
		}
	}
	
	public void merge(LinkedList<TraceEvent> mergeTrace)
	{
		trace.addAll(mergeTrace);
		sort();
	}
	
	public void sort()
	{
		Collections.sort(trace, new TraceEvent.TimeComp());
	}
	
	public void print() {
		Iterator<TraceEvent> itr = samplingedTrace.iterator();
		System.out.println("size : " + samplingedTrace.size());
		while(itr.hasNext()) {
			TraceEvent event = itr.next();
			System.out.println(event);
		}
	}
	
	public void print(String msg) {
		System.out.println(msg);
		print();
	}
	
	public void printCompress() {
		Iterator<TraceEvent> itr = compressedTrace.iterator();
		System.out.println("size : " + compressedTrace.size());
		while(itr.hasNext()) {
			TraceEvent event = itr.next();
			System.out.println(event);
		}
	}
	
	public Trace clone() {
		Trace obj = null;
		try {
			obj = (Trace)super.clone();

			obj.trace = (LinkedList<TraceEvent>) this.trace.clone();
			obj.compressedTrace = new LinkedList<TraceEvent>();
			obj.samplingedTrace = new LinkedList<TraceEvent>();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}
}
