package com.datayes.textmining.classification;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.MongoDB;
import com.datayes.textmining.rptClassification.model.Category;
import com.datayes.textmining.rptClassification.model.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class RptOrgDataAgent {
	private static Properties prop = null;
	private static Logger logger = null;
	private static MongoDB db_connector = new MongoDB();
	private DBCollection collReport = null;
	public RptOrgDataAgent() throws Exception
	{
		prop = ConfigFileLoader.clsProps;
		logger = ConfigFileLoader.logger;
		initDBConnection();
	}
	public void initDBConnection() throws Exception {

		if (db_connector == null || db_connector.isclose()) {
			String url = prop.getProperty("MongoURL");
			String reportsDBName = prop.getProperty("reportsDBName");
			String usr = prop.getProperty("DBuser");
			String password = prop.getProperty("DBpassword");
			if (usr.equals(""))
				usr = null;
			if (password.equals(""))
				password = null;
			db_connector = new MongoDB();
			db_connector.connect(url, 27017, reportsDBName, usr,
					password);
		}
		setColl();
	}
	public void setColl() throws Exception
	{
		String collectionName = prop.getProperty("rptClctName");
		DBObject options = new BasicDBObject();
		if(!db_connector.getDb().collectionExists(collectionName))
			collReport = db_connector.getDb().createCollection(collectionName, options);
		else collReport = db_connector.getDb().getCollection(collectionName);
	}
	/***********************get report data*********************************/
	public List<FileInfo> getRptsByDateBatch(String StartingDateStr, String EndingDateStr) throws Exception {
		// TODO Auto-generated method stub
		
		DBCursor cursor = null;
		BasicDBObject condition = new BasicDBObject();
		//condition.put("publish_date", StartingDateStr); 
		condition.put("publish_date", new BasicDBObject("$gte",StartingDateStr).append("$lte",EndingDateStr));
		//condition.put("publish_date", new BasicDBObject("$gte", StartingDateStr)); 
		condition.put("parsed",null);
		//condition.put("notice.s3", true);
		cursor = collReport.find(condition);
		cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
		List<FileInfo> unParsedFiles = new ArrayList<FileInfo>();
		FileInfo fileInfo = null;
		logger.info(cursor.count()+" files hasn't been processed in reportsDB");
		while(cursor.hasNext())
		{
			DBObject fileInfoDB = cursor.next();
			fileInfo = getInfoFromDB(fileInfoDB);
			unParsedFiles.add(fileInfo);
		}
		cursor.close();
		return unParsedFiles;
	}
	public List<FileInfo> getRptsByDateBatch() {
		List<FileInfo> unParsedFiles = new ArrayList<FileInfo>();
		try {
			DBCursor cursor = null;
			BasicDBObject condition = new BasicDBObject();
			cursor = collReport.find(condition);
			System.out.println(cursor.count());
			FileInfo fileInfo = null;
			int count = 0;
			while (cursor.hasNext()) {
				if (count % 100 == 0)
					System.out.println(count);
				DBObject fileInfoDB = cursor.next();
				count++;
				fileInfo = getInfoFromDB(fileInfoDB);
				unParsedFiles.add(fileInfo);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return unParsedFiles;
	}
	public FileInfo getFile(String reportIDStr)
	{
		FileInfo fileInfo = null;
    	//System.out.println(mydb.toString());
		DBCursor cursor = null;
		cursor = collReport.find(new BasicDBObject("_id", new ObjectId(reportIDStr)));
		//System.out.println(cursor);
		if(!cursor.hasNext())
		{
			logger.info(reportIDStr+" not existed in mongoDB:"+db_connector.toString()+collReport.toString());
		}
		
		while(cursor.hasNext())
		{
			
			DBObject fileInfoDB = cursor.next();
			fileInfo = getInfoFromDB(fileInfoDB);
		}        
        if(fileInfo == null)
        	System.out.println("no file found!"+reportIDStr);
        return fileInfo;
	}
	private FileInfo getInfoFromDB(DBObject fileInfoDB) {
		// TODO Auto-generated method stub
		FileInfo fileInfo = null;
		fileInfo = new FileInfo(fileInfoDB.get("_id").toString(), fileInfoDB
				.get("description").toString(), fileInfoDB.get("full_path")
				.toString(), fileInfoDB.get("stock_id").toString(),
				fileInfoDB.get("publish_date").toString(),
				fileInfoDB.get("year").toString());
		return fileInfo;
	}
	/***************************update unparsed flag of report**************/
	public void updateParsed(String contentLink) {
		// TODO Auto-generated method stub
		DBCursor cursor = null;
		BasicDBObject condition = new BasicDBObject(); 
		condition.put("full_path", contentLink); 
		//condition.put("notice.s3", true);
		cursor = collReport.find(condition);
		if (cursor.hasNext()) {
			DBObject rptDB = cursor.next();
			DBObject updatedValue=new BasicDBObject();
			updatedValue.put("parsed", true);
			DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
			//rptDB.put("reportType", getRptType(reportLink));
			collReport.update(
					new BasicDBObject("full_path", rptDB
							.get("full_path")), updateSetValue);
		}
	}
}
