package kr.ac.snu.cares.sampleTrace.Vo;

public class TouchEvent extends TraceEvent implements Cloneable {
	public String toString() {
		return getTimeStr() + " touch";
	}
	public TouchEvent clone()
	{
		TouchEvent objReturn = null;
		objReturn = (TouchEvent)super.clone();
		return objReturn;
	}

}
