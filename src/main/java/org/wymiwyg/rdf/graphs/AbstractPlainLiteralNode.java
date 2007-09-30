/*
 * Copyright  2002-2005 WYMIWYG (http://wymiwyg.org)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.wymiwyg.rdf.graphs;


/**
 * @author reto
 * 
 */
public abstract class AbstractPlainLiteralNode implements PlainLiteralNode {

	//private static final Log log = LogFactory.getLog(AbstractPlainLiteralNode.class);
	
	private int theHash = 0;

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PlainLiteralNode) {
			PlainLiteralNode other = (PlainLiteralNode) obj;
			if (getLocale() != null) {
				return getLocale().equals(other.getLocale())
						&& getLexicalForm().equals(other.getLexicalForm());
			} else {
				return (other.getLocale() == null)
						&& getLexicalForm().equals(other.getLexicalForm());
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		if (theHash == 0) {
			int result = getLexicalForm().hashCode();
			if (getLocale() != null) {
				result += getLocale().hashCode();
			}
			theHash = result;
		}
		return theHash;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append('\"');
		result.append(getLexicalForm());
		result.append('\"');
		if (getLocale() != null) {
			result.append('@');
			result.append(getLocale());
		}
		return result.toString();
	}
}
