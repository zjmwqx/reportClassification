package com.datayes.algorithm.textmining.patternFinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class Pattern implements Serializable{

	private static final long serialVersionUID = 512692613027352058L;
	private String patternName;
	private Integer patternCnt;
	private Set<String> categories = new HashSet<String>();
	private Map<String, Integer> categCount = new HashMap<String, Integer>();
	private HashSet<Pattern> parPats = new HashSet<Pattern>();
	private List<String> terms = new ArrayList<String>();
	public Pattern() {
		// TODO Auto-generated constructor stub
	}
	public Pattern(String patternName, Integer patternCnt) {
		// TODO Auto-generated constructor stub
		this.patternName = patternName;
		this.patternCnt = patternCnt;
	}
	public Pattern(String patternName, Integer totalCount, List<String> terms) {
		// TODO Auto-generated constructor stub
		this.patternName = patternName;
		this.patternCnt = totalCount;
		this.terms = terms;
	}
	public void addParPattern(Pattern parPat)
	{
		parPats.add(parPat);
	}
	public void addParPattern(Set<Pattern> parPat)
	{
		parPats.addAll(parPat);
	}
	public void inc()
	{
		patternCnt++;
	}
	public void addCate(String cateStr)
	{
		categories.add(cateStr);
	}

	public Integer getPatternCnt() {
		if(parPats.size() > 0 && patternCnt == 0)
		{
			patternCnt = 0;
			for(Pattern pt : getParPats())
				patternCnt += pt.getPatternCnt();
		}
		return patternCnt;
	}
	public void setPatternCnt(Integer patternCnt) {
		this.patternCnt = patternCnt;
	}
	public Set<String> getCategories() {
		if(parPats.size() > 0 && categories.size() == 0)
		{
			for(Pattern pt : getParPats())
				categories.addAll(pt.getCategories());
		}
		return categories;
	}
	public int getCateCnt()
	{
		return categories.size();
	}
	public String getPatternName() {
		return patternName;
	}
	public HashSet<Pattern> getParPats() {
		return parPats;
	}
	public List<String> getTerms() {
		return terms;
	}
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	public void incCateg(String categName, int cnt)
	{
		if(categCount.get(categName) == null)
		{
			categCount.put(categName, cnt);
		}
		else
			categCount.put(categName, categCount.get(categName) + cnt);
	}
	public void incCateg(Set<String> categNameList)
	{
		for(String categName :  categNameList)
		{
			incCateg(categName, 1);
		}
	}
	public void incCateg(Pattern pattern) {
		// TODO Auto-generated method stub
		for(String categName :  pattern.getCategories())
		{
			incCateg(categName, pattern.getCategCount(categName));
		}
	}
	public int getCategCount(String categName) {
		// TODO Auto-generated method stub
		if(categCount.containsKey(categName))
			return categCount.get(categName);
		else
			return 0;
	}
	public void incChildCateg()
	{
		if(categCount.size() == 0 && parPats.size() > 0)
		{
			for(Pattern parpat : parPats)
			{
				incCateg(parpat);
			}
		}
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return patternName.equals(((Pattern)obj).getPatternName());
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return patternName.hashCode();
	}
}
