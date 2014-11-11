/**   
* @Description: TODO
* @author weifu.du
* @date Nov 12, 2013 
* @version V1.0   
*/
package com.datayes.textmining.Utils;

import java.io.Serializable;

/**
 * @author weifu
 *
 */
@SuppressWarnings("serial")
public class Term implements Serializable, Comparable<Term> {
	public int termID;
	public String termStr;
	public String getTermStr() {
		return termStr;
	}
	public Term(int termID, String termStr) {
		this.termID = termID;
		this.termStr = termStr;
	}
	
	public Term(String termStr) {
		this.termStr = termStr;
		this.termID = -1;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((termStr == null) ? 0 : termStr.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Term)) {
			return false;
		}
		Term other = (Term) obj;
		if (termStr == null) {
			if (other.termStr != null) {
				return false;
			}
		} else if (!termStr.equals(other.termStr)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Term o) {
		// TODO Auto-generated method stub
		Integer mine = this.termID;
		Integer others = o.termID;
		return mine.compareTo(others);
	}
	
	@Override
	public String toString() {
		return "termID: " + this.termID + "termStr: " + termStr;
	}
}
