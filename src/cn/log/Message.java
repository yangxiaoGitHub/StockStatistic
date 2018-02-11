package cn.log;

public class Message {
	private static Log log = Log.getLoger();
	
	//方法被执行的message
	private static String methodExecuteMessage = "";
	//方法被执行的次数
	private static long methodExecuteNumber = 0;
	
	public static void clear() {
		methodExecuteNumber = 0;
		methodExecuteMessage = "";
	}

	public static void addMethodExecuteNumber() {
		++methodExecuteNumber;
	}
	
	public static String outputMethodExecuteMessage() {
		return methodExecuteMessage.replace("number", String.valueOf(methodExecuteNumber));
	}
	
	public static boolean methodExecuteMessageIsEmpty() {
		return methodExecuteMessage.equals("");
	}
	
	public static void inputMethodExecuteMessage(String messages) {
		methodExecuteMessage = messages;
	}

	public static void printMethodExecuteMessage() {
		System.out.println(outputMethodExecuteMessage());
		log.loger.info(outputMethodExecuteMessage());
	}
}
