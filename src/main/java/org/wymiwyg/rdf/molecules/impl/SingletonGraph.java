/*
 * Copyright  2002-2006 WYMIWYG (http://wymiwyg.org)
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
package org.wymiwyg.rdf.molecules.impl;

import java.util.Iterator;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Triple;

/**
 * @author reto
 *
 */
public class SingletonGraph extends AbstractGraph {
	
	

	private Triple triple;

	/**
	 * @param triple
	 */
	public SingletonGraph(Triple triple) {
		this.triple = triple;
	}

	@Override
	public Iterator<Triple> iterator() {
		return new Iterator<Triple>() {

			boolean returned = false;
			
			public boolean hasNext() {
				return !returned;
			}

			public Triple next() {
				if (returned) return null;
				returned = true;
				return triple;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	@Override
	public int size() {
		return 1;
	}

}
