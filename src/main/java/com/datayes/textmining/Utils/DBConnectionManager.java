package com.datayes.textmining.Utils;

import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.sql.Connection;

import com.mysql.jdbc.Driver;
import com.mchange.v2.c3p0.ComboPooledDataSource;  
import com.mchange.v2.c3p0.DataSources;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
/**
 * @author liangzhao
 * 
 */
public class DBConnectionManager {
	private static Hashtable<String, DataSource> pools = new Hashtable<String, DataSource>();
    private static DBConnectionManager manager;  
    private static final Logger logger = Logger.getLogger(DBConnectionManager.class);
    private DBConnectionManager(String dbConfigFile){  
        init(dbConfigFile);
    }  
    public static final DBConnectionManager getInstance(String dbConfigFile){  
        if(manager==null){  
            try{  
            	manager = new DBConnectionManager(dbConfigFile);
            }catch (Exception e) { 
            	logger.error(e.getMessage(), e);
                //e.printStackTrace();  
            }  
        }  
        return manager;  
    }
    
    public synchronized final Connection getConnection(String DBName) {    
        try {  
        	Connection connection = pools.get(DBName).getConnection();
        	if(connection == null){
        		logger.info("no avilable connection");
        	}
            return connection;  
        } catch (SQLException e) {
        	logger.error(e.getMessage(), e);
            //e.printStackTrace();  
        }  
        return null;  
    }
    
    private void init(String configFile) {
		readConfig(configFile);
	}
    
    private void readConfig(String configFile) {
		InputStream fis = null;
		try {
			System.out.println(configFile);
			fis = new BufferedInputStream(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			//e1.printStackTrace();
		}
		try {
			Properties dbProps = new Properties();
			try {
				dbProps.load(fis);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				//e.printStackTrace();
				logger.error("cannot find config file");
				return;
			}
			createPools(dbProps);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			//e.printStackTrace();
			logger.error("read config file error");
		}
	}
    
    private void createPools(Properties props) {
		Enumeration propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".pool")) {
				String poolName = props.getProperty(name);
				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					logger.info("There is no pool of : " + poolName);
					continue;
				}
				String user = props.getProperty(poolName + ".username");
				String password = props.getProperty(poolName + ".password");
				String maxconn = props.getProperty(poolName + ".poolSize");
				String driver = props.getProperty(poolName + ".driverClassName");
				int max;
				try {
					max = Integer.valueOf(maxconn).intValue();
				} catch (NumberFormatException e) {
					logger.error("错误的最大连接数限制: " + maxconn + " .连接池: " + poolName);
					max = 0;
				}
				ComboPooledDataSource ds = new ComboPooledDataSource();
				ds.setJdbcUrl(url);
				ds.setUser(user);
				ds.setPassword(password);
				ds.setMaxPoolSize(max);
				ds.setMinPoolSize(1);
//				ds.setInitialPoolSize(10);
				try {
					ds.setDriverClass(driver);
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
					//e.printStackTrace();
				}
				//解决MySql的timeout
				ds.setTestConnectionOnCheckin(true);
				if(!poolName.equalsIgnoreCase("securityMaster")){
					ds.setAutomaticTestTable("C3P0TestTable");
				}
				//每秒检查所有连接池中的空闲连接。
				ds.setIdleConnectionTestPeriod(480);
				//最大空闲时间,25000秒内未使用则连接被丢弃。
				ds.setMaxIdleTime(25000);
				ds.setTestConnectionOnCheckout(true);
				pools.put(poolName, ds);
				logger.info("Create pool successfully: " + poolName);
			}
		}
	}
    
    public void freeConnection(String name, Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			//e.printStackTrace();
		}
	}
    
    private void closePool(String DBName){
    	DataSource ds = pools.get(DBName);
    	try {
			DataSources.destroy(ds);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			//e.printStackTrace();
		}
    }
    
    private void closePoolManager(){
    	Iterator iter = pools.keySet().iterator();
		while(iter.hasNext()) {
			String DBName = (String) iter.next();
			closePool(DBName);
		}
		pools.clear();
		manager = null;
		logger.info("The pool manager is closed");
    }
    
    public static void main(String[] args) {
		String configFile = "/home/liangzhao/j2ee/workspace/financial_news_analysis/configure/db.properties";
		DBConnectionManager manager = DBConnectionManager.getInstance(configFile);
		int count = 0;
//		Connection news = null;
//		while(count < 10){
//			news = manager.getConnection("news");
//		}
		Connection news = manager.getConnection("news");
		Connection news2 = manager.getConnection("news");
		Connection news3 = manager.getConnection("news");
		Connection news7 = manager.getConnection("securityMaster");
		Connection sm = manager.getConnection("juling");
		try {
			PreparedStatement ps = news.prepareStatement("select count(*) from news_main_backup");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getInt(1));
			}
			manager.freeConnection("news", news);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeout = 1000*60*480L;
		try {
			Thread.currentThread().sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection news4 = manager.getConnection("news");
		manager.freeConnection("news", news4);
		Connection news5 = manager.getConnection("news");
		manager.closePoolManager();
	}
}
