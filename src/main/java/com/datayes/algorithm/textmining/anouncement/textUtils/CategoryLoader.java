package com.datayes.algorithm.textmining.anouncement.textUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.rptClassification.model.Category;

public class CategoryLoader {
	public static List<Category> categories = new ArrayList<Category>();
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
	public static void main(String[] args) {
		try {
			ConfigFileLoader.initConf(args);
			CategoryLoader.initCategories(ConfigFileLoader.titleKeywordsPath);
			CategoryLoader.initKeywords(ConfigFileLoader.titleKeywordsPath, "title");
			for(Category cat : categories)
			{
				System.out.println(Arrays.asList(cat.getTitleKeywords()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
