package com.datayes.textmining.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.datayes.algorithm.textmining.anouncement.summary.utility.AnnouncementPreProccess;
import com.datayes.algorithm.textmining.anouncement.summary.utility.Pair;
import com.datayes.algorithm.textmining.anouncement.summary.summarizer.DatayesSummarizer_v0;
import com.datayes.algorithm.textmining.anouncement.summary.summarizer.ISummarizer;
import com.datayes.algorithm.textmining.anouncement.summary.utility.SummaryStruct;
import com.datayes.algorithm.textmining.anouncement.textUtils.ContentGetAgent;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.DBConnectionManager;
import com.datayes.textmining.Utils.S3Connection;

/**
 * RptSummarizeBolt.java
 * com.datayes.algorithm.textmining.reportClsStorm.storm.bolt
 * 工程：rptClassificationKeyWords
 * 功能： TODO bolt for report summarization
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午2:13:10
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class RptSummarizeBolt extends BaseRichBolt{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9131191244222503883L;
	OutputCollector outputCollector;
	private Connection newsConnection;
	private DBConnectionManager dbConnectionManager;
	private PreparedStatement selectNewsStatement;
	private PreparedStatement updateNewsDetHasSummStatement;
	private PreparedStatement insertSummaryStatement;
	private PreparedStatement checkExistStatement;
	private PreparedStatement updateNewsSummStatement;
	private PreparedStatement updateNewsDetBodyStatement;

	private ISummarizer summarizer;
	private Logger logger = null;
	private S3Connection s3Con;
	private String[] args = null;
	public RptSummarizeBolt(String[] args) {
		this.args = args;
	}
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
		
		try {
			ConfigFileLoader.initConf(args);
			logger = ConfigFileLoader.logger;
			summarizer = new DatayesSummarizer_v0(ConfigFileLoader.dbConfigFile);
			s3Con = ConfigFileLoader.s3Con;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.trace(e);
		}
	}

	private void initDBConnection() throws InterruptedException {
		try {

			try {
				while (newsConnection == null || newsConnection.isClosed()) {
					dbConnectionManager = DBConnectionManager.getInstance(ConfigFileLoader.dbConfigFile);
					newsConnection = dbConnectionManager.getConnection("news");
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				//e.printStackTrace();
			}
			

			String sqlSelect = "select news_id, news_title, news_url, news_body " + " from news_detail_backup " + " where has_summary = 0 and source_type='report' and news_id = ? ";
			// sqlSelect ="select * from news_summary";

			selectNewsStatement = newsConnection.prepareStatement(sqlSelect);

			String sqlUpdate = "update news_detail_backup set has_summary = true where news_id=?";
			updateNewsDetHasSummStatement = newsConnection.prepareStatement(sqlUpdate);

			String UpdateNewsDetBodySql = "update news_detail_backup set news_body = ? where news_id=?";
			updateNewsDetBodyStatement = newsConnection.prepareStatement(UpdateNewsDetBodySql);

			String sqlInsert = "INSERT INTO news_summary_backup(news_id, text_summary, summaryPos,insert_time, update_time)" + " VALUES (?, ?, ?, now(), now())";
			insertSummaryStatement = newsConnection.prepareStatement(sqlInsert);

			String checkExistSql = "select * from news_summary_backup where news_id=?";
			checkExistStatement = newsConnection.prepareStatement(checkExistSql);

			String updateNewsSummSql = "update news_summary_backup set text_summary =?, summaryPos=?, update_time=now() where news_id=?";
			updateNewsSummStatement = newsConnection.prepareStatement(updateNewsSummSql);

		} catch (SQLException e) {
			logger.trace(e);
		}
	}

	@Override
	public void execute(Tuple tuple) {
		String newsIDString = tuple.getStringByField("newsID");
		Long newsID = null;
		if (newsIDString == null) {
			logger.info("[[RptSummarizeBolt]ERROR INPUY from bolt newsID:" + newsIDString);
			return;
		}

		logger.info("[[RptSummarizeBolt] INPUY info newsID:" + newsIDString);

		ResultSet resultSet = null;
		ResultSet checkExistSet = null;
		String newsTitle = null;
		String reportUrl = null;
		String newsContent = null;
		String contentToInsert = null;
		String currentDateTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
		ContentGetAgent contentGet = new ContentGetAgent();
		try {

			if (newsConnection == null || newsConnection.isClosed()) {
				initDBConnection();
			}
			newsID = Long.parseLong(newsIDString);
			selectNewsStatement.setLong(1, newsID);
			resultSet = selectNewsStatement.executeQuery();
			if (resultSet.next()) {
				newsID = resultSet.getLong(1);
				newsTitle = resultSet.getString(2);
				reportUrl = resultSet.getString(3);
				newsContent = resultSet.getString(4);

				if (newsContent == null || newsContent.equals("")) {
					if (reportUrl == null || !reportUrl.trim().toLowerCase().endsWith(".pdf")) {
						logger.info("[[RptSummarizeBolt]ERROR INPUY from db newsID:" + newsIDString);
						return;
					}
					contentGet.setFilePath(reportUrl);
					
					contentGet.setPageCnt(100);
					contentGet.setS3Con(s3Con);
					ExecutorService exec = Executors.newCachedThreadPool();
					Future<String> future = exec.submit(contentGet);
					try {
						// 等待计算结果，最长等待timeout秒，timeout秒后中止任务
						newsContent = future.get(50, TimeUnit.SECONDS);
					} catch (Exception e) {
						exec.shutdownNow();
						newsContent = null;
						String errorMessage = "[RptSummarizeBolt]Load file error: NewsId:" + newsID + " " + reportUrl;
						System.out.println(errorMessage);
						logger.error(errorMessage);
						newsContent = "请查看公告原文!";
					}
					if (newsContent != null && newsContent.length() > 0) {

						// mysql中，text类型的字段最大长度为65535,对应21845个字符
						if (newsContent.length() > 21845) {
							contentToInsert = newsContent.substring(0, 21845);
						}
						else
						{
							contentToInsert = newsContent;
						}
						updateNewsDetBodyStatement.setString(1, contentToInsert);
						updateNewsDetBodyStatement.setLong(2, newsID);
						updateNewsDetBodyStatement.execute();
						System.out.println("updateNews detail body by newsID"+newsID);
					}
				}

				newsContent = AnnouncementPreProccess.PreProccess(newsContent);
				SummaryStruct summaryStruct = new SummaryStruct("", newsContent, 5, 2, 5, 50, 120);

				if (summarizer.getSummary(summaryStruct) == 0) {
					summaryStruct.setSummary("请查看公告原文!");
					summaryStruct.setKeywordList(new ArrayList<Pair<String, Integer>>());
					// System.out.println(newsID+" 请查看公告原文!" + reportUrl);
				}

				System.out.println("updateSummary");
				logger.info("updateSummary");
				
				checkExistStatement.setLong(1, newsID);
				checkExistSet = checkExistStatement.executeQuery();

				if (checkExistSet.next()) {
					// update news_summary_backup set text_summary =?,
					// summaryPos=?, update_time=now() where news_id=?
					updateNewsSummStatement.setString(1, summaryStruct.getSummary());
					updateNewsSummStatement.setString(2, summaryStruct.getHigligPosList().toString());
					updateNewsSummStatement.setLong(3, newsID);
					updateNewsSummStatement.execute();
					System.out.println("updateSummary");
					logger.info("updateSummary");
				} else {
					insertSummaryStatement.setLong(1, newsID);
					insertSummaryStatement.setString(2, summaryStruct.getSummary());
					insertSummaryStatement.setString(3, summaryStruct.getHigligPosList().toString());
					insertSummaryStatement.execute();
					System.out.println("insertSummary");
					logger.info("insertSummary");
				}

				updateNewsDetHasSummStatement.setLong(1, newsID);
				updateNewsDetHasSummStatement.execute();
				System.out.println("success get Summary by newsId "+ newsID);
				logger.info("success get Summary by newsId "+ newsID);

			} else {
				logger.info("[[RptSummarizeBolt] No info in db by newsId:" + newsID);
			}

		} catch (Exception e) {
			logger.error("[RptSummarizeBolt]newsID:" + newsID + " " + e);
			logger.error(e.getStackTrace());
		}
		finally{
			outputCollector.ack(tuple);
			logger.info("sum ack rptID:\t"+newsID);
			if(newsConnection!=null){
				dbConnectionManager.freeConnection("news", newsConnection);
				newsConnection = null;
			}
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		// outputFieldsDeclarer.declare(new Fields("groupID", "newsID", "who",
		// "subjectID"));
	}

	public void close() {
		try {
			newsConnection.close();
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
	}

	public static void main(String args[]) {
		String dbConnectInfo = "jdbc:mysql://10.20.111.101:3306/news?user=news_app&password=lKTOAIyoewzvCyc&useUnicode=true&characterEncoding=utf-8";
		//RptSummarizeBolt rptSummarizeBolt = new RptSummarizeBolt("/home/guangpeng/workspace/javaPro/maven.1376986376114/trunk/reportClassification/maven.1386239723502/library/userLibrary/userLibrary.dic", dbConnectInfo);

		//rptSummarizeBolt.execute(null, null);
	}

}
