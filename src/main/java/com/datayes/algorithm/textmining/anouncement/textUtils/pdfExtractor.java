package com.datayes.algorithm.textmining.anouncement.textUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.util.PDFTextStripper;

public class pdfExtractor {
   /**
   * @param args
   */
	private static Logger logger = Logger.getLogger(pdfExtractor.class);;
	private String pdfNamePath= null;
	private InputStream pdfStream = null;
	public pdfExtractor(String pdfNamePath) {
		// TODO Auto-generated constructor stub
		this.pdfNamePath = pdfNamePath;
	}
	public pdfExtractor(InputStream pdfStream) {
		// TODO Auto-generated constructor stub
		this.pdfStream = pdfStream;
	}
	public String getString()
	{
		
		String content = "";
		try
		{
		File file = new File(pdfNamePath);
		//System.out.println(pdfNamePath);
	   	PDDocument doc = PDDocument.load(file);
	   	PDFTextStripper ts = new  PDFTextStripper();
	   	if(doc.getNumberOfPages()>5)
	   	{
	   		ts.setStartPage(0);
	   		ts.setEndPage(5);
	   	}
	    content = ts.getText(doc);
	    doc.close();
		}
		catch(Exception e)
		{
			System.err.println(pdfNamePath);
			e.printStackTrace();
        	logger.trace(e);
		}
	    return content;
	}
	public String getStringFromPDoc(int pageCnt)
	{
		String content = "";
		try
		{
			// TODO Auto-generated method stub
			System.out.println(pdfNamePath);
			String urlstr= "http://10.21.136.81:8080/pipeline"+pdfNamePath;
			//urlstr = "http://10.21.136.81:8080/pipeline/datayes/pipeline/data/other_reports/sh/SH600230CN/SH600230CN_2014_2014-02-18_沧州大化关于恢复正常生产的公告.pdf";
			int indx = urlstr.lastIndexOf("/");
			String title = urlstr.substring(indx+1);
			String path = urlstr.substring(0,indx+1);
			urlstr = path + URLEncoder.encode(title, "UTF-8");
			//URL url = new URL("http://10.21.136.81:8080/pipeline/datayes/pipeline/data/other_reports/szmb/SZ200530CN/" + URLEncoder.encode("SZ200530CN_2013_2013-10-24_大  冷Ｂ：六届六次董事会议决议公告（英文版）.pdf", "UTF-8"));
			URL url = new URL(urlstr);
	        URLConnection c = url.openConnection();
	        PDDocument doc = PDDocument.load(c.getInputStream());
	        
			//File file = new File(pdfNamePath);
			//System.out.println(pdfNamePath);

		   	PDFTextStripper ts = new  PDFTextStripper();
		   	if(doc.getNumberOfPages()>pageCnt)
		   	{
		   		ts.setStartPage(0);
		   		ts.setEndPage(pageCnt);
		   	}
		   	
		    content = ts.getText(doc);
		    //System.out.println(content);
		    doc.close();
		}catch(Exception e)
		{
			//System.err.println(pdfStream);
			
			e.printStackTrace();
        	logger.trace(e);
        	//System.out.println("count="+count);
        	
		}
	    return content;
	}
	public String getStringFromStream(int pageCnt)
	{
		String content = "";
		try
		{
			//File file = new File(pdfNamePath);
			//System.out.println(pdfNamePath);
			//System.out.println(pdfStream.available());
		   	PDDocument doc = PDDocument.load(pdfStream);
		   	PDFTextStripper ts = new  PDFTextStripper();
		   	if(doc.getNumberOfPages()>pageCnt)
		   	{
		   		ts.setStartPage(0);
		   		ts.setEndPage(pageCnt);
		   	}
		    content = ts.getText(doc);
		    //System.out.println(content);
		    doc.close();
		}catch(Exception e)
		{
			System.err.println(pdfStream);
			e.printStackTrace();
        	logger.trace(e);
		}
	    return content;
	}
	
	public static void main(String[] args) {
		
	}
}
