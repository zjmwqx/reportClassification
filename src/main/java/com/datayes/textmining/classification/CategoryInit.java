package com.datayes.textmining.classification;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import com.datayes.textmining.rptClassification.model.Category;

public class CategoryInit {
	public static List<Category> categories = new ArrayList<Category>();
	public static List<BitSet> repelentList = new ArrayList<BitSet>();
	public static void initCategories(String KeywordsPath) throws Exception {
		// TODO Auto-generated method stub
		DataInputStream in = new DataInputStream(
				new FileInputStream(KeywordsPath));
		BufferedReader cateInput = new BufferedReader(
				new InputStreamReader(in, "utf-8"));
		String cateKeywords = null;
		//collReport = mydb.getDb().getCollection(collectionName);
		while ((cateKeywords = cateInput.readLine()) != null) {
			String cateInfo[] = cateKeywords.split(" ");
			Category newCate = new Category(cateInfo[0]);
			categories.add(newCate);
		}
	}
	public static void initKeywords(String KeywordsPath, String regName) throws Exception {
		// TODO Auto-generated method stub
		DataInputStream in = new DataInputStream(
				new FileInputStream(KeywordsPath));
		BufferedReader cateInput = new BufferedReader(
				new InputStreamReader(in, "utf-8"));
		
		String cateKeywords = null;
		int cateIndx = 0;
		//collReport = mydb.getDb().getCollection(collectionName);
		while ((cateKeywords = cateInput.readLine()) != null) {
			String cateInfo[] = cateKeywords.split(" ");
			String[] keywords = new String[cateInfo.length - 1];
			keywords = Arrays.copyOfRange(cateInfo, 1, cateInfo.length);
			categories.get(cateIndx).initKeywords(keywords, regName);
			cateIndx++;
		}
	}
	public static void initRepelentCate(String repelentListPath) throws Exception {
		// TODO Auto-generated method stub
		DataInputStream in = new DataInputStream(
				new FileInputStream(repelentListPath));
		BufferedReader cateInput = new BufferedReader(
				new InputStreamReader(in, "utf-8"));
		int cateNum = categories.size();
		String repelentCates;
		while ((repelentCates = cateInput.readLine()) != null) {
			String repelentInfo[] = repelentCates.split(" ");
			
			BitSet bs = new BitSet(cateNum);
			bs.set(0, cateNum);
			for(int i = 1; i < repelentInfo.length; ++i)
			{
				if(repelentInfo[i].trim().length()>0)
				{
					bs.clear(Category.getIndexByName(repelentInfo[i].trim()));
				}
			}
			repelentList.add(bs);
		}
	}
}
