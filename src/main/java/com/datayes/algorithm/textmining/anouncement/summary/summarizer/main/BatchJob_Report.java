/**   
* @Title: BatchJob_Report.java 
* @Package com.datayes.algorithm.text.financial_news_summarize.main 
* @Description: TODO(对公告提取摘要) 
* @author guangpeng.chen    
* @date Dec 3, 2013 10:51:01 AM 
* @version V1.0   
*/ 
package com.datayes.algorithm.textmining.anouncement.summary.summarizer.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.datayes.algorithm.textmining.anouncement.summary.summarizer.DatayesSummarizer_v0;
import com.datayes.algorithm.textmining.anouncement.summary.summarizer.ISummarizer;
import com.datayes.algorithm.textmining.anouncement.summary.utility.AnnouncementPreProccess;
import com.datayes.algorithm.textmining.anouncement.summary.utility.Pair;
import com.datayes.algorithm.textmining.anouncement.summary.utility.SummaryStruct;
import com.datayes.algorithm.textmining.anouncement.textUtils.ContentGetAgent;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.DBConnectionManager;
import com.datayes.textmining.Utils.S3Connection;
import com.datayes.textmining.reportJobs.ReportJob;

/**
 * @author guangpeng
 *
 */
public class BatchJob_Report {

	private Connection newsConnection;
	private DBConnectionManager dbConnectionManager;
	private PreparedStatement selectNewsStatement;
	private PreparedStatement updateNewsDetHasSummStatement;
	private PreparedStatement insertSummaryStatement;
	private PreparedStatement checkExistStatement;
	private PreparedStatement updateNewsSummStatement;
	private PreparedStatement updateNewsDetBodyStatement;
	public static S3Connection  s3Con =null;
	private ISummarizer summarizer;
	private String dbConfigFile=null;
	public static Logger logger = null;
	public BatchJob_Report() throws Exception{
		//logfile == null
		s3Con = ConfigFileLoader.s3Con;
		logger = ConfigFileLoader.logger;
		summarizer = new DatayesSummarizer_v0(ConfigFileLoader.userDefDicFile);
		this.dbConfigFile = ConfigFileLoader.dbConfigFile;
	}

	private void initDBConnection(String dbAddr) {
		try {

			try {
				while (newsConnection == null || newsConnection.isClosed()) {
					dbConnectionManager = DBConnectionManager.getInstance(dbConfigFile);
					newsConnection = dbConnectionManager.getConnection("news");
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				//e.printStackTrace();
			}

			String sqlSelect = 	"select news_id, news_title, news_url, news_body " + 
								" from news_detail_backup " + 
								" where source_type='report' and has_summary=0 and news_id  > ?  order by news_id asc limit 1000";
			// sqlSelect ="select * from news_summary";

			selectNewsStatement = newsConnection.prepareStatement(sqlSelect);

			String sqlUpdate = "update news_detail_backup set has_summary = true where news_id=?";
			updateNewsDetHasSummStatement = newsConnection.prepareStatement(sqlUpdate);
			
			String UpdateNewsDetBodySql = "update news_detail_backup set news_body = ? where news_id=?";
			updateNewsDetBodyStatement = newsConnection.prepareStatement(UpdateNewsDetBodySql);
			
			String sqlInsert = "INSERT INTO news_summary_backup(news_id, text_summary, summaryPos,insert_time, update_time)" + " VALUES (?, ?, ?, now(), now())";			
			insertSummaryStatement = newsConnection.prepareStatement(sqlInsert);
			
			String checkExistSql ="select * from news_summary_backup where news_id=?";
			checkExistStatement = newsConnection.prepareStatement(checkExistSql);
			
			String updateNewsSummSql ="update news_summary_backup set text_summary =?, summaryPos=?, update_time=now() where news_id=?"; 
			updateNewsSummStatement = newsConnection.prepareStatement(updateNewsSummSql);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.trace(e);
		}
	}
	
	public void run(long startNewsID) {

		initDBConnection("10.20.111.101");
		ResultSet resultSet = null;
		ResultSet checkExistSet = null;
		String newsTitle = null;
		Long newsID = (long) -1;
		String newsContent = null;
		String reportUrl = null;
		String contentToInsert = null;
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ContentGetAgent contentGet= new ContentGetAgent();

		long count = 0;
		try {
			selectNewsStatement.setLong(1, startNewsID);
			resultSet = selectNewsStatement.executeQuery();

			resultSet.last();
			while (resultSet.getRow() > 0) {
				resultSet.beforeFirst();
				while (resultSet.next()) {
					try {
						newsID = resultSet.getLong(1);
						newsTitle = resultSet.getString(2);
						reportUrl = resultSet.getString(3);
						newsContent = resultSet.getString(4);
						
						
						if(newsContent==null||newsContent.equals("")){
							if(reportUrl==null||!reportUrl.trim().toLowerCase().endsWith(".pdf")){
								//System.out.println("Not pdf");
								continue;
							}
							//pdfExtractor.setFileName(reportUrl);
							System.out.println(reportUrl);
							contentGet.setFilePath(reportUrl);
							contentGet.setPageCnt(100);
							if(ReportJob.Version.equals("staging"))
							{
								contentGet.setS3Con(s3Con);
							}
							ExecutorService exec = Executors.newCachedThreadPool();  
						    Future<String> future = exec.submit(contentGet);
						    try {  
							    // 等待计算结果，最长等待timeout秒，timeout秒后中止任务  
							    newsContent = future.get(50, TimeUnit.SECONDS); 
					        } catch (Exception e) {  
					            exec.shutdownNow();
					            newsContent=null;
					            String errorMessage = "[BatchJob:Report Summary]Load file error: NewsId:"+newsID+" "+reportUrl;
						    	System.out.println(errorMessage);
						    	logger.error(errorMessage);
						    	newsContent = "请查看公告原文!";
					        }
						    if(newsContent!=null && newsContent.length()>0){

					    		//mysql中，text类型的字段最大长度为65535,对应21845个字符
						    	if(newsContent.length() > 21845){

						    		contentToInsert = newsContent.substring(0, 21845);
						    	}
						    	else
						    	{
						    		contentToInsert = newsContent;
						    	}
						    	updateNewsDetBodyStatement.setString(1, contentToInsert);
						    	updateNewsDetBodyStatement.setLong(2, newsID);
						    	updateNewsDetBodyStatement.execute();
						    }
						}
						
						newsContent = AnnouncementPreProccess.PreProccess(newsContent);						
						SummaryStruct summaryStruct = new SummaryStruct("", newsContent, 5, 2, 5, 50, 120);

						if (summarizer.getSummary(summaryStruct) == 0) {
							summaryStruct.setSummary("请查看公告原文!");
							summaryStruct.setKeywordList(new ArrayList<Pair<String,Integer>>());
							//System.out.println(newsID+" 请查看公告原文!" + reportUrl);
						}


						checkExistStatement.setLong(1, newsID);
						checkExistSet = checkExistStatement.executeQuery();
						
						if(checkExistSet.next()){
							//update news_summary_backup set text_summary =?, summaryPos=?, update_time=now() where news_id=?
							updateNewsSummStatement.setString(1, summaryStruct.getSummary());
							updateNewsSummStatement.setString(2, summaryStruct.getHigligPosList().toString());
							updateNewsSummStatement.setLong(3, newsID);
							updateNewsSummStatement.execute();
						}else{
							insertSummaryStatement.setLong(1, newsID);
							insertSummaryStatement.setString(2, summaryStruct.getSummary());
							insertSummaryStatement.setString(3, summaryStruct.getHigligPosList().toString());
							insertSummaryStatement.execute();
						}
						
						updateNewsDetHasSummStatement.setLong(1, newsID);
						updateNewsDetHasSummStatement.execute();
						
						count++;
						if (count % 10== 0) {
							logger.info("[Report Summary]"+dataFormat.format(System.currentTimeMillis()) + "\tCount:" + count + " \tCurrent newsID:" + newsID);
							System.out.println("[Report Summary]"+dataFormat.format(System.currentTimeMillis()) + "\tCount:" + count + " \tCurrent newsID:" + newsID);
						}
					} catch (Exception e) {
				    	System.out.println("[BatchJob:Report Summary] E1 newsID:" + newsID + " " + e.getMessage());
						logger.error("[BatchJob:Report Summary] E1 newsID:" + newsID + " " + e.getMessage());
					}
				}
				selectNewsStatement.setLong(1, newsID);
				resultSet = selectNewsStatement.executeQuery();
				resultSet.last();
			}
		} catch (SQLException e) {
	    	System.out.println("[BatchJob:Report Summary] E2 newsID:" + newsID + " " + e.getMessage());
			logger.error("[BatchJob:Report Summary] E2 newsID:" + newsID + " " + e.getMessage());

		}

	}

	
	/** 
	 * @Title: main 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param args    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public static void main(String[] args) {
		long startNewsID = 3385275;
		try {
			ConfigFileLoader.initConf(args);
			BatchJob_Report batchJob_Report = new BatchJob_Report();
			batchJob_Report.run(startNewsID);
			//logger.info(startNewsID + "has been processed");
			System.out.println(startNewsID + "has been processed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
