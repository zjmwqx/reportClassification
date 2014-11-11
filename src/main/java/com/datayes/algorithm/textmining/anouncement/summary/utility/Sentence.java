/**   
 * @Title: Sentence.java 
 * @Package com.datayes.algorithm.text.financial_news_summarize.utility 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author guangpeng.chen    
 * @date Sep 24, 2013 11:51:39 AM 
 * @version V1.0   
 */
package com.datayes.algorithm.textmining.anouncement.summary.utility;

import java.util.List;

/**
 * @author guangpeng
 * 
 */
public class Sentence {
	public String originalContent;
	public List<String> segContentList;
	public int beginIndex;
	public int endIndex;

	public Sentence(String originalContent, List<String> segContentList,
			int beginIndex, int endIndex) {
		this.originalContent = originalContent;
		this.segContentList = segContentList;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
}
