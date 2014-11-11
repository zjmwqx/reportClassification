/*package com.datayes.textmining.Utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;

import com.datayes.algorithm.textmining.anouncement.summary.utility.WordSegment;



public class WordSpliter_backup {
	private Analysis udf;
	private Pattern pattern;
	private Pattern toDelType;
	Set<String> stopwordSet;
	public WordSpliter_backup() throws Exception {
		// TODO Auto-generated constructor stub

		pattern = Pattern.compile("(^(\\pP|\\pS)$)|(^\\d$)");
		toDelType = Pattern.compile("^(null)");

		HashMap<String, String> stopwordsDic = new HashMap<String, String>();
		stopwordSet = new TreeSet<String>();
		String stopwordsFile = "stopwords.txt";
		MyStaticValue.userLibrary=ConfigFileLoader.userDefDicFile;
		MyStaticValue.ambiguityLibrary = ConfigFileLoader.ambiguityFile;
		FileReader frstop;

		InputStream fstream = WordSegment.class
				.getResourceAsStream("/stopwords.txt");
		BufferedReader brstop = new BufferedReader(new InputStreamReader(
				new DataInputStream(fstream), "UTF-8"));

		String stopword;
		while (brstop.ready()) {
			stopword = brstop.readLine();
			stopwordSet.add(stopword);
		}

	}
	public List<String> splitSentence(String str)
	{
		List<String> dyTermSet = new ArrayList<String>();
		udf = new ToAnalysis(new StringReader(str));
		org.ansj.domain.Term term = null;
		try {
			while ((term = udf.next()) != null) {
				String word = term.getName();
				if (!isValid(word)) {
					continue;
				}
				dyTermSet.add(word);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dyTermSet;
	}
	private  static boolean isValid(String str) {
		boolean res = true;
		if (str.length() == 1) {
			res = false;
			return res;
		}
		if (StringUtility.containsDigit(str)) {
			res = false;
			return res;
		}
		
		if (StringUtility.isPunctuation(str)) {
			res = false;
			return res;
		}
		return res;
	}
	public static void main(String[] args) {
		
		try {
			ConfigFileLoader.initConf(args);
			WordSpliter_backup aa = new WordSpliter_backup();
			List<String>  wds = aa.splitSentence("某公司审计报告");
			for(String wd : wds)
			{
				System.out.println(wd);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
*/