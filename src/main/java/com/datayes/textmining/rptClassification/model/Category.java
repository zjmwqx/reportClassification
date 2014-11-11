package com.datayes.textmining.rptClassification.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Category.java
 * com.datayes.algorithm.textmining.anouncement.model
 * 工程：rptClassificationKeyWords
 * 功能： 类：分类标签，类包含的各种关键字
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午2:06:59
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class Category {
	private String categoryName;
	private Map<String, KeyWord> keyWordsMap= new TreeMap<String, KeyWord>();
	private String[] titleKeywords;
	private String[] contentKeywords;
	private Map<String, List<String>> blackwordsMap =
			new HashMap<String, List<String>>();;
	private Map<KeyWord, Double> KeyWordContribution = 
			new HashMap<KeyWord, Double>();
	private String[] blackList;
	public void initKeywords(String[] keywords, String regName) throws Exception
	{
		if(regName.equals("title"))
			setTitleKeywords(keywords);
		else if(regName.equals("content"))
		{
			setContentKeywords(keywords);
		}
		else if(regName.equals("black"))
		{
			setBlackWords(keywords);
		}
		else if(regName.equals("lb_black"))
		{
			setBlackList(keywords);
		}
		else
		{
			throw new Exception("WrongRegName");
		}
	}
	private void setBlackWords(String[] keywords) {
		// TODO Auto-generated method stub
		
		for(String blackWord : keywords)
		{
			String[] wdArr = blackWord.split("\\(|\\)", -1);
			List<String> blkWords = getBlackwordsMap().get(wdArr[1]);
			if(blkWords == null)
				getBlackwordsMap().put(wdArr[1], new ArrayList<String>());
			getBlackwordsMap().get(wdArr[1]).add(wdArr[2]);
			/*for(String a : getBlackwordsMap().get(wdArr[1]))
			{
				System.out.println(a);
			}*/
		}
	}
	public void initcontentKeywords(String[] keywords)
	{
		
	}
	public void initBlackwords(String[] keywords)
	{
		setBlackWords(keywords);
	}
	public static int getIndexByName(String categoryName)
	{
		for (int i = 0; i < nameOfCategories.length; i++) {
			if(nameOfCategories[i].equals(categoryName))
			{
				return i;
			}
		}
		return -1;
	}
	public int getIndex()
	{
		for (int i = 0; i < nameOfCategories.length; i++) {
			if(nameOfCategories[i].equals(getCategoryName()))
			{
				return i;
			}
		}
		return -1;
	}
	private final static String[] nameOfCategories = 
		{"大宗交易","担保事项","高管变动","股份减持","股份增持","股权激励",
		"关联交易","合同签订","回购","获得奖项","兼并收购","交易异动",
		"配股","首发","诉讼仲裁","违规违纪","业绩发布","预减","预增",
		"增发","债券发行","政府补贴","质押","重大损失","专利获取",
		"资产重组", "未分类"};
	public Category() {
		// TODO Auto-generated constructor stub
	}
	public Category(String categoryName)
	{
		setCategoryName(categoryName);
	}
	public Category(String categoryName, double probVal)
	{
		setCategoryName(categoryName);
		
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryName() {
		return categoryName;
	}

	public void addKeyWord(KeyWord keyWord)
	{
		keyWordsMap.put(keyWord.getWord(), keyWord);
	}
	public void removeKeyWord(KeyWord keyWord)
	{
		keyWordsMap.remove(keyWord.getWord());
	}
	public int getSize()
	{
		return keyWordsMap.size()-1;
	}
	public Map<String, KeyWord> getKeyWordsMap() {
		return keyWordsMap;
	}
	public void setKeyWordsMap(Map<String, KeyWord> keyWordsMap) {
		this.keyWordsMap = keyWordsMap;
	}
	public Map<KeyWord, Double> getKeyWordContribution() {
		return KeyWordContribution;
	}
	public void setKeyWordContribution(Map<KeyWord, Double> keyWordContribution) {
		KeyWordContribution = keyWordContribution;
	}
	public void setKeyWordContribution(KeyWord keyWord, double contribution)
	{
		KeyWordContribution.put(keyWord, contribution);
	}
	public double getKeyWordContribution(KeyWord keyWord)
	{
		return KeyWordContribution.get(keyWord);
	}
	public static String[] getNameofcategories() {
		return nameOfCategories;
	}
	public static int getCateNum()
	{
		return nameOfCategories.length;
	}
	public String[] getTitleKeywords() {
		return titleKeywords;
	}
	public void setTitleKeywords(String[] titleKeywords) {
		this.titleKeywords = titleKeywords;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}
	public String[] getContentKeywords() {
		return contentKeywords;
	}
	public void setContentKeywords(String[] contentKeywords) {
		this.contentKeywords = contentKeywords;
	}
	public List<String> getBlackList(String keyword) {
		// TODO Auto-generated method stub
		if(blackwordsMap.containsKey(keyword))
			return blackwordsMap.get(keyword);
		else
			return new ArrayList<String>();
	}
	public Map<String, List<String>> getBlackwordsMap() {
		return blackwordsMap;
	}
	public void setBlackwordsMap(Map<String, List<String>> blackwordsMap) {
		this.blackwordsMap = blackwordsMap;
	}
	public boolean fileFilterByLbBlackList(String description) {
		// TODO Auto-generated method stub
		for(String blkWord: blackList)
		{
			if(description.split(blkWord, -1).length > 1)
			{
				return true;
			}
		}
		return false;
	}
	public String[] getBlackList() {
		return blackList;
	}
	public void setBlackList(String[] blackList) {
		this.blackList = blackList;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return categoryName;
	}
}
