package com.datayes.textmining.classification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datayes.textmining.rptClassification.model.Category;

public class ContentAbstracter {
	public static List<String> subTitleAbstract(String content)
	{
		List<String> keyContent = new ArrayList<String>();
		try
		{
			//get key content
			Pattern pat = Pattern.compile("[一二三四五六七八九十1-9]、[\\s\\S]*?[\\n|。]");
			Matcher matcher = pat.matcher(content);
			
			while(matcher.find())
			{
				//System.out.println(matcher.group());
				String hitString =  matcher.group();
				keyContent.add(hitString);
			}
			pat = Pattern.compile("审议[\\s\\S]*?[\\n|。]");
			matcher = pat.matcher(content);
			while(matcher.find())
			{
				String hitString = matcher.group();
				keyContent.add(hitString);
			}
			//System.out.println("key:"+keyContent);
			//classify based on content
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return keyContent;
	}
	public static void main(String[] args) {
		String content = "adasdasdasd啊是打算打算\n二、公司审议通过啊是打算大大是打算打算打；\nasdasda asdasdasd as1、啊是打算打算\n打算审议asdasdas\n";
		System.out.println(subTitleAbstract(content));
	}
}
