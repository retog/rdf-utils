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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.impl.GraphUtil;
import org.wymiwyg.rdf.graphs.impl.SourceNodeNotFoundException;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.impl.SimpleContextualMolecule;
import org.wymiwyg.rdf.molecules.impl.SimpleTerminalMolecule;

final class PatchedDecomposition implements ReferenceGroundedDecomposition {
	/**
	 * @param contextualMolecules 
	 * @param replacingNodes
	 * @param newNode
	 */
	private static void replaceInContextualMolecules(Set<MaximumContextualMolecule> contextualMolecules, Set<? extends Node> replacingNodes, FunctionallyGroundedNode newNode) {
		Set<MaximumContextualMolecule> newContextualMolecules = new HashSet<MaximumContextualMolecule>();
		for (Iterator<MaximumContextualMolecule> iter = contextualMolecules.iterator(); iter.hasNext();) {
			MaximumContextualMolecule current = iter.next();
			try {
				SimpleContextualMolecule newMolecule = new SimpleContextualMolecule();
				newMolecule.addAll(GraphUtil.replaceNode(current, replacingNodes, newNode));
				newMolecule.markFinalized();
				iter.remove();
				newContextualMolecules.add(newMolecule);
			} catch (SourceNodeNotFoundException e) {
				//nothing, did not remove
			}
		}
		contextualMolecules.addAll(newContextualMolecules);
	}
	/**
	 * @param replacingNodes
	 * @param newNode
	 */
	private static void replaceInTerminalMolecules(Set<TerminalMolecule> terminalMolecules, Set<? extends Node> replacingNodes, FunctionallyGroundedNode newNode) {
		Set<TerminalMolecule> newTerminalMolecules = new HashSet<TerminalMolecule>();
		for (Iterator<TerminalMolecule> iter = terminalMolecules.iterator(); iter.hasNext();) {
			TerminalMolecule current = iter.next();
			try {
				SimpleTerminalMolecule newMolecule = new SimpleTerminalMolecule();
				newMolecule.addAll(GraphUtil.replaceNode(current, replacingNodes, newNode));
				newMolecule.markFinalized();
				iter.remove();
				newTerminalMolecules.add(newMolecule);
			} catch (SourceNodeNotFoundException e) {
				//nothing, did not remove
			}
		}
		terminalMolecules.addAll(newTerminalMolecules);
		
	}
	private Set<MaximumContextualMolecule> contextualMolecules = new HashSet<MaximumContextualMolecule>();

	private final ReferenceGroundedDecomposition dec;	
	
	private Set<FunctionallyGroundedNode> functionallyGroundedNodes = new HashSet<FunctionallyGroundedNode>();

	private Set<TerminalMolecule> terminalMolecules = new HashSet<TerminalMolecule>();

	public PatchedDecomposition(ReferenceGroundedDecomposition dec, MoleculeDiff diff) {
		//this.diff = diff;
		this.dec = dec;
		terminalMolecules.addAll(dec.getTerminalMolecules());
		contextualMolecules.addAll(dec.getContextualMolecules());
		Set<TerminalMolecule> terminalOnlyIn1 = diff.getTerminalMoleculesOnlyIn1();
		Set<TerminalMolecule> terminalOnlyIn2 = diff.getTerminalMoleculesOnlyIn2();
		Set<MaximumContextualMolecule> contextualOnlyIn1 = diff.getContextualMoleculesOnlyIn1();
		Set<MaximumContextualMolecule> contextualOnlyIn2 = diff.getContextualMoleculesOnlyIn2();
		handleFunctionallyGroundedNode(diff,terminalOnlyIn1, terminalOnlyIn2, contextualOnlyIn1, contextualOnlyIn2);
		exchangeTerminalMolecules(terminalOnlyIn1, terminalOnlyIn2);
		exchangeContextualMolecules(contextualOnlyIn1, contextualOnlyIn2);
		
	}

	private void exchangeContextualMolecules(Set<MaximumContextualMolecule> contextualOnlyIn1, Set<MaximumContextualMolecule> contextualOnlyIn2) {
		contextualMolecules.removeAll(contextualOnlyIn1);
		contextualMolecules.addAll(contextualOnlyIn2);
	}

	private void exchangeTerminalMolecules(Set<TerminalMolecule> terminalOnlyIn1, Set<TerminalMolecule> terminalOnlyIn2) {
		terminalMolecules.removeAll(terminalOnlyIn1);
		terminalMolecules.addAll(terminalOnlyIn2);
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

	private void handleFunctionallyGroundedNode(
			MoleculeDiff diff, Set<TerminalMolecule> terminalOnlyIn1,
			Set<TerminalMolecule> terminalOnlyIn2,
			Set<MaximumContextualMolecule> contextualOnlyIn1, Set<MaximumContextualMolecule> contextualOnlyIn2) {
		
		functionallyGroundedNodes.addAll(dec.getFunctionallyGroundedNodes());
		functionallyGroundedNodes.addAll(diff.getCommonFgNodesInDiffMolecules());
		functionallyGroundedNodes.removeAll(diff.getFgNodesOnlyIn1());
		functionallyGroundedNodes.addAll(diff.getFgNodesOnlyIn2());
		for (CrossGraphFgNode cgFgNode : diff.getCrossGraphFgNodes()) {
			//FunctionallyGroundedNode newNode = new FunctionallyGroundedNodeImpl(cgFgNode.getGroundingMolecules());
			//replaceInMolecules(cgFgNode.getNodesIn1(), newNode);
			if (cgFgNode.is1To1()) {
				//FunctionallyGroundedNode versionIn1  = cgFgNode.getNodesIn1().iterator().next();
				FunctionallyGroundedNode versionIn2  = cgFgNode.getNodesIn2().iterator().next();
				Set<CrossGraphFgNode> cgFgSingleton = Collections.singleton(cgFgNode);
				replaceInMolecules(cgFgNode.getNodesIn1(), versionIn2);
				replaceInTerminalMolecules(terminalOnlyIn1, cgFgSingleton, versionIn2);
				replaceInTerminalMolecules(terminalOnlyIn2, cgFgSingleton, versionIn2);
				replaceInContextualMolecules(contextualOnlyIn1, cgFgSingleton, versionIn2);
				replaceInContextualMolecules(contextualOnlyIn2, cgFgSingleton, versionIn2);
				//TODO replace in non-terminal molecules
			} else {
//				replaceInTerminalMolecules(terminalOnlyIn1, cgFgNode.getNodesIn1(), newNode);
//				replaceInTerminalMolecules(terminalOnlyIn2, cgFgNode.getNodesIn2(), newNode);
//				replaceInContextualMolecules(contextualOnlyIn1, cgFgNode.getNodesIn1(), newNode);
//				replaceInContextualMolecules(contextualOnlyIn2, cgFgNode.getNodesIn2(), newNode);
			}
			//TODO in a tolerant mode (i.e. patch than can be applied to others than original nodes, we should remove only fgnodes that are no longer refernced
			functionallyGroundedNodes.removeAll(cgFgNode.getNodesIn1());
			functionallyGroundedNodes.addAll(cgFgNode.getNodesIn2());
		}
		
	}

	/**
	 * @param replacingNodes
	 * @param newNode
	 */
	private void replaceInMolecules(Set<FunctionallyGroundedNode> replacingNodes, FunctionallyGroundedNode newNode) {
		// TODO possibly optimize by keeping referenced from (fg)nodes to their containg triples and their graphs
		replaceInContextualMolecules(contextualMolecules, replacingNodes, newNode);
		replaceInTerminalMolecules(terminalMolecules,replacingNodes, newNode);
		
		
		
	}
}