package cn.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import cn.com.PropertiesUtils;
import cn.log.Log;

import com.mysql.jdbc.PreparedStatement;

public abstract class BaseDao {

	//���ز����ӱ��ص�SQLServer
   private static String driveName=PropertiesUtils.getProperty("driveName");
   private String url=PropertiesUtils.getProperty("url");
   static Log log = Log.getLoger();
   
   String userName=PropertiesUtils.getProperty("userName");
   String pwd=PropertiesUtils.getProperty("password");

   //��̬�����  
   static {  
       try {  
           // ��������  
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
            con.setAutoCommit(false);
       }catch(Exception e){
            e.printStackTrace();
            log.loger.error(e);
       }
       return con;  
   }
   
   protected abstract void close();
   protected abstract void close(PreparedStatement state);
   
   /* 
    * �ر����ݿ����ӣ�ע��رյ�˳�� 
    */  
   protected abstract void close(ResultSet rs, PreparedStatement ps, Connection conn);
}
