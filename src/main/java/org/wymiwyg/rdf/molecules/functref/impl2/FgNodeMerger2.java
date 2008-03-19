/*
 *  Copyright 2008 reto.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.wymiwyg.rdf.molecules.functref.impl2;

import static org.wymiwyg.rdf.molecules.functref.impl2.FgNodeMerger2.mergeFgNodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * This class provides utility method to merge the fgnodes in a map, where the
 * fgnodes are the value. at the end of operation all keys are still present and
 * mapped to an equivalent fg-node
 * 
 * @author reto
 * @param <T>
 * 
 */
public class FgNodeMerger2 {

	private static final Log log = LogFactory.getLog(FgNodeMerger2.class);

	/**
	 * @param <T>
	 * @param originalNodeMap
	 * @return
	 */
	public static <T> Map<T, FunctionallyGroundedNode> mergeFgNodes(
			Map<T, FunctionallyGroundedNode> originalNodeMap) {
		Map<FunctionallyGroundedNode, Set<T>> nodes2Keys = new HashMap<FunctionallyGroundedNode, Set<T>>();
		for (Map.Entry<T, FunctionallyGroundedNode> entry : originalNodeMap
				.entrySet()) {
			final FunctionallyGroundedNode value = entry.getValue();
			Set<T> keySet = nodes2Keys.get(value);
			if (keySet == null) {
				keySet = new HashSet<T>();
				nodes2Keys.put(value, keySet);
			}
			keySet.add(entry.getKey());
		}
		int sizeBeforeMerging = nodes2Keys.size();
		//log.info("Size before merging: " + sizeBeforeMerging);
		mergeFgNodes(nodes2Keys);
		while (nodes2Keys.size() < sizeBeforeMerging) {
			sizeBeforeMerging = nodes2Keys.size();
			if (sizeBeforeMerging > 1)
				mergeFgNodes(nodes2Keys);
		}
		//log.info("Size after merging: " + sizeBeforeMerging);
		Map<T, FunctionallyGroundedNode> result = new HashMap<T, FunctionallyGroundedNode>();
		for (Map.Entry<FunctionallyGroundedNode, Set<T>> nodes2KeyEntry : nodes2Keys
				.entrySet()) {
			FunctionallyGroundedNode node = nodes2KeyEntry.getKey();
			for (T key : nodes2KeyEntry.getValue()) {
				result.put(key, node);

			}
		}
		// ystem.err.println("result: "+result);
		return result;
	}

	static <T> void mergeFgNodes(
			Map<FunctionallyGroundedNode, Set<T>> nodes2Keys) {

		// finding group of equivalent nodes
		Map<NonTerminalMolecule, Set<FunctionallyGroundedNode>> nt2fgsMap = new HashMap<NonTerminalMolecule, Set<FunctionallyGroundedNode>>();
		{
			// this is to look up the entries with a certain set as values.
			Map<Set<FunctionallyGroundedNode>, Set<NonTerminalMolecule>> inverseIndex = new HashMap<Set<FunctionallyGroundedNode>, Set<NonTerminalMolecule>>();
			for (FunctionallyGroundedNode fgNode : nodes2Keys.keySet()) {
				Set<FunctionallyGroundedNode> currentNodeSet = new HashSet<FunctionallyGroundedNode>();
				currentNodeSet.add(fgNode);
				for (NonTerminalMolecule ntMol : fgNode.getGroundingMolecules()) {
					Set<FunctionallyGroundedNode> nodeSet = nt2fgsMap
							.get(ntMol);
					if (nodeSet != null) {
						Set<NonTerminalMolecule> keysOfExistingNodeSet = inverseIndex
						.get(nodeSet);
						Set<FunctionallyGroundedNode> newNodeSet;
						if (nodeSet.containsAll(currentNodeSet)) {
							newNodeSet = nodeSet;
						} else {
							newNodeSet = new HashSet<FunctionallyGroundedNode>(
									nodeSet);
							newNodeSet.addAll(currentNodeSet);
							// replace existing entries pointing to nodeSet
							
							for (NonTerminalMolecule ntKey : keysOfExistingNodeSet) {
								nt2fgsMap.put(ntKey, newNodeSet);
							}


							inverseIndex.remove(nodeSet);
							inverseIndex.put(newNodeSet, keysOfExistingNodeSet);

						}
						{
							// replace existing entries pointing to
							// currentNodeSet
							Set<NonTerminalMolecule> entrySet = inverseIndex
									.get(currentNodeSet);
							if (entrySet != null) {
								for (NonTerminalMolecule ntKey : entrySet) {
									nt2fgsMap.put(ntKey, newNodeSet);
								}
								inverseIndex.remove(currentNodeSet);

								Set<NonTerminalMolecule> newEntrySet = new HashSet<NonTerminalMolecule>(entrySet);
								newEntrySet.addAll(keysOfExistingNodeSet);
								inverseIndex.put(newNodeSet, newEntrySet);
							}
						}

						currentNodeSet = newNodeSet;
					} else {
						nt2fgsMap.put(ntMol, currentNodeSet);
						Set<NonTerminalMolecule> entriesWithCurrentSet = inverseIndex
								.get(currentNodeSet);
						if (entriesWithCurrentSet == null) {
							entriesWithCurrentSet = new HashSet<NonTerminalMolecule>();
							inverseIndex.put(currentNodeSet,
									entriesWithCurrentSet);
						}
						entriesWithCurrentSet.add(ntMol);
					}
				}
			}
		}



		// the fgnodes sharing an nt-molecule are now grouped
		// if at least one group has more than one element
		// for every group with size > 1 we create a new fg-node,
		// update nodes2Keys and

		Set<Set<FunctionallyGroundedNode>> groups = new HashSet<Set<FunctionallyGroundedNode>>(
				nt2fgsMap.values());

		nt2fgsMap = null;
		Map<FunctionallyGroundedNode, FunctionallyGroundedNode> old2NewMap = new HashMap<FunctionallyGroundedNode, FunctionallyGroundedNode>();
		Set<FunctionallyGroundedNodeImpl> newNodes = new HashSet<FunctionallyGroundedNodeImpl>();
		for (Set<FunctionallyGroundedNode> group : groups) {
			Set<NonTerminalMolecule> ntMolecules = new HashSet<NonTerminalMolecule>();
			Set<T> keys = new HashSet<T>();
			for (FunctionallyGroundedNode nodeInGroup : group) {
				for (NonTerminalMolecule ntMolecule : nodeInGroup
						.getGroundingMolecules()) {
					ntMolecules.add(ntMolecule);
				}

				keys.addAll(nodes2Keys.remove(nodeInGroup));
			}
			FunctionallyGroundedNodeImpl newNode = new FunctionallyGroundedNodeImpl(
					ntMolecules);
			newNodes.add(newNode);
			nodes2Keys.put(newNode, keys);
			for (FunctionallyGroundedNode nodeInGroup : group) {
				old2NewMap.put(nodeInGroup, newNode);
			}

		}



		// replace member of the group occuring in nt-molecules of any fg-node
		// with the new node,
		// this involves replacing original-fg-nodes which unfinalised ones,
		// where this
		// occurs node2Keys and recursively the other nodes are updated
		// for all nodes
		Collection<NonTerminalMoleculeImpl> newNtMolecules = new ArrayList<NonTerminalMoleculeImpl>();
		Map<FunctionallyGroundedNode, Set<T>> resultNodes2Keys = nodes2Keys;
		while (old2NewMap.size() > 0) {
			Map<FunctionallyGroundedNode, FunctionallyGroundedNode> newOld2NewMap = new HashMap<FunctionallyGroundedNode, FunctionallyGroundedNode>();
			Map<FunctionallyGroundedNode, Set<T>> newNodes2Keys = new HashMap<FunctionallyGroundedNode, Set<T>>();
			for (FunctionallyGroundedNode node : resultNodes2Keys.keySet()) {
				// we add the replacement and the untouched nodes to a
				// newNodes2keySet
				// if newNodes contains a key we modify it
				// if we create a new key it is added to newOld2NewMap
				boolean moleculeReplaced = false;
				Set<NonTerminalMolecule> moleculesOfNewNode = new HashSet<NonTerminalMolecule>();
				for (NonTerminalMolecule molecule : node
						.getGroundingMolecules()) {
					NonTerminalMoleculeImpl replacementMolecule = null;
					Triple triple = molecule.iterator().next(); // nt-molecules
					// have exactly
					// one element
					Node subject = triple.getSubject();
					if (subject != NonTerminalMolecule.GROUNDED_NODE) {
						Node replacementSubject = old2NewMap.get(subject);
						if (replacementSubject != null) {
							replacementMolecule = new NonTerminalMoleculeImpl(
									new TripleImpl(replacementSubject, triple
											.getPredicate(),
											NonTerminalMolecule.GROUNDED_NODE));
						}
					} else {
						Node replacementObject = old2NewMap.get(triple
								.getObject());
						if (replacementObject != null) {
							replacementMolecule = new NonTerminalMoleculeImpl(
									new TripleImpl(
											NonTerminalMolecule.GROUNDED_NODE,
											triple.getPredicate(),
											replacementObject));
						}

					}
					if (replacementMolecule != null) {
						moleculeReplaced = true;
						moleculesOfNewNode.add(replacementMolecule);
						newNtMolecules.add(replacementMolecule);
					} else {
						moleculesOfNewNode.add(molecule);
					}
				}
				Set<T> keys = resultNodes2Keys.get(node);
				if (newNodes.contains(node)) {
					((FunctionallyGroundedNodeImpl) node)
							.setMolecules(moleculesOfNewNode);
					newNodes2Keys.put(node, keys);
				} else {
					if (moleculeReplaced) {
						FunctionallyGroundedNodeImpl newNode = new FunctionallyGroundedNodeImpl(
								moleculesOfNewNode);
						newNodes.add(newNode);
						newOld2NewMap.put(node, newNode);
						// Set<T> keys = nodes2Keys.remove(node);
						newNodes2Keys.put(newNode, keys);
					} else {
						newNodes2Keys.put(node, keys);
					}
				}
			}
			old2NewMap = newOld2NewMap;
			resultNodes2Keys = newNodes2Keys;
		}

		// at the end we finalize our nodes and molecules
		for (FunctionallyGroundedNodeImpl newNode : newNodes)
			newNode.markFinalized();
		for (NonTerminalMoleculeImpl newMol : newNtMolecules)
			newMol.markFinalized();

		

		// rehash nodes2Keys
		nodes2Keys.clear();
		for (Map.Entry<FunctionallyGroundedNode, Set<T>> entry : resultNodes2Keys
				.entrySet()) {
			if (nodes2Keys.containsKey(entry.getKey())) {
				// due to the changed hashes resultNodes2Keys may contain
				// duplicate keys
				Set<T> mergedValue = new HashSet<T>(entry.getValue());
				mergedValue.addAll(nodes2Keys.get(entry.getKey()));
				nodes2Keys.put(entry.getKey(), mergedValue);
			} else {
				nodes2Keys.put(entry.getKey(), entry.getValue());
			}
		}

		

		// nodes2Keys.putAll(nodes2KeysClone);
		// and recurse (call mergeFgNodes)
		// with every node affected by a grouping/ or *for all touched nodes
		// together*/or for all nodes ???
		// currently iterating --> all nodes are reprocessed.

	}
}
