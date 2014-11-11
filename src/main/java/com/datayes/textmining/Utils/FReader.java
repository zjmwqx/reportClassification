/**   
* @Description: TODO
* @author weifu.du   
* @date May 16, 2013 
* @version V1.0   
*/ 
package com.datayes.textmining.Utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;



public class FReader {
	
	private static final Logger LOGGER = Logger.getLogger(FReader.class.getName());
	
	public BufferedReader br;
	
	public FReader(String filename, boolean isMapReduce) {
		try{
			if (isMapReduce) {
				System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
				InputStream fstream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
				br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream), "UTF-8"));

			} else {
				br = new BufferedReader( new InputStreamReader(	new FileInputStream(filename),"utf-8"));
			}
		}
		catch(IOException e){
			LOGGER.error("error opening the file for reading, cause:",e);
		}
	}
	
	public String readLine(){
		try{
			return br.readLine();
		}
		catch(IOException e){
			LOGGER.error("error reading the file, cause:",e);
			return "";
		}
	}
	
	public void close(){
		try{
			br.close();
		}
		catch(IOException e){}
	}
}
