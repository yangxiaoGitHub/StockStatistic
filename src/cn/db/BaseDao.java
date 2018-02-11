package cn.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;

import cn.com.CommonUtils;
import cn.com.PropertiesUtils;
import cn.log.Log;

public abstract class BaseDao {

	// 加载并连接本地的SQLServer
	private final static String DRIVE_NAME = PropertiesUtils.getProperty("driveName");
	private final static String DB_URL = PropertiesUtils.getProperty("url");
	private final static String USER_NAME = PropertiesUtils.getProperty("userName");
	private final static String PASSWORD = PropertiesUtils.getProperty("password");

	private static Connection connection;
	protected static Log log = Log.getLoger();

	// 静态代码块
	static {
		try {
			// 加载驱动
			Class.forName(DRIVE_NAME);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(ex));
		}
	}

	protected synchronized static Connection getConnection() {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
				//connection.setAutoCommit(false);
			} catch (Exception ex) {
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
		return connection;
	}
	
	/*protected Connection getConnection() {
		Connection connection_ = null;
		try {
			connection_ = DriverManager.getConnection(url, userName, pwd);
			connection_.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
			log.loger.error(CommonUtils.errorInfo(e));
		}
		return connection_;
	}*/

	/**
	 * 关闭连接
	 * 
	 */
	protected static synchronized void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.loger.error(CommonUtils.errorInfo(ex));
			}
		}
	}

	//开始事务
	protected abstract void beginTransaction();
	//提交事务
	protected abstract void commitTransaction();
	//回滚事务
	protected abstract void rollBackTransaction();
	//还原连接状态
	protected abstract void resetConnection();

	protected abstract void close();
	protected abstract void close(PreparedStatement state);
	protected abstract void close(ResultSet result, PreparedStatement statement);

	/*
	 * 关闭数据库连接，注意关闭的顺序
	 */
	protected abstract void close(ResultSet rs, PreparedStatement ps, Connection conn);
}
