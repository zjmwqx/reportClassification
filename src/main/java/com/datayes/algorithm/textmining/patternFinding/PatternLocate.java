package com.datayes.algorithm.textmining.patternFinding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.LCS;
import com.datayes.textmining.rptClassification.model.Category;

public class PatternLocate extends PatternFinder {
	private Map<String, Double> cateResMap;
	private Set<String> patternList;
	public PatternLocate() {
		// TODO Auto-generated constructor stub
		super();
		loadPatternsTree();
		loadNameEntityModel();
	}
	public void parseWithTree(String fileName) {
		// TODO Auto-generated method stub
		//System.out.println("parse " + fileName);
		cateResMap  = new HashMap<String, Double>();
		patternList = new HashSet<String>();
		String[] patList = getRegularizedPattern(fileName);
		for(String pat : patList)
		{
			List<String> filen = ConfigFileLoader.wdsp.splitSentence(fileName, false);
			locateInTree(filen, lcsTree);
		}
/*		if(cateResMap.size() == Category.getNameofcategories().length)
		{
			cateResMap.clear();
			cateResMap.add("未分类");
		}*/
		//System.out.println();
	}
	private Boolean locateInTree(List<String> fileName, Set<TreeNode> lcsTree) {
		// TODO Auto-generated method stub
		Boolean isMatch = false;
		for(TreeNode patNode : lcsTree)
		{
			if(LCS.judgeLcsBelong(patNode.getPat().getTerms(),
					fileName))
			{
				if(locateInTree(fileName, patNode.getChildSet()) == false)
				{
					StringBuilder sb = new StringBuilder();
					for (String s : patNode.getPat().getTerms())
					{
					    sb.append(s);
					    sb.append("\t");
					}
					patternList.add(sb.toString());
					for(String cateStr : patNode.getPat().getCategories())
					{
						double pr = (double)(patNode.getPat().getCategCount(cateStr))
								/patNode.getPat().getPatternCnt();
						if(cateResMap.get(cateStr) != null)
						{
							if(pr > cateResMap.get(cateStr))
							{
								cateResMap.put(cateStr, pr);
							}
						}
						else
						{
							cateResMap.put(cateStr, pr);
						}
					}
					//System.out.print(patNode.getPat().getPatternName()+"|");
					String patStr = patNode.getPat().getPatternName();
				}
				isMatch = true;
			}
		}
		return isMatch;
	}
	private Boolean locateInTree(String fileName, Set<TreeNode> lcsTree) {
		// TODO Auto-generated method stub
		Boolean isMatch = false;
		for(TreeNode patNode : lcsTree)
		{
			if(LCS.judgeLcsBelong(patNode.getPat().getPatternName(),
					fileName))
			{
				if(locateInTree(fileName, patNode.getChildSet()) == false)
				{
					StringBuilder sb = new StringBuilder();
					for (String s : patNode.getPat().getTerms())
					{
					    sb.append(s);
					    sb.append("\t");
					}
					patternList.add(sb.toString());
					for(String cateStr : patNode.getPat().getCategories())
					{
						double pr = (double)(patNode.getPat().getCategCount(cateStr))
								/patNode.getPat().getPatternCnt();
						if(cateResMap.get(cateStr) != null)
						{
							if(pr > cateResMap.get(cateStr))
							{
								cateResMap.put(cateStr, pr);
							}
						}
						else
						{
							cateResMap.put(cateStr, pr);
						}
					}
					//System.out.print(patNode.getPat().getPatternName()+"|");
					String patStr = patNode.getPat().getPatternName();
				}
				isMatch = true;
			}
		}
		return isMatch;
	}
	public void loadPatternsTree()
	{
		System.out.println("load pattern");
		getBatchPatterns(true);
		buildUpTree(true);
	}
	public Map<String, Double> getCateResMap() {
		return cateResMap;
	}
	public Set<String> getPatternList() {
		return patternList;
	}
	
	public static void main(String[] args) {
		ConfigFileLoader.initConf(args);
		PatternLocate ptLocate = new PatternLocate();
		ptLocate.parseWithTree("审议了《公司关于继续推进公司发行股份购买资产并募集配套资金暨关联交易的议案》");
		Map<String, Double> cateRes = ptLocate.getCateResMap();
		System.out.print("algRes: ");
		for(String cate : cateRes.keySet())
		{
			System.out.print(cate+cateRes.get(cate)+"|");
		}
		System.out.println();
	}
}
