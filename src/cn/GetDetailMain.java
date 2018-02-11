package cn;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import timer.RunTask;

public class GetDetailMain {
	
	public static void main(String[] args) {
		try {
			timerManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//ʱ����(һ��)
	private static final long PERIOD_DAY = 24*60*60*1000;
	public static void timerManager() {
		Calendar calendar = Calendar.getInstance();
		//ÿ��15:30:00
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		//��һ��ִ�ж�ʱ�����ʱ��
		Date startTime = calendar.getTime();
        Timer  timer = new Timer();
        RunTask runTask = new RunTask();
		//��������״�����ʱ����ˣ�����ִ�����񣬲����״�����ʱ��͸�Ϊ����
		if (startTime.before(new Date())) {
			runTask.run();
			startTime = addDay(startTime, 1);
        }
		//ÿ24Сʱִ��һ��
        timer.scheduleAtFixedRate(runTask, startTime, PERIOD_DAY);
//		Timer  timer = new Timer();
//      RunTask runTask = new RunTask();
//		timer.schedule(runTask, 0, 60*1000);
	}

	//���ӻ��������  
    public static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();  
        startDT.setTime(date);  
        startDT.add(Calendar.DAY_OF_MONTH, num);  
        return startDT.getTime();
    }
}
