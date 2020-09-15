package kr.ac.snu.cares.tracegen.Vo;

public class InitialStat {
	public int timeLength;
	public int touchCnt;
	public int notifyCnt;
	
	@Override
	public String toString() {
		return "timeLength : " + timeLength + " touchCnt : " + touchCnt + " notifyCnt : " + notifyCnt; 
	}
}
