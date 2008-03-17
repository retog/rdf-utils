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
package org.wymiwyg.rdf.graphs;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.wymiwyg.rdf.graphs.matcher.GraphMatcher;

/**
 * @author reto
 * 
 */
public abstract class AbstractGraph extends AbstractCollection<Triple>
		implements Graph {

	private static final Log log = LogFactoryImpl.getLog(AbstractGraph.class);

	private int hash;

	private boolean finalized;

	private boolean hashComputed;

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Graph)) {
			return false;
		}
		//removed as hashfreeset relies on this (do iff fimalized?)
		/*if (hashCode() != obj.hashCode()) {
			return false;
		}*/
		return GraphMatcher.getValidMapping(this, (Graph) obj) != null;
	}


	public synchronized int hashCode() {
		if (finalized) {
			if (!hashComputed) {
				synchronized (this) {
					if (!hashComputed) {
						hash = computeHash();
						hashComputed = true;
					}
				}
			}
			return hash;
		}
		log.warn("requesting hash for unfinalized graph");
		return computeHash();
	}

	protected int computeHash() {
		int result = 0;
		for (Iterator iter = iterator(); iter.hasNext();) {
			result += getBlankNodeBlindHash((Triple) iter.next());
		}
		//log.info("GRAPH: computed hash "+result+" for "+this);
		return result;
	}

	/**
	 * @param triple
	 * @return
	 */
	//TODO should object/subject be shifted?
	private int getBlankNodeBlindHash(Triple triple) {
		int hash = triple.getPredicate().hashCode();
		Node subject = triple.getSubject();

		if ((subject instanceof GroundedNode)) {// && (!(subject instanceof
												// FunctionallyGroundedNode))){
			hash ^= subject.hashCode();// >> 1;
		}
		Node object = triple.getObject();
		if ((object instanceof GroundedNode)) {// && (!(object instanceof
												// FunctionallyGroundedNode))){
			hash ^= object.hashCode();// << 1;
		}
		/*log.info("GRAPH: computed BlankNodeBlindHash triple hash " + hash
				+ " for " + triple);*/
		return hash;
	}

	/**
	 * This method is invoked whne no more triples will be added and the
	 * hashcode of the triple won't chnge i.e. when the contained fg-nodes are
	 * finalizes as well.
	 */
	public void markFinalized() {
		finalized = true;
	}

	protected void verifyNotFinalized() {
		if (finalized) {
			throw new RuntimeException("Graph already finalized");
		}
	}

	@Override
	public boolean addAll(Collection<? extends Triple> c) {
		if (finalized) {
			throw new RuntimeException("Graph already finalized");
		}
		return super.addAll(c);
	}

	@Override
	public void clear() {
		if (finalized) {
			throw new RuntimeException("Graph already finalized");
		}
		super.clear();
	}

	protected boolean isFinalized() {
		return finalized;
	}

}
