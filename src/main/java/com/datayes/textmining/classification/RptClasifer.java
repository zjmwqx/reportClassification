package com.datayes.textmining.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.datayes.algorithm.textmining.anouncement.textUtils.ContentGetAgent;
import com.datayes.algorithm.textmining.patternFinding.PatternFinder;
import com.datayes.algorithm.textmining.patternFinding.PatternLocate;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.MongoDB;
import com.datayes.textmining.reportJobs.ReportJob;
import com.datayes.textmining.rptClassification.model.Category;
import com.datayes.textmining.rptClassification.model.FileInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class RptClasifer implements Serializable{

	private static final long serialVersionUID = 4538707253493003209L;
	
	public static PatternLocate ptLocate;
	
	public RptClasifer()
	{
		try {
			ptLocate = new PatternLocate();
			initCategory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void initCategory() throws Exception
	{
		CategoryInit.initCategories(ConfigFileLoader.titleKeywordsPath);
		if(ConfigFileLoader.contentKeywordsPath != null)
			CategoryInit.initKeywords(ConfigFileLoader.contentKeywordsPath, "content");
		if(ConfigFileLoader.titleKeywordsPath != null)
			CategoryInit.initKeywords(ConfigFileLoader.titleKeywordsPath, "title");
		if(ConfigFileLoader.blackListPath != null)
			CategoryInit.initKeywords(ConfigFileLoader.blackListPath, "black");
		if(ConfigFileLoader.repelentListPath != null)
			CategoryInit.initRepelentCate(ConfigFileLoader.repelentListPath);
	}
	
	public List<FileInfo> procFilesOrg(String stDate, String edDate) throws Exception {
		// TODO Auto-generated method stub
		List<FileInfo> unParsedFiles = null;
		RptOrgDataAgent orgRptGetter = new RptOrgDataAgent();
		unParsedFiles = orgRptGetter.getRptsByDateBatch(
				stDate, edDate);
		int count = 0;
		System.out.println(unParsedFiles.size());
		for(FileInfo fi : unParsedFiles)
		{
			if(count % 1000 == 0)
				System.out.println("has classify QA reports: " + count);
			tryClassify(fi);
			count++;
		}
		System.out.println("准确率: " + hitCount + "/"+ totalCount + " = " + hitCount/(double)totalCount);
		return unParsedFiles;
	}
	

	public List<FileInfo> procFilesQAed() throws Exception {
		// TODO Auto-generated method stub
		List<FileInfo> unParsedFiles = null;
		RptQADataAgent qaRptGetter = new RptQADataAgent();
		unParsedFiles = qaRptGetter.getFromQARes();
		int count = 0;
		System.out.println(unParsedFiles.size());
		for(FileInfo fi : unParsedFiles)
		{
			if(count % 1000 == 0)
				System.out.println("has classify QA reports: " + count);
			tryClassify(fi);
			count++;
		}
		System.out.println("准确率: " + hitCount + "/"+ totalCount + " = " + hitCount/(double)totalCount);
		return unParsedFiles;
	}
	
	public void tryClassify(FileInfo fileInfo) throws Exception{
		// TODO Auto-generated method stub
		//插入各种规则
		//标题规则
		System.out.println(fileInfo.getDescription());
		regulationClassify(fileInfo);
		//tryClassifyBasedOnContent(fileInfo);
		BitSet bst = repellentGroup(fileInfo.getCategoryAlgList());
		fileInfo.getCategoryAlgList().clear();

		int from  = -1;
		//得到通过互斥filter以后剩下的分类
		while((from = bst.nextSetBit(from+1)) != -1)
		{
			fileInfo.getCategoryAlgList().add(new Category(
					Category.getNameofcategories()[from]));
		}
/*		for(Category cate : fileInfo.getCategoriesList())
		{
			System.out.println(cate.getCategoryName());
		}*/
		if(fileInfo.getCategoryAlgList().size() == 0)
		{
			fileInfo.addCateAlg(new Category("未分类"));
		}
		System.out.println("alg:" + fileInfo.getCategoryAlgList());
		System.out.println("QA" + fileInfo.getQACategoriesStrList());
	}
	private static void tryClassifyBasedOnContent(FileInfo fileInfo) {
		// TODO Auto-generated method stub
		String content = getContent(fileInfo.getFull_path());
		//List<String> keyList = ContentAbstracter.subTitleAbstract(content);
		//System.out.println(keyList);
		Map<String, Double> titleCate = ptLocate.getCateResMap();
		ptLocate.parseWithTree(content);
		Map<String, Double> contentCate = ptLocate.getCateResMap();
		/*for(String cateName : titleCate.keySet())
		{
			if(contentCate.get(cateName).equals(obj)
			{
				
			}
		}
		if(resCat != null)
		{
			Map<String, Double> contentCate = ptLocate.getCateResMap();
			if(contentCate.get(resCat) != null 
					&& contentCate.get(resCat) >= 0.9)
			{
				fileInfo.addCateAlg(new Category(resCat));
			}
		}
		
		if(resCat != null)
		{
			for(String cateStr : titleCate.keySet())
			{
				if(titleCate.get(cateStr) >= 0.1)
				{
					Map<String,Double> contentCate = ptLocate.getCateResMap();
					if(contentCate.get(cateStr) != null 
							&& contentCate.get(cateStr) >= 0.7)
					{
						fileInfo.addCateAlg(new Category(resCat));
						double val = contentCate.get(cateStr)*titleCate.get(cateStr);
						if(val > 0.001)
						{
							
						}
					}
				}
			}
		}*/
	}
	

	private static String getRes(Map<String, Double> titleCate) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String getContent(String full_path) {
		// TODO Auto-generated method stub
		ContentGetAgent contentGet = new ContentGetAgent();
		contentGet.setFilePath(full_path);
		contentGet.setPageCnt(5);
		String content = null;
		if(ReportJob.Version.equals("staging"))
			try {
				content = contentGet.pdfFromS3(ConfigFileLoader.s3Con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
		{
			content = contentGet.pdfFromFile(full_path);
		}
		//去掉pdf自己添加的换行，得到准确的文字
		content = ContentGetAgent.deleteNewLine(content);
		return content;
	}
	public static String getRes()
	{
		Map<String, Double> cateRes = ptLocate.getCateResMap();
		//System.out.print("algRes: ");
		double maxPr = 0.0;
		String resCate = null;
		for(String cate : cateRes.keySet())
		{
			//System.out.print(cate+cateRes.get(cate)+"|");
			if(cateRes.get(cate) > maxPr)
			{
				maxPr = cateRes.get(cate);
				if(maxPr > 0.7)
					resCate = cate;
			}
		}
		return resCate;
	}
	
	private static void showRes(FileInfo fileInfo)
	{
		Map<String, Double> cateRes = ptLocate.getCateResMap();
		System.out.print("algRes: ");
		double maxPr = 0.0;
		String resCate = null;
		for(String cate : cateRes.keySet())
		{
			System.out.print(cate+cateRes.get(cate)+"|");
			if(cateRes.get(cate) > maxPr)
			{
				maxPr = cateRes.get(cate);
				resCate = cate;
			}
		}
		System.out.println();
		
		System.out.print("QARes: ");

		for(String cate : fileInfo.getQACategoriesStrList())
		{
			System.out.print(cate+"|");
		}
		System.out.println();
		if((maxPr >= 0.8) && (!resCate.equals("未分类")))
		{
			if(fileInfo.getQACategoriesStrList().size() >= 1)
			{
				totalCount ++;
				//System.out.println("*"+fileInfo.getCategoriesStrList() + " == " + resCate + "pro:" + maxPr);
				for(String cateName : fileInfo.getQACategoriesStrList())
				{
					if(cateName.equals(resCate))
					{
						hitCount ++;
						break;
					}
					else
					{
						System.out.println("*"+fileInfo.getQACategoriesStrList() + " == " + resCate + "pro:" + maxPr);
					}
				}
			}
		}
	}
	static int hitCount = 0;
	static int totalCount = 0;
	private static void titleClassifyBasedOnPatterns(FileInfo fileInfo)
	{
		//lookup in KeyWords lib
		String fileName = fileInfo.getDescription();
		
		ptLocate.parseWithTree(fileName);
		String resCat = getRes();
		fileInfo.setPatternList(ptLocate.getPatternList());
		
		if(resCat != null)
		{
			fileInfo.addCateAlg(new Category(resCat));
			fileInfo.setCredit(0.95);
		}
		//showRes(fileInfo);
	}
	
	private static void regulationClassify(FileInfo fileInfo) {
		// TODO Auto-generated method stub
		titleClassifyBasedOnPatterns(fileInfo);
		
		if(fileInfo.getCategoryAlgList().size() > 0)
		{
			
			/*for(Category cate : fileInfo.getCategoryAlgList())
			{
				System.out.println("keyword:" + cate.getCategoryName());
			}*/
/*			if(fileInfo.getCategoriesList().contains(new Category("未分类")))
			{
				Set<Category> cateList = new HashSet<Category>();
				cateList.add(new Category("未分类"));
				fileInfo.setCategoriesList(cateList);
			}
			return;*/
			//System.out.print("keywords");
		}
		else
		{
			//titleClassifyBasedOnKeywords(fileInfo);
			//System.out.print("pattern");
			fileInfo.setMethod("TitleKeyWords");
			fileInfo.setCredit(0.9);
		}
		
	}
	private static boolean inBlackList(String keyContent, Category curCategory, String keyword) {
		// TODO Auto-generated method stub
		for(String blkword : curCategory.getBlackList(keyword))
		{
			Pattern pat = Pattern.compile(blkword);
			Matcher match = pat.matcher(keyContent);
			if(match.find())
			{
				return true;
			}
		}
		return false;
	}
	private static void titleClassifyBasedOnKeywords(FileInfo fileInfo) {
		// TODO Auto-generated method stub
		String fileName = fileInfo.getDescription();
		Iterator<Category> cateIt =  CategoryInit.categories.iterator();
		while(cateIt.hasNext())
		{
			Category curCategory = cateIt.next();
			for(String keyword : curCategory.getTitleKeywords())
			{
				Pattern pat = Pattern.compile(keyword);
				Matcher match = pat.matcher(fileName);
				if(match.find())
				{
					if(!inBlackList(fileName, curCategory, keyword))
					{
						fileInfo.getCategoryAlgList().add(curCategory);
						fileInfo.getKeywords().add(keyword);//保存keyword
					}
				}
			}
		}
	}
	private static BitSet repellentGroup(Set<Category> cateSet) {
		// TODO Auto-generated method stub
		//对于所有的标签求与操作
		//返回与完以后的结果
		BitSet bst = new BitSet();
		for(Category cate : cateSet)
		{
			bst.set(Category.getIndexByName(cate.getCategoryName()));
		}
		for(Category cate : cateSet)
		{
			bst.and(CategoryInit.repelentList.get(Category.getIndexByName(cate.getCategoryName())));
		}
		return bst;		
	}
	public static void main(String[] args) throws Exception {
		ConfigFileLoader.initConf(args);
		RptClasifer rptCls = new RptClasifer();
		/*String content = rptCls.getContent("/datayes/pipeline/data/other_reports/sh/SH601789CN/SH601789CN_2014_2014-03-01_宁波建工第二届董事会第十二次会议决议公告.pdf");
		System.out.println(content);
		List<String> keyList = ContentAbstracter.subTitleAbstract(content);
		System.out.println(keyList);
		for(String str : keyList)
		{
			ptLocate.parseWithTree(str);
			String rs = rptCls.getRes();
			System.out.println("classify to:" + rs);
		}*/
		FileInfo fi = new FileInfo();
		fi.setFull_path("安居宝：国信证券股份有限公司关于公司使用部分超募资金设立电子控股子公司的核查意见.pdf");
		fi.setDescription("安居宝：国信证券股份有限公司关于公司使用部分超募资金设立电子控股子公司的核查意见.pdf");
		rptCls.tryClassify(fi);
	}
}
