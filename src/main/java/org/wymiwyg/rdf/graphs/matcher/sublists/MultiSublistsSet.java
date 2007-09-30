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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author reto
 *
 */
public class MultiSublistsSet extends AbstractSet {

	/**
	 * @author reto
	 *
	 */
	public class MultiSublistIter extends AbstractListsIterator {
		int pos = 0;
		ListsIterator firstIter;
		List<?> currentFromFirst;
		MultiSublistsSet rest;
		ListsIterator currentRestIter;
		MultiSublistIter() {
			firstIter = (ListsIterator) subListSets[0].iterator();
			SubListsSet[] restSubListSets = new SubListsSet[subListSets.length -1];
			System.arraycopy(subListSets, 1, restSubListSets, 0, restSubListSets.length);
			rest = new MultiSublistsSet(restSubListSets);
			currentFromFirst = firstIter.nextList();
			currentRestIter = (ListsIterator) rest.iterator();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			if (currentRestIter.hasNext()) {
				return true;
			}
			if (firstIter.hasNext()) {
				currentFromFirst = firstIter.nextList();
				currentRestIter = (ListsIterator) rest.iterator();
				return true;
			}
			return false;
		}


		/* (non-Javadoc)
		 * @see sublists.ListsIterator#nextList()
		 */
		public List<Object> nextList() {
			if (!hasNext()) {
				return null;
			}
			List<Object> result = new ArrayList<Object>(currentFromFirst);
			result.addAll(currentRestIter.nextList());
			return result;
		}

	}

	private SubListsSet[] subListSets; 
	
	public MultiSublistsSet(SubListsSet[] subListSets) {
		this.subListSets = subListSets;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	public int size() {
		int result = 1;
		for (int i = 0; i < subListSets.length; i++) {
			result *= subListSets[i].size();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator iterator() {
		if (subListSets.length > 1) {
			return new MultiSublistIter();
		} else {
			if (subListSets.length > 0) {
				return subListSets[0].iterator();
			} else {
				return new AbstractListsIterator() {

					public List<Object> nextList() {
						return null;
					}

					public boolean hasNext() {
						return false;
					}
				
				};
			}
		}
	}

}
