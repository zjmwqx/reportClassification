/**   
* @Description: TODO
* @author weifu.du   
* @date May 16, 2013 
* @version V1.0   
*/ 
package com.datayes.textmining.Utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FWriter {
	public OutputStreamWriter osw;
	
	public FWriter(String filename){
		try{
			osw = new OutputStreamWriter( new BufferedOutputStream(new  FileOutputStream(filename)), "utf-8");
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @param string
	 * @param b
	 */
	public FWriter(String filename, boolean b)
	{
		// TODO Auto-generated constructor stub
		try{
			osw = new OutputStreamWriter( new BufferedOutputStream(new  FileOutputStream(filename,b)), "utf-8");
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public void println(String str){
		try{
//			osw.write(str + "\r\n");
			osw.write(str + "\n");
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void print(String str){
		try{
			osw.write(str);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void close(){
		try{
			osw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void flush(){
		try{
			osw.flush();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}

