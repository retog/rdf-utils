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
package org.wymiwyg.rdf.graphs.matcher.sublists;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author reto
 *
 */
public class SubListsSet extends AbstractSet {
	
	private List list;
	private int sublistSize;

	public SubListsSet(List list, int sublistSize) {
		this.list = list;
		this.sublistSize = sublistSize;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	public int size() {
		//Formula:  listSize! /  ((listSize - sublistSize)!*sublistSize!)
		//take bigger diving factorialy, mutiply listSize*(listSize-1) while bigger, divide by other diving factorial
		//return 0;
		throw new RuntimeException("not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator iterator() {
		return new SubListsIterator(list, sublistSize);
	}

}
