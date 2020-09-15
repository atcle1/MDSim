package kr.ac.snu.cares.MDSim.Vo;

import java.util.Comparator;
import java.util.LinkedList;

import kr.ac.snu.cares.MDSim.Util.MyUtil;

public class Term {
	public long start = 0;
	public long end = 0;
	public TermState state;
		
	public Term(long startMillis)
	{
		start = startMillis;
	}
	
	public Term(long startMillis, long endMillis)
	{
		start = startMillis;
		end = endMillis;
	}
	
	public void setEndTime(long endMillis)
	{
		end = endMillis;
	}
	
	public void setstartTime(long startMillis)
	{
		start = startMillis;
	}

		public long getLength()
	{
		long length = end - start;
		if (length >= 0)
			return length;
		return 0;
	}
	
	
	public static class TimeComp implements Comparator<Term> {
		@Override
		public int compare(Term arg0, Term arg1) {
			// TODO Auto-generated method stub
			return (int)(arg0.getLength() - arg1.getLength());
		}
	}
	
	@Override
	public String toString()
	{
		String r = "";
		r =  String.format("TERM %s~%s [%4d] %s", MyUtil.MillisToStr(start),
				MyUtil.MillisToStr(end),
				 getLength()/1000,
				 state);
		return r;
	}
}
