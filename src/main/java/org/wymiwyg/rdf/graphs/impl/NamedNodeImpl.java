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
package org.wymiwyg.rdf.graphs.impl;

import org.wymiwyg.rdf.graphs.NamedNode;

/**
 * @author reto
 *
 */
public class NamedNodeImpl implements NamedNode {

	private String uriRef;

	public NamedNodeImpl(String uriRef) {
		super();
		this.uriRef = uriRef;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.graphs.NamedNode#getURIRef()
	 */
	public String getURIRef() {
		return uriRef;
	}

	public boolean equals(Object obj) {
		if (obj instanceof NamedNode) {
			return ((NamedNode)obj).getURIRef().equals(uriRef);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return uriRef.hashCode();
	}

	public String toString() {
		return "<"+uriRef+">";
	}
	
	

}
