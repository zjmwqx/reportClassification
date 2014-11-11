/**   
* @Description: TODO
* @author weifu.du
* @date Nov 12, 2013 
* @version V1.0   
*/
package com.datayes.textmining.Utils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * @author weifu
 *
 */
@SuppressWarnings("serial")
public class Label implements Serializable {
	public int labelID;
	public String lableName;
	
	/**
	 * @param lableName
	 */
	public Label(String lableName) {
		this.lableName = lableName;
		this.labelID = -1;
	}


	public Label(int labelID, String lableName) {
		this.labelID = labelID;
		this.lableName = lableName;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lableName == null) ? 0 : lableName.hashCode());
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
		if (!(obj instanceof Label)) {
			return false;
		}
		Label other = (Label) obj;
		if (lableName == null) {
			if (other.lableName != null) {
				return false;
			}
		} else if (!lableName.equals(other.lableName)) {
			return false;
		}
		return true;
	}
}
