package com.datayes.textmining.rptClassification.model;
import java.util.HashMap;
import java.util.Map;

/**
 * KeyWord.java
 * com.datayes.algorithm.textmining.anouncement.model
 * 工程：rptClassificationKeyWords
 * 功能： 类：关键词
 *
 * author    date          time     
 * ─────────────────────────────────────────────
 * jiminzhou     2014年2月10日   下午2:08:35
 *
 * Copyright (c) 2014, Datayes All Rights Reserved.
*/
public class KeyWord {
	private String word;
	private int posInDic;//在字典中的位置
	private double value;
	public KeyWord(String word) {
		// TODO Auto-generated constructor stub
		this.word = word;
	}
	public void setPosInDic(int posInDic) {
		this.posInDic = posInDic;
	}
	public int getPosInDic() {
		return posInDic;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getWord() {
		return word;
	}	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return word;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return ((KeyWord)obj).getWord().equals(this.getWord())
				&& ((KeyWord)obj).getPosInDic() == (this.getPosInDic());
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
