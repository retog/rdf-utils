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
package org.wymiwyg.rdf.molecules.functref.impl2;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A set that relies on the consumer not to add an element twice
 * 
 * @author reto
 * @param <T> 
 *
 */
public class GoodFaithSet<T> extends AbstractSet<T> implements Set<T> {

	Collection<T> data = new ArrayList<T>();
	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean add(T e) {
		return data.add(e);
	}

	
}
