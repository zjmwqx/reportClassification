package com.datayes.textmining.storm.bolt;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.LogUtil;
import com.datayes.textmining.classification.RptClasifer;
import com.datayes.textmining.classification.RptOrgDataAgent;
import com.datayes.textmining.classification.RptQADataAgent;
import com.datayes.textmining.reportJobs.ReportBatch;
import com.datayes.textmining.reportJobs.ReportStream;
import com.datayes.textmining.rptClassification.model.FileInfo;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * ReportClsbolt.java
 * com.datayes.algorithm.textmining.reportClsStorm.storm.bolt
 * 工程：rptClassificationKeyWords
 * 功能： TODO bolt for report classification
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午2:13:03
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class ReportClsbolt extends BaseRichBolt{
	private static final long serialVersionUID = 4105819421841938919L;
	OutputCollector outputCollector;
	private Logger logger = null;
	private RptClasifer rptCls = null;
	private RptQADataAgent qaRptAg = null;
	private RptOrgDataAgent orgRptAg = null;
	private String[] args = null;
	public ReportClsbolt(String[] args) {
		// TODO Auto-generated constructor stub
		this.args = args;
				//System.out.println(logProps+ " " + logger);
	}
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.outputCollector = outputCollector;
		try {
			System.out.println(Arrays.asList(args));
			ConfigFileLoader.initConf(args);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		logger = ConfigFileLoader.logger;
		rptCls = new RptClasifer();
		try {
			qaRptAg = new RptQADataAgent();
			orgRptAg  = new RptOrgDataAgent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("load model successfully");
		logger.info("load model successfully");
		/*try {
			Main start = new Main(logConfig);
			start.db_connector = new MongoDB();
			start.db_connector.initDBConnection(clsConfigPath);
			start.db_QAconnector = new MongoDB();
			start.db_QAconnector.initQADBConnection(clsConfigPath);
			TitleContentBasedClassificationImp.initCategory(titleKeywordsPath, contentKeywordsPath, blackListPath, repelentListPath);
			start.myImp = 
					new TitleContentBasedClassificationImp();
			start.qaDB = new QAInsertToDB(clsConfigPath);
			if(Main.Version.equals("staging"))
			{
				start.s3Con = new S3Connection();
				start.s3Con.initS3Service(clsConfigPath);
			}
		} 
		catch (Exception e) {
			logger.trace(e);
		}*/
	}
	@Override
	public void execute(Tuple tuple) {
		
		String reportIDStr = tuple.getStringByField("reportID");
		// TODO Auto-generated method stub
		FileInfo fileInfo = orgRptAg.getFile(reportIDStr);
		try {
			logger.info("process " + fileInfo.getFull_path());
			rptCls.tryClassify(fileInfo);
			logger.info("classifyed successfully!" +  fileInfo.getCategoryAlgList());
			qaRptAg.insertQAMongoDBOne(fileInfo);
			logger.info("insert and update successfully!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			outputCollector.ack(tuple);
			logger.info("cls ack reportID:\t"+reportIDStr);
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		//outputFieldsDeclarer.declare(new Fields("newsID", "who", "subjectID"));
	}

}
