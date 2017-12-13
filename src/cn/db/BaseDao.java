package cn.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import cn.com.PropertiesUtils;
import cn.log.Log;

import com.mysql.jdbc.PreparedStatement;

public abstract class BaseDao {

	//加载并连接本地的SQLServer
   private final static String driveName = PropertiesUtils.getProperty("driveName");
   private final static String url = PropertiesUtils.getProperty("url");
   private final static String userName=PropertiesUtils.getProperty("userName");
   private final static String pwd=PropertiesUtils.getProperty("password");
   protected static Log log = Log.getLoger();

   //静态代码块  
   static {  
       try {  
           // 加载驱动  
           Class.forName(driveName);
       } catch (ClassNotFoundException e) {  
            e.printStackTrace();
            log.loger.error(e);
       }  
   }
     
   protected Connection getConnection(){
       Connection con = null;  
       try{
            con=(Connection) DriverManager.getConnection(url, userName, pwd);
            //con.setAutoCommit(false);
       }catch(Exception e){
            e.printStackTrace();
            log.loger.error(e);
       }
       return con;  
   }
   
   protected abstract void close();
   protected abstract void close(PreparedStatement state);
   
   /* 
    * 关闭数据库连接，注意关闭的顺序 
    */  
   protected abstract void close(ResultSet rs, PreparedStatement ps, Connection conn);
}
