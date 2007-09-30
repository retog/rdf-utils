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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * @author reto
 * 
 */
public class FunctionallyGroundedNodeImpl extends FunctionallyGroundedNodeBase implements FunctionallyGroundedNode {

	static final Log log = LogFactoryImpl
			.getLog(FunctionallyGroundedNodeImpl.class);

	public Set<NonTerminalMolecule> groundingMolecules = new HashFreeSet<NonTerminalMolecule>();


	private List<FunctionallyGroundedNode> perfomingEqualsAgainst = new ArrayList<FunctionallyGroundedNode>();

	boolean hashComputed;

	
	
	public FunctionallyGroundedNodeImpl() {

	}

	/**
	 * The molecules are added by invoking addMolecule, (so that subclasses just
	 * overwrite that)
	 * 
	 * @param groundingMolecules
	 */
	public FunctionallyGroundedNodeImpl(
			Set<NonTerminalMolecule> groundingMolecules) {
		for (NonTerminalMolecule molecule : groundingMolecules) {
			addMolecule(molecule);
		}
	}

	public void addMolecule(NonTerminalMolecule molecule) {
		if (isFinalized()) {
			throw new RuntimeException(
					"modifying not allowed (already finalized)");
		}
		this.groundingMolecules.add(molecule);

		/*
		 * if (groundingMolecules.size() == size) { log.info("adding
		 * "+molecule+" to "+this+" didn't work out");
		 * groundingMolecules.add(molecule); }
		 */
		// because the molecule may contain this fg-node instance which would
		// change hash of existing elements
		// regenerate hash
		/*
		 * Set<NonTerminalMolecule> newSet = new HashSet<NonTerminalMolecule>();
		 * for (NonTerminalMolecule existingMolecule : groundingMolecules) {
		 * newSet.add(existingMolecule); } groundingMolecules = newSet;
		 */

	}

	/**
	 * @param molecule
	 */
	public void removeMolecule(NonTerminalMolecule molecule) {
		if (isFinalized()) {
			throw new RuntimeException(
					"modifying not allowed (already finalized)");
		}
		groundingMolecules.remove(molecule);
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.molecules.FunctionallyGroundedNode#getGroundingMolecules()
	 */
	public Set<NonTerminalMolecule> getGroundingMolecules() {
		/*
		 * if (!finalized) { groundingMolecules = new HashSet<NonTerminalMolecule>(groundingMolecules); }
		 */
		return Collections.unmodifiableSet(groundingMolecules);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode#getOriginalNode()
	 */
	/*
	 * public Node getAnonymousNode() { return anonymousNode; }
	 */

	protected int computeWeakHash() {
		List<FunctionallyGroundedNode> callers = new ArrayList<FunctionallyGroundedNode>();

		return computeHash(callers);
	}

	private int computeHash(List<FunctionallyGroundedNode> callers) {
		for (FunctionallyGroundedNode caller : callers) {
			if (caller == this) {
				return 44;
			}
		}
		/*
		 * if (callers.contains(this)) { return 44; //something different than
		 * 22 which was the hash of every node in a circle }
		 */
		callers.add(this);
		int result = 0;
		for (NonTerminalMolecule molecule : getGroundingMolecules()) {
			result += getMoleculeHash(molecule, callers);
		}
		callers.remove(callers.size() - 1);
		return result;
	}

	/**
	 * @param molecule
	 * @param callers
	 * @return
	 */
	private int getMoleculeHash(NonTerminalMolecule molecule,
			List<FunctionallyGroundedNode> callers) {
		int result = 0;
		for (Iterator<Triple> iter = molecule.iterator(); iter.hasNext();) {
			result += getBlankNodeBlindHash(iter.next(), callers);
		}
		return result;
	}

	/**
	 * @param triple
	 * @param callers
	 * @return
	 */
	// TODO should object/subject be shifted?
	private int getBlankNodeBlindHash(Triple triple,
			List<FunctionallyGroundedNode> callers) {
		int hash = triple.getPredicate().hashCode();
		Node subject = triple.getSubject();
		if (subject instanceof FunctionallyGroundedNodeImpl) {
			hash ^= ((FunctionallyGroundedNodeImpl) subject)
					.computeHash(callers);
		} else {
			if ((subject instanceof GroundedNode)) {// && (!(subject instanceof
				// FunctionallyGroundedNode))){
				hash ^= subject.hashCode();// >> 1;
			}
		}
		Node object = triple.getObject();
		if (object instanceof FunctionallyGroundedNodeImpl) {
			hash ^= ((FunctionallyGroundedNodeImpl) object)
					.computeHash(callers);
		} else {
			if ((object instanceof GroundedNode)) {// && (!(object instanceof
				// FunctionallyGroundedNode))){
				hash ^= object.hashCode();// << 1;
			}
		}
		/*
		 * log.debug("GRAPH: computed BlankNodeBlindHash triple hash " + hash + "
		 * for predicate " + triple.getPredicate());
		 */
		return hash;
	}

	/**
	 * 
	 */
	public void removeAllMolecules() {
		if (isFinalized()) {
			throw new RuntimeException(
					"modifying not allowed (already finalized)");
		}
		groundingMolecules.clear();

	}

	public void markFinalized() {

		super.markFinalized();
	}

	/**
	 * must not be invoked before prepareForFinalization has been invoked on
	 * this and all all fg-nodes this depends on.
	 * 
	 */
	public void notifyAllFinalized() {
		// using HashSet: more efficient + allows computation of set-hash
		groundingMolecules = new HashSet<NonTerminalMolecule>(
				groundingMolecules);

	}


}
