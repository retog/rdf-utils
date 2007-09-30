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
package org.wymiwyg.rdf.molecules.functref.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedBuilder;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedNodeBase;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedNodeImpl;
import org.wymiwyg.rdf.graphs.fgnodes.impl.HashFreeMap;
import org.wymiwyg.rdf.graphs.fgnodes.impl.HashFreeSet;
import org.wymiwyg.rdf.graphs.impl.NodeImpl;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.impl.SimpleContextualMolecule;
import org.wymiwyg.rdf.molecules.impl.SimpleTerminalMolecule;
import org.wymiwyg.rdf.molecules.model.modelref.CandidateNonTerminalPartTriple;
import org.wymiwyg.rdf.molecules.model.modelref.ModelRefGroundedNode;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingTriple;
import org.wymiwyg.rdf.molecules.model.modelref.NonTerminalTriple;

/**
 * @author reto
 * 
 */
public class ReferenceGroundedDecompositionImpl implements
		ReferenceGroundedDecomposition {

	private final static Log log = LogFactory
			.getLog(ReferenceGroundedDecompositionImpl.class);

	private Set<TerminalMolecule> _terminalMolecules;

	private Set<TerminalMolecule> terminalMolecules;

	private Set<FunctionallyGroundedNode> functionallyGroundedNodes;

	private Set<MaximumContextualMolecule> contextualMolecules;

	// replaceFunctionallyGroundedAndClassify will fill this out
	Set<ModelReferencingTriple> contextualPartTriples = new HashSet<ModelReferencingTriple>();

	FunctionallyGroundedBuilder fgBuilder = new FunctionallyGroundedBuilder();

	public ReferenceGroundedDecompositionImpl(ModelReferencingDecomposition base) {
		_terminalMolecules = new HashSet<TerminalMolecule>();
		for (Triple triple : base.getNaturallyGroundedTriples()) {
			_terminalMolecules.add(new SETerminalMolecule(triple));
		}
		Set<ModelReferencingTriple> nonNonTerminalTriples = new HashSet<ModelReferencingTriple>();
		nonNonTerminalTriples.addAll(base.getNonFunctionalTriples());
		Set<CandidateNonTerminalPartTriple> editableCandidateSet = new HashSet<CandidateNonTerminalPartTriple>(
				base.getCandidateNonTerminalPartTriples());
		Map<ModelRefGroundedNode, ? extends FunctionallyGroundedNode> modelNodeReferenceNodeMap = prepareFunctionallyGroundedNodes(
				base.getNonTerminalTriples(), editableCandidateSet);
		functionallyGroundedNodes = Collections
				.unmodifiableSet(new HashSet<FunctionallyGroundedNode>(
						modelNodeReferenceNodeMap.values()));
		nonNonTerminalTriples.addAll(editableCandidateSet);
		replaceFunctionallyGroundedAnClassify(nonNonTerminalTriples,
				modelNodeReferenceNodeMap);
		contextualMolecules = Collections
				.unmodifiableSet(mergeContextualParts());

		terminalMolecules = Collections.unmodifiableSet(_terminalMolecules);

		log.info("Reference grounded decomposition created");
		if (log.isDebugEnabled()) {
			log.debug("ReferenceGroundedDecomposition fg-nodes: "+this.getFunctionallyGroundedNodes());
		}
	}

	/**
	 * @return contextual molecules from contextualPartTriples
	 */
	private Set<MaximumContextualMolecule> mergeContextualParts() {
		Set<MaximumContextualMolecule> resultSet = new HashSet<MaximumContextualMolecule>();
		// Create map fgnodes-triples
		Map<ModelRefGroundedNode, Set<ModelReferencingTriple>> node2TripleMap = new HashMap<ModelRefGroundedNode, Set<ModelReferencingTriple>>();
		for (ModelReferencingTriple modelRefTriple : contextualPartTriples) {
			for (ModelRefGroundedNode node : modelRefTriple
					.getModelRefGroungetdedNodes()) {
				Set<ModelReferencingTriple> triples4Node = node2TripleMap
						.get(node);
				if (triples4Node == null) {
					triples4Node = new HashSet<ModelReferencingTriple>();
					node2TripleMap.put(node, triples4Node);
				}
				triples4Node.add(modelRefTriple);
			}
		}
		// start with first triple and recursively add connected, remove triples
		// added
		while (node2TripleMap.size() > 0) {
			resultSet.add(getAContextualMolecule(node2TripleMap));
		}
		return resultSet;
	}

	/**
	 * @param node2TripleMap
	 * @param contextualPartTriples2
	 * @return
	 */
	/*
	 * take one Node, add correspoding triples to Collection, remove map-entry.
	 * for every node do the same, if node no longe in map don't add any triple.
	 * Skolemize/Detach right away?
	 */
	private final MaximumContextualMolecule getAContextualMolecule(
			Map<ModelRefGroundedNode, Set<ModelReferencingTriple>> node2TripleMap) {
		Iterator<Entry<ModelRefGroundedNode, Set<ModelReferencingTriple>>> entryIter = node2TripleMap
				.entrySet().iterator();
		Entry<ModelRefGroundedNode, Set<ModelReferencingTriple>> entry = entryIter
				.next();
		entryIter.remove();
		Set<Triple> contextulaMoleculeTriples = new HashSet<Triple>();
		Map<ModelRefGroundedNode, Node> detachMap = new HashMap<ModelRefGroundedNode, Node>();
		detachMap.put(entry.getKey(), new NodeImpl());
		// Set<ModelRefGroundedNode> processedNodes = new
		// HashSet<ModelRefGroundedNode>();
		// processedNodes.add(entry.getKey());
		// Stack<ModelRefGroundedNode> processStack = new
		// Stack<ModelRefGroundedNode>();
		for (ModelReferencingTriple triple : entry.getValue()) {
			Node subject = triple.getSubject();
			if (subject instanceof ModelRefGroundedNode) {
				subject = detachNodeIntoContextual(
						(ModelRefGroundedNode) subject, node2TripleMap,
						detachMap, contextulaMoleculeTriples);
			}
			Node object = triple.getObject();
			if (object instanceof ModelRefGroundedNode) {
				object = detachNodeIntoContextual(
						(ModelRefGroundedNode) object, node2TripleMap,
						detachMap, contextulaMoleculeTriples);
			}
			contextulaMoleculeTriples.add(new TripleImpl(subject, triple
					.getPredicate(), object));
		}
		SimpleContextualMolecule result = new SimpleContextualMolecule();
		result.addAll(contextulaMoleculeTriples);
		result.markFinalized();
		return result;
	}

	/**
	 * @param modelRefNode
	 * @param node2TripleMap
	 * @param detachMap
	 * @param contextulaMoleculeTriples
	 * @return
	 */
	private Node detachNodeIntoContextual(
			ModelRefGroundedNode modelRefNode,
			Map<ModelRefGroundedNode, Set<ModelReferencingTriple>> node2TripleMap,
			Map<ModelRefGroundedNode, Node> detachMap,
			Set<Triple> contextulaMoleculeTriples) {
		Node result = detachMap.get(modelRefNode);
		if (result != null) {
			return result;
		}
		result = new NodeImpl();
		detachMap.put(modelRefNode, result);
		Set<ModelReferencingTriple> modelTriples = node2TripleMap
				.get(modelRefNode);
		node2TripleMap.remove(modelRefNode);
		for (ModelReferencingTriple triple : modelTriples) {
			Node subject = triple.getSubject();
			if (subject instanceof ModelRefGroundedNode) {
				subject = detachNodeIntoContextual(
						(ModelRefGroundedNode) subject, node2TripleMap,
						detachMap, contextulaMoleculeTriples);
			}
			Node object = triple.getObject();
			if (object instanceof ModelRefGroundedNode) {
				object = detachNodeIntoContextual(
						(ModelRefGroundedNode) object, node2TripleMap,
						detachMap, contextulaMoleculeTriples);
			}
			contextulaMoleculeTriples.add(new TripleImpl(subject, triple
					.getPredicate(), object));
		}
		return result;
	}

	/**
	 * Replaces the nodes with fgns and puts the triple into _terminalMolecules
	 * or contextualPartTriples
	 * 
	 * @param modelNodeReferenceNodeMap
	 */
	private void replaceFunctionallyGroundedAnClassify(
			Set<ModelReferencingTriple> modelReferencingTriples,
			Map<ModelRefGroundedNode, ? extends FunctionallyGroundedNode> modelNodeReferenceNodeMap) {
		for (ModelReferencingTriple triple : modelReferencingTriples) {
			ModelReferencingTriple replacement = replaceInTriple(triple,
					modelNodeReferenceNodeMap);
			if (replacement.getModelRefGroungetdedNodes().size() > 0) {
				contextualPartTriples.add(replacement);
			} else {
				SimpleTerminalMolecule terminalMolecule = new SimpleTerminalMolecule();
				terminalMolecule.add(replacement);
				terminalMolecule.markFinalized();
				_terminalMolecules.add(terminalMolecule);
			}
		}

	}

	/**
	 * @param nonTerminalTriples
	 * @param candidateNonTerminalPartTriples
	 *            the used candidates are removed from the collection
	 * @return
	 */
	private Map<ModelRefGroundedNode, ? extends FunctionallyGroundedNode> prepareFunctionallyGroundedNodes(
			Set<NonTerminalTriple> nonTerminalTriples,
			Set<CandidateNonTerminalPartTriple> candidateNonTerminalPartTriples) {
		Map<ModelRefGroundedNode, FunctionallyGroundedNodeImpl> origAfgn2FgnMap = new HashMap<ModelRefGroundedNode, FunctionallyGroundedNodeImpl>();
		// first the easy ones
		for (NonTerminalTriple currentNtTriple : nonTerminalTriples) {
			ModelRefGroundedNode origAfgn = currentNtTriple.getAfgn();
			addNtTriple2FGMap(origAfgn2FgnMap, origAfgn, currentNtTriple);
		}

		// now see what we can do with the candidates
		while (true) { // will break when no new fgn created
			boolean newFGNCreated = false;
			// for (CandidateNonTerminalPartTriple currentCandidate :
			// remainingCandidates) {
			for (Iterator<CandidateNonTerminalPartTriple> iter = candidateNonTerminalPartTriples
					.iterator(); iter.hasNext();) {
				CandidateNonTerminalPartTriple currentCandidate = iter.next();
				ModelRefGroundedNode groundingNode = currentCandidate
						.getGroundingNode();
				if (origAfgn2FgnMap.containsKey(groundingNode)) {
					if (log.isDebugEnabled()) {
						log.debug("origAfgn2FgnMap contains " + groundingNode);
					}
					ModelRefGroundedNode origAfgn = currentCandidate
							.getCandidateAfgn();
					// for now don't replace groundingNode with
					// result.get(groundingNode), this will be done after
					// merging fg-nodes
					/*
					 * Triple replaceTriple = replaceInTriple(currentCandidate,
					 * groundingNode, origAfgn2FgnMap.get(groundingNode));
					 * newFGNCreated = addNtTriple2FGMap(origAfgn2FgnMap,
					 * origAfgn, replaceTriple);
					 */
					if (addNtTriple2FGMap(origAfgn2FgnMap, origAfgn,
							currentCandidate)) {
						newFGNCreated = true;
					}
					iter.remove();
				} else {
					if (log.isDebugEnabled()) {
						log.debug("origAfgn2FgnMap does not contain "
								+ groundingNode);
					}
				}
			}
			if (!newFGNCreated) {
				break;
			}
		}

		Map<ModelRefGroundedNode, FunctionallyGroundedNode> unMergedFgNodes = new HashMap<ModelRefGroundedNode, FunctionallyGroundedNode>();
		// Set<ModelRefGroundedNode> removingKeys = new
		// HashSet<ModelRefGroundedNode>();
		for (Entry<ModelRefGroundedNode, FunctionallyGroundedNodeImpl> entry : origAfgn2FgnMap
				.entrySet()) {
			FunctionallyGroundedNodeBase value = entry.getValue();
			// value.markFinalized();
			unMergedFgNodes.put(entry.getKey(), value);
		}
		unMergedFgNodes = replaceModelNodesInFgNodes(unMergedFgNodes);
		//so we dont mix
		fgBuilder.release();
		//no longer needs to be hash-free
		unMergedFgNodes = new HashMap<ModelRefGroundedNode, FunctionallyGroundedNode>(unMergedFgNodes);
		Map<ModelRefGroundedNode, FunctionallyGroundedNode> result = new FgNodeMerger<ModelRefGroundedNode>(
				unMergedFgNodes);
		// TODO not sure if this is still needed
		//result = replaceModelNodesInFgNodes(result);
		return result;
	}

	/**
	 * @param result
	 */
	private Map<ModelRefGroundedNode, FunctionallyGroundedNode> replaceModelNodesInFgNodes(
			Map<ModelRefGroundedNode, FunctionallyGroundedNode> map) {
		// we must deal whith self containing fg-nodes
		HashFreeMap<ModelRefGroundedNode, FunctionallyGroundedNodeImpl> workingMap = new HashFreeMap<ModelRefGroundedNode, FunctionallyGroundedNodeImpl>();
		HashFreeMap<FunctionallyGroundedNode, FunctionallyGroundedNode> map2WorkingMap = new HashFreeMap<FunctionallyGroundedNode, FunctionallyGroundedNode>();
		// this is to have only one instance per node
		Map<Set<NonTerminalMolecule>, FunctionallyGroundedNodeImpl> molecules2NodeMap = new HashFreeMap<Set<NonTerminalMolecule>, FunctionallyGroundedNodeImpl>();
		for (Entry<ModelRefGroundedNode, ? extends FunctionallyGroundedNode> entry : map
				.entrySet()) {
			Set<NonTerminalMolecule> groundingMolecules = entry.getValue()
					.getGroundingMolecules();

			FunctionallyGroundedNodeImpl fgImpl = molecules2NodeMap
					.get(groundingMolecules);
			if (fgImpl == null) {
				fgImpl = fgBuilder.createFGNode(groundingMolecules);
				molecules2NodeMap.put(groundingMolecules, fgImpl);
			}
			map2WorkingMap.putNew(entry.getValue(), fgImpl);
			workingMap.putNew(entry.getKey(), fgImpl);
		}

		for (Entry<ModelRefGroundedNode, FunctionallyGroundedNodeImpl> entry : workingMap
				.entrySet()) {
			FunctionallyGroundedNodeImpl fgNode = entry.getValue();
			HashFreeSet<NonTerminalMolecule> replacementMolecules = new HashFreeSet<NonTerminalMolecule>();
			for (NonTerminalMolecule molecule : fgNode.getGroundingMolecules()) {
				NonTerminalMolecule replacementMolecule = fgBuilder
						.createNTMolecule(NonTerminalMolecule.GROUNDED_NODE);
				for (Triple triple : molecule) {
					// TODO using map the original not-referenced node is
					// inserted (testIFPHash2)
					Triple replacementTriple = replaceInTriple(triple,
							workingMap);
					replacementMolecule.add(replacementTriple);
					if (replacementTriple.getSubject() instanceof ModelRefGroundedNode) {
						throw new RuntimeException("No mapping for subject in "
								+ triple);
					}
					if (replacementTriple.getObject() instanceof ModelRefGroundedNode) {
						throw new RuntimeException("No mapping for object in "
								+ triple);
					}

				}
				replacementMolecules.addNew(replacementMolecule);

			}
			fgNode.removeAllMolecules();
			for (NonTerminalMolecule replacementMolecule : replacementMolecules) {
				fgNode.addMolecule(replacementMolecule);
			}
			if (log.isDebugEnabled()) {
				log.debug("new fg node " + fgNode);
			}
		}

		// second round to replace the map-fg nodes with the processed ones
		for (Entry<ModelRefGroundedNode, FunctionallyGroundedNodeImpl> entry : workingMap
				.entrySet()) {
			FunctionallyGroundedNodeImpl fgNode = entry.getValue();
			Set<NonTerminalMolecule> replacementMolecules = new HashFreeSet<NonTerminalMolecule>();
			for (NonTerminalMolecule molecule : fgNode.getGroundingMolecules()) {
				NonTerminalMolecule replacementMolecule = fgBuilder
						.createNTMolecule(NonTerminalMolecule.GROUNDED_NODE);
				for (Triple triple : molecule) {
					Triple replacementTriple = replaceInTriple(triple,
							map2WorkingMap);
					replacementMolecule.add(replacementTriple);

				}
				replacementMolecules.add(replacementMolecule);

			}
			fgNode.removeAllMolecules();
			for (NonTerminalMolecule replacementMolecule : replacementMolecules) {
				fgNode.addMolecule(replacementMolecule);
			}
		}
		/*
		 * for (FunctionallyGroundedNodeImpl node : workingMap.values()) {
		 * node.markFinalized(); }
		 */
		Map<ModelRefGroundedNode, FunctionallyGroundedNode> result = new HashMap<ModelRefGroundedNode, FunctionallyGroundedNode>(
				workingMap);

		return result;
	}

	/**
	 * 
	 * @param map
	 * @param origAfgn
	 * @param currentNtTriple
	 * @return if a new fgn was created
	 */
	private boolean addNtTriple2FGMap(
			Map<ModelRefGroundedNode, FunctionallyGroundedNodeImpl> map,
			ModelRefGroundedNode origAfgn, Triple currentNtTriple) {
		final boolean result;
		FunctionallyGroundedNodeImpl fgn;
		;
		if (map.containsKey(origAfgn)) {
			fgn = map.get(origAfgn);
			result = false;
		} else {
			fgn = fgBuilder.createFGNode();
			map.put(origAfgn, fgn);
			result = true;
		}
		NonTerminalMolecule ntMolecule = fgBuilder.createNTMolecule(origAfgn);
		ntMolecule.add(currentNtTriple);
		// ntMolecule.markFinalized();
		fgn.addMolecule(ntMolecule);
		return result;
	}

	private ModelReferencingTriple replaceInTriple(
			ModelReferencingTriple triple,
			Map<? extends Node, ? extends Node> map) {
		Node subject = triple.getSubject();
		Node object = triple.getObject();
		PropertyNode predicate = triple.getPredicate();
		boolean somethingReplaced = false;
		Node replacementSubject = map.get(subject);
		if (replacementSubject != null) {
			subject = replacementSubject;
			somethingReplaced = true;
		}
		Node replacementObject = map.get(object);
		if (replacementObject != null) {
			object = replacementObject;
			somethingReplaced = true;
		}
		if (somethingReplaced) {
			return new ModelReferencingTripleImpl(subject, predicate, object);
		} else {
			return triple;
		}
	}

	private static Triple replaceInTriple(Triple triple,
			Map<? extends Node, ? extends Node> map) {
		Node subject = triple.getSubject();
		Node object = triple.getObject();
		PropertyNode predicate = triple.getPredicate();
		boolean somethingReplaced = false;
		Node replacementSubject = map.get(subject);
		if (replacementSubject != null) {
			subject = replacementSubject;
			somethingReplaced = true;
		}
		Node replacementObject = map.get(object);
		if (replacementObject != null) {
			object = replacementObject;
			somethingReplaced = true;
		}
		if (somethingReplaced) {
			return new TripleImpl(subject, predicate, object);
		} else {
			return triple;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.molecules.ReferenceGroundedDecomposition#getTerminalMolecules()
	 */
	public Set<TerminalMolecule> getTerminalMolecules() {
		return terminalMolecules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.molecules.ReferenceGroundedDecomposition#getFunctionallyGroundedNode()
	 */
	public Set<FunctionallyGroundedNode> getFunctionallyGroundedNodes() {
		return functionallyGroundedNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.molecules.ReferenceGroundedDecomposition#getContextualMolecules()
	 */
	public Set<MaximumContextualMolecule> getContextualMolecules() {
		return contextualMolecules;
	}

}
