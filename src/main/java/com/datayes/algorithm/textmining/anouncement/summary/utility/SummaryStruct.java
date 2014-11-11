/**   
 * @Title: SummaryStruct.java 
 * @Package com.datayes.algorithm.text.financial_news_summarize.utility 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author guangpeng.chen    
 * @date Sep 5, 2013 4:57:03 PM 
 * @version V1.0   
 */
package com.datayes.algorithm.textmining.anouncement.summary.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guangpeng
 * 
 */
public class SummaryStruct {
	private String title;
	private String originalText;
	private String summary;
	private int keywordNum;
	private int wordsNum;
	private int minOutputWordsNum;
	private int minOutputSenNum;
	private List<Pair<String, Integer>> keywordList;
	private String summarizerName;
	private List<Pair<Integer, Integer>> higligPosList;
	private String textWithHigLight;
	private int maxOutputWordsNum;
	private int maxOutputSenNum;
	//public String operatingDetail;
	
	public String getTitle() {
		return this.title;
	}

	public String getOriginalText() {
		return this.originalText;
	}
	
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}
	
	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	/**
	 * @return the keywordNum
	 */
	public int getKeywordNum() {
		return keywordNum;
	}
	
	public void setKeywordNum(int keywordNum){
		this.keywordNum = keywordNum;
	}
	
	
	public void incWordsNum(int inc) {
		this.wordsNum = this.wordsNum + inc;
	}

	/**
	 * @param wordsNum
	 *            the wordsNum to set
	 */
	public void setWordsNum(int wordsNum) {
		this.wordsNum = wordsNum;
	}

	/**
	 * @return the wordsNum
	 */
	public int getWordsNum() {
		return wordsNum;
	}

	/**
	 * @return the minOutputWordsNum
	 */
	public int getMinOutputWordsNum() {
		return minOutputWordsNum;
	}

	/**
	 * @return the minOutputSenNum
	 */
	public int getMinOutputSenNum() {
		return minOutputSenNum;
	}

	public List<Pair<String, Integer>> getKeywordList() {
		return this.keywordList;
	}

	public void setKeywordList(List<Pair<String, Integer>> keywordList) {
		this.keywordList = keywordList;
	}

	public String getSummarizerName() {
		return this.summarizerName;
	}

	public void setSummarizerName(String summarizerName) {
		this.summarizerName = summarizerName;
	}

	public List<Pair<Integer, Integer>> getHigligPosList() {
		return this.higligPosList;
	}

	public void setHigligPosList(List<Pair<Integer, Integer>> higligPosList) {
		this.higligPosList = higligPosList;
	}

	public String getTextWithHigLight() {
		return this.textWithHigLight;
	}

	public void setTextWithHigLight(String textWithHigLight) {
		this.textWithHigLight = textWithHigLight;
	}

	public int getMaxOutputWordsNum() {
		return this.maxOutputWordsNum;
	}

	public int getMaxOutputSenNum() {
		return this.maxOutputSenNum;
	}



	public SummaryStruct(String title, String content, int keywordNum,
			int minOutputSenNum, int maxOutputSenNum, int minOutputWordsNum,
			int maxOutputWordsNum) {
		this.title = title;
		this.originalText = content;
		this.keywordNum = keywordNum;
		this.minOutputSenNum = minOutputSenNum;
		this.summary = "";
		this.keywordList = new ArrayList<Pair<String, Integer>>();
		this.minOutputWordsNum = minOutputWordsNum;
		//this.operatingDetail = "";
		this.higligPosList = new ArrayList<Pair<Integer, Integer>>();
		this.textWithHigLight = "";
		this.maxOutputSenNum = maxOutputSenNum;
		this.maxOutputWordsNum = maxOutputWordsNum;
	}

	public String toString() {
		StringBuffer output = new StringBuffer();
		output.append("title: " + this.title + "\n"
				+ "total word num:           " + this.wordsNum + "\n"
				+ "key word num:             " + this.keywordNum + "\n"
				+ "Summarizer:               " + this.summarizerName + "\n");

		if (this.minOutputSenNum > 0)
			output.append("min output sentences num: " + this.minOutputSenNum
					+ "\n");
		if (this.minOutputWordsNum > 0)
			output.append("min output words Num:     " + this.minOutputWordsNum
					+ "\n");
		output.append("key words:\n" + this.keywordList.toString() + "\n");
/*		if (!this.operatingDetail.equals(""))
			output.append(this.operatingDetail + "\n");*/
		output.append("Summary:\n"
				+ this.summary
				+ "\n"
				+ "\n-----------------------------Original Text------------------------------\n"
				+ this.originalText + "\n\n\n\n");
		return output.toString();
	}

	public String getXml() {
		StringBuffer output = new StringBuffer();
		output.append("<summarybody>");

		output.append("<metadata>");
		output.append("<title>");
		output.append(this.title);
		output.append("</title>");

		output.append("<wordsNum>");
		output.append(this.wordsNum);
		output.append("</wordsNum>");

		output.append("<keywordNum>");
		output.append(this.keywordNum);
		output.append("</keywordNum>");

		output.append("<summarizerName>");
		output.append(this.summarizerName);
		output.append("</summarizerName>");

		output.append("<minOutputSenNum>");
		output.append(this.minOutputSenNum);
		output.append("</minOutputSenNum>");

		output.append("<minOutputWordsNum>");
		output.append(this.minOutputWordsNum);
		output.append("</minOutputWordsNum>");

		output.append("</metadata>");

		output.append("<content>");

		output.append("<keywordList>");
		output.append(this.keywordList.toString().substring(1,
				this.keywordList.toString().length() - 1));
		output.append("</keywordList>");

		output.append("<summary>");
		output.append(this.summary);
		output.append("</summary>");

		output.append("<originalText>");
		output.append(this.originalText);
		output.append("</originalText>");

		output.append("<textWithHigLight>");
		output.append(this.textWithHigLight);
		output.append("</textWithHigLight>");

		output.append("</content>");
		output.append("</summarybody>");
		return output.toString();
	}
}
