package com.datayes.textmining.test;

public class regexpTest {
	public static void main(String[] args) {
		String orgStr = "<MISC>第二届</MISC> 董事会 <MISC>第二十二次</MISC>";
		String outStr = orgStr.replaceAll("<MISC>[\\s\\S]*?</MISC>", "C");
		System.out.println(outStr);
	}
}
