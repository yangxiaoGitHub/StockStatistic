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
	
	//时间间隔(一天)
	private static final long PERIOD_DAY = 24*60*60*1000;
	public static void timerManager() {
		Calendar calendar = Calendar.getInstance();
		//每天15:30:00
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		//第一次执行定时任务的时间
		Date startTime = calendar.getTime();
        Timer  timer = new Timer();
        RunTask runTask = new RunTask();
		//如果今天首次运行时间过了，立即执行任务，并把首次运行时间就改为明天
		if (startTime.before(new Date())) {
			runTask.run();
			startTime = addDay(startTime, 1);
        }
		//每24小时执行一次
        timer.scheduleAtFixedRate(runTask, startTime, PERIOD_DAY);
//		Timer  timer = new Timer();
//      RunTask runTask = new RunTask();
//		timer.schedule(runTask, 0, 60*1000);
	}

	//增加或减少天数  
    public static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();  
        startDT.setTime(date);  
        startDT.add(Calendar.DAY_OF_MONTH, num);  
        return startDT.getTime();
    }
}
