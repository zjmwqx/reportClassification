package com.datayes.textmining.rptClassification.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.datayes.textmining.rptClassification.model.Category;

/**
 * FileInfo.java
 * com.datayes.algorithm.textmining.anoucement.DBUtils
 * 工程：rptClassificationKeyWords
 * 功能： 类：存储一个文件对应的属性，分类后的结果，关键词，置信度等
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午1:35:26
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class FileInfo {
	public FileInfo() {
		// TODO Auto-generated constructor stub
		categoryAlgList = new HashSet<Category>();
		keywords = new HashSet<String>();
		setPatternList(new HashSet<String>());
		credit = 0;
	}
	public FileInfo(String _id, String description, String full_path, 
			String stock_id, String publishDate, String year)
	{
		set_id(_id);
		setDescription(description);
		setFull_path(full_path);
		setStock_id(stock_id);
		setPublishDate(publishDate);
		setYear(year);
		categoryAlgList = new HashSet<Category>();
		keywords = new HashSet<String>();
		credit = 0;
	}
	private String _id;
	private String description;
	private String full_path;
	private String stock_id;
	private String publishDate;
	private String year;
	private Set<String> qAcategoriesStrList;
	private Set<Category> categoryAlgList;
	private Set<String> patternList;
	private Set<String> keywords;
	private String method;//keywords;content
	private double credit;
	public void set_id(String _id) {
		this._id = _id;
	}
	public String get_id() {
		return _id;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setFull_path(String full_path) {
		this.full_path = full_path;
	}
	public String getFull_path() {
		return full_path;
	}

	public Set<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public boolean fileFilterByLbBlackList(Category cat) {
		// TODO Auto-generated method stub
		return cat.fileFilterByLbBlackList(getDescription());
	}
	public Set<String> getQACategoriesStrList() {
		return qAcategoriesStrList;
	}
	public void setQACategoriesStrList(Set<String> categoriesStrList) {
		this.qAcategoriesStrList = categoriesStrList;
	}
	public Set<Category> getCategoryAlgList() {
		return categoryAlgList;
	}
	public void setCategoryAlgList(Set<Category> categoryAlgList) {
		this.categoryAlgList = categoryAlgList;
	}
	public void addCateAlg(Category cate)
	{
		categoryAlgList.add(cate);
	}
	public String getStock_id() {
		return stock_id;
	}
	public void setStock_id(String stock_id) {
		this.stock_id = stock_id;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Set<String> getPatternList() {
		return patternList;
	}
	public void setPatternList(Set<String> patternList) {
		this.patternList = patternList;
	}

}
