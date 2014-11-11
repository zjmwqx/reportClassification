package com.datayes.textmining.storm.spout;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.LogUtil;
import com.datayes.textmining.rabbitMQ.RabbitMQConfig;
import com.google.gson.Gson;
import com.rabbitmq.client.QueueingConsumer;

/**
 * ReportInsSpout.java
 * com.datayes.algorithm.dpipe.reportClsStorm.storm.spout
 * 工程：rptClassificationKeyWords
 * 功能：report processing spout
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午1:19:21
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class ReportProcessSpout extends BaseRichSpout{
	   private SpoutOutputCollector spoutOutputCollector;

//	    private Long count;

	    private RabbitMQConfig rabbitMQConfig;

	    private QueueingConsumer consumer;
	    
	    private Gson gson;
	    private static Logger logger = null;
		public ReportProcessSpout() {
			// TODO Auto-generated constructor stub
			logger = ConfigFileLoader.logger;
		}

	    @Override
	    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
//	        outputFieldsDeclarer.declare(new Fields("id", "trade_quantity"));
	        outputFieldsDeclarer.declare(new Fields("newsID", "reportID"));
	    }

	    @Override
	    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
	    	this.spoutOutputCollector = spoutOutputCollector;
	        gson = new Gson();
	        Properties prop = ConfigFileLoader.clsProps;
	        //logger.info("in sport");
	        try {
				String url = prop.getProperty("rabbitMQServer");
				String queueName = prop.getProperty("InsQueueName");
				String usr = prop.getProperty("rabbitMQuser");
				String password = prop.getProperty("rabbitMQpwd");
		        rabbitMQConfig = new RabbitMQConfig(url,usr,password,5672, queueName);
		        consumer = rabbitMQConfig.getConsumer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error(e.getMessage(), e);
	        	logger.trace(e);
			}              
	        
	    }

	    @Override
	    public void nextTuple() {
	        try {
//	            if (consumer == null) {
//	            		rabbitMQConfig = new RabbitMQConfig("10.20.201.176","guest","guest",5672, "sqlQueue");
//	                consumer = rabbitMQConfig.getConsumer();
//	            }

	            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	            if (delivery == null){
	            		logger.error("delivery is null");
	                return;
	            }
	            String jsonStr = null;
				try {
					jsonStr = new String(delivery.getBody(),"utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
		        	logger.trace(e);
				}
	            ReportsMessage message = gson.fromJson(jsonStr, ReportsMessage.class);
				//System.out.println(operation+" "+subjectID+" "+subject+" "+relatedKeywords);
				//String how = message.refer.get(0).how;
				//只处理insert的消息
//				if (how.equalsIgnoreCase("update")) {
//					return;
//				}
				String reportID = null;
				String newsID = null;
				for (Map.Entry<String, String> entry : message.refer.get(0).key.entrySet()) {
					System.out.println(entry.getKey());
					System.out.println(entry.getValue());
					if (entry.getKey().equalsIgnoreCase("_id")) {
						reportID = entry.getValue();
					}
					if (entry.getKey().equalsIgnoreCase("news_id")) {
						newsID = entry.getValue();
					}
				}
				//ObjectId rptID = new ObjectId(reportsID);
				System.out.println("*********"+newsID+"==========="+reportID);
				spoutOutputCollector.emit(new Values(newsID,reportID), message);

	        } catch (InterruptedException e) {
	            //e.printStackTrace();
	        	logger.error(e.getMessage(), e);
	        	logger.trace(e);
	        }
	    }

	    @Override
	    public void ack(Object o) {

	    }

	    @Override
	    public void fail(Object o) {
//	        String sentence = (String) o;
//	        spoutOutputCollector.emit(new Values(sentence),sentence);
	    }

	    public void close(){
	        try {
	            rabbitMQConfig.getChannel().close();
	            rabbitMQConfig.getConnection().close();
	        } catch (IOException e) {
	        	logger.error(e.getMessage(), e);
	        	logger.trace(e);
	            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	        }
	    }
}
