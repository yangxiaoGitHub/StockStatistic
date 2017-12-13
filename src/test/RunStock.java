package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title: RunStock2.java</p>
 * <p>Description:定时插入mydql数据库 </p>
 *
 */
public class RunStock {
	long starttime;

	public void RunStock() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		long s = new Date().getTime();
		String dates = sdf1.format(new Date());
		String dates1 = sdf.format(new Date());
		System.out.println(dates1);
		String ss = dates + " 09:00:00";
		System.out.println(ss);
		try {
			Date sdate = sdf.parse(ss);
			long l;

			System.out.println("开始" + sdate.getTime());
			System.out.println("结束" + new Date().getTime());
			System.out.println(dates);

			l = sdate.getTime() - new Date().getTime();
			System.out.println("绝对值=" + Math.abs(l));
			if (l < 0) {
				starttime = Math.abs(l) + 12 * 3600 * 1000;
			} else {
				starttime = l;
			}

			System.out.print(l);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		MyTask mt = new MyTask();
		mt.run();
		//mt.Command();

	}

	public static void main(String[] args) {
		RunStock rs = new RunStock();
		rs.RunStock();
	}
}
