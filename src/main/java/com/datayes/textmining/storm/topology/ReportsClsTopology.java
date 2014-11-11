package com.datayes.textmining.storm.topology;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.LogUtil;
import com.datayes.textmining.storm.bolt.ReportClsbolt;
import com.datayes.textmining.storm.bolt.RptSummarizeBolt;
import com.datayes.textmining.storm.spout.ReportProcessSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

/**
 * ReportsClsTopology.java
 * com.datayes.algorithm.dpipe.reportClsStorm.storm.topology
 * 工程：rptClassificationKeyWords
 * 功能： report processing topology
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午1:24:17
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class ReportsClsTopology {
	public ReportsClsTopology() {
		// TODO Auto-generated constructor stub
		
	}
	public static void main(String[] args) {
		// TridentTopology topology = new TridentTopology();
		// Map<Integer,Integer> result = new TreeMap<Integer, Integer>();
		// Log4jConfig log4j = new Log4jConfig();
		// log4j.setLog4jFilePath(args[0]);
		// log4j.setLog4jThreshold("debug");
		// log4j.resetLog4jConfig();
		
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout("run_spout", new ReportProcessSpout(), 1);

		// topologyBuilder.setSpout("update_spout", new ModelUpdtSpout(), 1);

		// topologyBuilder.setBolt("company_classifier",
		// new CompanyClassificationBolt(), 1).shuffleGrouping("input");
		// topologyBuilder.setBolt("event_classifier",
		// new EventClassificationBolt(), 1).shuffleGrouping(
		// "company_classifier");
		//
		// topologyBuilder.setBolt("hotnews_mining", new HotNewsMiningBolt(), 1)
		// .shuffleGrouping("input");

		topologyBuilder.setBolt("rpt_classifier", new ReportClsbolt(args), 1).shuffleGrouping("run_spout");
		topologyBuilder.setBolt("rpt_summerizar", new RptSummarizeBolt(args) , 1).shuffleGrouping("run_spout");
		 
		// topologyBuilder.setBolt("aggregation", new
		// AggregationBolt("10.20.112.103",6379), 1)12c
		// .shuffleGrouping("event_classifier")
		// .shuffleGrouping("hotnews_mining")
		// .shuffleGrouping("subject_extraction")
		// .shuffleGrouping("summarizer");

		Config config = new Config();
		config.setDebug(false);
		config.setNumWorkers(1);
		config.setMaxSpoutPending(5000);
		config.setNumAckers(1);
		// config.setMessageTimeoutSecs(5);
		// config.setMaxTaskParallelism(5);
		//
		/*LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("report_classification", config,
		topologyBuilder.createTopology());*/

		
		try {
			StormSubmitter.submitTopology("report_classification", config, topologyBuilder.createTopology());
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
