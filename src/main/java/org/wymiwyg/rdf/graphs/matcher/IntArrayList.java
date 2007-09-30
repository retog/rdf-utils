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
package org.wymiwyg.rdf.graphs.matcher;


/**
 * @author reto
 *
 */
public class IntArrayList {
	private final static int increaseSize = 10;
	private int size = 0;
	private int[] values = new int[increaseSize];
	
	/* (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public int size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(int value) {
		for (int i = 0; i < size; i++) {
			if (value == values[i]) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(java.lang.Object)
	 */
	public void add(int value) {
		if (values.length == size) {
			int[] newValues = new int[values.length+increaseSize];
			System.arraycopy(values, 0, newValues, 0, size);
			values = newValues;
		}
		values[size] = value;
		size++;
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(int value) {
		// TODO Auto-generated method stub
		return false;
	}

	

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public int get(int index) {
		if (index > size) {
			throw new RuntimeException("out of bounds: requested"+index+" but size is "+size);
		}
		return values[index];
	}

	public void removeLast() {
		size--;
	}

}
