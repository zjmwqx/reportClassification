package com.datayes.textmining.reportJobs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

import com.datayes.algorithm.textmining.anouncement.summary.summarizer.main.BatchJob_Report;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.LogUtil;
import com.datayes.textmining.Utils.S3Connection;
import com.datayes.textmining.classification.RptClasifer;
import com.datayes.textmining.classification.RptQADataAgent;
import com.datayes.textmining.rptClassification.model.FileInfo;


public class ReportBatch extends ReportJob{
	
	public static Logger logger = null;
	
	/**
	 * @param args
	 */

	public static Long startNewsID = null;
	public ReportBatch() {
		// TODO Auto-generated constructor stub
		logger = ConfigFileLoader.logger;
	}
	public void doBatchJobOnQARes()
	{
		try {
			//获得数据库对象
			int count = 0;
			RptClasifer rptCls = new RptClasifer();
			RptQADataAgent qaRptIns = new RptQADataAgent();
			List<FileInfo> rptList = rptCls.procFilesQAed();
			for(FileInfo file : rptList)
			{
				count++;
				qaRptIns.insertAlgQAMongoDBOne(file, "pat");;
				if(count % 100 == 0)
				{
					System.out.println(count + " files has been update in QA mongoDB");
					logger.info(count + " files has been update in QA mongoDB");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e.getStackTrace());
		}
	}
	public void doBatchJobOnOrg(String stDate, String edDate)
	{
		try {
			//获得数据库对象
			RptClasifer rptCls = new RptClasifer();
			RptQADataAgent qaRptIns = new RptQADataAgent();
			int count = 0;
			List<FileInfo> rptList = rptCls.procFilesOrg(stDate, edDate);
			for(FileInfo file : rptList)
			{
				count ++;
				qaRptIns.insertQAMongoDBOne(file);
				if(count % 100 == 0)
				{
					System.out.println(count + " files has been update in QA mongoDB");
					logger.info(count + " files has been update in QA mongoDB");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e.getStackTrace());
		}
	}
	public static void main(String[] args) {
		try {
			
			ConfigFileLoader.initConf(args);
			ReportBatch rptClsQABatch  = new ReportBatch();
			rptClsQABatch.doBatchJobOnQARes();
			
			//ReportBatch rptClsOrgBatch  = new ReportBatch();
			//rptClsOrgBatch.doBatchJobOnOrg("2014-03-05", "2114-03-06");
			//BatchJob_Report.main(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
