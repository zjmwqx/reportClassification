package com.datayes.algorithm.textmining.anouncement.textUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import com.datayes.textmining.Utils.ConfigFileLoader;
import com.datayes.textmining.Utils.S3Connection;
import com.datayes.textmining.classification.RptClasifer;
import com.datayes.textmining.reportJobs.ReportJob;


public class ContentGetAgent implements Callable<String>{
	private String filePath;
	private int pageCnt;
	private S3Connection s3Con;
	public static String reportContentGet(String reportFilePath)
	{
		String text = null;
		if(reportFilePath.contains(".pdf") || 
				reportFilePath.contains(".PDF"))
		{
			pdfExtractor pdfEx = new pdfExtractor(
					reportFilePath);
			text = pdfEx.getString();
		}
		else if((reportFilePath.indexOf(".html")!=-1) 
				|| (reportFilePath.indexOf(".htm")!=-1)
				|| (reportFilePath.indexOf(".txt")!=-1))
		{
			htmlExtractor htmlEx = new htmlExtractor();
			text = htmlEx.getHtmlContent(reportFilePath);
		}
		else 
		{
			return null;
		}
		text = reportFilePath.substring(reportFilePath.lastIndexOf("/")
				+1,reportFilePath.lastIndexOf(".")) + text;
		return text;
	}
	public static void IncursiveCopyFiles(File dir)
	{
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {// 判断是否有子目录，如果有，就调用自己，没有就直接输出文件

				IncursiveCopyFiles(files[i]);
			} else {
				File f = files[i];
				//System.out.println(f.getName()); // 文件名
				//BufferedWriter outputFile = 
				//		new BufferedWriter(f.getPath());
//				++fileNo;
//				logger.log(Level.INFO, "No." + fileNo + ": " + f.getName()
//						+ " is Processing...");
				//System.out.println(f.getPath()+"\n");
				
			}
		}
	}
	public String pdfFromS3(S3Connection s3Con) throws Exception {
		S3Object s3Object = null;
		String content = null;
		try{
		
		/*s3Object = s3Con.getS3Service().getObject(s3Con.getBucketName(), getFilePath());
		System.out.println(s3Object.getBucketName() + " "+ 
				s3Object.getContentDisposition()+ " "+
				s3Object.getContentEncoding()+ " " +
				s3Object.getContentLanguage()+ " " +
				s3Object.getContentLength()+ " " +
				s3Object.getContentType()+ " " +
				s3Object.getETag());*/
		//s3Object = s3Con.getS3Service().getObject(s3Con.getBucketName(),"/abc/def.txt");
		//S3Object [] tmp_list= s3Con.getS3Service().listObjects("/datayes/pipeline/data/other_reports/szmb/SZ000606CN/SZ000606CN_2013_2013-10-24_青海明胶：控股子公司管理制度（2013年10月）.pdf");
		//System.out.println(tmp_list.length);
		//S3Object [] tmp_list= s3Con.getS3Service().listObjects("pipeline", "/pipeline/datayes/pipeline/data/other_reports/szmb/SZ000606CN/SZ000606CN_2013_2013-10-24_青海明胶：第六届董事会2013年第八次临时会议决议公告.pdf");
		//System.out.println(tmp_list.length);
		/*s3Object = s3Con.getS3Service().getObject(s3Con.getBucketName(),"/datayes/pipeline/data/other_reports/szmb/SZ000606CN/SZ000606CN_2013_2013-10-24_青海明胶：第六届董事会2013年第八次临时会议决议公告.pdf");
		System.out.println(s3Object.getBucketName() + " "+ 
				s3Object.getContentDisposition()+ " "+
				s3Object.getContentEncoding()+ " " +
				s3Object.getContentLanguage()+ " " +
				s3Object.getContentLength()+ " " +
				s3Object.getContentType()+ " " +
				s3Object.getETag()+" " +
						s3Object.getAcl() +" " +
						s3Object.getName() +" "
				);*/
		//s3Object = s3Con.getS3Service().getObject(s3Con.getBucketName(),"/datayes/pipeline/data/other_reports/szmb/SZ000606CN/SZ000606CN_2013_2013-10-24_青海明胶：控股子公司管理制度（2013年10月）.pdf");
		/*System.out.println(s3Object.getBucketName() + " "+ 
				s3Object.getContentDisposition()+ " "+
				s3Object.getContentEncoding()+ " " +
				s3Object.getContentLanguage()+ " " +
				s3Object.getContentLength()+ " " +
				s3Object.getContentType()+ " " +
				s3Object.getETag() +" " +
				s3Object.getAcl() +" " +
				s3Object.getName());*/
		//InputStream inputStream = s3Object.getDataInputStream();
		/*byte[] b = new byte[10*1024];  
		FileOutputStream fos = new FileOutputStream("1.txt");  
		while(inputStream.read(b,0,10240) != -1){  
			fos.write(b,0,10240);  
		}  
		fos.flush(); 
		fos.close();*/
		//pdfExtractor mypdf = new pdfExtractor(inputStream);
		
		//content = mypdf.getStringFromStream(getPageCnt());
			
		pdfExtractor mypdf = new pdfExtractor(getFilePath());
		//pdfExtractor mypdf = new pdfExtractor("/datayes/pipeline/data/other_reports/sme/SZ002294CN/SZ002294CN_2013_2013-10-12_信立泰：2013年第一次临时股东大会的法律意见书.pdf");
		content = mypdf.getStringFromPDoc(getPageCnt());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println(content);
			
		return content;
	}
	
	public static void convertToTextByPathList(String pathNameList) throws Exception
	{
		BufferedReader inputFileReader = new BufferedReader(new FileReader("pathNameList.txt"));
		//ContentGetAgent.IncursiveCopyFiles(new File("/home/jimingzhou/remoteDir/other_reports/"));
		String filePath = null;
		while((filePath = inputFileReader.readLine()) != null)
		{
			filePath = filePath.replace("/datayes/pipeline/data/", "/home/jimingzhou/remoteDir/");
			System.out.println(filePath);
			String[] tempList = filePath.split("/");
			String outputFile = "/home/jimingzhou/ReportTxt/"+
					tempList[tempList.length-1];
			outputFile = outputFile.replace(".pdf", ".txt");
			BufferedWriter reportoutput =
					new BufferedWriter(new FileWriter(outputFile));
			String content = null;
			try{
				content = reportContentGet(filePath);
			}catch(Exception e)
			{
				continue;
			}
			reportoutput.write(content);
			reportoutput.close();
		}
		inputFileReader.close();
	}
	@Override
	public String call() throws Exception {
		if(ReportJob.Version.equals("staging"))
            return pdfFromS3(s3Con);
		else
			return pdfFromFile(filePath);
	}
	public static String getContent(String path) throws Exception
	{
		ContentGetAgent contentGet = new ContentGetAgent();
		contentGet.setFilePath(path);
		contentGet.setPageCnt(5);
		String content = null;
		content = contentGet.pdfFromS3(ConfigFileLoader.s3Con);
		content = ContentGetAgent.deleteNewLine(content);
		return content;
	}
	public static void main(String[] args) throws Exception {
		ConfigFileLoader.initConf(args);
		ContentGetAgent contentGet = new ContentGetAgent();
		contentGet.setFilePath("/datayes/pipeline/data/other_reports/sh/SH600203CN/SH600203CN_2014_2014-03-27_福日电子关于为控股子公司福建福日科技有限公司提供担保的公告.pdf");
		contentGet.setPageCnt(5);
		String content = null;
		content = contentGet.pdfFromS3(ConfigFileLoader.s3Con);
		content = ContentGetAgent.deleteNewLine(content);	
		System.out.println(content);
		}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public int getPageCnt() {
		return pageCnt;
	}
	public void setPageCnt(int pageCnt) {
		this.pageCnt = pageCnt;
	}
	public void setS3Con(S3Connection s3Con) {
		this.s3Con = s3Con;
	}
	public S3Connection getS3Con() {
		return s3Con;
	}
	public String pdfFromFile(String full_path) {
		// TODO Auto-generated method stub
		String content = null;
		try{
		InputStream pdfStream = new FileInputStream(full_path);
		pdfExtractor mypdf = new pdfExtractor(pdfStream);
		content = mypdf.getStringFromStream(getPageCnt());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return content;
	}
	public static String deleteNewLine(String content) {
		// TODO Auto-generated method stub
		String[] contentList = content.split("\n");
		StringBuilder newContent = new StringBuilder();
		//String patStr = "([\\(（][一二三四五六七1-9１２３４５６７８９][\\)）])|([一二三四五六七1-9１２３４５６７８９][、\\.])";
		for(String ps : contentList)
		{
			String tempStr = ps.trim();
			if(tempStr.length() > 0)
			{
//				if(ps.split(patStr).length > 1)
//				{
//					newContent.append("\n"+ps+"\n");
//				}
				if(tempStr.charAt(tempStr.length()-1) == '.'
						|| tempStr.charAt(tempStr.length()-1) == '。'
						|| tempStr.charAt(tempStr.length()-1) == '；'
						|| tempStr.charAt(tempStr.length()-1) == ';'
						|| tempStr.charAt(tempStr.length()-1) == '》')
				{
					newContent.append(ps+"\n");
				}
				else if(tempStr.length() < 30)
				{
					newContent.append(ps+"\n");
				}
				else
				{
					newContent.append(ps);
				}
			}
		}
		return newContent.toString();
	}
}
