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
package org.wymiwyg.rdf.leanifier;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.NaturalizedGraph;
import org.wymiwyg.rdf.graphs.fgnodes.impl.NoReplacementFoundException;
import org.wymiwyg.rdf.graphs.impl.AnonymizedGraph;
import org.wymiwyg.rdf.graphs.impl.DeAnonymizedGraph;
import org.wymiwyg.rdf.graphs.impl.GraphUtil;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.SourceNodeNotFoundException;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.graphs.matcher.SubGraphMatcher;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.FgNodeMerger;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;
import org.wymiwyg.rdf.molecules.functref.impl2.ReferenceGroundedDecompositionImpl2;
import org.wymiwyg.rdf.molecules.impl.SimpleContextualMolecule;
import org.wymiwyg.rdf.molecules.impl.SimpleTerminalMolecule;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;

/**
 * Leanifier similar to GraphLeanifier.
 * 
 * However this does not generally remove b-nodes wich could be mapped to a
 * grounded node, so if you need to be sure to have a completly lean graph, use
 * GraphLeanifier, however this will usually take much longer.
 * 
 * 
 * @author reto
 * 
 */
public class MoleculeBasedLeanifier {

	private final static Log log = LogFactory
			.getLog(MoleculeBasedLeanifier.class);

	/**
	 * @param graph
	 *            the graph to be leanified
	 * @return a lean graph expressing the same content
	 */
	public static Graph getLeanVersionOf(Graph graph) {
		graph = new AnonymizedGraph(graph);
		Graph result = getLeanVersionWithoutAnonymizing(graph);
		return new DeAnonymizedGraph(result);
	}

	/**
	 * SAme as getLeanVersionOf except that is doesn't anonymize the graph
	 * before leanification. As a consequence owl:saneAs statements are ignored.
	 * 
	 * @param graph
	 * @return
	 */
	private static Graph getLeanVersionWithoutAnonymizing(Graph graph) {
		if (log.isDebugEnabled()) {
			StringWriter stringWriter = new StringWriter();
			JenaUtil.getModelFromGraph(graph).write(stringWriter);
			log.debug("Anonymized graph: ");
			log.debug(stringWriter);
		}
		ReferenceGroundedDecomposition refDec = new ReferenceGroundedDecompositionImpl2(graph);
		ReferenceGroundedDecomposition leanifiedDec = getLeanVersionWithoutAnonymizing(refDec);
		Graph nonNaturalGraph = new SimpleGraph();
		for (Iterator<MaximumContextualMolecule> iter = leanifiedDec
				.getContextualMolecules().iterator(); iter.hasNext();) {
			nonNaturalGraph.addAll(iter.next());
		}
		for (Iterator<TerminalMolecule> iter = leanifiedDec.getTerminalMolecules()
				.iterator(); iter.hasNext();) {
			nonNaturalGraph.addAll(iter.next());
		}

		try {
			SimpleGraph result = new NaturalizedGraph(nonNaturalGraph, leanifiedDec
					.getFunctionallyGroundedNodes());
			result.markFinalized();
			return result;
		} catch (NoReplacementFoundException ex) {
			log.error(ex.toString());
			throw ex;
		}
	}

	/**
	 * Gets a lean version of a decompopsition without anonymizing nodes
	 * 
	 * @param refDec
	 * @return
	 */
	public static ReferenceGroundedDecomposition getLeanVersionWithoutAnonymizing(
			final ReferenceGroundedDecomposition refDec) {

		if (log.isDebugEnabled()) {
			StringWriter stringWriter = new StringWriter();
			try {
				ReferenceGroundedUtil.print(refDec, stringWriter);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			log.debug("Ref Dec: ");
			log.debug(stringWriter);
		}
		Set<MaximumContextualMolecule> contextualMolecules = refDec
				.getContextualMolecules();
		{
			// have to put in new set as to avoid duplicate after leanification
			Set<Graph> leanifiedMolecules = new HashSet<Graph>();
			for (Iterator<MaximumContextualMolecule> iter = contextualMolecules
					.iterator(); iter.hasNext();) {
				MaximumContextualMolecule current = (MaximumContextualMolecule) iter
						.next();
				// test, why does it takes sooo much longer with molecule?
				SimpleGraph currentGraph = new SimpleGraph();
				currentGraph.addAll(current);
				GraphLeanifier.makeLean(currentGraph);
				currentGraph.markFinalized();
				leanifiedMolecules.add(currentGraph);
			}
			contextualMolecules = new HashSet<MaximumContextualMolecule>();
			for (Iterator<Graph> iter = leanifiedMolecules.iterator(); iter
					.hasNext();) {
				Graph current = iter.next();
				if (!isSubgrapgOfOther(current, leanifiedMolecules)) {
					SimpleContextualMolecule contextualMolecule = new SimpleContextualMolecule();
					contextualMolecule.addAll(current);
					contextualMolecules.add(contextualMolecule);
				} else {
					iter.remove();
				}
			}
		}
		Map<FunctionallyGroundedNode, FunctionallyGroundedNode> fgNodeMap = new HashMap<FunctionallyGroundedNode, FunctionallyGroundedNode>();
		for (FunctionallyGroundedNode fgNode : refDec
				.getFunctionallyGroundedNodes()) {
			fgNodeMap.put(fgNode, fgNode);
		}
		fgNodeMap = FgNodeMerger.mergeFgNodes(fgNodeMap);
		boolean nodeReplaced = false;
		Set<TerminalMolecule> terminalMolecules = refDec.getTerminalMolecules();
		for (Entry<FunctionallyGroundedNode, FunctionallyGroundedNode> entry : fgNodeMap
				.entrySet()) {
			FunctionallyGroundedNode orig = entry.getKey();
			FunctionallyGroundedNode current = entry.getValue();
			if (!current.equals(orig)) {
				Set<MaximumContextualMolecule> newContextualModelcules = new HashSet<MaximumContextualMolecule>();
				for (MaximumContextualMolecule maximumContextualMolecule : contextualMolecules) {
					try {
						MaximumContextualMolecule replacement = new GraphUtil<MaximumContextualMolecule>()
								.replaceNode(maximumContextualMolecule, orig,
										current, new SimpleContextualMolecule());
						newContextualModelcules.add(replacement);
						nodeReplaced = true;
					} catch (SourceNodeNotFoundException e) {
						newContextualModelcules.add(maximumContextualMolecule);
						log.debug("source not found");
					}
				}
				contextualMolecules = newContextualModelcules;
				Set<TerminalMolecule> newTerminalMolecules = new HashSet<TerminalMolecule>();
				for (TerminalMolecule terminalMolecule : terminalMolecules) {
					try {
						TerminalMolecule replacement = new GraphUtil<TerminalMolecule>()
								.replaceNode(terminalMolecule, orig, current,
										new SimpleTerminalMolecule());
						newTerminalMolecules.add(replacement);
						nodeReplaced = true;
					} catch (SourceNodeNotFoundException e) {
						newTerminalMolecules.add(terminalMolecule);
						log.debug("source not found");
					}
				}
				terminalMolecules = newTerminalMolecules;
			} else {
				log.debug("keeping");
			}
		}
		final Set<FunctionallyGroundedNode> newFgNodes = new HashSet<FunctionallyGroundedNode>(
				fgNodeMap.values());

		final Set<MaximumContextualMolecule> unmodifiableContextualMolecules = Collections
				.unmodifiableSet(contextualMolecules);
		final Set<TerminalMolecule> unmodifiableTerminalMolecules = Collections
				.unmodifiableSet(terminalMolecules);

		ReferenceGroundedDecomposition result = new ReferenceGroundedDecomposition() {

			public Set<MaximumContextualMolecule> getContextualMolecules() {
				return unmodifiableContextualMolecules;
			}

			public Set<FunctionallyGroundedNode> getFunctionallyGroundedNodes() {
				return newFgNodes;
			}

			public Set<TerminalMolecule> getTerminalMolecules() {
				return unmodifiableTerminalMolecules;
			}

		};

		if (nodeReplaced) {
			return getLeanVersionWithoutAnonymizing(result);
		} else {
			return result;
		}
	}

	/**
	 * @param current
	 * @param leanifiedMolecules
	 * @return
	 */
	private static boolean isSubgrapgOfOther(Graph current,
			Set<Graph> leanifiedMolecules) {
		for (Iterator<Graph> iter = leanifiedMolecules.iterator(); iter
				.hasNext();) {
			Graph other = iter.next();
			if (current == other) {
				continue;
			}
			if (SubGraphMatcher.getValidMapping(current, other) != null) {
				return true;
			}
		}
		return false;
	}

}
