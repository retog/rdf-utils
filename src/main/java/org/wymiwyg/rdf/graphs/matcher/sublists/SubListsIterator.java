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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * @author reto
 *
 */
public class SubListsIterator extends AbstractListsIterator {

	boolean isLast;
	Iterator<? extends List> currentIter;
	private List list;
	private int size;

	/**
	 * @param list
	 * @param i
	 */
	public SubListsIterator(List list, int size) {
		if (size == list.size()) {
			currentIter = Collections.singleton(list).iterator();
			isLast = true;
		} else {
			if (size == 0) {
				currentIter = Collections.singleton(new ArrayList()).iterator();
				isLast = true;
			} else {
				currentIter = new SubListsIterator(list.subList(1,list.size()), size);
				isLast = false;
			}
		}
		
		this.list = list;
		this.size = size;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (currentIter.hasNext()) {
			return true;
		}
		if (!isLast) {
			currentIter = new PrependingListIter(list.get(0), new SubListsIterator(list.subList(1,list.size()), size-1));
			isLast = true;
		}
		return currentIter.hasNext();
	}
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public List<Object> nextList() {
		if (!hasNext()) {
			return null;
		}
		return currentIter.next();
	}

}
