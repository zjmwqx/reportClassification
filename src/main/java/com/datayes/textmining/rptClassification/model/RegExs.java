package com.datayes.textmining.rptClassification.model;
/*package com.datayes.algorithm.textmining.anouncement.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*//**
 * RegExs.java
 * com.datayes.algorithm.textmining.anouncement.model
 * 工程：rptClassificationKeyWords
 * 功能： 类：正则式
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午2:09:47
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*//*
public class RegExs {
	private static Map<String, List<String>> regExLib = 
			new HashMap<String, List<String>>();
	
	public RegExs() {
		// TODO Auto-generated constructor stub
	}
	public static void init()
	{
		try {
			BufferedReader regExIn = new BufferedReader(
					new FileReader("regExLib"));
			String cateRegExStr = null;
			while((cateRegExStr = regExIn.readLine()) != null)
			{
				String[] cateReg = cateRegExStr.split(":| ");
				List<String> regExs = new ArrayList<String>();
				for(int i = 1; i < cateReg.length; ++i)
					regExs.add(cateReg[i]);
				regExLib.put(cateReg[0].trim(), regExs);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Map<String, List<String>> getRegExLib() {
		return regExLib;
	}
}
*/