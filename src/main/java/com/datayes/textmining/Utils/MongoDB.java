/**
 * @Title MongoDB.java
 * @Author yue.you
 * @Description TODO(interactions with MongoDB)
 * @Date Created At: Aug 19, 2013 10:07:21 AM
 * @Version V1.0
 */
package com.datayes.textmining.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoOptions;
import com.mongodb.DB;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

/**
 * MongoDB.java
 * com.datayes.algorithm.textmining.anoucement.DBUtils
 * 工程：rptClassificationKeyWords
 * 功能： 初始化mongoDB数据库，连接公告存储数据库，连接公告QA结果数据库
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午1:38:05
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class MongoDB {

	private String host;// 服务器地址
	private int port;// 端口号
	private String database;// 数据库名
	private String username;// 用户名
	private String password;// 密码
	private MongoClient mc;
	private DB db;
	private static BasicDBObject whereQuery = new BasicDBObject();
	// connect to the DB
	public void connect(String host, int port, String database,
			String username, String password) throws Exception {
		// 连接数据库参数配置
		System.out.println(host + " " + port + " " + database + " " + username
				+ " " + password);
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;

		ServerAddress sa = new ServerAddress(host, port);
		this.mc = new MongoClient(sa);// 获取Instance
		if (mc != null)
			this.setDb(mc.getDB(database));
		else {
			throw new Exception(
					"Can not get database instance! Please ensure connected to mongoDB correctly.");
		}
		if (username != null && password != null) {
			boolean auth = getDb().authenticate(username,
					password.toCharArray());// 用户名密码认证
			if (auth)
				System.out.println("Connect to mongodb successfully!");
			else {
				setDb(null);
				throw new Exception(
						"Can not connect to mongoDB. Failed to authenticate!");
			}
		}
	}

	// close db connection
	public void close() {
		if (mc != null)
			mc.close();
	}

	public MongoDB setHost(String host) {
		this.host = host;
		return this;
	}

	public MongoDB setPort(int port) {
		this.port = port;
		return this;
	}

	public MongoDB setDatabase(String database) {
		this.database = database;
		return this;
	}

	public MongoDB setUsername(String username) {
		this.username = username;
		return this;
	}

	public MongoDB setPassword(String password) {
		this.password = password;
		return this;
	}

	public boolean isclose() {
		if (mc == null) {
			return true;
		}
		return false;
	}

	public DB getDb() throws Exception {
		if (db == null)
			throw new Exception(
					"Can not get database instance! Please ensure connected to mongoDB correctly.");
		else
			return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	/*public void initQADBConnection(String clsConfigPath) throws Exception {
		//logger.info("initDB...");
		if (Main.db_QAconnector == null || Main.db_QAconnector.isclose()) {
			Properties prop = new Properties();
			DataInputStream prStm = new DataInputStream(
					new FileInputStream(clsConfigPath));
			prop.load(prStm);
			String url = prop.getProperty("MongoURL");
			String rptQADBName = prop.getProperty("rptQADBName");
			String usr = prop.getProperty("DBuser");
			String password = prop.getProperty("DBpassword");
			if (usr.equals(""))
				usr = null;
			if (password.equals(""))
				password = null;
			connect(url, 27017, rptQADBName, usr,
					password);
		}
	}

	public void initDBConnection(String clsConfigPath) throws Exception {

		if (Main.db_connector == null || Main.db_connector.isclose()) {
			Properties prop = new Properties();
			DataInputStream prStm = new DataInputStream(
					new FileInputStream(clsConfigPath));
			prop.load(prStm);
			String url = prop.getProperty("MongoURL");
			String reportsDBName = prop.getProperty("reportsDBName");
			String usr = prop.getProperty("DBuser");
			String password = prop.getProperty("DBpassword");
			if (usr.equals(""))
				usr = null;
			if (password.equals(""))
				password = null;
			Main.db_connector = new MongoDB();
			Main.db_connector.connect(url, 27017, reportsDBName, usr,
					password);
		}
	}*/
	public static void main(String[] args) {
		
		MongoDB mdb = new MongoDB();
		if (mdb == null || mdb.isclose()) {
			Properties prop = new Properties();
			String url = prop.getProperty("MongoURL");
			String reportsDBName = prop.getProperty("reportsDBName");
			String usr = prop.getProperty("DBuser");
			String password = prop.getProperty("DBpassword");
			if (usr.equals(""))
				usr = null;
			if (password.equals(""))
				password = null;
			mdb = new MongoDB();
			try {
				mdb.connect(url, 27017, reportsDBName, usr,
						password);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
