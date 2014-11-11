package com.datayes.textmining.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.log4j.*;

/**
 * LogUtil.java
 * com.datayes.algorithm.dpipe.reportClsStorm.Utils
 * 工程：rptClassificationKeyWords
 * 功能： 动态生成logger
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午1:28:44
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class LogUtil implements Serializable{
	private static final long serialVersionUID = 4413053543508847020L;
	private static DailyRollingFileAppender appender;

    public Logger getLogger(Class clazz,Properties properties){
        Logger logger = Logger.getLogger(clazz);
        logger.setAdditivity(false);
        logger.addAppender(getAppender(properties));
        logger.setLevel(Level.toLevel(properties.getProperty("LOG_THRESHOLD")));
        return logger;
    }

    private DailyRollingFileAppender getAppender(Properties properties){
        if (appender==null){
            appender = new DailyRollingFileAppender();
            //DatePattern
            appender.setDatePattern(properties.getProperty("DATE_PATTERN"));
            // log path
            appender.setFile(properties.getProperty("LOG_PATH"));
            // log format
            PatternLayout layout = new PatternLayout();
            layout.setConversionPattern(properties.getProperty("CONVERSION_PATTERN"));
            appender.setLayout(layout);
            // log charset
            appender.setEncoding(properties.getProperty("ENCODING"));
            // true:在已存在log文件后面追加 false:新log覆盖以前的log
            appender.setAppend(true);
            // 适用当前配置
            appender.activateOptions();
        }
        return appender;
    }
    public static Properties getLogProperties(String logConfigFile){
        InputStream fis = null;
        try {
                fis = new FileInputStream(logConfigFile);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        Properties logProps = null;
        try {
                logProps = new Properties();
                logProps.load(fis);
        } catch (Exception e) {
                e.printStackTrace();
                return null;
        }
        return logProps;
    }
    public static void main(String[] args){
    	String configFile = "/home/liangzhao/j2ee/log.properties";
		Properties logProps = getLogProperties(configFile);
		Logger logger = new LogUtil().getLogger(LogUtil.class, logProps);
		logger.info("Test INFO");
		logger.debug("Test DEBUG");
		logger.error("Test ERROR");
    }
}
