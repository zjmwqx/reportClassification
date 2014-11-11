package com.datayes.algorithm.textmining.patternFinding;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TreeNode implements Serializable,Comparable{
	private static final long serialVersionUID = 757623003289404518L;
	private Pattern pat;
	private Set<TreeNode> childSet = new TreeSet<TreeNode>();
	public TreeNode(Pattern pat) {
		// TODO Auto-generated constructor stub
		this.pat = pat;
	}
	public Pattern getPat() {
		return pat;
	}
	public void setPat(Pattern pat) {
		this.pat = pat;
	}
	public Set<TreeNode> getChildSet() {
		return childSet;
	}
	public void setChildSet(Set<TreeNode> childSet) {
		this.childSet = childSet;
	}
	public void addChild(TreeNode newNode) {
		// TODO Auto-generated method stub
		childSet.add(newNode);
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		if(this.getPat().getPatternName().length() < ((TreeNode)o).getPat().getPatternName().length())
			return -1;
		else 
			return 1;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return pat.equals(((TreeNode)obj).getPat());
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return pat.hashCode();
	}
	
}
