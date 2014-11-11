/**   
 * @Title: KeyWord.java 
 * @Package com.datayes.algorithm.text.financial_news_summarize.utility 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author guangpeng.chen    
 * @date Oct 10, 2013 12:22:00 PM 
 * @version V1.0   
 */
package com.datayes.algorithm.textmining.anouncement.summary.utility;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author guangpeng
 * 
 */
public class KeyWord {
	private String name;
	private Integer frequence;
	private Boolean isInTitle;

	public KeyWord(String name, Integer frequance, Boolean isInTitle) {
		this.name = name;
		this.frequence = frequance;
		this.isInTitle = isInTitle;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param frequence
	 *            the frequence to set
	 */
	public void setFrequence(Integer frequence) {
		this.frequence = frequence;
	}

	public void incFrequence(Integer incFrequence) {
		this.frequence = this.frequence + incFrequence;
	}

	/**
	 * @return the frequence
	 */
	public Integer getFrequence() {
		return frequence;
	}

	/**
	 * @return the isInTitle
	 */
	public Boolean getIsInTitle() {
		return isInTitle;
	}

	/**
	 * @param isInTitle
	 *            the isInTitle to set
	 */
	public void setIsInTitle(Boolean isInTitle) {
		this.isInTitle = isInTitle;
	}

	public String toString() {
		return this.name + " " + this.frequence + " " + this.isInTitle;
	}

}
