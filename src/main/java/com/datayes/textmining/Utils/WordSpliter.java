package com.datayes.textmining.Utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
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

import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.ansj.dic.LearnTool;
import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.domain.TermNature;



public class WordSpliter {
	List<Term> termsList;
	Set<String> stopwordSet;
	private Pattern pattern;
	private Pattern toDelType;
	public WordSpliter() throws Exception {
		// TODO Auto-generated constructor stub
		stopwordSet = new TreeSet<String>();
		MyStaticValue.userLibrary=ConfigFileLoader.userDefDicFile;
		MyStaticValue.ambiguityLibrary = ConfigFileLoader.ambiguityFile;

		InputStream fstream = WordSpliter.class
				.getResourceAsStream("/stopwords.txt");
		BufferedReader brstop = new BufferedReader(new InputStreamReader(
				new DataInputStream(fstream), "UTF-8"));

		String stopword;
		while (brstop.ready()) {
			stopword = brstop.readLine();
			stopwordSet.add(stopword);
		}

	}
	public WordSpliter(String usrDefDicPath) throws Exception {
		// TODO Auto-generated constructor stub

		pattern = Pattern.compile("(^(\\pP|\\pS)$)|(^\\d$)");
		toDelType = Pattern.compile("^(null)");

		HashMap<String, String> stopwordsDic = new HashMap<String, String>();
		stopwordSet = new TreeSet<String>();
		String stopwordsFile = "stopwords.txt";
		if(usrDefDicPath!=null && !usrDefDicPath.equals("")){
			MyStaticValue.userLibrary=usrDefDicPath;
		}
		FileReader frstop;

		InputStream fstream = WordSpliter.class
				.getResourceAsStream("/stopwords.txt");
		BufferedReader brstop = new BufferedReader(new InputStreamReader(
				new DataInputStream(fstream), "UTF-8"));

		String stopword;
		while (brstop.ready()) {
			stopword = brstop.readLine();
			stopwordSet.add(stopword);
		}
	}
	public String segSentence(String content) throws IOException {

		StringBuffer contentBuffer = new StringBuffer();
		List<Term> termList = NlpAnalysis.parse(content);
		for(Term term : termList)
		{
			// term = udf.get(i);
			TermNature[] termNatures = term.getTermNatures().termNatures;
			String wordType = termNatures[0].nature.natureStr;
			String word = term.getRealName();

			if (word.isEmpty() || toDelType.matcher(wordType).find()
					|| pattern.matcher(word).find()
					|| stopwordSet.contains(word)) {
				continue;
			}
			contentBuffer.append(word + " ");
			// System.out.println(wordType + "->" + word);
		}

		return contentBuffer.toString();
	}
	public List<String> splitSentence(String str, Boolean nature)
	{
		List<String> dyTermSet = new ArrayList<String>();
		termsList = NlpAnalysis.parse(str);
		System.out.println(termsList);
		try {
			for(Term term : termsList)
			{
				String  word = null;
				if(!nature)
					word = term.getRealName();
				else
					word = term.getName();
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
/*		if (str.length() == 1) {
			res = false;
			return res;
		}*/
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
			LearnTool learnTool = new LearnTool() ;
			NlpAnalysis.parse("说过，社交软件也是打着沟通的平台，让无数寂寞男女有了肉体与精神的寄托。", learnTool) ;
	        NlpAnalysis.parse("其实可以打着这个需求点去运作的互联网公司不应只是社交类软件与可穿戴设备，还有携程网，去哪儿网等等，订房订酒店多好的寓意", learnTool) ;
	        NlpAnalysis.parse("限购成交量表现不一。",learnTool) ;
	        NlpAnalysis.parse("与未限购城市", learnTool);

	          //取得学习到的topn新词,返回前10个。这里如果设置为0则返回全部
	        System.out.println(learnTool.getTopTree(10));

	          //只取得词性为Nature.NR的新词
	        System.out.println(learnTool.getTopTree(10,Nature.NR));

			//List<String>  wds = aa.splitSentence("“限购”与“未限购”城市,成交量表现不一。");
			/*for(String wd : wds)
			{
				System.out.println(wd);
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
