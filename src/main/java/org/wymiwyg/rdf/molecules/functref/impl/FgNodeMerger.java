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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedBuilder;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedNodeImpl;
import org.wymiwyg.rdf.graphs.impl.GraphUtil;
import org.wymiwyg.rdf.graphs.impl.SourceNodeNotFoundException;
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
public class FgNodeMerger<T> extends HashMap<T, FunctionallyGroundedNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	FunctionallyGroundedBuilder fgBuilder = new FunctionallyGroundedBuilder();

	/*
	 * private Collection<MergerFunctionallyGroundedNodeImpl> fgNodes = new
	 * ArrayList<MergerFunctionallyGroundedNodeImpl>(); private Collection<MergerNonTerminalMoleculeImpl>
	 * ntMolecules = new ArrayList<MergerNonTerminalMoleculeImpl>();
	 * 
	 * private class MergerFunctionallyGroundedNodeImpl extends
	 * FunctionallyGroundedNodeImpl {
	 * 
	 * 
	 * private boolean released = false;
	 * 
	 * MergerFunctionallyGroundedNodeImpl() { fgNodes.add(this); //this.creator =
	 * creator; }
	 * 
	 * 
	 * public MergerFunctionallyGroundedNodeImpl( Set<NonTerminalMolecule>
	 * resultMolecules) { super(resultMolecules); fgNodes.add(this);
	 * //this.creator = creator; }
	 * 
	 * public int hashCode() { if (!released) { if (isFinalized()) { throw new
	 * RuntimeException("Not release but finalized"); } return 1; } else {
	 * return super.hashCode(); } }
	 * 
	 * public boolean isOriginatingFrom(FgNodeMerger merger) { //return
	 * creator.equals(merger); return FgNodeMerger.this.equals(merger); }
	 * 
	 * public void release() { released = true;
	 *  } }
	 * 
	 * private class MergerNonTerminalMoleculeImpl extends
	 * SimpleNonTerminalMolecule {
	 * 
	 * private boolean released = false;
	 * 
	 * 
	 * 
	 * 
	 * public MergerNonTerminalMoleculeImpl(Node afgn) { super(afgn);
	 * FgNodeMerger.this.ntMolecules .add(this); }
	 * 
	 * 
	 * 
	 * 
	 * public int hashCode() { if (!released) { if (isFinalized()) { throw new
	 * RuntimeException("Not release but finalized"); } return 1; } else {
	 * return super.hashCode(); } }
	 * 
	 * 
	 * 
	 * public void release() { released = true;
	 *  }
	 *  }
	 */

	private static final Log log = LogFactory.getLog(FgNodeMerger.class);

	/**
	 * @author reto
	 * 
	 */
	class FgNodeRoleDescriptor implements GroundedNode {

		FunctionallyGroundedNodeImpl fgNode;

		Set<T> keys = new HashSet<T>();

		Set<FunctionallyGroundedNode> origFgNodes = new HashSet<FunctionallyGroundedNode>();

		@Override
		public String toString() {
			return super.toString()+" origFgNodes: "+origFgNodes+", fgNode: "+fgNode;
		}
		

	}

	// private final static Log log = LogFactory.getLog(FgNodeMerger.class);

	/**
	 * Creates a new FgNodeMErger
	 * 
	 * @param indexedOrigFgNodes
	 *            the map with the original fg-nodes
	 */
	public FgNodeMerger(
			Map<T, FunctionallyGroundedNode> indexedOrigFgNodes) {
		if (log.isDebugEnabled()) {
			log.debug("mergeOverlappingFGNodes invoked with map of size "
					+ indexedOrigFgNodes.size());
			int debCounter = 0;
			for (final FunctionallyGroundedNode fgNode : indexedOrigFgNodes
					.values()) {
				log.debug("-- (" + debCounter++ + ") " + fgNode);
			}
		}

		final Map<T, FunctionallyGroundedNode> indexedAdopetedFgNodes = adoptByBuilder(indexedOrigFgNodes);

		// this is because fgnodes may be multiple times in the map alredy at
		// the beginning

		final Map<FunctionallyGroundedNode, FgNodeRoleDescriptor> nodes2Roles = new HashMap<FunctionallyGroundedNode, FgNodeRoleDescriptor>();

		final Set<Entry<T, FunctionallyGroundedNode>> entries = indexedAdopetedFgNodes
				.entrySet();
		for (final Entry<T, FunctionallyGroundedNode> entry : entries) {
			final T key = entry.getKey();
			final FunctionallyGroundedNode origFgNode = entry.getValue();
			// TODO replace contained with own-builder version
			final FunctionallyGroundedNodeImpl fgNode = fgBuilder
					.createFGNode();// entry.getValue().getGroundingMolecules());
			for (final NonTerminalMolecule ntMolecule : origFgNode
					.getGroundingMolecules()) {
				fgNode.addMolecule(ntMolecule);
			}
			FgNodeRoleDescriptor descriptor = nodes2Roles.get(fgNode);
			if (descriptor == null) {
				descriptor = new FgNodeRoleDescriptor();
				descriptor.fgNode = fgNode;

				nodes2Roles.put(fgNode, descriptor);
			}
			descriptor.origFgNodes.add(origFgNode);
			descriptor.keys.add(key);
		}

		Collection<FgNodeRoleDescriptor> descriptorList = nodes2Roles.values();
		replaceNodesInNodes(descriptorList, true);

		descriptorList = mergeFgNodeDescriptors(nodes2Roles.values());
		for (final FgNodeRoleDescriptor descriptor : descriptorList) {
			for (final T key : descriptor.keys) {
				put(key, descriptor.fgNode);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Summary: original size: " + keySet().size()
					+ " new size "
					+ new HashSet<FunctionallyGroundedNode>(values()).size());
			log.debug("result");
			int debCounter = 0;
			for (final FunctionallyGroundedNode fgNode : values()) {
				log.debug("-- (" + debCounter++ + ") " + fgNode);
			}
		}
		verifyResultConsistency(new HashSet<FunctionallyGroundedNode>(values()));

		fgBuilder.release();

		rehash();

	}

	/**
	 * @param indexedOrigFgNodes
	 * @return
	 */
	private Map<T, FunctionallyGroundedNode> adoptByBuilder(
			Map<T, FunctionallyGroundedNode> indexedOrigFgNodes) {
		Map<T, FunctionallyGroundedNode> result = new HashMap<T, FunctionallyGroundedNode>();
		for (Entry<T, FunctionallyGroundedNode> entry : indexedOrigFgNodes
				.entrySet()) {
			result.put(entry.getKey(), fgBuilder.adopt(entry.getValue()));
		}
		return result;
	}

	/**
	 * 
	 */
	private void rehash() {
		HashMap<T, FunctionallyGroundedNode> data = new HashMap<T, FunctionallyGroundedNode>(
				this);
		this.clear();
		this.putAll(data);

	}

	/**
	 * This is to remove overlapping fgnodes, i.e. fgnodes representing the same
	 * node because they share an nt-molecule
	 * 
	 * @param result
	 */
	/*
	 * iterate throw fg-nodes, for each nt-molecule that is present in another
	 * fg-node, add the other fg-node to a merge-set, if merge-set > 0, add
	 * curent fg-node to merge-set and create a new merged fg node. look up key
	 * for every value in merge-set and set the value in origAfgn2FgnMap to the
	 * new fgnode.
	 */
	/*
	 * map nt-molecules to a set of map entries of origAfgn2FgnMap in which with
	 * value-fgnode containing that molecule. iterate through the map, and where
	 * the set > 1, and the values (fgnodes) are different merge them, and set
	 * the value of the inner entry accordingly. start over (or could this done
	 * more selectively:
	 */
	/*
	 * the fg node are put in a list of FgNodeRoleDescriptor, for every element
	 * it is checked against all previuos elements, when a merge is done, the
	 * current elment is removed and the algorithm restarts at the element
	 * following the merged element.
	 */

	private Collection<FgNodeRoleDescriptor> mergeFgNodeDescriptors(
			Collection<FgNodeRoleDescriptor> descriptors) {
		List<FgNodeRoleDescriptor> descriptorList = new ArrayList<FgNodeRoleDescriptor>(
				descriptors);
		boolean nodeMerged = false;
		for (int i = 1; i < descriptorList.size(); i++) {
			int mergePos = checkAgainstPreviousOnList(descriptorList, i);
			if (mergePos > -1) {
				if (log.isDebugEnabled()) {
					log.debug("merged nodes at position " + i
							+ " size of descriptorList "
							+ descriptorList.size());
				}
				nodeMerged = true;
				break;
				// i = mergePos;
			}
		}
		if (nodeMerged) {
			replaceNodesInNodes(descriptorList, false);
			// if (replaceNodesInNodes(descriptorList)) {
			return mergeFgNodeDescriptors(descriptorList);
			// }
		}
		Collection<FunctionallyGroundedNode> nodes = new HashSet<FunctionallyGroundedNode>();
		for (FgNodeRoleDescriptor descriptor : descriptorList) {
			nodes.add(descriptor.fgNode);
		}
		verifyResultConsistency(nodes);
		return descriptorList;
	}

	/**
	 * this replaces origFgNodes present in itself and other nodes with the
	 * currentFgNode, orifFgNodes is then set to have only currentFgNode as
	 * element
	 * 
	 * @param <T>
	 * @param descriptors
	 */
	private boolean replaceNodesInNodes(
			Collection<FgNodeRoleDescriptor> descriptors, boolean force) {
		boolean result = false;
		Set<FunctionallyGroundedNode> origNodes = new HashSet<FunctionallyGroundedNode>();
		for (FgNodeRoleDescriptor descriptor : descriptors) {
			origNodes.addAll(descriptor.origFgNodes);
			if (force || (descriptor.origFgNodes.size() > 1)) {
				//TODO check if really necessary to process current first
				// as the fgnode of the current descriptor may have to be
				// replaced we do this as first thing
				// so that the old version isn't used in the other fg-nodes
				{
					for (FunctionallyGroundedNode origNode : descriptor.origFgNodes) {
						if (replaceFgNode(descriptor.fgNode, origNode,
								descriptor.fgNode)) {
							result = true;
						}
					}
				}
				for (FunctionallyGroundedNode origNode : descriptor.origFgNodes) {
					if (replaceInOtherDescriptors(descriptors, descriptor,
							origNode, descriptor.fgNode)) {
						result = true;
					}
				}

			}
		}

		Collection<FunctionallyGroundedNode> nodes = new HashSet<FunctionallyGroundedNode>();
		for (FgNodeRoleDescriptor descriptor : descriptors) {
			nodes.add(descriptor.fgNode);
		}
		//TODO move to the verifiyResultConsistency method
		for (FunctionallyGroundedNode node : nodes) {
			for (NonTerminalMolecule ntMolecule : node.getGroundingMolecules()) {
				for (Triple triple : ntMolecule) {
					Node subject = triple.getSubject();
					if (subject instanceof FunctionallyGroundedNode) {
						if (!nodes.contains(subject)) {
							throw new RuntimeException();
						}
					}
					Node object = triple.getObject();
					if (object instanceof FunctionallyGroundedNode) {
						if (!nodes.contains(object)) {
							log.warn("object in OrigNodes: " + origNodes.contains(object));
							log.warn("object "+object);
							for (FunctionallyGroundedNode node2 : nodes) {
								log.warn("equals " + object.equals(node2)
										+ " node2: "
										+ node2);
							}
							throw new RuntimeException(object + " not found");
						}
					}
				}
			}
		}
		resetOrigFgNodes(descriptors);
		verifyResultConsistency(nodes);
		return result;
	}

	private boolean replaceInOtherDescriptors(
			Collection<FgNodeRoleDescriptor> descriptors,
			FgNodeRoleDescriptor thisDescriptor,
			FunctionallyGroundedNode source, FunctionallyGroundedNode target) {
		boolean result = false;
		for (FgNodeRoleDescriptor otherDescriptor : descriptors) {
			if (otherDescriptor == thisDescriptor) {
				continue;
			}

			if (replaceFgNode(otherDescriptor.fgNode, source, target)) {
				result = true;
			}

		}
		return result;
	}

	private void resetOrigFgNodes(Collection<FgNodeRoleDescriptor> descriptors) {
		for (FgNodeRoleDescriptor descriptor : descriptors) {
			// if (descriptor.origFgNodes.size() > 1) {
			descriptor.origFgNodes = new HashSet<FunctionallyGroundedNode>(
					Collections.singleton(descriptor.fgNode));
			// }
		}
	}


	private boolean replaceFgNode(FunctionallyGroundedNodeImpl editableFgNode,
			FunctionallyGroundedNode source, FunctionallyGroundedNode target) {
		boolean result = false;
		Collection<NonTerminalMolecule> newMolecules = new ArrayList<NonTerminalMolecule>();
		for (NonTerminalMolecule ntMolecule : editableFgNode
				.getGroundingMolecules()) {
			// the molecule contains exactly one triple
			Set<Triple> editableTripleSet = new HashSet<Triple>(ntMolecule);
			boolean replaced = true;
			try {
				editableTripleSet = new GraphUtil<Set<Triple>>().replaceNode(
						editableTripleSet, source, target,
						new HashSet<Triple>());
			} catch (SourceNodeNotFoundException e) {
				// log.debug("source not found");
				replaced = false;
			}
			if (replaced) {
				if (log.isDebugEnabled()) {
					log.debug("replaced " + source + " with " + target + " in "
							+ editableFgNode);
					log.debug("Source and target equals: "
							+ source.equals(target));
				}
				result = true;
			}

			NonTerminalMolecule newNonTerminal = fgBuilder
					.createNTMolecule(NonTerminalMolecule.GROUNDED_NODE);
			newNonTerminal.addAll(editableTripleSet);
			newMolecules.add(newNonTerminal);
		}
		editableFgNode.removeAllMolecules();
		for (NonTerminalMolecule molecule : newMolecules) {
			editableFgNode.addMolecule(molecule);
		}
		return result;
	}

	/**
	 * checks the element at pos against previous elemnts, starting with 0, till
	 * a merge can be made
	 * 
	 * @param descriptorList
	 * @param pos
	 * @return the position of the merge or -1 if no merge was made
	 */
	private int checkAgainstPreviousOnList(
			List<FgNodeRoleDescriptor> descriptorList, int pos) {
		FgNodeRoleDescriptor descriptor = descriptorList.get(pos);
		for (int i = 0; i < pos; i++) {
			if (shareMolecule(descriptor.fgNode, descriptorList.get(i).fgNode)) {
				if (log.isDebugEnabled()) {
					log.debug("The fgnode " + descriptor.fgNode + " and "
							+ descriptorList.get(i).fgNode
							+ " represent the same node");
				}
				mergeFgNodesDescriptor(descriptorList.get(i), descriptor);
				descriptorList.remove(pos);
				return i;
			}
		}
		return -1;
	}

	/**
	 * merge two descriptors into the first
	 * 
	 * @param descriptor
	 * @param descriptor2
	 * @return
	 */
	private void mergeFgNodesDescriptor(FgNodeRoleDescriptor descriptor,
			FgNodeRoleDescriptor descriptor2) {
		descriptor.keys.addAll(descriptor2.keys);
		descriptor.origFgNodes.addAll(descriptor2.origFgNodes);
		descriptor.fgNode = mergeFgNodes(descriptor.fgNode, descriptor2.fgNode);
	}

	/**
	 * @param fgNode
	 * @param node
	 * @return if the fgNodes have a ciommon Nt-molecule
	 */
	private static boolean shareMolecule(FunctionallyGroundedNode fgNode1,
			FunctionallyGroundedNode fgNode2) {
		Set<NonTerminalMolecule> molecules2 = fgNode2.getGroundingMolecules();
		for (NonTerminalMolecule molecule : fgNode1.getGroundingMolecules()) {
			if (molecules2.contains(molecule)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param fgNode
	 * @param impl
	 * @return
	 */
	FunctionallyGroundedNodeImpl mergeFgNodes(
			FunctionallyGroundedNodeImpl fgNode1,
			FunctionallyGroundedNode fgNode2) {
		for (NonTerminalMolecule molecule : fgNode2.getGroundingMolecules()) {
			fgNode1.addMolecule(molecule);
		}
		return fgNode1;
		/*
		 * Set<NonTerminalMolecule> resultMolecules = new HashSet<NonTerminalMolecule>(
		 * fgNode1.getGroundingMolecules());
		 * resultMolecules.addAll(fgNode2.getGroundingMolecules()); return new
		 * MergerFunctionallyGroundedNodeImpl(resultMolecules);
		 */
	}

	private void verifyResultConsistency(
			Collection<FunctionallyGroundedNode> fgNodes) {
		// Uncomment to fail early
		// for (FunctionallyGroundedNode node : fgNodes) {
		// for (NonTerminalMolecule ntMolecule : node.getGroundingMolecules()) {
		// for (Triple triple : ntMolecule) {
		// Node subject = triple.getSubject();
		// if (subject instanceof FunctionallyGroundedNode) {
		// if (!fgNodes.contains(subject)) {
		// throw new RuntimeException();
		// }
		// }
		// Node object = triple.getObject();
		// if (object instanceof FunctionallyGroundedNode) {
		// if (!fgNodes.contains(object)) {
		// for (FunctionallyGroundedNode node2 : fgNodes) {
		// log.warn("equals " + object.equals(node2)
		// + " object: " + object + " node2: "
		// + node2);
		// }
		// throw new RuntimeException(object + " not found");
		// }
		// }
		// }
		// }
		// }
	}

}
