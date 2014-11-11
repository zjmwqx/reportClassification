/**   
* @Title: ISummarizer.java 
* @Package com.datayes.algorithm.text.financial_news_summarize.summarizer 
* @Description: TODO(用一句话描述该文件做什么) 
* @author guangpeng.chen    
* @date Sep 3, 2013 3:07:17 PM 
* @version V1.0   
*/ 
package com.datayes.algorithm.textmining.anouncement.summary.summarizer;

import com.datayes.algorithm.textmining.anouncement.summary.utility.SummaryStruct;

/**
 * @author guangpeng
 *
 */
public interface ISummarizer {

	public int getSummary(SummaryStruct summaryStruct);

}
