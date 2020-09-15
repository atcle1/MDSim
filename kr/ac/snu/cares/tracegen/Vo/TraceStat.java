package kr.ac.snu.cares.tracegen.Vo;

public class TraceStat {
	public InitialStat initialStat; 

	public float touchIntervalAvg;
	public float touchIntervalStd;
	

	public float notiNotifyIntervalAvg;
	public float notiNotifyIntervalStd;
	
	public TraceStat() {
		initialStat = new InitialStat();
	}
	
	public static TraceStat from(InitialStat initialStat) {
		TraceStat stat = new TraceStat();
		stat.initialStat = initialStat;
		return stat;
	}
	
	@Override
	public String toString() {
		String r;
		r = String.format(" NI %.2f(%.2f) TI %.2f(%.2f)", notiNotifyIntervalAvg, notiNotifyIntervalStd, 
				touchIntervalAvg, touchIntervalStd);
		return initialStat.toString() + r; 
	}
}
