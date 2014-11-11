/**   
 * @Title: ReadFile.java 
 * @Package com.datayes.algorithm.text.financial_news_summarize.utility 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author guangpeng.chen    
 * @date Sep 3, 2013 3:50:53 PM 
 * @version V1.0   
 */
package com.datayes.algorithm.textmining.anouncement.summary.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author guangpeng.chen
 * 
 */
public class ReadFile {

	static public String readFile(String fileName) {
		String contentString = "", str = "";
		FileReader fr;
		try {
			fr = new FileReader(fileName);

			BufferedReader reader = new BufferedReader(fr);

			int count = 0;
			while ((str = reader.readLine()) != null) {
				if (str.equals(""))
					continue;
				/*
				 * Pattern pattern = Pattern.compile("\\pP( |　)*$"); Matcher m =
				 * pattern.matcher(str);
				 */
				/* if (!m.find()) { */
				contentString = contentString + str;/*
													 * "&lt;p&gt;"+str +
													 * "。&lt;/p&gt;";
													 */
				/*
				 * } else { contentString = contentString +
				 * "&lt;p&gt;"+str+"&lt;/p&gt;"; }
				 */
/*				if (count++ % 100 == 0)
					System.out.println(count);*/
			}
			// System.out.println(contentString);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			contentString = "";
		}
		return contentString;
	}
}
