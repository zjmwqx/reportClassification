package com.datayes.textmining.Utils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.StorageObject;
import org.jets3t.service.security.AWSCredentials;

import com.datayes.algorithm.textmining.anouncement.textUtils.pdfExtractor;

/**
 * S3Connection.java
 * com.datayes.algorithm.textmining.anoucement.DBUtils
 * 工程：rptClassificationKeyWords
 * 功能： 连接S3数据库，需要jet3t配置文件
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午1:48:37
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class S3Connection {
	//private static Logger logger = Logger.getLogger("RptCls");
	private String bucketName = null;
	private S3Service s3Service = null;
	public void initS3Service (String clsConfigPath) throws Exception
	{
		Properties prop = new Properties();
		DataInputStream prStm = new DataInputStream(new FileInputStream(clsConfigPath));
		prop.load(prStm);
	          
        bucketName = prop.getProperty("bucketName");
		//S3Bucket myBucket = null;
		String awsAccessKey = prop.getProperty("awsAccessKey");
		String awsSecreyKey = prop.getProperty("awsSecreyKey");
		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecreyKey);
		
		//String bucketName = prop.getProperty("bucketName");
		s3Service = new RestS3Service(awsCredentials);
		//myBucket = s3Service.createBucket(bucketName);
	}
	public S3Service getS3Service() {
		return s3Service;
	}
	public void setS3Service(S3Service s3Service) {
		this.s3Service = s3Service;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public static void main(String[] args) {
		/*S3Connection con = new S3Connection();
		con.initS3Service();
		S3Object s3Object = null;
		try {
			s3Object = con.getS3Service().getObject(con.getBucketName(),"");
			InputStream inputStream = s3Object.getDataInputStream();
			pdfExtractor mypdf = new pdfExtractor(inputStream);
			String content = mypdf.getStringFromStream(5);
			System.out.println(content);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
