package com.datayes.algorithm.textmining.anouncement.textUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class htmlExtractor {
	public static String matchCharset(String content) {
		String chs = "utf-8";
		Pattern p = Pattern.compile("(?<=charset=)(.+)(?=\")");
		if(content != null)
		{
			Matcher m = p.matcher(content);
			if (m.find())
				return m.group();
		}
		return chs;
	}
	public String getHtmlContent(String htmlFilePath)
	{
		String content = "";
		try
		{
			BufferedReader fileInfoInput = new BufferedReader(
					new FileReader(htmlFilePath));
			String temp = null;
			
			while((temp = fileInfoInput.readLine())!=null)
			{
				content = content + temp;
			}
			String charset = matchCharset(content);
			if(content == null)
				content = "";
			content = new String(content.getBytes(), charset);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return content;
	}
}
