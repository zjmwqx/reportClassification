package com.datayes.textmining.Utils;
/**   
 * @Description: TODO
 * @author weifu.du
 * @date Oct 31, 2013 
 * @version V1.0   
 *//*
package com.datayes.algorithm.textmining.anoucement.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;



*//**
 * @author weifu
 * 
 *//*
public class DBConnectionPool {
	private Vector<Connection> pool;
	private String url;
	private String username;
	private String password;
	private String driverClassName;
	*//**
	 * 连接池的大小，也就是连接池中有多少个数据库连接。
	 *//*
	private int poolSize = 10;

	private static DBConnectionPool instance = null;

	*//**
	 * 私有的构造方法，禁止外部创建本类的对象，要想获得本类的对象，通过<code>getIstance</code>方法。 使用了设计模式中的单子模式。
	 *//*
	private DBConnectionPool(String dbConnectInfo) {
		init( dbConnectInfo);
	}

	*//**
	 * 连接池初始化方法，读取属性文件的内容 建立连接池中的初始连接
	 *//*
	private void init(String dbConnectInfo) {
		pool = new Vector<Connection>(poolSize);
		this.url = "jdbc:mysql://10.20.111.101:3306/news?";
		this.username = "news_app";
		this.password = "lKTOAIyoewzvCyc";
		this.driverClassName = "com.mysql.jdbc.Driver";
		addConnection( dbConnectInfo);
	}

	*//**
	 * 返回连接到连接池中
	 *//*
	public synchronized void release(Connection conn) {
		pool.add(conn);

	}

	*//**
	 * 关闭连接池中的所有数据库连接
	 *//*
	public synchronized void closePool() {
		for (int i = 0; i < pool.size(); i++) {
			try {
				((Connection) pool.get(i)).close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pool.remove(i);
		}
	}

	*//**
	 * 返回当前连接池的一个对象
	 *//*
	public static DBConnectionPool getInstance(String dbConnectInfo) {
		if (instance == null) {
			synchronized (DBConnectionPool.class) {
				if (null == instance)
					instance = new DBConnectionPool(dbConnectInfo);
			}
		}
		return instance;
	}

	*//**
	 * 返回连接池中的一个数据库连接
	 *//*
	public synchronized Connection getConnection() {
		if (pool.size() > 0) {
			Connection conn = pool.get(0);
			pool.remove(conn);
			return conn;
		} else {
			return null;
		}
	}

	*//**
	 * 在连接池中创建初始设置的的数据库连接
	 *//*
	private void addConnection(String dbConnectInfo) {
		Connection conn = null;
		for (int i = 0; i < poolSize; i++) {
			try {
				Class.forName(driverClassName);
				conn = java.sql.DriverManager.getConnection(dbConnectInfo);
				pool.add(conn);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
*/