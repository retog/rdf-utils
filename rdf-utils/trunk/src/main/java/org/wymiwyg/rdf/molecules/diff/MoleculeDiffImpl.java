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
package org.wymiwyg.rdf.molecules.diff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author reto
 * 
 */
public class MoleculeDiffImpl extends MoleculeDiffBase {

	private ReferenceGroundedDecomposition dec1;

	private ReferenceGroundedDecomposition dec2;

	Set<TerminalMolecule> terminalMoleculesOnlyIn1 = new HashSet<TerminalMolecule>();

	Set<TerminalMolecule> terminalMoleculesOnlyIn2 = new HashSet<TerminalMolecule>();

	Set<MaximumContextualMolecule> contextualMoleculesOnlyIn1 = new HashSet<MaximumContextualMolecule>();

	Set<MaximumContextualMolecule> contextualMoleculesOnlyIn2 = new HashSet<MaximumContextualMolecule>();

	// Set<FunctionallyGroundedNode> commonFgNodes;

	Set<FunctionallyGroundedNode> commonFgNodesInDiffMolecules = new HashSet<FunctionallyGroundedNode>();

	Set<CrossGraphFgNode> crossGraphFgNodes = new HashSet<CrossGraphFgNode>();

	Set<FunctionallyGroundedNode> fgNodesOnlyIn1 = new HashSet<FunctionallyGroundedNode>();

	Set<FunctionallyGroundedNode> fgNodesOnlyIn2 = new HashSet<FunctionallyGroundedNode>();

	Set<Molecule> commonMolecules = new HashSet<Molecule>();

	private Set<TerminalMolecule> terminalMolecules1;

	private Set<TerminalMolecule> terminalMolecules2;

	private Set<MaximumContextualMolecule> contextualMolecules1;

	private Set<MaximumContextualMolecule> contextualMolecules2;

	// TODO make constructor argument
	/**
	 * If this is true refrences to functionally grounded nodes are replaced by
	 * references to the FGnodes. As a consequence the diff becomes shorter, but
	 * becomes more like a diff to the union of the two models. If you want to
	 * know what triples have been added two a model, this is fine, and allows
	 * to add those triples to other models, however it may not be possible to
	 * 'substract' such a diff to a model
	 * 
	 */
	private boolean allwaysReplaceWithCGFGNodes = false;

	private Map<FunctionallyGroundedNode, CrossGraphFgNode> nonCgFgNodesInMolecules2ConatiningFgNodesMap = new HashMap<FunctionallyGroundedNode, CrossGraphFgNode>();;

	/**
	 * 
	 */
	public MoleculeDiffImpl(ReferenceGroundedDecomposition dec1,
			ReferenceGroundedDecomposition dec2) {
		this.dec1 = dec1;
		this.dec2 = dec2;
		create();

	}

	public MoleculeDiffImpl(ReferenceGroundedDecomposition dec1,
			ReferenceGroundedDecomposition dec2,
			boolean allwaysReplaceWithCGFGNodes) {
		this.dec1 = dec1;
		this.dec2 = dec2;
		this.allwaysReplaceWithCGFGNodes = allwaysReplaceWithCGFGNodes;
		create();

	}

	public MoleculeDiffImpl(Model model1, Model model2,
			boolean useDefaultOntology) {
		this.dec1 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model1, useDefaultOntology));
		this.dec2 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model2, useDefaultOntology));
		create();

	}

	public MoleculeDiffImpl(Model model1, Model model2,
			boolean useDefaultOntology, boolean allwaysReplaceWithCGFGNodes) {
		this.dec1 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model1, useDefaultOntology));
		this.dec2 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model2, useDefaultOntology));
		this.allwaysReplaceWithCGFGNodes = allwaysReplaceWithCGFGNodes;
		create();

	}

	public MoleculeDiffImpl(Model model1, Model model2, Model ontology,
			boolean useDefaultOntology) {
		this.dec1 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model1, ontology, useDefaultOntology));
		this.dec2 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model2, ontology, useDefaultOntology));
		create();
	}

	/**
	 * @param model1
	 * @param model2
	 * @param useDefaultOntology
	 * @param ontology
	 */
	public MoleculeDiffImpl(Model model1, Model model2, Model ontology,
			boolean useDefaultOntology, boolean allwaysReplaceWithCGFGNodes) {
		this.dec1 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model1, ontology, useDefaultOntology));
		this.dec2 = new ReferenceGroundedDecompositionImpl(JenaUtil
				.getGraphFromModel(model2, ontology, useDefaultOntology));
		this.allwaysReplaceWithCGFGNodes = allwaysReplaceWithCGFGNodes;
		create();
	}

	private void create() {
		prepareFgNodes();
		handleTerminalMolecules();
		handleContextualMolecules();
		identifyCommonFgNodes();
	}

	/**
	 * 
	 */
	private void identifyCommonFgNodes() {
		addFgNodesToCommons(terminalMolecules1);
		addFgNodesToCommons(terminalMolecules2);
		addFgNodesToCommons(contextualMolecules1);
		addFgNodesToCommons(contextualMolecules2);
		addFgNodedInFgNodes2commonFgNodes();
		commonFgNodesInDiffMolecules.removeAll(fgNodesOnlyIn1);
		commonFgNodesInDiffMolecules.removeAll(fgNodesOnlyIn2);
		for (CrossGraphFgNode cgNode : crossGraphFgNodes) {
			commonFgNodesInDiffMolecules.removeAll(cgNode.getNodesIn1());
			commonFgNodesInDiffMolecules.removeAll(cgNode.getNodesIn2());
		}
	}

	/**
	 * @param terminalMolecules
	 * @return
	 */
	// shouldnt try to change exiting molecules, new molecules, preserve type?
	private <M extends Molecule> Set<M> replaceWithCrossGrapgFgNodes(
			Set<M> molecules,
			Map<FunctionallyGroundedNode, CrossGraphFgNode> fgNodes2CrossGraphFgNodes,
			MoleculeFactory<M> factory) {
		Set<M> result = new HashSet<M>();
		for (M currentMolecule : molecules) {
			// TODO cannot finalize molecules
			M resultMolecule = factory.createMolecule();
			for (Triple currentTriple : currentMolecule) {
				// check if subject is mappable
				boolean modified = false;
				Node subject = currentTriple.getSubject();
				if (subject instanceof FunctionallyGroundedNode) {
					if (fgNodes2CrossGraphFgNodes
							.containsKey(((FunctionallyGroundedNode) subject))) {
						subject = fgNodes2CrossGraphFgNodes
								.get(((FunctionallyGroundedNode) subject));
						modified = true;
					} else {
						/*
						 * commonFgNodesInDiffMolecules
						 * .add((FunctionallyGroundedNode) subject);
						 */
					}
				}
				Node object = currentTriple.getObject();
				if (object instanceof FunctionallyGroundedNode) {
					if (fgNodes2CrossGraphFgNodes
							.containsKey(((FunctionallyGroundedNode) object))) {
						object = fgNodes2CrossGraphFgNodes
								.get(((FunctionallyGroundedNode) object));
						modified = true;
					} else {
						/*
						 * commonFgNodesInDiffMolecules
						 * .add((FunctionallyGroundedNode) object);
						 */
					}
				}
				if (modified) {
					resultMolecule.add(new TripleImpl(subject, currentTriple
							.getPredicate(), object));
				} else {
					resultMolecule.add(currentTriple);
				}

			}
			factory.release(resultMolecule);
			result.add(resultMolecule);
		}
		return result;
	}

	private void addFgNodesToCommons(Set<? extends Molecule> molecules) {
		for (Molecule currentMolecule : molecules) {
			for (Triple currentTriple : currentMolecule) {
				// check if subject is mappable
				Node subject = currentTriple.getSubject();
				if (subject instanceof FunctionallyGroundedNode) {
					commonFgNodesInDiffMolecules
							.add((FunctionallyGroundedNode) subject);
				}
				Node object = currentTriple.getObject();
				if (object instanceof FunctionallyGroundedNode) {
					commonFgNodesInDiffMolecules
							.add((FunctionallyGroundedNode) object);
				}

			}
		}
	}

	/**
	 * 
	 */
	private void handleTerminalMolecules() {
		Set<TerminalMolecule> commonTerminalMolecules = new HashSet<TerminalMolecule>(
				terminalMolecules1);
		commonTerminalMolecules.retainAll(terminalMolecules2);
		commonMolecules.addAll(commonTerminalMolecules);
		terminalMolecules1.removeAll(commonTerminalMolecules);
		terminalMoleculesOnlyIn1.addAll(terminalMolecules1);
		terminalMolecules2.removeAll(commonTerminalMolecules);
		terminalMoleculesOnlyIn2.addAll(terminalMolecules2);

	}

	private void handleContextualMolecules() {
		Set<MaximumContextualMolecule> commonContextualMolecules = new HashSet<MaximumContextualMolecule>(
				contextualMolecules1);
		commonContextualMolecules.retainAll(contextualMolecules2);
		commonMolecules.addAll(commonContextualMolecules);
		contextualMolecules1.removeAll(commonContextualMolecules);
		contextualMoleculesOnlyIn1.addAll(contextualMolecules1);
		contextualMolecules2.removeAll(commonContextualMolecules);
		contextualMoleculesOnlyIn2.addAll(contextualMolecules2);

	}

	/**
	 * This method find cg-fg-nodes and replaces references to point to
	 * 1-1-cg-fg-nodes in other molecules
	 * 
	 */
	private void prepareFgNodes() {

		Set<FunctionallyGroundedNode> fgNodes1 = new HashSet<FunctionallyGroundedNode>(
				dec1.getFunctionallyGroundedNodes());
		Set<FunctionallyGroundedNode> fgNodes2 = new HashSet<FunctionallyGroundedNode>(
				dec2.getFunctionallyGroundedNodes());
		// commonFgNodes = new HashSet<FunctionallyGroundedNode>();
		for (Iterator iter = fgNodes1.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode current = (FunctionallyGroundedNode) iter
					.next();
			if (fgNodes2.contains(current)) {
				// commonFgNodes.add(current);
				iter.remove();
				fgNodes2.remove(current);
			}
		}
		Map<FunctionallyGroundedNode, CrossGraphFgNode> fgNodes12CrossGraphFgNodes = new HashMap<FunctionallyGroundedNode, CrossGraphFgNode>();
		Map<FunctionallyGroundedNode, CrossGraphFgNode> fgNodes22CrossGraphFgNodes = new HashMap<FunctionallyGroundedNode, CrossGraphFgNode>();
		for (Iterator iter = fgNodes1.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode fgnode1 = (FunctionallyGroundedNode) iter
					.next();
			CrossGraphFgNode matching = new CrossGraphFgNode();
			matching.getNodesIn1().add(fgnode1);

			boolean fgNodeInCrossGraph = false;
			for (Iterator iterator = fgnode1.getGroundingMolecules().iterator(); iterator
					.hasNext();) {
				NonTerminalMolecule current = (NonTerminalMolecule) iterator
						.next();
				FunctionallyGroundedNode fgnode2 = getFgNodeWith(fgNodes2,
						current);
				if (fgnode2 != null) {
					fgNodeInCrossGraph = true;
					matching.getNodesIn2().add(fgnode2);
					fgNodes22CrossGraphFgNodes.put(fgnode2, matching);
				}
			}
			if (!fgNodeInCrossGraph) {
				fgNodesOnlyIn1.add(fgnode1);
			} else {
				fgNodes12CrossGraphFgNodes.put(fgnode1, matching);
				//TODO ensure later finalization
				crossGraphFgNodes.add(matching);
			}
		}
		for (Iterator<FunctionallyGroundedNode> iter = fgNodes2.iterator(); iter
				.hasNext();) {
			FunctionallyGroundedNode fgnode2 = iter.next();
			boolean fgNodeInCrossGraph = false;
			for (Iterator iterator = fgnode2.getGroundingMolecules().iterator(); iterator
					.hasNext();) {
				NonTerminalMolecule current = (NonTerminalMolecule) iterator
						.next();
				FunctionallyGroundedNode fgnode1 = getFgNodeWith(fgNodes1,
						current);
				if (fgnode1 != null) {
					fgNodeInCrossGraph = true;
					// check if there is already an OverlappingNodes with
					// fgnode1 and 2
					CrossGraphFgNode crossNodeWith2 = fgNodes22CrossGraphFgNodes
							.get(fgnode2);
					if (crossNodeWith2 == null) {
						// graph is not lean
						for (NonTerminalMolecule molecule : fgnode2
								.getGroundingMolecules()) {
							FunctionallyGroundedNode fgnode2alt = getFgNodeWith(
									fgNodes2, molecule);
							if ((fgnode2alt != null)
									&& (!fgnode2.equals(fgnode2alt))) {
								crossNodeWith2 = fgNodes22CrossGraphFgNodes
										.get(fgnode2alt);
								if (crossNodeWith2 != null) {
									break;
								}
							}
						}
						if (crossNodeWith2 == null) {
							throw new RuntimeException(
									"giving up, sorry - please leanify your graphs before diff");
						}
					}
					if (crossNodeWith2.getNodesIn1().contains(fgnode1)) {
						continue;
					}
					// otherwise merge CrrossNode with fgnode1 to the
					// overlappingnode with contains fgnode2
					CrossGraphFgNode crossNodeWith1 = fgNodes12CrossGraphFgNodes
							.get(fgnode1);
					crossNodeWith1.getNodesIn1().addAll(
							crossNodeWith2.getNodesIn1());
					crossNodeWith1.getNodesIn2().addAll(
							crossNodeWith2.getNodesIn2());
					crossGraphFgNodes.remove(crossNodeWith2);
					// replace all crossNodeWith2 with crossNodeWith1 in
					// fgNodes12CrossGraphFgNodes and fgNodes22CrossGraphFgNodes
					replaceValues(fgNodes12CrossGraphFgNodes, crossNodeWith2,
							crossNodeWith1);
					replaceValues(fgNodes22CrossGraphFgNodes, crossNodeWith2,
							crossNodeWith1);
				}
			}

			if (!fgNodeInCrossGraph) {
				fgNodesOnlyIn2.add(fgnode2);
			}
		}
		if (!allwaysReplaceWithCGFGNodes) {
			removeNot1To1CGFGnodesFromMaps(fgNodes12CrossGraphFgNodes,
					fgNodes22CrossGraphFgNodes);
		}
		// replace fgnodes in both dec with their respective crossgraphfgnode ->
		// may cause terminal molecules
		// to become identical (withing a dec)
		// TODO add to commonFgNodesInDiff afterwards, whatch out that molecules
		// taht are already in not 1-1 fgnode don't get duplicate
		terminalMolecules1 = replaceWithCrossGrapgFgNodes(dec1
				.getTerminalMolecules(), fgNodes12CrossGraphFgNodes,
				new TerminalMoleculeFactory());
		terminalMolecules2 = replaceWithCrossGrapgFgNodes(dec2
				.getTerminalMolecules(), fgNodes22CrossGraphFgNodes,
				new TerminalMoleculeFactory());
		contextualMolecules1 = replaceWithCrossGrapgFgNodes(dec1
				.getContextualMolecules(), fgNodes12CrossGraphFgNodes,
				new ContextualMoleculeFactory());
		contextualMolecules2 = replaceWithCrossGrapgFgNodes(dec2
				.getContextualMolecules(), fgNodes22CrossGraphFgNodes,
				new ContextualMoleculeFactory());

		commonFgNodesInDiffMolecules.removeAll(fgNodesOnlyIn1);
		commonFgNodesInDiffMolecules.removeAll(fgNodesOnlyIn2);
		// TODO change references to cg-fg-nodes in the nt-molecule (of
		// cg-fg-nodes and others)?
		for (CrossGraphFgNode cgNode : crossGraphFgNodes) {
			commonFgNodesInDiffMolecules.removeAll(cgNode.getNodesIn1());
			commonFgNodesInDiffMolecules.removeAll(cgNode.getNodesIn2());
		}
		// fgNodesOnlyIn1.removeAll(commonFgNodesInDiffMolecules);
		// fgNodesOnlyIn2.removeAll(commonFgNodesInDiffMolecules);

	}

	/**
	 * 
	 */
	private void addFgNodedInFgNodes2commonFgNodes() {
		for (FunctionallyGroundedNode fgNode : fgNodesOnlyIn1) {
			addFgNodesToCommons(fgNode.getGroundingMolecules());
		}
		for (FunctionallyGroundedNode fgNode : fgNodesOnlyIn2) {
			addFgNodesToCommons(fgNode.getGroundingMolecules());
		}

		for (CrossGraphFgNode cgNode : crossGraphFgNodes) {
			// here we add also non 1-1 fg-nodes so that possible inner fg-nodes
			// will be extracted
			for (FunctionallyGroundedNode fgNode : cgNode.getNodesIn1()) {
				addFgNodesToCommons(fgNode.getGroundingMolecules());

			}
			for (FunctionallyGroundedNode fgNode : cgNode.getNodesIn2()) {
				addFgNodesToCommons(fgNode.getGroundingMolecules());
			}
		}
		// recursive add, add fg-nodes in (common) fg-nodes
		while (true) {
			Set<FunctionallyGroundedNode> commFgNodesCopy = new HashSet<FunctionallyGroundedNode>(
					commonFgNodesInDiffMolecules);
			// invoking on copy to avoid concurrent modification
			for (FunctionallyGroundedNode fgNode : commFgNodesCopy) {
				addFgNodesToCommons(fgNode.getGroundingMolecules());
			}
			if (commFgNodesCopy.size() == commonFgNodesInDiffMolecules.size()) {
				break;
			}
		}
	}

	/**
	 * Removes CG-FG-NOdes from the maps where it does not conatin exactly one
	 * fg-node per graph
	 * 
	 * @param crossGraphFgNodes2
	 * @param fgNodes12CrossGraphFgNodes
	 * @param fgNodes22CrossGraphFgNodes
	 */
	private void removeNot1To1CGFGnodesFromMaps(
			Map<FunctionallyGroundedNode, CrossGraphFgNode> fgNodes12CrossGraphFgNodes,
			Map<FunctionallyGroundedNode, CrossGraphFgNode> fgNodes22CrossGraphFgNodes) {
		for (Iterator<CrossGraphFgNode> iter = crossGraphFgNodes.iterator(); iter
				.hasNext();) {
			CrossGraphFgNode current = iter.next();
			if (!current.is1To1()) {
				removeCGFgNodeFromMap(current, fgNodes12CrossGraphFgNodes);
				removeCGFgNodeFromMap(current, fgNodes22CrossGraphFgNodes);
			}
		}
	}

	/**
	 * @param current
	 * @param map
	 */
	private void removeCGFgNodeFromMap(FunctionallyGroundedNode cgFgNode,
			Map<FunctionallyGroundedNode, CrossGraphFgNode> map) {
		for (Iterator<Entry<FunctionallyGroundedNode, CrossGraphFgNode>> iterator = map
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<FunctionallyGroundedNode, CrossGraphFgNode> current = iterator
					.next();
			if (current.getValue().equals(cgFgNode)) {
				nonCgFgNodesInMolecules2ConatiningFgNodesMap.put(current
						.getKey(), current.getValue());
				iterator.remove();
			}

		}

	}

	/**
	 * @param fgNodes12CrossGraphFgNodes
	 * @param crossNodeWith2
	 * @param crossNodeWith1
	 */
	private <K, V> void replaceValues(Map<K, V> map, V oldValue, V newValue) {
		for (Iterator<Entry<K, V>> iter = map.entrySet().iterator(); iter
				.hasNext();) {
			Entry<K, V> current = iter.next();
			if (current.getValue().equals(oldValue)) {
				current.setValue(newValue);
			}
		}
	}

	/**
	 * @param fgNodes2
	 * @param current
	 * @return
	 */
	private static FunctionallyGroundedNode getFgNodeWith(Set fgNodes,
			NonTerminalMolecule ntMolecule) {
		for (Iterator iter = fgNodes.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode currentNode = (FunctionallyGroundedNode) iter
					.next();
			for (Iterator iterator = currentNode.getGroundingMolecules()
					.iterator(); iterator.hasNext();) {
				NonTerminalMolecule currentMoleculeFromNode = (NonTerminalMolecule) iterator
						.next();
				if (ntMolecule.equals(currentMoleculeFromNode)) {
					return currentNode;
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getCrossGraphFgNodes()
	 */
	public Set<CrossGraphFgNode> getCrossGraphFgNodes() {
		return crossGraphFgNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getCommonFgNodes()
	 */
	/*
	 * public Set<FunctionallyGroundedNode> getCommonFgNodes() { return
	 * commonFgNodes; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getFgNodesOnlyIn1()
	 */
	public Set<FunctionallyGroundedNode> getFgNodesOnlyIn1() {
		return fgNodesOnlyIn1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getFgNodesOnlyIn2()
	 */
	public Set<FunctionallyGroundedNode> getFgNodesOnlyIn2() {
		return fgNodesOnlyIn2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getCommonMolecules()
	 */
	public Set<Molecule> getCommonMolecules() {
		return commonMolecules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getContainingCrossGrapgFgNode(org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode)
	 */
	public CrossGraphFgNode getContainingCrossGrapgFgNode(
			FunctionallyGroundedNode fgNode) {
		return nonCgFgNodesInMolecules2ConatiningFgNodesMap.get(fgNode);
	}

	public Set<FunctionallyGroundedNode> getCommonFgNodesInDiffMolecules() {
		return commonFgNodesInDiffMolecules;
	}

	public Set<MaximumContextualMolecule> getContextualMoleculesOnlyIn1() {
		return contextualMoleculesOnlyIn1;
	}

	public Set<MaximumContextualMolecule> getContextualMoleculesOnlyIn2() {
		return contextualMoleculesOnlyIn2;
	}

	public Set<TerminalMolecule> getTerminalMoleculesOnlyIn1() {
		return terminalMoleculesOnlyIn1;
	}

	public Set<TerminalMolecule> getTerminalMoleculesOnlyIn2() {
		return terminalMoleculesOnlyIn2;
	}

}
