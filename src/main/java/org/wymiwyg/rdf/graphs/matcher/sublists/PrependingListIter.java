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
import java.util.Iterator;
import java.util.List;

/**
 * @author reto
 *
 */
public class PrependingListIter extends AbstractListsIterator implements
		Iterator {

	private Object prepended;
	private ListsIterator base;

	/**
	 * @param object
	 * @param iter
	 */
	public PrependingListIter(Object prepended, ListsIterator base) {
		this.prepended = prepended;
		this.base = base;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return base.hasNext();
	}

	/* (non-Javadoc)
	 * @see ListsIterator#nextList()
	 */
	public List<Object> nextList() {
		List<Object> result = new ArrayList<Object>();
		result.add(prepended);
		result.addAll(base.nextList());
		return result;
	}

}
