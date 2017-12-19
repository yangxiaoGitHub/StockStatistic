package cn.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public final class Log {

	public Logger loger;
	// 将Log类封装成单实例的模式，独立于其他类。以后要用到日志的地方只要获得Log的实例就可以方便使用
	private static Log log;

	// 构造函数，用于初始化Logger配置需要的属性
	private Log() {
		// 获得当前目录路径
		String filePath = this.getClass().getResource("/").getPath();
		// 找到log4j.properties配置文件所在的目录(已经创建好)
		filePath = filePath.substring(1).replace("bin", "src");
		// 获得日志类loger的实例
		loger = Logger.getLogger(this.getClass());
		// loger所需的配置文件路径
		PropertyConfigurator.configure(filePath + "log4j.properties");
	}

	public static Log getLoger() {

		if (log == null) {
			log = new Log();
		}
		return log;
	}
}
