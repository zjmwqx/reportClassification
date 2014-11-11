/**   
* @Title: SubjectCountInfo.java 
* @Package com.datayes.algorithm.text.financial_news_analysis.storm.spout 
* @Description: TODO(用一句话描述该文件做什么) 
* @author guangpeng.chen    
* @date Oct 23, 2013 2:58:33 PM 
* @version V1.0   
*/ 
package com.datayes.algorithm.textmining.anouncement.summary.utility;

/**
 * @author guangpeng
 *
 */
public class SubjectCountInfo {
	Long id;
	Integer count;
	public SubjectCountInfo(Long id, Integer count){
		this.id = id;
		this.count = count;
	}
}
