package com.datayes.textmining.Utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jets3t.service.model.S3Bucket;

import com.datayes.textmining.reportJobs.ReportJob;

//import com.datayes.algorithm.dpipe.reportClsStorm.Utils.Log4jConfig;

public class ConfigFileLoader implements Serializable {
	
	public static Logger logger = null;
	public static S3Connection s3Con = null;
	/**
	 * @param args
	 */
	public static String userDefDicFile = null;
	public static String dbConfigFile = null;
	public static String configureFileName = null;
	public static String logConfigFile = null;
	public static String titleKeywordsPath = null;
	public static String contentKeywordsPath = null;
	public static String clsConfigPath = null;
	public static String repelentListPath = null;
	public static String blackListPath = null;
	public static String serDir = null;
	public static String classifierDir = null;
	public static String ambiguityFile = null;
	public static String defaultFile = null;
	public static Long startNewsID = null;
	public static Properties logProps = null;
	public static Properties clsProps = new Properties();
	public static WordSpliter wdsp = null;
	public ConfigFileLoader() {
		// TODO Auto-generated constructor stub
	}

	public static void initConf(String[] args){
		// TODO Auto-generated method stub

		String usage = "usage:\t-cf ConfigureFileName ";

		if (args.length < 2) {
			System.out.println(usage);
			return;
		}

		for (int i = 0; i < args.length; i += 2) {
			if (args[i].equalsIgnoreCase("-help")) {
				System.out.println(usage);
				return;
			}

			if (args[i].equalsIgnoreCase("-cf")) {
				configureFileName = args[i + 1];
			}
		}

		if (configureFileName == null) {
			System.out.println(usage);
			return;
		}
		try
		{
			InputStream in = new BufferedInputStream(new FileInputStream(
					configureFileName));
			Properties properties = new Properties();
			properties.load(in);
			String tmpProperty = null;
			if ((tmpProperty = properties.getProperty("UserDefDicFile")) != null) {
				userDefDicFile = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("StartNewsId")) != null) {
				startNewsID = Long.parseLong(tmpProperty);
			}
			if ((tmpProperty = properties.getProperty("dbConfigFile")) != null) {
				dbConfigFile = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("LogConfigFilePath")) != null) {
				logConfigFile = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("TitleKeyWordsFile")) != null) {
				titleKeywordsPath = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("ContentKeyWordsFile")) != null) {
				contentKeywordsPath = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("BlackListFile")) != null) {
				blackListPath = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("repelentList")) != null) {
				repelentListPath = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("ClsConfigPath")) != null) {
				clsConfigPath = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("SerDir")) != null) {
				serDir = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("ClassifierDir")) != null) {
				classifierDir = tmpProperty;
			}
			if ((tmpProperty = properties.getProperty("AmbiguityDicFile")) != null) {
				ambiguityFile = tmpProperty;
			}

			if (logConfigFile == null || titleKeywordsPath == null
					|| userDefDicFile == null || dbConfigFile == null
					|| clsConfigPath == null || contentKeywordsPath == null
					|| blackListPath == null || repelentListPath == null
					|| serDir == null || classifierDir ==null
					|| ambiguityFile == null) {

				System.out.println("Error Configure File\n" + usage);
				return;
			}
			logProps = LogUtil.getLogProperties(logConfigFile);
			logger = new LogUtil().getLogger(ConfigFileLoader.class, logProps);
			DataInputStream prStm = new DataInputStream(new FileInputStream(clsConfigPath));
			clsProps.load(prStm);
			if(ReportJob.Version.equals("staging"))
			{
				s3Con = new S3Connection();
				try {
					s3Con.initS3Service(ConfigFileLoader.clsConfigPath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			wdsp = new WordSpliter();
		}catch(Exception e)
		{
			System.out.println(e.getStackTrace());
		}
		
	}
	public static void main(String[] args) {
		try {
			ConfigFileLoader.initConf(args);
			ConfigFileLoader.logger.info("aa");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
