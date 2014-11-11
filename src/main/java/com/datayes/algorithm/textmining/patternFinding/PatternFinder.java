package com.datayes.algorithm.textmining.patternFinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.commons.lang.SerializationException;

import com.datayes.algorithm.textmining.anouncement.textUtils.CategoryLoader;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.LCS;
import com.datayes.textmining.Utils.WordSpliter;
import com.datayes.textmining.classification.RptOrgDataAgent;
import com.datayes.textmining.classification.RptQADataAgent;
import com.datayes.textmining.rptClassification.model.Category;
import com.datayes.textmining.rptClassification.model.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * 
 * PatternFinder.java com.datayes.algorithm.textmining.patternFinding
 * 工程：rptClassificationKeyWords 功能： TODO
 * 
 * author date time ───────────────────────────────────────────── jiminzhou
 * 2014年2月22日 下午1:58:22
 * 
 * Copyright (c) 2014, Datayes All Rights Reserved.
 */
public class PatternFinder {
	static List<FileInfo> unParsedFiles = new ArrayList<FileInfo>();
	static Map<String, Pattern> patterns = new HashMap<String, Pattern>();
	static Map<String, Set<String>> ch2Pats = new HashMap<String, Set<String>>();
	static List<String> cpNameList = new ArrayList<String>();
	static Map<String, Map<String, Pattern>> chlcsPatterns = new HashMap<String, Map<String, Pattern>>();
	static Map<String, Pattern> totalLcsPats = new HashMap<String, Pattern>();
	static Map<String, Pattern> dominantLcs = new HashMap<String, Pattern>();
	static Set<TreeNode> lcsTree = new TreeSet<TreeNode>();
	static Set<TreeNode> haveExist = new HashSet<TreeNode>();
	private static BufferedWriter bw;
	static AbstractSequenceClassifier<CoreLabel> classifier = null;
	/////////////////parameters//to//show///res////////////////////
	static Boolean redoAllData = true;//(true)if you want to see the res, false please, and make sure 'loadTree' and 'loadAllPatterns' is true
	static Boolean loadTree = false;//(false)
	static Boolean loadAllPatterns = false;//(false)load all data from mongodb and abstract patterns, always true to load the core object.
	static Boolean printRepeats =false;
	/////////////////parameter//when//regenerate//data////////////
	static Boolean dealWithAll = false;//(false)true when you want to deal with all data, not only QAed ones
	static Boolean loadCh2PatsMap = false;//(false)load characters to patterns' map, here we treat term as character
	static Boolean loadChLcsPattern = false;//(false)load map of 'term to longest common string(lcs) of all patterns' map, we calculate them in group of terms
	static Boolean loadLcsTotal = false;//(false)load total lcs
	static Boolean loadDominantLcs = false;//(false)load dominant lcs, this object is essential to build TREE
	static int LCSCntThreashold = 5;//(100)10 for QAed data, 100 for all data
	
	static int categoryCntThreadshold = 30;//无法按照数量限制
	static int LCSLengthThreadshold = 4;//always 4
	
	static int patCntThreashold = 0;//always 0
	
	//
	public PatternFinder() {
		// TODO Auto-generated constructor stub
		try {
			// Initialize companies' name-list
			BufferedReader br = new BufferedReader(new InputStreamReader(this
					.getClass().getClassLoader()
					.getResourceAsStream("companyName.txt")));
			String resFileName = null;
			
			String cpNameStr = null;
			while ((cpNameStr = br.readLine()) != null) {
				cpNameList.addAll(Arrays.asList(cpNameStr.split("\t")));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// initialize parameters
		
		try {
			ConfigFileLoader.initConf(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("config/lib files read error!");
			return;
		}
		PatternFinder ptFinder = new PatternFinder();
		ptFinder.prepareAllPatsAndLcs(redoAllData);
		/*********************** delete ones whose counts under LCSThreashold ***************************/
		ptFinder.findDominantLcs(loadDominantLcs, LCSCntThreashold, LCSLengthThreadshold,
				categoryCntThreadshold);
		ptFinder.goldWordsTest(dominantLcs.keySet());
		ptFinder.buildUpTree(loadTree);
		
		ptFinder.printRes();
	}

	private void goldWordsTest(Set<String> testSet) {
		// TODO Auto-generated method stub
		//for all keywords in user-defined list, list all patterns containing them
		try {
			CategoryLoader.initCategories(ConfigFileLoader.titleKeywordsPath);
			CategoryLoader.initKeywords(ConfigFileLoader.titleKeywordsPath, "title");
			for(Category cat : CategoryLoader.categories)
			{
				System.out.println(cat.getCategoryName());
				for(String kw : cat.getTitleKeywords())
				{
					System.out.print(kw+" : ");
					for(String tsKw : testSet)
					{
						if(tsKw.contains(kw))
						{
							System.out.print(tsKw + " ");
						}
					}
					System.out.println();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addCategoryRepCnt() {
		// TODO Auto-generated method stub
		//int count = 0;
		for(String lcsPat : dominantLcs.keySet())
		{
			/*if(count%1000 == 0)
			{
				System.out.println("count of categories: lcsPat has been cal: " + count);
			}*/
			for(String orgPat : patterns.keySet())
			{
				if(orgPat.length() >= lcsPat.length())
				{
					if(LCS.judgeLcsBelong(lcsPat, orgPat))
					{
						dominantLcs.get(lcsPat).incChildCateg();
					}
				}
			}
			//count++;
		}
	}

	//////////////////////////////////algorithm//////////////////////////////////////////////////
	protected void getBatchPatterns(Boolean loadAllPatterns) {
		// TODO Auto-generated method stub
		if (loadAllPatterns) {
			patterns = (Map<String, Pattern>) (unSerilazition("patterns"));
		} 
		else {
			loadNameEntityModel();
			RptOrgDataAgent orgDataController = null;
			RptQADataAgent qaDataController = null;
			try {
				orgDataController = new RptOrgDataAgent();
				qaDataController = new RptQADataAgent();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(dealWithAll)
				unParsedFiles.addAll(orgDataController.getRptsByDateBatch());
			unParsedFiles.addAll(qaDataController.getFromQARes());
			int count = 0;
			for (FileInfo finf : unParsedFiles) {
				if (count % 10000 == 0) {
					System.out.println(count);
				}
				getPatterns(finf);
				count++;
			}
			serialization("patterns", patterns);
		}
		/********************** output result of patterns ******************/
		Map.Entry[] entries = null;
		// sort all patterns by count decreased
		entries = getSortedPatternTableByValue(patterns);
		for (int i = 0; i < entries.length; i++) {
			System.out.print(entries[i].getKey() + "="
					+ ((Pattern) (entries[i].getValue())).getPatternCnt());
			Set<String> cateSet = ((Pattern) (entries[i].getValue()))
					.getCategories();
			if (cateSet.size() > 0) {
				System.out.print("===>曾QA到分类：");
				for (String cateStr : cateSet) {
					System.out.print(cateStr);
				}
			}
			System.out.println();
		}
		System.out.println("patterns:" + patterns.size());
	}

	protected static void loadNameEntityModel() {
		// TODO Auto-generated method stub
		
		String serializedClassifier = ConfigFileLoader.classifierDir + "chinese.misc.distsim.crf.ser.gz";
		classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
	}

	private void prepareAllPatsAndLcs(Boolean redoAllData) {
		
		/********************* start *************************/
		/**************already has data to build tree*********/

		if (!redoAllData) {
			loadAllPatterns = true;
			loadDominantLcs = true;
			getBatchPatterns(loadAllPatterns);
			findDominantLcs(loadDominantLcs, -1, -1, -1);
			return;
		}
		/***************** redo data according to parameters **************/
		// get patterns
		getBatchPatterns(loadAllPatterns);
		// filter all patterns whose count under the threshold
		filterByCountThreashold(patCntThreashold);

		// build up Map<character,List<pattern>> : store all patterns containing
		// a certain of Character
		// we can get all patterns sharing one or more character with the
		// pattern we try to analyze

		
		/*******************************************************************/
		getCh2PatsMap(loadCh2PatsMap, patterns.keySet());
		/******************** output results of ch2Pats ********************/
		// 公司:33544有限公司:20084公告:18986股东:16053资金:13358报告:13015
/*		ch2Pats.remove("公司");
		ch2Pats.remove("有限公司");
		ch2Pats.remove("公告");
		ch2Pats.remove("股东");
		ch2Pats.remove("资金");
		ch2Pats.remove("报告");*/
		//entries = getSortedCh2PatsTableByValue(ch2Pats);
		/*for (int i = 0; i < entries.length; i++) {
			System.out.println(entries[i].getKey() + ":"
					+ ((Set<String>) (entries[i].getValue())).size());
		}*/
		/*********************** cal lcs of all pattern by character group ******************************/
		calLCSperCh(loadChLcsPattern);

		/*********************** delete repeats *********************************************************/
		getLcsTotal(loadLcsTotal);
		

	}

	protected void buildUpTree(Boolean loadTree) {
		// TODO Auto-generated method stub
		
		/*for (int i = 0; i < entries.length; i++) {
			System.out.print(entries[i].getKey() + "="
					+ ((Pattern) (entries[i].getValue())).getPatternCnt());
			Set<String> cateSet = ((Pattern) (entries[i].getValue()))
					.getCategories();
			if (cateSet.size() > 0) {
				System.out.print("===>曾QA到分类：");
				for (String cateStr : cateSet) {
					System.out.print(cateStr);
				}
			}
			System.out.println();
		}*/
		if(loadTree)
		{
			lcsTree = (Set<TreeNode>)unSerilazition("lcsTree");
		}
		else
		{
			Map.Entry[] entries = getSortedDominantLcsTableByValue(dominantLcs);
			int count = 0;
			for (int i = 0; i < entries.length; i++) {
				if(count % 1000 == 0)
					System.out.println("count of nodes have been inserted: " + count);
				insertNode((Pattern) (entries[i].getValue()), lcsTree);
				count ++;
			}
			//trimTreeNoDecendant();
			//trimTreeByCategoriesCnt(lcsTree, null);
			
			
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter("firstLayerTrim.txt"));
				
				for(TreeNode tn : lcsTree)
				{
					br.write(tn.getPat().getPatternName()+"\n");
				}
				br.close();
			} catch (IOException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
			}
			//trimTreeByFirstLayerName(lcsTree);
			serialization("lcsTree", lcsTree);
		}
		
		//printTree(lcsTree, "");
		System.out.println("the count of nodes in 1st layer is " + lcsTree.size());
	}
	private void trimTreeNoDecendant() {
		// TODO Auto-generated method stub
		Iterator<TreeNode> it = lcsTree.iterator();
		while(it.hasNext())
		{
			TreeNode nd = it.next();
			if(nd.getChildSet().size() == 0)
			{
				it.remove();
			}
		}
	}

	private void trimTreeByFirstLayerName(Set<TreeNode> lcsTree2) {
		// TODO Auto-generated method stub
		/*BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
				.getResourceAsStream("firstLayerTrim.txt")));*/
		
		for(TreeNode node : lcsTree2)
		{
			System.out.print(node.getPat().getPatternName() + 
			"\t=总数=" + node.getPat().getPatternCnt() + "=" );
			for(String cateStr : node.getPat().getCategories())
			{
				if(cateStr.length() > 1)
					System.out.print(cateStr + node.getPat().getCategCount(cateStr)  + "|");
			}
			System.out.println("plz input y if preserve, n if not");
			Scanner sc=new Scanner(System.in);  
			
	        if(sc.hasNext())  
	        {  
	            if(sc.next().equals("n"))
	            {
	            	node.getChildSet().clear();
	            	System.out.println("remove successfully!");
	            }
	        }  
		}
	}

	private Boolean trimTreeByCategoriesCnt(Set<TreeNode> childSet, Set<String> cates) {
		// TODO Auto-generated method stub
		Iterator<TreeNode> it = childSet.iterator();
		Boolean hasClear = true;
		while(it.hasNext())
		{
			TreeNode chilNode = it.next();
			if(!trimTreeByCategoriesCnt(chilNode.getChildSet(), chilNode.getPat().getCategories()))
			{
				hasClear = false;
			}
			else
			{
				if(cates != null)
				{
					if(chilNode.getPat().getCategories().containsAll(cates))
					{
						it.remove();
					}
				}
			}
		}
		if(hasClear == true)
		{
			return true;
		}
		return false;
	}

	private void printTree(Set<TreeNode> lcsTree, String grade) {
		// TODO Auto-generated method stub
		for(TreeNode tn : lcsTree)
		{
			try {
				if((haveExist.contains(tn)) && (!printRepeats))
				{
					continue;
				}
				haveExist.add(tn);
				bw.write(grade + tn.getPat().getPatternName() + 
						"\t=总数=" + tn.getPat().getPatternCnt() + "=");
				for(String cateStr : tn.getPat().getCategories())
				{
					if(cateStr.length() > 1)
						bw.write(cateStr + tn.getPat().getCategCount(cateStr)  + "|");
				}
				bw.write("\n");
				printTree(tn.getChildSet(), grade+" ");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	private void insertNode(Pattern pattern, Set<TreeNode> lcsTree) {
		// TODO Auto-generated method stub
		Boolean hasInserted = false;
		for(TreeNode tnode : lcsTree)
		{
			if(LCS.judgeLcsBelong(tnode.getPat().getTerms(), 
					pattern.getTerms()))
			{
				insertNode(pattern, tnode.getChildSet());
				hasInserted = true;
			}
		}
		if(!hasInserted)
		{
			TreeNode newNode = new TreeNode(pattern);
			lcsTree.add(newNode);
		}
	}

	protected void findDominantLcs(Boolean loadDominantLcs, int LCSCntThreashold,
			int LCSLengthThreadshold, int categoryCntThreadshold) {
		// TODO Auto-generated method stub
		if (loadDominantLcs) {
			dominantLcs = (Map<String, Pattern>) (unSerilazition("dominantLcs"));
		} else {
			dominantLcs.clear();
			int[] lcsCountAccess = new int[10000];
			for (String pat : totalLcsPats.keySet()) {
				
				if (pat.length() >= LCSLengthThreadshold) {
					int ptCnt = totalLcsPats.get(pat).getPatternCnt();

					//System.out.println(ptCnt);
					//System.out.println(totalLcsPats.get(pat).getCategories().size());
					lcsCountAccess[ptCnt / 100]++;
					if (ptCnt >= LCSCntThreashold
							&& totalLcsPats.get(pat).getCategories().size() <= categoryCntThreadshold) {
						dominantLcs.put(pat, totalLcsPats.get(pat));
					}
				}
			}
/*			for (int i = 0; i < 100; ++i) {
				System.out.println("the count of less then " + (i + 1) * 100
						+ " is  " + lcsCountAccess[i]);
			}*/
			/************ count the repeats of categories for every lcsPattern ********/
			addCategoryRepCnt();
			deleteContainedPattern();
			deleteUncertainPattern();
			serialization("dominantLcs", dominantLcs);
		}
		/************ cal label quality********************************************/
		
		//calLabelQuality();
		System.out.println("The count of dominantLcs is " + dominantLcs.size());
	}

	private void deleteContainedPattern() {
		// TODO Auto-generated method stub
		Iterator<Map.Entry<String, Pattern>> patIt =
				dominantLcs.entrySet().iterator();
		while(patIt.hasNext())
		{
			if(dominantLcs.size() % 1000 == 0)
				System.out.println(dominantLcs.size());
			Map.Entry<String, Pattern> domPat = patIt.next();
			for(String pat :  dominantLcs.keySet())
			{
				if( (dominantLcs.get(pat).getCateCnt() ==
					domPat.getValue().getCateCnt())
					&& (!pat.equals(domPat.getKey()))
					&& LCS.judgeLcsBelong(dominantLcs.get(pat).getTerms()
							,domPat.getValue().getTerms()))
				{
					patIt.remove();
					break;
				}
			}
		}
	}

	private void deleteUncertainPattern() {
		// TODO Auto-generated method stub
		Iterator<String> iter = dominantLcs.keySet().iterator();
		while(iter.hasNext())
		{
			String patName = iter.next();
			if(((double)dominantLcs.get(patName).getCategCount("未分类"))
					/dominantLcs.get(patName).getPatternCnt() >= 0.2)
			{
				iter.remove();
			}
		}
	}

	private void calLabelQuality() {
		// TODO Auto-generated method stub
		double everageCateCnt = 0;
		Integer[] cateNumQuality = new Integer[100];
		for(int i = 0; i < 100; ++i)
			cateNumQuality[i] = new Integer(0);
		for(String pat : dominantLcs.keySet())
		{
			everageCateCnt += dominantLcs.get(pat).getCategories().size();
			cateNumQuality[dominantLcs.get(pat).getCategories().size()]++;
		}
		System.out.println("lsc label everage quality: " + everageCateCnt + "/" + dominantLcs.size() + 
				" = " + everageCateCnt/dominantLcs.size());
		/*for(int cnt = 1; cnt < 10 ; ++cnt)
		{
			System.out.println("cateCnt: " + cnt + " ===> lcsCnt:" + cateNumQuality[cnt]);
		}*/
		
	}

	/*
	 * we can get all patterns sharing one or more character with the pattern we
	 * try to analyze "ATTETION": when calculate the whole map, you should set
	 * CountThreashold to 0
	 */
	private void getCh2PatsMap(Boolean loadCh2PatsMap, Set<String> patsSet) {
		// TODO Auto-generated method stub
		if (loadCh2PatsMap) {
			ch2Pats = (Map<String, Set<String>>) (unSerilazition("ch2Pats"));
		} else {
			for (String pat : patsSet) {
				addNewPattern2Chs(pat);
			}
			serialization("ch2Pats", ch2Pats);
		}
		System.out.println("characters count: " + ch2Pats.size());
	}

	private void addNewPattern2Chs(String pat) {
		// TODO Auto-generated method stub
		List<String> terms = patterns.get(pat).getTerms();
		for (String term : terms) {
			Set<String> patSet = null;
			if (ch2Pats.containsKey(term)) {
				patSet = ch2Pats.get(term);
			} else {
				patSet = new HashSet<String>();
				ch2Pats.put(term, patSet);
			}
			patSet.add(pat);
		}
	}

	private void calLCSperCh(boolean loadChLcsPattern) {
		// TODO Auto-generated method stub
		if (loadChLcsPattern) {
			chlcsPatterns = (Map<String, Map<String, Pattern>>) unSerilazition("chlcsPatterns");
		} else {
			int count = 0;
			for (String ch : ch2Pats.keySet()) {
				Set<String> pats = ch2Pats.get(ch);
				// 计算每一个汉字的一级公共lcs
				chlcsPatterns.put(ch, new HashMap<String, Pattern>());
				calLCSperCh(ch,
						(String[]) (pats.toArray(new String[pats.size()])),
						chlcsPatterns.get(ch));
				if (count % 1000 == 0) {
					System.out.println("1st-lcs cal: " + count
							+ "2ch has been pprocessed");
				}
				count++;
				// System.out.println("the count of 1st-lcs of "+ ch +"is " +
				// chlcsPatterns.get(ch).size());
			}
			serialization("chlcsPatterns", chlcsPatterns);
		}
	}

	/*private void calLCSperCh(String ch, String[] patsStrArray,
			Map<String, Pattern> lcsPatterns) {
		// TODO Auto-generated method stub
		int count = 0;
		for (int i = 0; i < patsStrArray.length; ++i) {
			String pat1 = patsStrArray[i];
			for (int j = i + 1; j < patsStrArray.length; ++j) {
				String pat2 = patsStrArray[j];
				LCS lcs = new LCS(pat1, pat2);
				lcs.getLength();
				// System.out.println("common str:" + lcs.getLcsRes());
				// 公共子串保存在lcsPatterns中。count字段为0，parPats集合中保存父亲节点
				// 子串必须包含ch,减小运算规模
				if (lcs.getLcsRes().contains(ch.toString())) {
					if (!lcsPatterns.containsKey(lcs.getLcsRes())) {
						lcsPatterns.put(lcs.getLcsRes(),
								new Pattern(lcs.getLcsRes(), 0));
						count++;
						if (count % 10000 == 0) {
							System.out.println(ch + ": " + count);
						}
					}
					lcsPatterns.get(lcs.getLcsRes()).addParPattern(
							patterns.get(pat1));
					lcsPatterns.get(lcs.getLcsRes()).addParPattern(
							patterns.get(pat2));

				}
				
				 * for (Integer t : lcs.getIndexList()) { System.out.print(t); }
				 
			}
		}

		
		 * for(String pat : lcsPatterns.keySet()) { System.out.print(ch + "==>"
		 * + pat +"parentParttern:"); for(Pattern parPat :
		 * lcsPatterns.get(pat).getParPats()) {
		 * System.out.print(parPat.getPatternName()+" | "); }
		 * System.out.println();
		 * 
		 * }
		 

	}*/
	private void calLCSperCh(String ch, String[] patsStrArray,
			Map<String, Pattern> lcsPatterns) {
		// TODO Auto-generated method stub
		int count = 0;
		for (int i = 0; i < patsStrArray.length; ++i) 
		{
			List<String> pat1 = patterns.get(patsStrArray[i]).getTerms();
			for (int j = i + 1; j < patsStrArray.length; ++j) {
				List<String> pat2 = patterns.get(patsStrArray[j]).getTerms();
				LCS lcs = new LCS(pat1, pat2);
				lcs.getLengthStr();
				// System.out.println("common str:" + lcs.getLcsRes());
				// 公共子串保存在lcsPatterns中。count字段为0，parPats集合中保存父亲节点
				// 子串必须包含ch,减小运算规模
				if (lcs.getLcsRes().contains(ch.toString())) {
					if (!lcsPatterns.containsKey(lcs.getLcsRes())) {
						lcsPatterns.put(lcs.getLcsRes(),
								new Pattern(lcs.getLcsRes(), 0, lcs.getLcsResList()));
						count++;
						if (count % 10000 == 0) {
							System.out.println(ch + ": " + count);
						}
					}
					lcsPatterns.get(lcs.getLcsRes()).addParPattern(
							patterns.get(patsStrArray[i]));
					lcsPatterns.get(lcs.getLcsRes()).addParPattern(
							patterns.get(patsStrArray[j]));

				}
				/*
				 * for (Integer t : lcs.getIndexList()) { System.out.print(t); }
				 */
			}
		}
		//recount lcsPatterns' count
		for(String pat1 : lcsPatterns.keySet())
		{
			for(String pat2 : lcsPatterns.keySet())
			{
				if(pat1.contains(pat2))
				{
					lcsPatterns.get(pat2).addParPattern(
							lcsPatterns.get(pat1).getParPats());
				}
			}
		}
		/*
		 * for(String pat : lcsPatterns.keySet()) { System.out.print(ch + "==>"
		 * + pat +"parentParttern:"); for(Pattern parPat :
		 * lcsPatterns.get(pat).getParPats()) {
		 * System.out.print(parPat.getPatternName()+" | "); }
		 * System.out.println();
		 * 
		 * }
		 */

	}
	protected void getLcsTotal(Boolean loadLcsTotal) {
		// TODO Auto-generated method stub
		if (loadLcsTotal) {
			totalLcsPats = (Map<String, Pattern>) (unSerilazition("totalLcsPats"));
		} else {
			for (String keyword : chlcsPatterns.keySet()) {
				System.out.println(keyword + " -- patterns count : " + chlcsPatterns.get(keyword).size());
				Map<String, Pattern> chLcsPats = chlcsPatterns.get(keyword);
				for (String pat : chLcsPats.keySet()) {
					if (!totalLcsPats.containsKey(pat))
						totalLcsPats.put(pat, chLcsPats.get(pat));
				}
			}
			serialization("totalLcsPats", totalLcsPats);
		}
		System.out.println("the total of lcsPatterns: " + totalLcsPats.size());
	}

	// ///////////////////////////////map//sort//////////////////////////////////////////////
	public static Map.Entry[] getSortedPatternTableByValue(Map map) {
		Set set = map.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set
				.size()]);
		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Long key1 = Long.valueOf(((Pattern) (((Map.Entry) arg0)
						.getValue())).getPatternCnt().toString());
				Long key2 = Long.valueOf(((Pattern) (((Map.Entry) arg1)
						.getValue())).getPatternCnt().toString());
				return key2.compareTo(key1);
			}
		});
		return entries;
	}

	public static Map.Entry[] getSortedCh2PatsTableByValue(Map map) {
		Set set = map.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set
				.size()]);
		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Long key1 = (long) ((Set<String>) ((Map.Entry) arg0).getValue())
						.size();
				Long key2 = (long) ((Set<String>) ((Map.Entry) arg1).getValue())
						.size();
				return key2.compareTo(key1);
			}
		});
		return entries;
	}

	public static Map.Entry[] getSortedDominantLcsTableByValue(Map map) {
		Set set = map.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set
				.size()]);
		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Long key1 = (long) ((String) ((Map.Entry) arg0).getKey())
						.length();
				Long key2 = (long) ((String) ((Map.Entry) arg1).getKey())
						.length();
				return key1.compareTo(key2);
			}
		});
		return entries;
	}

	// /////////////////////////////serialization//////////////////////////////////////////////
	private void serialization(String objName, Object obj) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(ConfigFileLoader.serDir + objName
					+ ".save")));
			oos.writeObject(obj.getClass().cast(obj));
			oos.flush();
			oos.close();
			System.out.println(objName + " serial complete");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Object unSerilazition(String objName) {
		// TODO Auto-generated method stub
		ObjectInputStream ois = null;
		Object obj = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(
					new File(ConfigFileLoader.serDir + objName
					+ ".save")));
			obj = ois.readObject();
			// obj = obj.getClass().cast(ois.readObject());
			ois.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;

	}

	// /////////////////////////data//processing///////////////////////////////////////////////
	public static String[] getRegularizedPattern(String description)
	{
		description = description.replace(".pdf", "");
		String partWithoutName = description;
		if (description.contains("关于"))
			partWithoutName = description
					.substring(description.indexOf("关于") + 2);
		else if (description.contains("："))
			partWithoutName = description
					.substring(description.indexOf("：") + 1);
		/************************************************************/
		partWithoutName = partWithoutName.replaceAll("（[\\S\\s]*）", "");
		partWithoutName = partWithoutName.replaceAll("ST[\\s\\S]{2}", "");
		partWithoutName = takeCpNameAway(partWithoutName, false);//if take away ,set true; if take it as A set false
		partWithoutName = partWithoutName.replaceAll("[a-zA-Z0-9]", "");
		partWithoutName = companyNameReplaceByNameEntity(partWithoutName);
		partWithoutName = partWithoutName.replaceAll("年度", "");
		partWithoutName = partWithoutName.replaceAll("年|月|日", "");
		partWithoutName = partWithoutName.replaceAll("[\\pP‘’“”]", "");
		//partWithoutName = partWithoutName.replaceAll("\\s*|\t|\r|\n", "");
		/*************************************************************/
/*		partWithoutName = partWithoutName.replaceAll("（[\\S\\s]*）", "");
		partWithoutName = takeCpNameAway(partWithoutName, true);//if take away ,set true; if take it as A set false
		partWithoutName = partWithoutName.replaceAll("ST[\\s\\S]{2}", "");
		partWithoutName = partWithoutName.replaceAll("[a-zA-Z0-9]", "");
		partWithoutName = partWithoutName.replaceAll("年度", "");
		partWithoutName = partWithoutName.replaceAll("年|月|日", "");
		partWithoutName = partWithoutName.replaceAll("[\\pP‘’“”]", "");
		partWithoutName = partWithoutName.replaceAll("\\s*|\t|\r|\n", "");*/
		
		String[] patsArr = partWithoutName.split("暨|的|并");
		return patsArr;
	}
	private static String companyNameReplaceByNameEntity(
			String partWithoutName) {
		// TODO Auto-generated method stub
		// String s2 = "I go to school at Stanford University, which is located in California.";
        List<String> splitStr = ConfigFileLoader.wdsp.splitSentence(partWithoutName,false);
        StringBuffer sb = new StringBuffer();
        for(String term : splitStr)
        	sb.append(term +" ");
        //System.out.println(sb.toString());
        partWithoutName = classifier.classifyWithInlineXML(sb.toString());
        partWithoutName = partWithoutName.replaceAll("<ORG>[\\s\\S]*?</ORG>", "");
        partWithoutName = partWithoutName.replaceAll("<GPE>[\\s\\S]*?</GPE>", "");
        partWithoutName = partWithoutName.replaceAll("<MISC>[\\s\\S]*?</MISC>", "");
        partWithoutName = partWithoutName.replaceAll("<PERSON>[\\s\\S]*?</PERSON>", "");
        partWithoutName = partWithoutName.replaceAll("<LOC>[\\s\\S]*?</LOC>", "");
        partWithoutName = partWithoutName.replaceAll("\\s*|\t|\r|\n", "");
        //System.out.println(partWithoutName);
		return partWithoutName;
	}

	protected static void getPatterns(FileInfo fInfo) {
		// TODO Auto-generated method stub
		String description = fInfo.getDescription();
		String[] patsArr = getRegularizedPattern(description);
		List<String> patsList = Arrays.asList(patsArr);

		for (String pat : patsList) {
			Pattern patn = patterns.get(pat);
			if (patn == null) {
				List<String> terms = ConfigFileLoader.wdsp.splitSentence(pat,false);
				patterns.put(pat, new Pattern(pat, 1, terms));
				
			} else {
				patn.inc();
			}
		}
		// set categories
		if (fInfo.getQACategoriesStrList() != null) {
			// System.out.println("has QAed: "+fInfo.getCategoriesStrList().size());
			for (String pat : patsList) {
				Pattern patn = patterns.get(pat);
				patn.getCategories().addAll(fInfo.getQACategoriesStrList());
				patn.incCateg(fInfo.getQACategoriesStrList());
			}
		}
	}

	private void filterByCountThreashold(int minCnt) {
		// TODO Auto-generated method stub
		Set<String> keys = patterns.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (patterns.get(key).getPatternCnt() < minCnt)
				iter.remove();
		}
		
	}

	private static String takeCpNameAway(String description, Boolean takeAway) {
		// TODO Auto-generated method stub
		for (String cpName : cpNameList) {
			if (description.contains(cpName)) {
				if(takeAway)
					description = description.replace(cpName, "");
				else
					description = description.replace(cpName, "");
			}
		}
		return description;
	}


	// /////////////////////print//res//////////////////////////////////////
	private void printRes() {
		// TODO Auto-generated method stub
		try {
			if(dealWithAll == false)
			bw = new BufferedWriter(new FileWriter("QAPatternRes.txt"));
			else
				bw = new BufferedWriter(new FileWriter("AllPatternRes.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		printTree(lcsTree, "");
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
