package com.datayes.textmining.reports;

import java.util.List;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.classification.RptQADataAgent;
import com.datayes.textmining.reportJobs.ReportBatch;
import com.datayes.textmining.rptClassification.model.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SaveContent2DB {
	private static List<FileInfo> unParsedFiles;
	private static DBCollection collReport = null;
	public static void main(String[] args) {
			try {
				
				ConfigFileLoader.initConf(args);
				
				RptQADataAgent qaRptGetter = new RptQADataAgent();
				
				qaRptGetter.preprocessedContent();
				
				//ReportBatch rptClsOrgBatch  = new ReportBatch();
				//rptClsOrgBatch.doBatchJobOnOrg("2014-03-05", "2114-03-06");
				//BatchJob_Report.main(args);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
}
