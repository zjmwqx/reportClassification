/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Nick Lothian. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        developers of Classifier4J (http://classifier4j.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "Classifier4J" must not be used to endorse or promote 
 *    products derived from this software without prior written 
 *    permission. For written permission, please contact   
 *    http://sourceforge.net/users/nicklothian/.
 *
 * 5. Products derived from this software may not be called 
 *    "Classifier4J", nor may "Classifier4J" appear in their names 
 *    without prior written permission. For written permission, please 
 *    contact http://sourceforge.net/users/nicklothian/.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package com.datayes.algorithm.textmining.anouncement.summary.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datayes.textmining.Utils.WordSpliter;

/**
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class Utilities {

	/**
	 * @Description: ${Statistic Chinese words number}
	 */
	public static int StaChWord(String str) {
		int count = 0;
		String regCh = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regCh);
		Matcher m = p.matcher(str);
		while (m.find()) {
			for (int i = 0; i <= m.groupCount(); i++)
				count = count + 1;
		}
		return count;
	}

	public static Map getWordFrequency(String input) {
		return getWordFrequency(input, false);
	}

	public static Map getWordFrequency(String input, boolean caseSensitive) {
		return getWordFrequency(input, caseSensitive, new DefaultTokenizer(), new DefaultStopWordsProvider());
	}

	/**
	 * Get a Map of words and Integer representing the number of each word
	 * 
	 * @param input
	 *            The String to get the word frequency of
	 * @param caseSensitive
	 *            true if words should be treated as separate if they have
	 *            different case
	 * @param tokenizer
	 *            a junit.framework.TestCase#run()
	 * @param stopWordsProvider
	 * @return
	 */
	public static Map getWordFrequency(String input, boolean caseSensitive, ITokenizer tokenizer, IStopWordProvider stopWordsProvider) {
		String convertedInput = input;
		if (!caseSensitive) {
			convertedInput = input.toLowerCase();
		}

		// tokenize into an array of words
		String[] words = tokenizer.tokenize(convertedInput);
		Arrays.sort(words);

		String[] uniqueWords = getUniqueWords(words);
		Map result = new HashMap();

		for (int i = 0; i < uniqueWords.length; i++) {
			if (stopWordsProvider == null) {
				// no stop word provider, so add all words
				result.put(uniqueWords[i], new Integer(countWords(uniqueWords[i], words)));
			} else if (isWord(uniqueWords[i]) && !stopWordsProvider.isStopWord(uniqueWords[i])) {
				// add only words that are not stop words
				result.put(uniqueWords[i], new Integer(countWords(uniqueWords[i], words)));
			}
		}

		return result;
	}

	private static boolean isWord(String word) {
		if (word != null && !word.trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Find all unique words in an array of words
	 * 
	 * @param input
	 *            an array of Strings
	 * @return an array of all unique strings. Order is not guarenteed
	 */
	public static String[] getUniqueWords(String[] input) {
		if (input == null) {
			return new String[0];
		} else {
			Set result = new TreeSet();
			for (int i = 0; i < input.length; i++) {
				result.add(input[i]);
			}
			return (String[]) result.toArray(new String[result.size()]);
		}
	}

	/**
	 * Count how many times a word appears in an array of words
	 * 
	 * @param word
	 *            The word to count
	 * @param words
	 *            non-null array of words
	 */
	public static int countWords(String word, String[] words) {
		// find the index of one of the items in the array.
		// From the JDK docs on binarySearch:
		// If the array contains multiple elements equal to the specified
		// object, there is no guarantee which one will be found.
		int itemIndex = Arrays.binarySearch(words, word);

		// iterate backwards until we find the first match
		if (itemIndex > 0) {
			while (itemIndex > 0 && words[itemIndex].equals(word)) {
				itemIndex--;
			}
		}

		// now itemIndex is one item before the start of the words
		int count = 0;
		while (itemIndex < words.length && itemIndex >= 0) {
			if (words[itemIndex].equals(word)) {
				count++;
			}

			itemIndex++;
			if (itemIndex < words.length) {
				if (!words[itemIndex].equals(word)) {
					break;
				}
			}
		}

		return count;
	}

	public static String highlightResult(Iterator<String> importSentences, String content) {
		String toReplaceString;
		int index;
		while (importSentences.hasNext()) {
			toReplaceString = importSentences.next();
			// index = originalText.indexOf(toReplaceString);
			content = content.replace(toReplaceString, "&lt;mark&gt;" + toReplaceString + "&lt;/mark&gt;");
			// originalText = originalText.replaceFirst(toReplaceString, );
		}
		return content;
	}

	public static boolean highlightResult(Iterator<Sentence> importSentences, SummaryStruct summaryStruct) {
		String toReplaceString;
		int indexBegin = 0, indexEnd = 0;
		StringBuffer strbuffer = new StringBuffer();

		while (importSentences.hasNext()) {
			Sentence currentSen = importSentences.next();
			toReplaceString = currentSen.originalContent;

			indexBegin = currentSen.beginIndex;
			if (indexBegin < 0)
				return false;

			if (indexBegin > 0) {
				strbuffer.append(summaryStruct.getOriginalText().subSequence(indexEnd, indexBegin));
			}
			indexEnd = currentSen.endIndex;
			summaryStruct.getHigligPosList().add(new Pair<Integer, Integer>(indexBegin, indexEnd));
			strbuffer.append("&lt;mark&gt;" + summaryStruct.getOriginalText().subSequence(indexBegin, indexEnd) + "&lt;/mark&gt;");
		}
		if (indexEnd < summaryStruct.getOriginalText().length()) {
			strbuffer.append(summaryStruct.getOriginalText().substring(indexEnd));
		}

		summaryStruct.setTextWithHigLight(strbuffer.toString());
		return true;

	}

	// set weight for sentences in the firs paragraph
	public static Map<String, Double> getSenWeightByPos(String inputStr) {

		if (inputStr == null || inputStr.equals("")) {
			return null;
		}

		Map<String, Double> senWeightMap = new HashMap<String, Double>();
		String firstPar = null;
		int firstParEndIndex = inputStr.indexOf("&lt;/p&gt;");
		if (firstParEndIndex > 0) {
			firstPar = inputStr.substring(9, firstParEndIndex);
		} else {
			firstParEndIndex = inputStr.indexOf("\n");
			if (firstParEndIndex > 0) {
				firstPar = inputStr.substring(0, firstParEndIndex);
			} else {
				firstPar = inputStr;
			}
		}
		// System.out.println("firstPar: " + firstPar);
		List<String> sentences = getSentences(firstPar);
		// System.out.println(sentences.size());
		Double intervalWeight = 1.0 / sentences.size();
		Double thisSenWeight = 2.0;
		for (int i = 0; i < sentences.size(); i++) {
			senWeightMap.put(sentences.get(i), thisSenWeight);
			thisSenWeight = thisSenWeight - intervalWeight;
		}
		return senWeightMap;
	}

	// 模糊匹配来确定关键词列表
	public static List<KeyWord> getKeyWordList(SummaryStruct summaryStruct, List<Pair<String, Integer>> wordList, boolean limitKeyWord,WordSpliter wordSegment) throws Exception {

		List<KeyWord> keyWordList = new ArrayList<KeyWord>();
		String segmentedTitleString = wordSegment.segSentence(summaryStruct.getTitle());
		Map titleWordMap = Utilities.getWordFrequency(segmentedTitleString, false, new DefaultTokenizer(2), null);

		for (int i = 0; i < wordList.size(); i++) {
			Iterator itTitleIterator = titleWordMap.entrySet().iterator();
			while (itTitleIterator.hasNext()) {
				Map.Entry entry2 = (Map.Entry) itTitleIterator.next();
				if (fuzzyMatch((String) wordList.get(i).first, (String) entry2.getKey())) {
					int j = 0;
					for (; j < keyWordList.size(); j++) {
						if (fuzzyMatch((String) wordList.get(i).first, keyWordList.get(j).getName())) {
							keyWordList.get(j).incFrequence(wordList.get(i).second);
							break;
						}
					}
					if (j == keyWordList.size()) {
						keyWordList.add(new KeyWord((String) entry2.getKey(), wordList.get(i).second, true));
					}
					break;
				}
			}
		}

		if (!limitKeyWord) {
			for (int i = 0; i < wordList.size(); i++) {
				int j = 0;
				for (; j < keyWordList.size(); j++) {
					if (keyWordList.get(j).getIsInTitle() && Utilities.fuzzyMatch(wordList.get(i).first, keyWordList.get(j).getName())) {
						break;
					}
				}
				if (j == keyWordList.size()) {
					keyWordList.add(new KeyWord(wordList.get(i).first, wordList.get(i).second, false));
				}
			}
		}

		if (limitKeyWord == true && keyWordList.size() < 5) {
			for (int i = 0; i < wordList.size(); i++) {
				int j = 0;
				for (; j < keyWordList.size(); j++) {
					if (keyWordList.get(j).getIsInTitle() && Utilities.fuzzyMatch(wordList.get(i).first, keyWordList.get(j).getName())) {
						break;
					}
				}
				if (j == keyWordList.size()) {
					keyWordList.add(new KeyWord(wordList.get(i).first, wordList.get(i).second, false));
				}

				if (keyWordList.size() >= 5) {
					break;
				}
			}
		}

		Collections.sort(keyWordList, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				int frequence0 = (Integer) ((KeyWord) arg0).getFrequence();
				int frequence1 = (Integer) ((KeyWord) arg1).getFrequence();
				return frequence1 - frequence0;
			}
		});

		for (int i = 0; i < keyWordList.size(); i++) {
			summaryStruct.getKeywordList().add(new Pair<String, Integer>(keyWordList.get(i).getName(), keyWordList.get(i).getFrequence()));
		}

		return keyWordList;
	}

	// get word list ordered by its frequence
	public static List<Pair<String, Integer>> getWordScoreList(WordSpliter wordSegment, String content, String title) throws IOException {
		List<Pair<String, Integer>> wordScoreList = new ArrayList<Pair<String, Integer>>();
		String segmentedTitleString = "";
		Map titleWordMap = null;

		String segmentedContent = wordSegment.segSentence(content.replaceAll("(&lt;/p&gt;)|(&lt;p&gt;)", ""));
		Map wordFrequencies = Utilities.getWordFrequency(segmentedContent, false, new DefaultTokenizer(2), null);

		if (title != null && !title.equals("")) {
			segmentedTitleString = wordSegment.segSentence(title);
			titleWordMap = Utilities.getWordFrequency(segmentedTitleString, false, new DefaultTokenizer(2), null);
		}
		// Modify the weights of words in title
		Iterator it1 = wordFrequencies.entrySet().iterator();
		while (it1.hasNext()) {
			Map.Entry entry = (Map.Entry) it1.next();
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (title != null && !title.equals("")) {
				Iterator itTitleIterator = titleWordMap.entrySet().iterator();
				while (itTitleIterator.hasNext()) {
					Map.Entry entry2 = (Map.Entry) itTitleIterator.next();
					if (fuzzyMatch((String) key, (String) entry2.getKey())) {
						value = ((Integer) value) * 10;
						break;
					}
				}
			}
			wordScoreList.add(new Pair<String, Integer>((String) key, (Integer) value));
		}

		Collections.sort(wordScoreList, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				int frequence0 = (Integer) ((Pair<String, Integer>) arg0).second;
				int frequence1 = (Integer) ((Pair<String, Integer>) arg1).second;
				return frequence1 - frequence0;
			}
		});

		return wordScoreList;
	}

	/* 模糊匹配两个字符串 */
	public static boolean fuzzyMatch(String longString, String shortString) {
		if (longString == null || shortString == null || shortString.equals("") || longString.length() < shortString.length()) {
			return false;
		}

		int longStrIndex = 0;
		int shortStrIndex = 0;

		while (longStrIndex < longString.length() && shortStrIndex < shortString.length()) {
			if (longString.charAt(longStrIndex) == shortString.charAt(shortStrIndex)) {
				longStrIndex++;
				shortStrIndex++;
			} else {
				longStrIndex++;
			}
		}

		if (shortStrIndex == shortString.length()) {
			return true;
		}
		return false;
	}

	public static void conSummaryBySentences(List<Sentence> sentences, SummaryStruct summaryStruct) {
		Collections.sort(sentences, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Sentence pair0 = (Sentence) arg0;
				Sentence pair1 = (Sentence) arg1;
				return pair0.beginIndex - pair1.beginIndex;
			}
		});

		StringBuffer summary = new StringBuffer("");
		Iterator<Sentence> it = sentences.iterator();

		Pattern sentenceEndPattern = Pattern.compile("(“|”|\"|,|，|;|；)( |　)*$");
		Matcher matcher;

		while (it.hasNext()) {
			String sentence = (String) it.next().originalContent;
			matcher = sentenceEndPattern.matcher(sentence);
			if (matcher.find()) {
				sentence = matcher.replaceFirst("");
			}
			summary.append(sentence.replaceAll(" |　", ""));
			int i = sentence.length() - 1;
			for (; i >= 0; i--) {
				if (sentence.substring(i, i + 1).equals("“")) {
					summary.append("”。");
					break;
				} else if (sentence.substring(i, i + 1).equals("”")) {
					summary.append("。");
					break;
				}
			}
			if (i < 0) {
				summary.append("。");
			}
		}
		summaryStruct.setSummary(summary.toString());// .replaceAll("“|”|\"",
														// "");
	}

	public static String[] getSentences(String input, boolean old) {
		if (input == null) {
			return null;
		} else {
			// split on a ".", a "!", a "?" followed by a space or EOL
			String[] sentences = input.split("(“|”)*((\\.(\\s|\\z))|!|\\?|;|。|？|！|；|\n|(&lt;p&gt;)|(&lt;/p&gt;))+(“|”)*");
			List<String> resultList = new ArrayList<String>();
			// System.out.println(sentences.length);

			for (int i = 0; i < sentences.length; i++) {
				if (sentences[i].equals("")) {
					continue;
				}
				resultList.add(sentences[i]);
			}
			String[] resultArray = new String[resultList.size()];
			for (int i = 0; i < resultList.size(); i++) {
				resultArray[i] = resultList.get(i);
			}
			return resultArray;
		}
	}

	/**
	 * 
	 * @param input
	 *            a String which may contain many sentences
	 * @return an array of Strings, each element containing a sentence
	 */
	public static List<String> getSentences(String input) {
		if (input == null) {
			return null;
		}

		//删除副标题（无标点符合结尾的短小句子）
		input = deleteSubtitle(input);
		
		String[] sentences = input.split("((\\.(\\s|\\z))|!|\\?|。|？|！|\n|(&lt;p&gt;)|(&lt;/p&gt;))");
		List<String> resultList = new ArrayList<String>();

		for (int i = 0; i < sentences.length; i++) {
			if (sentences[i].equals("")) {
				continue;
			}
			resultList.add(sentences[i].trim());
		}
		splitSenByDoubleQuote(resultList);
		splitSenBySemicolon(resultList);
		optimizeSen(resultList);
		return resultList;
	}

	private static void splitSenByDoubleQuote(List<String> sentences) {
		for (int i = 0; i < sentences.size(); i++) {
			String currString = sentences.get(i);

			for (int j = sentences.get(i).length() - 1; j >= 0; j--) {
				if (currString.substring(j, j + 1).equals("”")) {
					break;
				}
				if (currString.substring(j, j + 1).equals("“")) {
					if (i + 1 < sentences.size() && (!sentences.get(i + 1).startsWith("”"))) {
						sentences.set(i, currString.substring(0, j));
						sentences.add(i + 1, currString.substring(j + 1));
						i++;
					}
					break;
				}
			}
		}
	}

	private static void splitSenBySemicolon(List<String> sentences) {

		for (int i = 0; i < sentences.size(); i++) {
			String currString = sentences.get(i);
			String[] parts = currString.split(";|；");
			if (parts.length >= 2) {
				sentences.remove(i);
				int index = parts[0].length() - 1;
				boolean flag = false;
				String tmpString = "";
				while (index > 0) {
					tmpString = parts[0].substring(index, index + 1);
					if (tmpString.equals(",") || tmpString.equals("，")) {
						flag = true;
						String[] parts2 = parts[0].split(":|：");
						for (int j = 1; j < parts2.length; j++) {
							sentences.add(i, parts2[j]);
							i++;
						}
						break;
					}

					if (tmpString.equals(":") || tmpString.equals("：")) {
						break;
					}
					index--;
				}

				if (flag) {
					for (int j = 1; j < parts.length; j++) {
						sentences.add(i++, parts[j]);
					}
				} else {
					sentences.add(i, currString);
				}
			}
		}
	}

	public static void optimizeSen(List<String> sentences) {
		Pattern beginPattern1 = Pattern.compile("^(　| )*(一|二|三|四|五|六|七|八|九)(是|要|(\\pP|\\pS))");
		//Pattern beginPattern2 = Pattern.compile("^(　| )*(\\d)+(\\)|\\.|、|，|,)");
		Pattern beginPattern3 = Pattern.compile("^(　| )*其(一|二|三|四|五|六|七|八|九)(\\pP|\\pS)");
		Pattern beginPattern4 = Pattern.compile("^(　| )*((首先)|(其次)|(另外)|(此外))(\\pP|\\pS)");
		Pattern beginPattern5 = Pattern.compile("^(　| )*(\\pP|\\pS)(一|二|三|四|五|六|七|八|九)(\\pP|\\pS){1,2}");
		Pattern beginPattern6 = Pattern.compile("^(　| )*(\\pP|\\pS)*(\\d+)(\\pP|\\pS)+");
		//开头的特殊字符
		Pattern beginPattern7 = Pattern.compile("^(　| )*[^\\u4e00-\\u9fa5（(《“‘'\\w]+");
		
		//***年××月××日
		Pattern beginPattern8 = Pattern.compile("^(　| )*.{4}年.{1,2}月.{1,3}日(　| )*$"); 

		Pattern endPattern1 = Pattern.compile("(:|：)( |　)*$");
		//结尾的特殊字符
		Pattern endPattern2 = Pattern.compile("[^\\u4e00-\\u9fa5）)》”’'%\\w]+(　| )*$");

		Matcher matcherB1, matcherB2, matcherB3, matcherB4, matcherB5, matcherB6, matcherB7, matcherB8, matcherE1, matcherE2;
		String currString;
		for (int i = 0; i < sentences.size(); i++) {
			currString = sentences.get(i);
			matcherB1 = beginPattern1.matcher(currString);
			//matcher2 = beginPattern2.matcher(sentences.get(i));
			matcherB3 = beginPattern3.matcher(currString);
			matcherB4 = beginPattern4.matcher(currString);
			matcherB5 = beginPattern5.matcher(currString);
			matcherB6 = beginPattern6.matcher(currString);
			matcherB7 = beginPattern7.matcher(currString);
			matcherB8 = beginPattern8.matcher(currString);
			matcherE1 = endPattern1.matcher(currString);

			if (matcherE1.find()) {
				sentences.remove(i);
				i--;
				continue;
			}else if (currString.indexOf("【") > -1 || currString.indexOf("】") > -1) {
				sentences.remove(i);
				i--;
				continue;
			}else if (currString.startsWith("(") && currString.endsWith(")")) {
				sentences.remove(i);
				i--;
				continue;
			}else if (Utilities.StaChWord(currString) * 3 < currString.length()) { 				//删除表格信息
				sentences.remove(i);
				i--;
				continue;
			}else if(matcherB8.find()){
				sentences.remove(i);
				i--;
				continue;
			}
			
			if (matcherB1.find()) {
				sentences.set(i, matcherB1.replaceFirst(""));
			} /*else if (matcher2.find()) {
				sentences.set(i, matcher2.replaceFirst(""));
			}*/ else if (matcherB3.find()) {
				sentences.set(i, matcherB3.replaceFirst(""));
			} else if (matcherB4.find()) {
				sentences.set(i, matcherB4.replaceFirst(""));
			} else if (matcherB5.find()) {
				sentences.set(i, matcherB5.replaceFirst(""));
			} else if (matcherB6.find()) {
				sentences.set(i, matcherB6.replaceFirst(""));
			} else if (matcherB7.find()) {
				sentences.set(i, matcherB7.replaceFirst(""));
			}
			
			currString = sentences.get(i);
			matcherE2 = endPattern2.matcher(currString);
			if(matcherE2.find()){
				sentences.set(i, matcherE2.replaceFirst(""));
			}
			
		}
	}
	
	public static String deleteSubtitle(String input){
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]$");
		StringBuffer result = new StringBuffer();
		String [] paragraphs = input.split("\n");
		String currentString = "";
		for(int i=0;i<paragraphs.length;i++){
			currentString = paragraphs[i];
			Matcher matcher = pattern.matcher(currentString.replaceAll("　| ", ""));
			if(matcher.find()&&currentString.length()<20){
				continue;
			}else {
				result.append(currentString+"\n");
			}
		}
		return result.toString();
	}
}
