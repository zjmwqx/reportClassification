package com.datayes.textmining.reportJobs;

import java.io.ObjectInputStream.GetField;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.datayes.textmining.Utils.MongoDB;
import com.datayes.textmining.Utils.S3Connection;
import com.datayes.textmining.classification.RptClasifer;
import com.datayes.textmining.classification.RptOrgDataAgent;
import com.datayes.textmining.classification.RptQADataAgent;
import com.datayes.textmining.rptClassification.model.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class ReportStream {
	public static void procFileByMongoID(String reportIDStr,
			RptClasifer rptCls, RptQADataAgent qaRptAg,
			RptOrgDataAgent orgRptAg) {
		// TODO Auto-generated method stub
		FileInfo fileInfo = orgRptAg.getFile(reportIDStr);
		try {
			rptCls.tryClassify(fileInfo);
			qaRptAg.insertQAMongoDBOne(fileInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
