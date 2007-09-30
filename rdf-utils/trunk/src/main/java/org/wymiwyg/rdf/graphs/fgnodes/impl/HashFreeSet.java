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
package org.wymiwyg.rdf.graphs.fgnodes.impl;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/** A set implementation which does not rely on contstant hash-codes 
 * 
 * 
 * @author reto
 *
 */
public class HashFreeSet<T> extends AbstractCollection<T> implements Set<T> {

	Collection<T> data = new  ArrayList<T>();
	
	
	/**
	 * @param groundingMolecules
	 */
	public HashFreeSet(Set<T> base) {
		addAll(base);
	}

	/**
	 * 
	 */
	public HashFreeSet() {
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean add(T o) {
		for (T current : data) {
			if (o.equals(current)) {
				return false;
			}
		}
		data.add(o);
		return true;
	}
	/**
	 * The caller guarantees that the object is not already in the set
	 */
	public boolean addNew(T o) {
		data.add(o);
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Set)) {
			return false;
		}
		Set set = (Set)obj;
		if (set.size() != size()) {
			return false;
		}
		for (T element : data) {
			if (!set.contains(element)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		//throw new RuntimeException("Cannot compute hash of a set without computing hash of elements");
		int result = 0;
		for (T element : data) {
			result += element.hashCode();
		}
		return result;
	}

	
}
