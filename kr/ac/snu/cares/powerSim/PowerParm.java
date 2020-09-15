package kr.ac.snu.cares.powerSim;

public class PowerParm implements Comparable {
	// idle power 제외하면 알림으로인한 순수 에너지를 구할수 있음, 단 idle 에너지 의미 없어짐
	// idle power 제외 안하면 알림, 아이들 에너지 의미 있음, 최대 대기시간 계산할때 사용
	public static final PowerParm IDLE_SCREENOFF = new PowerParm("NOTI_IDLE_SCROFF", 0, 0, 14.26f, 0);
	
	public static final PowerParm NOTI_NOTIFY_WAKEUP = new PowerParm("NOTI_WAKEUP", 1, 0, 214.4f, 320);
	public static final PowerParm NOTI_NOTIFY_PROC = new PowerParm("NOTI_PROC", 1, 1, 338.3f, 7320);
	public static final PowerParm NOTI_NOTIFY_SUSPEND = new PowerParm("NOTI_SUSPEND", 1, 2, 202.1f, 460);
	
	public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 190.7f, 300);
	public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 79.5f, 6840);
	public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 57.2f, 610);
	
	
	/*
	// 0324 final vib/screen_off
	public static final PowerParm IDLE_SCREENOFF = new PowerParm("NOTI_IDLE_SCROFF", 0, 0, 14.26f, 0);
	
	public static final PowerParm NOTI_NOTIFY_WAKEUP = new PowerParm("NOTI_WAKEUP", 1, 0, 214.4f, 320);
	public static final PowerParm NOTI_NOTIFY_PROC = new PowerParm("NOTI_PROC", 1, 1, 338.3f, 7320);
	public static final PowerParm NOTI_NOTIFY_SUSPEND = new PowerParm("NOTI_SUSPEND", 1, 2, 202.1f, 460);
	
	public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 190.7f, 300);
	public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 79.5f, 6840);
	public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 57.2f, 610);
	
	// 0324 final vib/always_on
	public static final PowerParm IDLE_SCREENOFF = new PowerParm("NOTI_IDLE_SCROFF", 0, 0, 24.4f, 0);
	
	public static final PowerParm NOTI_NOTIFY_WAKEUP = new PowerParm("NOTI_WAKEUP", 1, 0, 274.33f, 330);
	public static final PowerParm NOTI_NOTIFY_PROC = new PowerParm("NOTI_PROC", 1, 1, 361.01f, 6570);
	public static final PowerParm NOTI_NOTIFY_SUSPEND = new PowerParm("NOTI_SUSPEND", 1, 2, 142.46f, 720);
	
	public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 240.11f, 250);
	public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 117.32f, 3840);
	public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 79.36f, 2560);
	
	// pure notification
	// 0324 final vib/screen_off
	public static final PowerParm IDLE_SCREENOFF = new PowerParm("NOTI_IDLE_SCROFF", 0, 0, 14.26f, 0);
	
	public static final PowerParm NOTI_NOTIFY_WAKEUP = new PowerParm("NOTI_WAKEUP", 1, 0, 214.4f - 14.26f, 320);
	public static final PowerParm NOTI_NOTIFY_PROC = new PowerParm("NOTI_PROC", 1, 1, 338.3f - 14.26f, 7320);
	public static final PowerParm NOTI_NOTIFY_SUSPEND = new PowerParm("NOTI_SUSPEND", 1, 2, 202.1f - 14.26f, 460);
	
	public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 190.7f - 14.26f, 300);
	public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 79.5f - 14.26f, 6840);
	public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 57.2f - 14.26f, 610);
	
	// 0324 final vib/always_on
	public static final PowerParm IDLE_SCREENOFF = new PowerParm("NOTI_IDLE_SCROFF", 0, 0, 24.4f, 0);
	
	public static final PowerParm NOTI_NOTIFY_WAKEUP = new PowerParm("NOTI_WAKEUP", 1, 0, 274.33f - 24.4f, 330);
	public static final PowerParm NOTI_NOTIFY_PROC = new PowerParm("NOTI_PROC", 1, 1, 361.01f - 24.4f, 6570);
	public static final PowerParm NOTI_NOTIFY_SUSPEND = new PowerParm("NOTI_SUSPEND", 1, 2, 142.46f - 24.4f, 720);
	
	public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 240.11f - 24.4f, 250);
	public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 117.32f - 24.4f, 3840);
	public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 79.36f - 24.4f, 2560);
	*/
	
	
	





	public String name = "";
	public int type;
	public int step;
	public float power;
	public int length_ms;
	
	public long start_ms;
	public long end_ms;
	
	public PowerParm(String name, int type, int step, float power, int length_ms)
	{
		this.name = name;
		this.type = type;
		this.step = step;
		this.power = power;
		this.length_ms = length_ms;
	}
	
	public PowerParm(PowerParm parm)
	{
		this.name = parm.name;
		this.type = parm.type;
		this.step = parm.step;
		this.power = parm.power;
		this.length_ms = parm.length_ms;
		this.start_ms = parm.start_ms;
		this.end_ms = parm.end_ms;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		PowerParm other = (PowerParm)arg0;
		return (int)(start_ms - other.start_ms);
		//return 0;
	}
	
	public String toString() {
		return String.format("%10d %10d %7d %10.1f %s", start_ms, end_ms, length_ms, getEnergy(), name);
	}
	
	public int getTypeStepTag() {
		return type * 100 + step;
	}
	public void setStartEnd(long start, long end)
	{
		this.start_ms = start;
		this.end_ms = end;
		this.length_ms = (int)(end_ms - start_ms);
	}
	
	public float getEnergy() {
		return (float) (length_ms * power / 1000.0);
	}
}
// cancel 1
/*
public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 162.22f, 420);
public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 80.72f, 7130);
public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 57.491f, 610);
*/
// cancel 2
/*
public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 219.74f, 210);
public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 74f, 4500);
public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 137.75f, 130);
*/


/*

public static final PowerParm IDLE_SCREENOFF = new PowerParm("NOTI_IDLE_SCROFF", 0, 0, 11.7f, 0);
	
public static final PowerParm NOTI_NOTIFY_WAKEUP = new PowerParm("NOTI_WAKEUP", 1, 0, 228.35f, 300);
public static final PowerParm NOTI_NOTIFY_PROC = new PowerParm("NOTI_PROC", 1, 1, 206.37f, 5100);
public static final PowerParm NOTI_NOTIFY_SUSPEND = new PowerParm("NOTI_SUSPEND", 1, 2, 161.06f, 170);

public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 219.74f, 210);
public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 74f, 4500);
public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 137.75f, 130);
*/
/*
public static final PowerParm NOTI_NOTIFY_WAKEUP = new PowerParm("NOTI_WAKEUP", 1, 0, 271.26f, 350);
public static final PowerParm NOTI_NOTIFY_PROC = new PowerParm("NOTI_PROC", 1, 1, 234.65f, 5000);
public static final PowerParm NOTI_NOTIFY_SUSPEND = new PowerParm("NOTI_SUSPEND", 1, 2, 171.03f, 180);

public static final PowerParm NOTI_CANCEL_WAKEUP = new PowerParm("NOTI_CANCEL_WAKEUP", 3, 0, 250.34f, 210);
public static final PowerParm NOTI_CANCEL_PROC = new PowerParm("NOTI_CANCEL_PROC", 3, 1, 104.61f, 4350);
public static final PowerParm NOTI_CANCEL_SUSPEND = new PowerParm("NOTI_CANCEL_SUSPEND", 3, 2, 155.55f, 130);
*/ 
