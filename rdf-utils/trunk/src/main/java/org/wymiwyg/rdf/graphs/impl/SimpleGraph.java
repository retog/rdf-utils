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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Triple;

/**
 * @author reto
 *
 */
public class SimpleGraph extends AbstractGraph {

	Set<Triple> tripleSet = new HashSet<Triple>();
	
	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	public int size() {
		return tripleSet.size();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator<Triple> iterator() {
		return tripleSet.iterator();
	}

	public boolean add(Triple o) {
		verifyNotFinalized();
		return tripleSet.add(o);
	}


}
