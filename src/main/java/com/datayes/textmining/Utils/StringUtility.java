/**   
* @Description: TODO
* @author weifu.du   
* @date May 29, 2013 
* @version V1.0   
*/ 

package com.datayes.textmining.Utils;

import java.util.Set;

public class StringUtility {
	private static String[] punctuations = {",", ".", "<", ">", "?", "/", "!", " ", 
		                                    "-", "_", ":", ";", "\"", "'", "[", "]", 
		                                    "{", "}", "\\", "|", "%", "@", "#", "$",
		                                    "，", "。","《", "》", "^", "&","(", ")", 
		                                    "？", "、"," ", "！", "：", "；", "【", "】",
		                                    "（", "）", "￥"};
	
	public static int count(String str, String subStr){
		if (str == null || str.isEmpty() || subStr == null || subStr.isEmpty()) {
			return 0;
		}
		int count = 0;
		int fromIndex = 0;
		while (fromIndex < str.length()) {
			int index = str.indexOf(subStr, fromIndex);
			if (index < 0) {
				return count;
			}
			count++;
			fromIndex = index + 1;
		}
		return count;
	}
	
	public static boolean containsDigit(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (Character.isDigit(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isPunctuation(String str) {
		for (int i = 0; i < punctuations.length; i++) {
			if (str.equals(punctuations[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAllDigit(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isAllCharacter(String str) {
		int pos;
		for (pos = 0; pos < str.length(); pos++) {
			char c = str.charAt(pos);
			int i = (int) c;
			if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
				continue;
			}else {
				break;
			}
		}
		return pos == str.length();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "IP(IPIP (NP (NN 行业) (NN 地位)) (IP (VA 稳固)))IP";
		String subStr = "Ip1";
		int count = StringUtility.count(str, subStr);
		System.out.println(count);
		System.out.println(StringUtility.isAllCharacter(subStr));

	}

}
