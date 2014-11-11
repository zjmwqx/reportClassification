package com.datayes.algorithm.textmining.anouncement.textUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class getRidOfRepeatedWords {
	public static void main(String[] args) {
		Set<String> words = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("/home/jiminzhou/Documents/rpt/manual-selected-feature.txt"));
			String line = null;
			while((line=br.readLine())!=null)
			{
				words.add(line);
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/jiminzhou/Documents/rpt/manual-selected-feature.txt"));
			for(String wd : words)
			{
				bw.write(wd+"\n");
			}
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
