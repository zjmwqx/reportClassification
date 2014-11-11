package com.datayes.algorithm.textmining.patternFinding;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.LCS;
import com.datayes.textmining.Utils.WordSpliter;
import com.datayes.textmining.classification.RptOrgDataAgent;
import com.datayes.textmining.classification.RptQADataAgent;
import com.datayes.textmining.rptClassification.model.FileInfo;

public class DataAnyliser extends PatternFinder {
	public static void main(String[] args) {
		try {
			ConfigFileLoader.initConf(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("config/lib files read error!");
			return;
		}
		DataAnyliser dataAnyliser = new DataAnyliser();
		dataAnyliser.analyzeCoverage();

	}

	public void analyzeCoverage() {
		loadNameEntityModel();
		getLcsTotal(true);
		try {
			RptQADataAgent qaDataController = new RptQADataAgent();
			unParsedFiles.addAll(qaDataController.getFromQARes());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for (int t = 1; t < 2; ++t) {
			for (int c = 0; c <= 0; c+=5) {
				int count = 0;
				double cntCover = 0;
				findDominantLcs(true, c, 2, t);
				for (FileInfo finf : unParsedFiles) {
					if (count % 10000 == 0) {
						System.out.println(count);
					}
					if(!finf.getQACategoriesStrList().contains("未分类"))
					{
						String description = finf.getDescription();
						String[] patsArr = getRegularizedPattern(description);
						int i = 0;
						for(;i < patsArr.length; ++i)
						{
							if (isMatch(patsArr[i])) {
								cntCover++;
								break;
							}
						}
						if(i == patsArr.length)
						{
							System.out.println(description);
						}
						count++;
					}
					
				}
				System.out.println("lcs count:" + c + " lcs category count:"
						+ t + " ===> coverage rate: " + cntCover + "/"
						+ count + " = " + cntCover
						/ count);
			}
		}

	}

	private boolean isMatch(String description) {
		// TODO Auto-generated method stub
		List<String> desTerms = ConfigFileLoader.wdsp.splitSentence(description,false);
		for (String pat : dominantLcs.keySet()) {
			if (LCS.judgeLcsBelong(dominantLcs.get(pat).getTerms(), desTerms)) {
				return true;
			}
		}
		return false;
	}
}
