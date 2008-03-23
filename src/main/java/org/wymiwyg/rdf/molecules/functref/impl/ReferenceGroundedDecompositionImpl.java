/*
 * Copyright 2008 WYMIWYG (http://wymiwyg.org)
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
package org.wymiwyg.rdf.molecules.functref.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.LiteralNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionalPropertyNode;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.InverseFunctionalPropertyNode;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.impl.ContextualMoleculeImpl;
import org.wymiwyg.rdf.molecules.impl.Finalizable;
import org.wymiwyg.rdf.molecules.impl.FunctionallyGroundedNodeImpl;
import org.wymiwyg.rdf.molecules.impl.NonTerminalMoleculeImpl;
import org.wymiwyg.rdf.molecules.impl.TerminalMoleculeImpl;



/**
 * Tries a cleaner implementation (still) relaying on Properties being instances
 * of different Types (functional and inverse functional)
 * 
 * @author reto
 * 
 */
public class ReferenceGroundedDecompositionImpl implements
		ReferenceGroundedDecomposition {

	private final static Log log = LogFactory.getLog(ReferenceGroundedDecompositionImpl.class);
	private Map<Node, Set<Triple>> groundeNodeMap = new HashMap<Node, Set<Triple>>();
	private Set<FunctionallyGroundedNode> functionallyGroundedNodes;
	private Set<TerminalMolecule> terminalMolecules;
	private Set<MaximumContextualMolecule> contextualMolecules;

	/**
	 * @param triples 
	 * 
	 */
	public ReferenceGroundedDecompositionImpl(Collection<Triple> triples) {
		Collection<Triple> remainingTriples = triples;
		while (remainingTriples.size() > (remainingTriples = extractNonTerminalTriples(remainingTriples)).size()) {
			//log.info("remaning triples: " + remainingTriples.size());
		}

		Map<Node, ? extends FunctionallyGroundedNode> fgNodesMap = createFunctionallyGroundedNodes();

		terminalMolecules = new HashSet<TerminalMolecule>();
		Map<Node, ContextualMoleculeImpl> contextualMoleculeMap = new HashMap<Node, ContextualMoleculeImpl>();

		for (Triple triple : remainingTriples) {
			Node subject = triple.getSubject();
			Node object = triple.getObject();
			if (fgNodesMap.containsKey(subject)) {
				subject = fgNodesMap.get(subject);
			}
			if (fgNodesMap.containsKey(object)) {
				object = fgNodesMap.get(object);
			}
			triple = new TripleImpl(subject, triple.getPredicate(),
					object);
			if (subject instanceof GroundedNode) {
				if (object instanceof GroundedNode) {
					terminalMolecules.add(new TerminalMoleculeImpl(triple));
				} else {
					ContextualMoleculeImpl contextualMolecule = contextualMoleculeMap.get(object);
					if (contextualMolecule == null) {
						contextualMolecule = new ContextualMoleculeImpl();
						contextualMoleculeMap.put(object, contextualMolecule);
					}
					contextualMolecule.add(triple);
				}
			} else {
				if (object instanceof GroundedNode) {
					ContextualMoleculeImpl contextualMolecule = contextualMoleculeMap.get(subject);
					if (contextualMolecule == null) {
						contextualMolecule = new ContextualMoleculeImpl();
						contextualMoleculeMap.put(subject, contextualMolecule);
					}
					contextualMolecule.add(triple);
				} else {
					ContextualMoleculeImpl contextualMoleculeForSubject = contextualMoleculeMap.get(subject);
					ContextualMoleculeImpl contextualMoleculeForObject = contextualMoleculeMap.get(object);

					if ((contextualMoleculeForSubject == null) && (contextualMoleculeForObject == null)) {
						ContextualMoleculeImpl contextualMolecule = new ContextualMoleculeImpl();
						contextualMoleculeMap.put(subject, contextualMolecule);
						contextualMoleculeMap.put(object, contextualMolecule);
						contextualMolecule.add(triple);
					} else {
						if (contextualMoleculeForSubject == null) {
							contextualMoleculeMap.put(subject, contextualMoleculeForObject);
							contextualMoleculeForObject.add(triple);
						} else {
							if (contextualMoleculeForObject == null) {
								contextualMoleculeMap.put(object, contextualMoleculeForSubject);
								contextualMoleculeForSubject.add(triple);
							} else {
								Node[] ubngn = contextualMoleculeForObject.getUsedButNotGroundedNodes();
								//all other have to reference to the modified contextualMolecule as well
								contextualMoleculeForSubject.add(triple);
								for (Node node : ubngn) {
									ContextualMoleculeImpl moleculeToBeAbsorbed = contextualMoleculeMap.get(node);
									if (moleculeToBeAbsorbed != null) {
										contextualMoleculeForSubject.addAll(moleculeToBeAbsorbed);
									}
									contextualMoleculeMap.put(node, contextualMoleculeForSubject);
								}
							}
						}
					}
				}
			}
		}
		functionallyGroundedNodes = new HashSet<FunctionallyGroundedNode>(
				fgNodesMap.size());
		functionallyGroundedNodes.addAll(fgNodesMap.values());
		fgNodesMap = null;
		contextualMolecules = new HashSet<MaximumContextualMolecule>();
		final Iterator<ContextualMoleculeImpl> iterator = contextualMoleculeMap.values().iterator();
		while (iterator.hasNext()) {
			ContextualMoleculeImpl molecule = iterator.next();
			molecule.markFinalized();
			contextualMolecules.add(molecule);
		}

	}

	/**
	 * @return
	 */
	private Map<Node, ? extends FunctionallyGroundedNode> createFunctionallyGroundedNodes() {

		Collection<Finalizable> finalizableObjects = new ArrayList<Finalizable>();
		Map<Node, FunctionallyGroundedNodeImpl> resultMap = new HashMap<Node, FunctionallyGroundedNodeImpl>(
				groundeNodeMap.size());
		Iterator<Node> nodeIter = groundeNodeMap.keySet().iterator();
		while (nodeIter.hasNext()) {
			FunctionallyGroundedNodeImpl functionallyGroundedNodeImpl = new FunctionallyGroundedNodeImpl();
			resultMap.put(nodeIter.next(), functionallyGroundedNodeImpl);
			finalizableObjects.add((Finalizable) functionallyGroundedNodeImpl);
		}
		Iterator<Entry<Node, Set<Triple>>> entryIter = groundeNodeMap.entrySet().iterator();
		while (entryIter.hasNext()) {
			final Entry<Node, Set<Triple>> entry = entryIter.next();
			final Node groundedNode = entry.getKey();
			FunctionallyGroundedNodeImpl functionallyGroundedNodeImpl = resultMap.get(groundedNode);
			Set<Triple> groundingTriples = entry.getValue();
			Iterator<Triple> iterator = groundingTriples.iterator();
			while (iterator.hasNext()) {
				Triple triple = iterator.next();
				NonTerminalMoleculeImpl nonTerminalMolecule = new NonTerminalMoleculeImpl(
						groundedNode, triple, resultMap);
				functionallyGroundedNodeImpl.addGroundingMolecule(nonTerminalMolecule);
				finalizableObjects.add((Finalizable) nonTerminalMolecule);
			}
		}
		for (Finalizable finalizable : finalizableObjects) {
			finalizable.markFinalized();
		}
		/*
		 * Map<Node, FunctionallyGroundedNode> result = new HashMap<Node,
		 * FunctionallyGroundedNode>( groundeNodeMap.size()); for (Entry<Node,
		 * FunctionallyGroundedNodeImpl> entry : resultMap .entrySet()) {
		 * result.put(entry.getKey(), entry.getValue()); }
		 */
		//return new FgNodeMerger(resultMap);
		return resultMap;
	}

	/**
	 * @param remainingTriples
	 * @return
	 */
	private Collection<Triple> extractNonTerminalTriples(
			Collection<Triple> triples) {
		Collection<Triple> result = new ArrayList<Triple>();
		for (Triple triple : triples) {
			Node groundedNode = getGroundedNode(triple);
			if (groundedNode == null) {
				result.add(triple);
			} else {
				addToGroundedNodes(groundedNode, triple);
			}
		}
		return result;
	}

	/**
	 * @param groundedNode
	 * @param triple
	 */
	private void addToGroundedNodes(Node groundedNode, Triple triple) {
		Set<Triple> tripleSet = groundeNodeMap.get(groundedNode);
		if (tripleSet == null) {
			tripleSet = new HashSet<Triple>();
			groundeNodeMap.put(groundedNode, tripleSet);
		}
		tripleSet.add(triple);

	}

	/**
	 * @param triple
	 * @return the (inverse) functionally grounded node or null
	 */
	private Node getGroundedNode(Triple triple) {
		if (triple.getPredicate() instanceof FunctionalPropertyNode) {
			if (isGroundedNode(triple.getSubject())) {
				return triple.getObject();
			}
		}
		if (triple.getPredicate() instanceof InverseFunctionalPropertyNode) {
			if (isGroundedNode(triple.getObject())) {
				return triple.getSubject();
			}
		}
		return null;
	}

	/**
	 * @param object
	 * @return
	 */
	private boolean isGroundedNode(Node node) {
		if (node instanceof LiteralNode) {
			return true;
		}
		if (node instanceof GroundedNode) {
			//log.info("grounded node in original triple set (why isn't the graph anonymized?)");
			return true;
		}

		return groundeNodeMap.containsKey(node);
	}

	public Set<MaximumContextualMolecule> getContextualMolecules() {
		return contextualMolecules;
	}

	public Set<FunctionallyGroundedNode> getFunctionallyGroundedNodes() {
		return functionallyGroundedNodes;
	}

	public Set<TerminalMolecule> getTerminalMolecules() {
		return terminalMolecules;
	}
}
