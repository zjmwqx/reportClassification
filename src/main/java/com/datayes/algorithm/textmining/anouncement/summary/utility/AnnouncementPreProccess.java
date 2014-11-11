/**   
 * @Title: AnnouncementPreProccess.java 
 * @Package com.datayes.algorithm.text.financial_news_summarize.utility 
 * @Description: TODO(对公告文本进行预处理) 
 * @author guangpeng.chen    
 * @date Nov 25, 2013 2:32:15 PM 
 * @version V1.0   
 */
package com.datayes.algorithm.textmining.anouncement.summary.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Term;
import org.ansj.domain.TermNature;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.datayes.algorithm.textmining.anouncement.summary.summarizer.DatayesSummarizer_v0;
import com.datayes.algorithm.textmining.anouncement.textUtils.ContentGetAgent;
import com.datayes.textmining.Utils.S3Connection;
/**
 * @author guangpeng
 * 
 */
public class AnnouncementPreProccess {

	static int count = 0;

	/**
	 * 
	* @Title: PreProccess 
	* @Description: TODO(对公告文本去除开头和结尾的噪音) 
	* @param  content 带处理的公告文本
	* @return String 完成去噪的文本 
	* @throws
	 */
	public static String PreProccess(String content) {
		
		if(content==null||content.equals("")){
			return null;
		}
		String[] contentArray = content.split("\n");
		List<String> paragraphs = new ArrayList<String>();
		StringBuffer contentBuffer = new StringBuffer();
		String tmpString = "";
		String currentStr = "";
		Matcher matcher;

		for (int i = 0; i < contentArray.length; i++) {

			currentStr = contentArray[i];
			if (currentStr.replaceAll("　| ", "").length() < 2) {
				continue;
			}

			// 删除表格信息
			/*
			 * if (Utilities.StaChWord(currentStr) * 3 < currentStr.length()) {
			 * continue; }
			 */

			if (currentStr.endsWith(" ")) {
				paragraphs.add(tmpString + currentStr);
				tmpString = "";
			} else {
				tmpString = tmpString + currentStr;
			}
		}

		boolean deletednoise = false;
		for (int i = 0; i < paragraphs.size(); i++) {
			// System.out.println(paragraphs.get(i));
			if (deletednoise == false && paragraphs.get(i).split("(公司董事会)|(公司及董事会)").length > 1) {
				for (int j = i; j > -1; j--) {
					paragraphs.remove(j);
				}
				i = 0;
				deletednoise = true;
			}

			if (i<paragraphs.size() && paragraphs.get(i).replaceAll(" ", "").contains("特此公告")) {
				while (i < paragraphs.size()) {
					paragraphs.remove(i);
				}
			}
		}
		for (int i = 0; i < paragraphs.size(); i++) {
			contentBuffer.append(paragraphs.get(i) + "\n");
		}
		return contentBuffer.toString();
	}

/*	public static String addPTag(String input) {
		StringBuffer result = new StringBuffer();
		String[] inputs = input.split("\n");
		for (int i = 0; i < inputs.length; i++) {
			result.append("<p>" + inputs[i] + "</p>");
		}
		return result.toString();
	}*/

	public static void recursiveExtractSummaries(DatayesSummarizer_v0 summarizer, 
			File file, PrintWriter pw, S3Connection s3Con) throws Exception {
		if (file.isDirectory())
			for (File childFile : file.listFiles())
				recursiveExtractSummaries(summarizer, childFile, pw, s3Con);
		else {
			String fullPath = file.getAbsolutePath();
			ContentGetAgent contentGet = new ContentGetAgent();
			contentGet.setFilePath(fullPath);
			contentGet.setPageCnt(100);
			String content = contentGet.pdfFromS3(s3Con);
			String[] paths = fullPath.split("/");
			String title, newContent;
			if (fullPath.contains(".pdf")) {
				 title = paths[paths.length - 1].replace(".pdf", "");
				System.out.println(fullPath);
				// System.out.println(content);
				 newContent = PreProccess(content);
			}else {

				System.out.println(fullPath);
				// System.out.println(content);
				title= paths[paths.length-1].replace(".txt", "");
				newContent = ReadFile.readFile(fullPath);
			}
			// System.out.println(content);
			//String toDisplay = addPTag(content);
			String className = paths[paths.length - 2];

			String localPath = paths[paths.length - 3] + "/" + paths[paths.length - 2] + "/" + paths[paths.length - 1];

			String newtitle = title.replaceAll("公告|关于", "");
			SummaryStruct summaryStruct = new SummaryStruct(newtitle, newContent, 5, 2, 5, 50, 120);
			summarizer.getSummary(summaryStruct);
			SummaryStruct summaryStructNoTitle = new SummaryStruct("", newContent, 5, 2, 5, 50, 120);
			summarizer.getSummary(summaryStructNoTitle);
			count++;
			pw.println("<tr><td  >" + className + "</td><td>\t\t" + count + "、" + title + "</td></tr>");
			pw.println("<tr><td rowspan=2 style=\"width:50%\"><a href=\"" + localPath + "\" target=\"_blank\">" + localPath + "</a></td><td>" + summaryStruct.getSummary() + "</td></tr>");
			//pw.println("<tr><td>" + summaryStruct.getKeywordList() + "</td></tr>");
			pw.println("<tr><td>" + summaryStructNoTitle.getSummary() + "</td></tr>");
			//pw.println("<tr><td>" + summaryStructNoTitle.getKeywordList() + "</td></tr>");
			pw.println("<tr><td colspan=2>&nbsp;</td></tr>");

		}
	}

	public static void main(String[] args) throws IOException {
		/*PrintWriter pw = new PrintWriter("/home/guangpeng/workspace/TextMining/summary_Announcement/AnnouncementSumm.html");

		String outputHtmlStr = "<html>";
		outputHtmlStr = outputHtmlStr + "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>公告摘要</title></head>";
		outputHtmlStr = outputHtmlStr + "<body><center><h2>公告摘要</h2></center><table border=\"1\">";

		pw.println(outputHtmlStr);
		File file = new File("/home/guangpeng/workspace/TextMining/summary_Announcement/data");
		// file = new
		// File("/home/guangpeng/workspace/TextMining/summary_Announcement/data/预减/信维通信：2013年度业绩预告.pdf");
		DatayesSummarizer_v0 summarizer = new DatayesSummarizer_v0("/home/guangpeng/workspace/javaPro/maven.1376986376114/trunk/financial_news_summarizer/library/userLibrary/userLibrary.dic");
		recursiveExtractSummaries(summarizer, file, pw);

		outputHtmlStr = "</table></body></html>";
		pw.println(outputHtmlStr);
		pw.close();

		
		 * Analysis udf = new ToAnalysis(new
		 * StringReader("诉讼仲裁:	杭萧钢构关于诉讼判决结果的公告"));
		 * 
		 * Term term = null; while ((term = udf.next()) != null) { // term =
		 * udf.get(i); TermNature[] termNatures =
		 * term.getTermNatures().termNatures; String wordType =
		 * termNatures[0].nature.natureStr; String word = term.getName();
		 * 
		 * System.out.println(wordType + "->" + word); }
		 
		String currentStr = " 》好人>";
		Pattern beginPattern7 = Pattern.compile("^ *[^\\u4e00-\\u9fa5（《“\\w]+");
		Matcher matcher = beginPattern7.matcher(currentStr);
		if (matcher.find()) {
			currentStr = matcher.replaceFirst("");
		}
		System.out.println(currentStr);*/
	}
}
