package com.datayes.textmining.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.Random;

public class LCS {
	private char[] x;
	private char[] y;
	private List<Integer> indexList;
	private String lcsRes;
	private List<String> lcsResList = new ArrayList<String>();
	private int b[][];
	private int c[][];// C[i,j]记录X与Y的LCS的长度

	private List<String> strx;
	private List<String> stry;
	public LCS(String s1, String s2) {
		x = new String(" " + s1).toCharArray();
		y = new String(" " + s2).toCharArray();
		indexList = new ArrayList<Integer>();
		b = new int[x.length][y.length];
		c = new int[x.length][y.length];
	}
	public LCS(List<String> s1, List<String> s2) {
		strx = new ArrayList();
		strx.add(" ");
		strx.addAll(s1);
		stry = new ArrayList();
		stry.add(" ");
		stry.addAll(s2);
		indexList = new ArrayList<Integer>();
		b = new int[strx.size()][stry.size()];
		c = new int[strx.size()][stry.size()];
	}
	public static void main(String[] args) {
		// 随机生成字符串
		//String ss = GetRandomStrings(9);
		//String ss1 = GetRandomStrings(6);
		/*String ss = "好人医生品按";
		String ss1 = "医啊请按";
		System.out.println("ss=" + ss);
		System.out.println("ss1=" + ss1);
		LCS lcs = new LCS(ss, ss1);
		lcs.getLength();
		System.out.println(lcs.getLcsRes());
		for (Integer t : lcs.getIndexList()) {
			System.out.print(t);
		}*/
		List<String> ss = new ArrayList<String>();
		ss.add("昨天");
		ss.add("是");
		ss.add("晴天");
		List<String> dd = new ArrayList<String>();
		dd.add("明天");
		dd.add("是");
		dd.add("大");
		dd.add("晴天");
		LCS lcs = new LCS(ss, dd);
		lcs.getLengthStr();
		System.out.println(lcs.getLcsRes());
		
	}

	void getLength() // 计算c[i][j],从前往后计算
	{

		for (int i = 1; i < x.length; i++) {
			for (int j = 1; j < y.length; j++) {
				if (x[i] == y[j]) {
					c[i][j] = c[i - 1][j - 1] + 1;
					b[i][j] = 1;
				} else if (c[i - 1][j] >= c[i][j - 1]) {
					c[i][j] = c[i - 1][j];
					b[i][j] = 0;
				} else {
					c[i][j] = c[i][j - 1];
					b[i][j] = -1;
				}
			}
		}
		getMaxStr();
	}
	private void getMaxStrStr() {
		// TODO Auto-generated method stub
		int i = strx.size() - 1, j = stry.size() - 1;
		Stack<String> sta = new Stack<String>();
		StringBuilder sb = new StringBuilder();
		
		while (i >= 1 && j >= 1) {
			if (b[i][j] == 1) {
				sta.push(strx.get(i));
				indexList.add(i);
				i--;
				j--;
			} else if (b[i][j] == 0)
			{
				i--;
			}
			else
			{
				j--;
			}
		}
		while (!sta.empty())
		{
			String t = sta.pop();
			lcsResList.add(t);
			sb.append(t);
			
			//System.out.print(sta.pop() + " ");
		}
		lcsRes = sb.toString();
	}
	public void getLengthStr() // 计算c[i][j],从前往后计算
	{

		for (int i = 1; i < strx.size(); i++) {
			for (int j = 1; j < stry.size(); j++) {
				if (strx.get(i).equals(stry.get(j))) {
					c[i][j] = c[i - 1][j - 1] + 1;
					b[i][j] = 1;
				} else if (c[i - 1][j] >= c[i][j - 1]) {
					c[i][j] = c[i - 1][j];
					b[i][j] = 0;
				} else {
					c[i][j] = c[i][j - 1];
					b[i][j] = -1;
				}
			}
		}
		getMaxStrStr();
	}

	public void getMaxStr() {// 输出最长公共子序列
		
		int i = x.length - 1, j = y.length - 1;
		Stack<Character> sta = new Stack<Character>();
		StringBuilder sb = new StringBuilder();
		
		while (i >= 1 && j >= 1) {
			if (b[i][j] == 1) {
				sta.push(x[i]);
				indexList.add(i);
				i--;
				j--;
			} else if (b[i][j] == 0)
			{
				i--;
			}
			else
			{
				j--;
			}
		}
		while (!sta.empty())
			sb.append(sta.pop());
			//System.out.print(sta.pop() + " ");
		lcsRes = sb.toString();
	}

	// 取得定长随机字符串
	public static String GetRandomStrings(int length) {
		StringBuffer buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < length; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}
	
	public static Boolean judgeLcsBelong(String patStr, String orgStr)
	{
		int i = 0, j = 0;
		while(i < patStr.length() && j < orgStr.length())
		{
			if(patStr.charAt(i) == orgStr.charAt(j))
			{
				i++;
				j++;
			}
			else
			{
				j++;
			}
		}
		if(i == patStr.length())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static Boolean judgeLcsBelong(List<String> patStr, List<String> orgStr)
	{
		int i = 0, j = 0;
		while(i < patStr.size() && j < orgStr.size())
		{
			if(patStr.get(i).equals(orgStr.get(j)))
			{
				i++;
				j++;
			}
			else
			{
				j++;
			}
		}
		if(i == patStr.size())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getLcsRes() {
		return lcsRes;
	}
	public List<String> getLcsResList() {
		return lcsResList;
	}

	public void setLcsRes(String lcsRes) {
		this.lcsRes = lcsRes;
	}
	public List<Integer> getIndexList() {
		return indexList;
	}
	public void setIndexList(List<Integer> indexList) {
		this.indexList = indexList;
	}

}