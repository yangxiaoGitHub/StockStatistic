package cn.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public final class Log {

	public Logger loger;
	// ��Log���װ�ɵ�ʵ����ģʽ�������������ࡣ�Ժ�Ҫ�õ���־�ĵط�ֻҪ���Log��ʵ���Ϳ��Է���ʹ��
	private static Log log;

	// ���캯�������ڳ�ʼ��Logger������Ҫ������
	private Log() {
		// ��õ�ǰĿ¼·��
		String filePath = this.getClass().getResource("/").getPath();
		// �ҵ�log4j.properties�����ļ����ڵ�Ŀ¼(�Ѿ�������)
		filePath = filePath.substring(1).replace("bin", "src");
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
