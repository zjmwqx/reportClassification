/*package com.datayes.algorithm.textmining.anouncement.summary.utility;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;


public class PdfExtractor implements Callable<String> {

	static Logger logger = Logger.getLogger(PdfExtractor.class);
	
	private  String fileName=null;
	
	public   void setFileName(String nfileName){
		this.fileName = nfileName;
	}
	

	public  String getText() {
		String content = "";
		try {
			File file = new File(this.fileName);
			PDDocument doc = PDDocument.load(file);
			PDFTextStripper ts = new PDFTextStripper();
            if(doc.getNumberOfPages()>100){
            	ts.setStartPage(0);
            	ts.setEndPage(100);
            }
			content = ts.getText(doc);
			doc.close();
		} catch (Exception e) {
			System.out.println("Parse pdf Error:"+e);
			logger.trace(e);
		}
		return content;
	}

	 (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 
	@Override
	public String call() throws Exception {
	     
            return getText();
	}
}
*/