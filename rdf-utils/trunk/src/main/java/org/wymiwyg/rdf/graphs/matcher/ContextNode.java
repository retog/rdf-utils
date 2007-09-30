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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;

/**
 * @author reto
 * 
 */
public class ContextNode implements Node {

	private Set<TriplePattern> patternSet = null;

	private int complexCount = 0;

	private int easyCount = 0;

	private Node node;

	IntArrayList contextHashLevelValues = new IntArrayList();
	int currentContentHashLevel = 0;

	private int fakeHash;

	private boolean fakeHashed;
	/**
	 * @param node
	 * @param node
	 */
	public ContextNode(Node node) {
		patternSet = new HashSet<TriplePattern>();
		this.node = node;
	}

	/**
	 * @param triple
	 * @param complex
	 *            whether the other non-predicate node is anoynmous
	 */
	public void addUsage(Triple triple, boolean complex) {
		if (complex) {
			complexCount++;
		} else {
			easyCount++;
		}
		patternSet.add(new TriplePattern(triple, this));

	}

	public Node getNode() {
		return node;
	}

	/**
	 * @param other
	 * @return
	 */
	public boolean samePatternAs(ContextNode other) {
		if (patternSet == null) {
			ensurePatternSetAvailable();
		}
		if (other.patternSet == null) {
			other.ensurePatternSetAvailable();
		}
		return patternSet.equals(other.patternSet);
	}

	/**
	 * @return
	 */
	private void ensurePatternSetAvailable() {
		/*if (patternSet == null) {
			patternSet = new HashSet();
			for (Iterator iter = easySet.iterator(); iter.hasNext();) {
				Triple current = (Triple) iter.next();
				patternSet.add(new TriplePattern(current, this));
			}
			for (Iterator iter = complexSet.iterator(); iter.hasNext();) {
				Triple current = (Triple) iter.next();
				patternSet.add(new TriplePattern(current, this));
			}
		}*/
		//return patternSet;
	}

	/**
	 * @return
	 */
	/*public int tripleCount() {
		return complexSet.size() + easySet.size();
	}*/

	public String toString() {
		return node.toString();
		//return "_:"+hashCode();
		/*return "Node used in " + easySet.size() + " easy, and "
				+ complexSet.size() + " complex triples.";*/
	}

	/**
	 * @return
	 */
	public int contextHash() {
		if (fakeHashed) {
			return fakeHash;
		}
		
		return contextHash(currentContentHashLevel);
	}

	/**
	 * @param currentContentHashLevel2
	 * @return
	 */
	int contextHash(int level) {
		if (contextHashLevelValues.size() <= level) {
			computeContextHash();
		}
		if (fakeHashed) {
			return fakeHash;
		}
		if (level == -1) {
			//return 0;
			return (complexCount << 4) ^ easyCount;
		}
		return contextHashLevelValues.get(level);
	}

	/**
	 * 
	 */
	private void computeContextHash() {
		int hash;
		hash = 0;
		ensurePatternSetAvailable();
		for (Iterator<TriplePattern> iter = patternSet.iterator(); iter.hasNext();) {
			TriplePattern current = iter.next();
			hash += current.contextHash(currentContentHashLevel -1);
		}
		if (contextHashLevelValues.size() > 0)	{
			//hash ^= contextHashLevelValues.get(contextHashLevelValues.size() -1);
		}
		contextHashLevelValues.add(hash);
	}

	/**
	 * 
	 */
	public void refineHash() {
		if (!fakeHashed) {
			currentContentHashLevel++;
		}
	}

	/**
	 * 
	 */
	public void resetHash() {
		if (!fakeHashed) {
			if (contextHashLevelValues.size() == currentContentHashLevel) {
				contextHashLevelValues.removeLast();
			} 
			currentContentHashLevel--;
		}
	}

	/**
	 * @param fakeHash
	 */
	public void setFakeHash(int fakeHash) {
		fakeHashed = true;
		this.fakeHash = fakeHash;	
	}
	
	public void removeFakeHash() {
		fakeHashed = false;
	}

}
