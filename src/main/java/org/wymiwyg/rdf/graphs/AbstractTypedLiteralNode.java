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
public abstract class AbstractTypedLiteralNode implements TypedLiteralNode {

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof TypedLiteralNode) {
			TypedLiteralNode other = (TypedLiteralNode) obj;
			boolean res = getDataType().equals(other.getDataType())
					&& getLexicalForm().equals(other.getLexicalForm());
			return res;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return getLexicalForm().hashCode() + getDataType().hashCode();
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append('\"');
		result.append(getLexicalForm());
		result.append('\"');
		result.append("^^");
		result.append(getDataType());
		return result.toString();
	}
}
