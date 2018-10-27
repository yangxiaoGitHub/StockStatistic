package cn.com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class testCom {
	
	@Test
	public void testCopy_Properties() {

		/*Student user1 = new Student();
		user1.setName("test");
		user1.setAge(10);
		user1.setNumber("100001");
		user1.setScore(100.0);
		People user2 = new People();
		try {
			ObjectUtils.copy_Properties(user1, user2);
			ObjectUtils.printProperties(user2);
			ObjectUtils.printProperties(user1);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	/*
	public static void main(String[] args) {
		
		try {
			Connection con_1 = getConnection();
			Connection con_2 = getConnection();
			if (con_1 == con_2) System.out.println("------con_1与con_2是同一个对象！");
			else System.out.println("------con_1与con_2不是同一个对象！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
*/
	private final static String driveName = "com.mysql.jdbc.Driver";
	private final static String userName = "root";
	private final static String pwd = "root";
	private final static String url = "jdbc:mysql://localhost:3306/stock?useUnicode=true&characterEncoding=gbk";
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection con = null;

		try {
			Class.forName(driveName);
			con = (Connection) DriverManager.getConnection(url, userName, pwd);
			con.setAutoCommit(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return con;
	}
}