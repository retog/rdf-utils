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
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** A set implementation which does not rely on contstant hash-codes 
 * 
 * 
 * @author reto
 *
 */
public class HashFreeMap<T,U>  implements Map<T,U> {
	
	static class EntryImpl<T,U> implements Entry<T, U> {

		private T key;
		private U value;
		
		EntryImpl(T key, U value) {
			this.key = key;
			this.value = value;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getKey()
		 */
		public T getKey() {
			return key;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#getValue()
		 */
		public U getValue() {
			return value;
		}

		/* (non-Javadoc)
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		public U setValue(U value) {
			U oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		

		
	}

	Set<Entry<T, U>> entries = new HashFreeSet<Entry<T,U>>();
	/*List<T> keys = new  ArrayList<T>();
	List<U> values = new ArrayList<U>();*/
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Map)) {
			return false;
		}
		Map map = (Map)obj;
		for (Entry<T,U> entry : entries) {
			if (!entry.getValue().equals(map.get(entry.getKey()))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		throw new RuntimeException("Cannot compute hash of a set without computing hash of elements");
		/*int result = 0;
		for (T element : data) {
			result += element.hashCode();
		}
		return result;*/
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#entrySet()
	 */
	public Set<java.util.Map.Entry<T, U>> entrySet() {
		return entries;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		entries.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		for (Entry<T, U> entry : entries) {
			if (entry.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		for (Entry<T, U> entry : entries) {
			if (entry.getValue().equals(value)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public U get(Object key) {
		for (Entry<T, U> entry : entries) {
			if (entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set<T> keySet() {
		return new AbstractSet<T>() {

			@Override
			public Iterator<T> iterator() {
				final Iterator<Entry<T,U>> entriesIter = entries.iterator();
				return new Iterator<T>() {

					public boolean hasNext() {
						return entriesIter.hasNext();
					}

					public T next() {
						return entriesIter.next().getKey();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
					
				};
			}

			@Override
			public int size() {
				return entries.size();
			}

			@Override
			public int hashCode() {
				throw new RuntimeException("Cannot compute HashCode for the key-set of a HashFreeMap");
			}
			
		};
	}

	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public U put(T key, U value) {
		for (Entry<T, U> entry : entries) {
			if (entry.getKey().equals(key)) {
				return entry.setValue(value);
			}
		}
		entries.add(new EntryImpl<T,U>(key, value));
		return null;
	}
	
	/** Same as put, except the caller guarantees that the key is not yet beingused
	 * @param key
	 * @param value
	 * @return null
	 */
	public U putNew(T key, U value) {		
		entries.add(new EntryImpl<T,U>(key, value));
	    return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends T, ? extends U> t) {
		for (Entry<? extends T, ? extends U> entry : t.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
		
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public U remove(Object key) {
		for (Entry<T, U> entry : entries) {
			if (entry.getKey().equals(key)) {
				U result =  entry.getValue();
				entries.remove(entry);
				return result;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		return entries.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection<U> values() {
		return new AbstractCollection<U>() {

			@Override
			public Iterator<U> iterator() {
				final Iterator<Entry<T,U>> entriesIter = entries.iterator();
				return new Iterator<U>() {

					public boolean hasNext() {
						return entriesIter.hasNext();
					}

					public U next() {
						return entriesIter.next().getValue();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
					
				};
			}

			@Override
			public int size() {
				return entries.size();
			}

			@Override
			public int hashCode() {
				throw new RuntimeException("Cannot compute HashCode for the key-set of a HashFreeMap");
			}
			
		};
	}



	
}
