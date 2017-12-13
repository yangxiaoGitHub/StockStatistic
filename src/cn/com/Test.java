package cn.com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Test {
	
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