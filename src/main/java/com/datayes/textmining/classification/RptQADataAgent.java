package com.datayes.textmining.classification;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.datayes.algorithm.textmining.anouncement.textUtils.ContentGetAgent;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.MongoDB;
import com.datayes.textmining.rptClassification.model.Category;
import com.datayes.textmining.rptClassification.model.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class RptQADataAgent {
	private static Properties prop = null;
	private static Logger logger = null;
	private static MongoDB db_connector = new MongoDB();
	private DBCollection collReport = null;
	private static RptOrgDataAgent orgDataController;
	public RptQADataAgent() throws Exception
	{
		prop = ConfigFileLoader.clsProps;
		initDBConnection();
		orgDataController = new RptOrgDataAgent();
		
	}
	public void initDBConnection() throws Exception {
		//logger.info("initDB...");
		if (db_connector == null || db_connector.isclose()) {
			String url = prop.getProperty("MongoURL");
			String rptQADBName = prop.getProperty("rptQADBName");
			String usr = prop.getProperty("DBuser");
			String password = prop.getProperty("DBpassword");
			if (usr.equals(""))
				usr = null;
			if (password.equals(""))
				password = null;
			db_connector.connect(url, 27017, rptQADBName, usr,
					password);
		}
		setColl();
	}
	public void setColl() throws Exception
	{
		String collectionName = prop.getProperty("rptQAInfoName");
		DBObject options = new BasicDBObject();
		if(!db_connector.getDb().collectionExists(collectionName))
			collReport = db_connector.getDb().createCollection(collectionName, options);
		else collReport = db_connector.getDb().getCollection(collectionName);
	}
	/*******************get report from mogoDBQA********************************/
	public List<FileInfo> getFromQARes() {
		// TODO Auto-generated method stub
		List<FileInfo> unParsedFiles = new ArrayList<FileInfo>();
		try {
			DBCursor cursor = null;
			BasicDBObject condition = new BasicDBObject();
			condition.put("qaCategory", new BasicDBObject("$ne", null));
			condition.put("qaUpdateTime", new BasicDBObject("$gte", "2013-12-20"));
			cursor = collReport.find(condition);
			System.out.println(cursor.count());
			FileInfo fileInfo = null;
			while (cursor.hasNext()) {
				DBObject fileInfoDB = cursor.next();
				fileInfo = getInfoFromQADB(fileInfoDB);
				unParsedFiles.add(fileInfo);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return unParsedFiles;
	}
	private FileInfo getInfoFromQADB(DBObject fileInfoDB) {
		// TODO Auto-generated method stub
		FileInfo fileInfo = null;
		fileInfo = new FileInfo(fileInfoDB.get("_id").toString(), fileInfoDB
				.get("title").toString(), fileInfoDB.get("contentLink")
				.toString(), fileInfoDB.get("stockID").toString(),
				fileInfoDB.get("publishDate_").toString(),
				fileInfoDB.get("finacialYear").toString());
		String labelStr = fileInfoDB.get("qaCategory").toString()
				.replaceAll("\\[|\\]|\"| ", "");
		String[] labelStrList = labelStr.split(",");
		Set<String> lsSet = new HashSet<String>();
		for (String lbStr : labelStrList) {
			if(lbStr.length() > 1)
				lsSet.add(lbStr);
		}
		fileInfo.setQACategoriesStrList(lsSet);
		return fileInfo;
	}
	/********************insert processed report into mongoDBQA*****************/
	public void insertQAMongoDBOne(FileInfo fileInfo) throws Exception {
		String[] reportCategory = new String[fileInfo.getCategoryAlgList().size()];
		int cateNum = 0;
		String reportLink = fileInfo.getFull_path();
		String reportTitle = fileInfo.getDescription();
		String fileName = reportLink.substring(reportLink
				.lastIndexOf("/") + 1);
		String stockID = fileInfo.getStock_id();
		String publishDate = fileInfo.getPublishDate();
		String finacialYear = fileInfo.getYear();
		StringBuilder keywords = new StringBuilder();
//			StringBuilder reportCate = new StringBuilder();
		if(fileInfo.getCategoryAlgList().size() == 0)
		{
			fileInfo.getCategoryAlgList().add(new Category("未分类"));
		}
		for (Category cate : fileInfo.getCategoryAlgList()) {
			reportCategory[cateNum] = cate.getCategoryName();
			cateNum++;
		}
		
//			for (int cateInt : reportCategory) {
//				reportCate.append(cateInt + " ");
//			}
		for (String keyword : fileInfo.getKeywords()) {
		
			keywords.append(keyword + " ");
		}
		DBCursor cursor = collReport.find(new BasicDBObject("contentLink",
				reportLink));
		if (cursor.hasNext()) {
			DBObject rptDB = cursor.next();
			DBObject updatedValue=new BasicDBObject();
			updatedValue.put("algCategory", reportCategory);
			updatedValue.put("keywords", keywords.toString().split(" "));
			updatedValue.put("version", "2");
			updatedValue.put("method", fileInfo.getMethod());
			updatedValue.put("credit", fileInfo.getCredit());
			updatedValue.put("publishDate_", publishDate);
			DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
			//rptDB.put("reportType", getRptType(reportLink));
			collReport.update(
					new BasicDBObject("contentLink", rptDB
							.get("contentLink")), updateSetValue);
			// System.out.println("update now");
		}
		else
		{
			BasicDBObject report = new BasicDBObject();
			report.put("title", reportTitle);
			report.put("contentLink", reportLink);
			report.put("fileName", fileName);
			report.put("stockID", stockID);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
			report.put("publishDate", format.parse(publishDate).getTime());
			report.put("finacialYear", finacialYear);
			report.put("version", "2");
			report.put("publishDate_", publishDate);
			report.put("insertDate", format.format(new Date()));
			report.put("algCategory", reportCategory);
			report.put("keywords", keywords.toString().split(" "));
			report.put("method", fileInfo.getMethod());
			report.put("credit", fileInfo.getCredit());
			report.put("version", "2");
			//report.put("reportType", getRptType(reportLink));
			collReport.insert(report);
			
			orgDataController.updateParsed((String)(report
					.get("contentLink")));
		}	
	}
	public void insertAlgQAMongoDBOne(FileInfo fileInfo, String algName) throws Exception {
		String[] reportCategory = new String[fileInfo.getCategoryAlgList().size()];
		int cateNum = 0;
		String reportLink = fileInfo.getFull_path();
		String fileName = reportLink.substring(reportLink
				.lastIndexOf("/") + 1);
		String stockID = fileInfo.getStock_id();
		String publishDate = fileInfo.getPublishDate();
		String reportTitle = fileInfo.getDescription();
		String finacialYear = fileInfo.getYear();
		StringBuilder keywords = new StringBuilder();
//			StringBuilder reportCate = new StringBuilder();
		if(fileInfo.getCategoryAlgList().size() == 0)
		{
			fileInfo.getCategoryAlgList().add(new Category("未分类"));
		}
		for (Category cate : fileInfo.getCategoryAlgList()) {
			reportCategory[cateNum] = cate.getCategoryName();
			cateNum++;
		}
		
//			for (int cateInt : reportCategory) {
//				reportCate.append(cateInt + " ");
//			}
		for (String keyword : fileInfo.getKeywords()) {
		
			keywords.append(keyword + " ");
		}
		DBCursor cursor = collReport.find(new BasicDBObject("contentLink",
				reportLink));
		if (cursor.hasNext()) {
			DBObject rptDB = cursor.next();
			DBObject updatedValue=new BasicDBObject();
			updatedValue.put("algCategory", reportCategory);
			updatedValue.put("keywords", keywords.toString().split(" "));
			updatedValue.put("patterns", fileInfo.getPatternList().toArray());
			updatedValue.put("version", "2");
			updatedValue.put("method", fileInfo.getMethod());
			updatedValue.put("credit", fileInfo.getCredit());
			updatedValue.put(algName, reportCategory);
			updatedValue.put("publishDate_", publishDate);
			DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
			//rptDB.put("reportType", getRptType(reportLink));
			collReport.update(
					new BasicDBObject("contentLink", rptDB
							.get("contentLink")), updateSetValue);
			// System.out.println("update now");
		}
		else
		{
			BasicDBObject report = new BasicDBObject();
			report.put("title", reportTitle);
			report.put("contentLink", reportLink);
			report.put("fileName", fileName);
			report.put("stockID", stockID);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
			report.put("publishDate", format.parse(publishDate).getTime());
			report.put("finacialYear", finacialYear);
			report.put("version", "2");
			report.put("publishDate_", publishDate);
			report.put("insertDate", format.format(new Date()));
			report.put("algCategory", reportCategory);
			report.put(algName, reportCategory);
			report.put("keywords", keywords.toString().split(" "));
			report.put("method", fileInfo.getMethod());
			report.put("credit", fileInfo.getCredit());
			report.put("version", "2");
			//report.put("reportType", getRptType(reportLink));
			collReport.insert(report);

		}	
	}
	public void preprocessedContent() throws Exception {

		// TODO Auto-generated method stub
		try {
			DBCursor cursor = null;
			BasicDBObject condition = new BasicDBObject();
			condition.put("qaCategory", new BasicDBObject("$ne", null));
			condition.put("qaUpdateTime", new BasicDBObject("$gte", "2013-12-20"));
			cursor = collReport.find(condition);
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			System.out.println(cursor.count());
			while (cursor.hasNext()) {
				DBObject fileInfoDB = cursor.next();
				DBObject updatedValue=new BasicDBObject();
				String content = ContentGetAgent.getContent(
						(String)fileInfoDB.get("contentLink"));
				String[] splitedContent = null;
				if(content.length() > 1)
				{
					List<String> contentList = ConfigFileLoader.wdsp.splitSentence(content, true);
					String[] contentArr = new String[contentList.size()];
					splitedContent = contentList.toArray(contentArr);
					System.out.println(splitedContent);
				}
				else
				{
					content = null;
				}
				updatedValue.put("content", content);
				updatedValue.put("splitedContent", splitedContent);
				DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
				collReport.update(
						new BasicDBObject("contentLink", fileInfoDB
								.get("contentLink")), updateSetValue);
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
