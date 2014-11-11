/**   
 * @Title: DatayesSummarizer_v0.java 
 * @Package com.datayes.algorithm.text.financial_news_summarize.summarizer 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author guangpeng.chen    
 * @date Oct 11, 2013 5:29:44 PM 
 * @version V1.0   
 */
package com.datayes.algorithm.textmining.anouncement.summary.summarizer;

/**
 * @author guangpeng
 *
 */
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.datayes.algorithm.textmining.anouncement.summary.utility.*;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.WordSpliter;

/**
 * @author guangpeng
 * 
 */
public class DatayesSummarizer_v0 implements ISummarizer,Serializable {

	private WordSpliter wordSegment;
	private static Logger logger = null;

	public DatayesSummarizer_v0(String userDefDicFile) throws Exception {
		logger = ConfigFileLoader.logger;
		
		wordSegment = new WordSpliter(userDefDicFile);
	}

	private List<Pair<Sentence, Double>> initialSenWithScoreList(
			SummaryStruct summaryStruct) throws IOException {
		List<Pair<Sentence, Double>> sentencesWithScoreList = new ArrayList<Pair<Sentence, Double>>();
		List<List<String>> workSentencesList = new ArrayList<List<String>>();
		List<Integer> senLenList = new ArrayList<Integer>();
		List<String> actualSentences = Utilities.getSentences(summaryStruct
				.getOriginalText());
		
		if(actualSentences.size() == 0){
			return null;
		}

		// 将句子分词
		for (int i = 0; i < actualSentences.size(); i++) {
			String[] segmentedSentence = wordSegment.segSentence(
					actualSentences.get(i).toLowerCase()).split(" ");
			List<String> sentence = new ArrayList<String>();
			for (int j = 0; j < segmentedSentence.length; j++) {
				sentence.add(segmentedSentence[j]);
			}
			workSentencesList.add(sentence);
			senLenList.add(sentence.size());
		}

		Collections.sort(senLenList);

		Integer limitMinSenLen = (int) Math.round(senLenList.get(senLenList
				.size() / 2) * 0.2);

		int beginIndex = 0, endIndex = 0;

		for (int i = 0; i < actualSentences.size(); i++) {

			int currentSentenceLen = workSentencesList.get(i).size();
			if (currentSentenceLen <= 2 || currentSentenceLen < limitMinSenLen
					|| Utilities.StaChWord(actualSentences.get(i)) < 6)
				continue;

			beginIndex = summaryStruct.getOriginalText().indexOf(
					actualSentences.get(i), endIndex);
			endIndex = beginIndex + actualSentences.get(i).length();
			Sentence currentSen = new Sentence(actualSentences.get(i),
					workSentencesList.get(i), beginIndex, endIndex);
			sentencesWithScoreList.add(new Pair<Sentence, Double>(currentSen,
					0.0));
		}

		return sentencesWithScoreList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.datayes.algorithm.text.financial_news_summarize.summarizer.ISummarizer
	 * #getSummary(com.datayes.algorithm.text.financial_news_summarize.utility.
	 * SummaryStruct)
	 */
	public int getSummary(SummaryStruct summaryStruct) {

		try {
			if (summaryStruct.getOriginalText() == null
					|| summaryStruct.getOriginalText().length() == 0) {
				return 0;
			}

			List<Sentence> outputSentences = new ArrayList<Sentence>();

			// set weight for sentences in the firs paragraph
			Map<String, Double> sentenceWeightMap = Utilities
					.getSenWeightByPos(summaryStruct.getOriginalText());

			// get word list ordered by its frequence
			List<Pair<String, Integer>> wordList = Utilities.getWordScoreList(
					wordSegment, summaryStruct.getOriginalText(),summaryStruct.getTitle());
			summaryStruct
					.setSummarizerName("DataYesSummarizer limiting min sentence length");
			summaryStruct.setWordsNum(Utilities.StaChWord(summaryStruct
					.getOriginalText()));

			if (wordList.size() <= summaryStruct.getKeywordNum()) {
				return 0;
			}

			// 模糊匹配来确定关键词列表,同时为summaryStruct.KeywordList初始化
			List<KeyWord> keywordList = Utilities.getKeyWordList(summaryStruct,
					wordList, false,wordSegment);

			List<Pair<Sentence, Double>> sentencesWithScoreList = initialSenWithScoreList(summaryStruct);
			
			//初始化带有分值的句子列表失败
			if(sentencesWithScoreList==null ||sentencesWithScoreList.size()==0){
				return 0;
			}

			int currentSummWordsNum = 0;
			int outputMaxWordsNum = (int) Math.round(summaryStruct
					.getWordsNum() * 0.15);

			if (outputMaxWordsNum < summaryStruct.getMinOutputWordsNum()) {
				outputMaxWordsNum = summaryStruct.getMinOutputWordsNum();
			}

			if (outputMaxWordsNum > summaryStruct.getMaxOutputWordsNum()) {
				outputMaxWordsNum = summaryStruct.getMaxOutputWordsNum();
			}

			// extract sentence
			while (sentencesWithScoreList.size() > 0) {

				// Scoring for the sentence
				for (int i = 0; i < sentencesWithScoreList.size(); i++) {
					int totalFrequence = 0;
					for (int j = 0; j < keywordList.size(); j++) {
						for (int k = 0; k < sentencesWithScoreList.get(i).first.segContentList
								.size(); k++) {
							String currentWord = sentencesWithScoreList.get(i).first.segContentList
									.get(k);
							Boolean inTitle = keywordList.get(j).getIsInTitle();
							String keyWordName = keywordList.get(j).getName();

							if ((inTitle && Utilities.fuzzyMatch(currentWord,
									keyWordName))
									|| (!inTitle && currentWord
											.equals(keyWordName))) {
								totalFrequence = totalFrequence
										+ keywordList.get(j).getFrequence();
								break;
							}
						}
					}
					sentencesWithScoreList.get(i).second = ((double) totalFrequence)
							/ sentencesWithScoreList.get(i).first.segContentList
									.size();

					// System.out.println("original Score: "+sentencesWithScoreList.get(i).second);
					// considering sentence pos
					Double sentenceWeight = sentenceWeightMap
							.get(sentencesWithScoreList.get(i).first.originalContent);
					if (sentenceWeight != null) {
						sentencesWithScoreList.get(i).second = sentencesWithScoreList
								.get(i).second * sentenceWeight;
					}

				}

				Collections.sort(sentencesWithScoreList, new Comparator() {
					public int compare(Object arg0, Object arg1) {
						Pair<Sentence, Double> pair0 = (Pair<Sentence, Double>) arg0;
						Pair<Sentence, Double> pair1 = (Pair<Sentence, Double>) arg1;
						Double tmpResult = pair1.second - pair0.second;

						if (tmpResult > 0)
							return 1;
						else if (tmpResult < 0)
							return -1;
						else {
							int indexOfSentence1 = pair0.first.beginIndex;
							int indexOfSentence2 = pair0.first.beginIndex;
							return indexOfSentence1 - indexOfSentence2;
						}
					}
				});

				outputSentences.add(sentencesWithScoreList.get(0).first);

				currentSummWordsNum = currentSummWordsNum
						+ Utilities
								.StaChWord(sentencesWithScoreList.get(0).first.originalContent);

				// delete keyword which has been covered
				for (int i = 0; i < keywordList.size(); i++) {
					for (int k = 0; k < sentencesWithScoreList.get(0).first.segContentList
							.size(); k++) {
						String currentWord = sentencesWithScoreList.get(0).first.segContentList
								.get(k);
						Boolean isInTitle = keywordList.get(i).getIsInTitle();
						String keyWordName = keywordList.get(i).getName();

						if ((isInTitle && Utilities.fuzzyMatch(currentWord,
								keyWordName))
								|| (!isInTitle && currentWord
										.equals(keyWordName))) {
							keywordList.remove(i);
							i--;
							break;
						}
					}
				}
				sentencesWithScoreList.remove(0);

				if (sentencesWithScoreList.size() <= 0) {
					break;
				}

				if (keywordList.size() <= 0) {
					break;
				}

				/*
				 * if (currentSummWordsNum <
				 * summaryStruct.getMinOutputWordsNum() ||
				 * outputSentences.size() < summaryStruct .getMinOutputSenNum())
				 * continue;
				 */

				if (outputSentences.size() >= summaryStruct
						.getMaxOutputSenNum()) {
					break;
				}

				if (currentSummWordsNum > outputMaxWordsNum) {
					break;
				}
			}
			
			if(outputSentences.size()==0){
				return 0;
			}
			Utilities.conSummaryBySentences(outputSentences, summaryStruct);

			Utilities
					.highlightResult(outputSentences.iterator(), summaryStruct);
			return 1;
		} catch (Exception ex) {
			logger.error("[DatayesSummarizer_v0.1]news title:" + summaryStruct.getTitle() + ex);
			System.out.println("[DatayesSummarizer_v0.1]news title:" + summaryStruct.getTitle() + ex);
			return 0;
		}
	}
}
