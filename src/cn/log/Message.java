package cn.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.aop.After;
import cn.aop.Aspect;
import cn.aop.Before;

@Aspect
public class Message {
	private static Log log = Log.getLoger();
	
	private static final String MESSAGES = " >>>>>>funcationName的方法(methodName)被执行number次！";
	//方法被执行的message
	private static String methodExecuteMessage = "";
	//方法被执行的次数
	private static long methodExecuteNumber = 0;
	
	@Before("cn.implement.ValidateStatisticDetailStockData.testLoop()")
	public static void clear() {
		methodExecuteNumber = 0;
		methodExecuteMessage = "";
	}

	@After("cn.implement.ValidateStatisticDetailStockData.testAop()")
	public static void addMethodExecuteNumber() {
		++methodExecuteNumber;
	}
	
//	@After("cn.implement.ValidateStatisticDetailStockData.testLoop()")
//	public static String outputMethodExecuteMessage() {
//		return methodExecuteMessage.replace("number", String.valueOf(methodExecuteNumber));
//	}

	@After("cn.implement.ValidateStatisticDetailStockData.testAop()")
	public static void inputMethodExecuteMessage(String functionName, String methodName) {
		if (methodExecuteMessage.equals("")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sDate = sdf.format(new Date());
			methodExecuteMessage = sDate + MESSAGES.replace("funcationName", functionName).replace("methodName", methodName);
		}
	}

	@After("cn.implement.ValidateStatisticDetailStockData.testLoop()")
	public static void printMethodExecuteMessage() {
		if (!methodExecuteMessage.equals("")) {
			String messages = methodExecuteMessage.replace("number", String.valueOf(methodExecuteNumber));
			System.out.println(messages);
			log.loger.info(messages);
		}
	}
}
