package cn.log;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public final class Log {

	public Logger loger;
	// ��Log���װ�ɵ�ʵ����ģʽ�������������ࡣ�Ժ�Ҫ�õ���־�ĵط�ֻҪ���Log��ʵ���Ϳ��Է���ʹ��
	private static Log log;

	// ���캯�������ڳ�ʼ��Logger������Ҫ������
	private Log() {
		// ��ñ������ڵ��ļ���
		String filePath = this.getClass().getResource("").getFile();
		//System.out.println("----->filePath: " + filePath);
		String packageName = this.getClass().getPackage().getName().replace(".", "/");
		//System.out.println("----->package: " + packageName);
		filePath = filePath.substring(1).replace(packageName, "");
		//System.out.println("----->filePath: " + filePath);
		// �����־��loger��ʵ��
		loger = Logger.getLogger(this.getClass());
		// loger����������ļ�·��
		PropertyConfigurator.configure(filePath + "log4j.properties");
	}

	public static Log getLoger() {

		if (log == null) {
			log = new Log();
		}
		return log;
	}
}
