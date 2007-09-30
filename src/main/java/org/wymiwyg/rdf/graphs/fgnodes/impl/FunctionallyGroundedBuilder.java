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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.impl.SimpleNonTerminalMolecule;

/**
 * @author reto
 * 
 */
public class FunctionallyGroundedBuilder {
	
	//private static final Log log = LogFactory.getLog(FunctionallyGroundedBuilder.class);

	class BuilderFunctionallyGroundedNodeImpl extends
			FunctionallyGroundedNodeImpl {

		public BuilderFunctionallyGroundedNodeImpl() {
			fgNodes.add(this);
			// this.creator = creator;
		}

		@Override
		public void addMolecule(NonTerminalMolecule molecule) {
			if (!(molecule instanceof BuilderNonTerminalMoleculeImpl)) {
				throw new RuntimeException("Not a BuilderNonTerminalMoleculeImpl");
			}
			BuilderNonTerminalMoleculeImpl builderNonTerminalMoleculeImpl = (BuilderNonTerminalMoleculeImpl) molecule;
			if (!builderNonTerminalMoleculeImpl.getBuilder().equals(FunctionallyGroundedBuilder.this)) {
				throw new RuntimeException("Not from the same builder");
			}
			super.addMolecule(molecule);
		}

		/**
		 * @param resultMolecules
		 */
		public BuilderFunctionallyGroundedNodeImpl(
				Set<NonTerminalMolecule> resultMolecules) {
			super(resultMolecules);
			fgNodes.add(this);
			// this.creator = creator;
		}

		public synchronized int hashCode() {
			//to avoid recursion and prevent warning that it is not finalized
			if (!isFinalized()) {
				return 1;//possible recursion (MoleculebasedLeanifierTest.testFile8) computeHash();
			} else {
				return super.hashCode();
			}
		}


		/**
		 * @return
		 */
		public FunctionallyGroundedBuilder getBuilder() {
			return FunctionallyGroundedBuilder.this;
		}

	}

	private class BuilderNonTerminalMoleculeImpl extends
			SimpleNonTerminalMolecule {


		/**
		 * @param afgn
		 */
		public BuilderNonTerminalMoleculeImpl(Node afgn) {
			super(afgn);
			ntMolecules.add(this);
		}
		
		private FunctionallyGroundedBuilder getBuilder() {
			return FunctionallyGroundedBuilder.this;
		}

		/*public int hashCode() {
			if (!released) {
				if (isFinalized()) {
					throw new RuntimeException("Not release but finalized");
				}
				return 1;
			} else {
				if (isFinalized()) {
					return super.hashCode();
				} else {
					return computeHash();
				}
			}
		}*/

		@Override
		public boolean add(Triple triple) {
			checkSameBuilder(triple.getSubject());
			checkSameBuilder(triple.getObject());
			return super.add(triple);
		}

		/**
		 * @param node
		 */
		private void checkSameBuilder(Node node) {
			if (node instanceof FunctionallyGroundedNode) {
				if (!(node instanceof BuilderFunctionallyGroundedNodeImpl)) {
					throw new RuntimeException("Not a BuilderNonTerminalMoleculeImpl");
				}
				BuilderFunctionallyGroundedNodeImpl builderFunctionallyGroundedNodeImpl = (BuilderFunctionallyGroundedNodeImpl) node;
				if (!builderFunctionallyGroundedNodeImpl.getBuilder().equals(FunctionallyGroundedBuilder.this)) {
					throw new RuntimeException("Not from the same builder");
				}
			}
			
		}

		/**
		 * 
		 */
		public void release() {
			released = true;

		}

	}

	private Collection<BuilderFunctionallyGroundedNodeImpl> fgNodes = new ArrayList<BuilderFunctionallyGroundedNodeImpl>();

	private Collection<BuilderNonTerminalMoleculeImpl> ntMolecules = new ArrayList<BuilderNonTerminalMoleculeImpl>();

	private Map<NonTerminalMolecule, BuilderNonTerminalMoleculeImpl> nonTermirminalAdoptionMap = new HashFreeMap<NonTerminalMolecule, BuilderNonTerminalMoleculeImpl>();

	private Map<FunctionallyGroundedNode, BuilderFunctionallyGroundedNodeImpl> fgNodeAdoptionMap = new HashMap<FunctionallyGroundedNode, BuilderFunctionallyGroundedNodeImpl>();

	private boolean released = false;

	public FunctionallyGroundedNodeImpl createFGNode() {
		if (released) {
			throw new RuntimeException("allready released");
		}
		return new BuilderFunctionallyGroundedNodeImpl();
	}

	public FunctionallyGroundedNodeImpl createFGNode(
			Set<NonTerminalMolecule> molecules) {
		if (released) {
			throw new RuntimeException("allready released");
		}
		return new BuilderFunctionallyGroundedNodeImpl(molecules);
	}

	public NonTerminalMolecule createNTMolecule(Node afgn) {
		if (released) {
			throw new RuntimeException("allready released");
		}
		return new BuilderNonTerminalMoleculeImpl(afgn);
	}





	private void finalizeNtMolecule() {
		for (BuilderNonTerminalMoleculeImpl molecule : ntMolecules) {
			molecule.markFinalized();
		}

	}

	private void prepareFgNodesForFinalization() {
		for (BuilderFunctionallyGroundedNodeImpl node : fgNodes) {
			node.markFinalized();
		}

	}
	
	private void finalizeFgNodes() {
		for (BuilderFunctionallyGroundedNodeImpl node : fgNodes) {
			node.notifyAllFinalized();
		}

	}

	/**
	 * 
	 */
	public void release() {
		released = true;
		prepareFgNodesForFinalization();
		finalizeNtMolecule();
		finalizeFgNodes();
	}

	/** Return a copy of the molecule from this builder
	 * 
	 * @param ntMolecule
	 * @return
	 */
	public NonTerminalMolecule adopt(NonTerminalMolecule ntMolecule) {
		if (nonTermirminalAdoptionMap.containsKey(ntMolecule)) {
			return nonTermirminalAdoptionMap.get(ntMolecule);
		}
		BuilderNonTerminalMoleculeImpl result = new BuilderNonTerminalMoleculeImpl(NonTerminalMolecule.GROUNDED_NODE);
		nonTermirminalAdoptionMap.put(ntMolecule, result);
		for (Triple triple : ntMolecule) {
			result.add(adopt(triple));
		}
		return result;
	}

	/** adoptd subject and/or object if functionally grounded
	 * @param triple
	 * @return
	 */
	private Triple adopt(Triple triple) {
		Node subject = triple.getSubject();
		boolean modified =  false;
		if (subject instanceof FunctionallyGroundedNode) {
			subject = adopt((FunctionallyGroundedNode)subject);
			modified = true;
		}
		Node object = triple.getObject();
		if (object instanceof FunctionallyGroundedNode) {
			object = adopt((FunctionallyGroundedNode)object);
			modified = true;
		}
		if (modified) {
			return new TripleImpl(subject, triple.getPredicate(), object);
		} else {
			return triple;
		}
	}

	/**
	 * @param node
	 * @return
	 */
	public FunctionallyGroundedNodeBase adopt(FunctionallyGroundedNode node) {
		if (fgNodeAdoptionMap.containsKey(node)) {
			return fgNodeAdoptionMap.get(node);
		}
		BuilderFunctionallyGroundedNodeImpl result = new BuilderFunctionallyGroundedNodeImpl();
		fgNodeAdoptionMap.put(node, result);
		//TODO during the following loop only == nodes should be equals, or fg-nodes should keep a collection of nt-molecules with duplicates
		for (NonTerminalMolecule ntMolecule : node.getGroundingMolecules()) {
			result.addMolecule(adopt(ntMolecule));
		}
		
		return result;
	}

}
