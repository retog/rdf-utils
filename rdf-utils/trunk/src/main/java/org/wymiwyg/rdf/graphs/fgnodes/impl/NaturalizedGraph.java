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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.NaturallyGroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * A graph in which all Nodes that are instance of GraoudedNode are naturally
 * grounded
 * 
 * @author reto
 * 
 */
public class NaturalizedGraph extends SimpleGraph implements Graph {
	
	private static final Log log = LogFactory.getLog(NaturalizedGraph.class);

	boolean finalized = false;
	
	/** Creates a naturalized graph using the default naturalizer 
	 * 
	 * @param graph
	 */
	public NaturalizedGraph(Graph graph) {
		this(graph,  new DefaultNaturalizer());
	}

	/** Creates a naturalized graph using the specified naturalizer for replacing fg-nodes
	 * 
	 * @param graph
	 * @param naturalizer
	 */
	public NaturalizedGraph(Graph graph, Naturalizer naturalizer) {
		Graph fgNodesGraph = new SimpleGraph();
		Map<Set<NonTerminalMolecule>, Node> replacementMap = new HashMap<Set<NonTerminalMolecule>, Node>();
		addGraphTriples(graph, true, naturalizer, replacementMap, fgNodesGraph);
		
		while (fgNodesGraph.size() > 0) {
			//add the triples of the fg-nodes graph and repeat if new triples result from fg-nodes replacemnets
			//withing these triples
			Graph addingFgNodesGraph = fgNodesGraph;
			fgNodesGraph = new SimpleGraph();
			addGraphTriples(	addingFgNodesGraph, true, naturalizer, replacementMap, fgNodesGraph);
		}
		markFinalized();
	}

	/** Crates a naturalized graph adding the referenced fg-nodes and a Collection of fgnodes
	 * 
	 * @param graph
	 * @param fgNodes
	 */
	public NaturalizedGraph(Graph graph,
			Collection<FunctionallyGroundedNode> fgNodes) {
		this(graph, fgNodes, false);
	}
	
	/** Creates a naturalized graph adding a collection of fgnodes and optionally other referenced fg-nodes
	 * 
	 * @param graph
	 * @param fgNodes 
	 * @param addReferencedFgNodes if false an exception is thrown if graph contians an fg-node which is not in fgNodes 
	 */
	public NaturalizedGraph(Graph graph,
			Collection<FunctionallyGroundedNode> fgNodes, boolean addReferencedFgNodes) {
		Map<Set<NonTerminalMolecule>, Node> replacementMap = new HashMap<Set<NonTerminalMolecule>, Node>();
		Graph fgNodesGraph = new SimpleGraph();
		for (Iterator<FunctionallyGroundedNode> iter = fgNodes.iterator(); iter
				.hasNext();) {
			FunctionallyGroundedNode fgNode = iter.next();
			// fg2NormalNodeMap.put(fgNode, fgNode.getOriginalNode());
			Node replacementNode = addFgNodeMolecules(fgNode, new DefaultNaturalizer(), fgNodesGraph);
			//rehashing necessary as long unfinalized nodes come 
			replacementMap.put(new HashSet<NonTerminalMolecule>(fgNode.getGroundingMolecules()), replacementNode);
		}
		Naturalizer naturalizer;
		if (addReferencedFgNodes) {
			naturalizer = new DefaultNaturalizer();
		} else {
			naturalizer = null;
		}
		addGraphTriples(graph, addReferencedFgNodes, naturalizer, replacementMap, fgNodesGraph);
		while (fgNodesGraph.size() > 0) {
			//add the triples of the fg-nodes graph and repeat if new triples result from fg-nodes replacemnets
			//withing these triples
			Graph addingFgNodesGraph = fgNodesGraph;
			fgNodesGraph = new SimpleGraph();
			addGraphTriples(addingFgNodesGraph, addReferencedFgNodes, naturalizer, replacementMap, fgNodesGraph);
		}
		markFinalized();
	}

	/**
	 * 
	 * @param fgNode
	 * @param naturalizer
	 * @param fgNodesGraph the graph to which the triples resulting from fg-node naturalization are added
	 * @return
	 */
	private Node addFgNodeMolecules(FunctionallyGroundedNode fgNode, Naturalizer naturalizer, Graph fgNodesGraph) {
		return naturalizer.naturalize(fgNode, fgNodesGraph);
	}

	/**
	 * @param graph the non-natural graph
	 * @param naturalizer 
	 * @param replacementMap 
	 * @param fgNodesGraph the graph to which the triples resulting from fg-node naturalization are added 
	 */
	private void addGraphTriples(Graph graph, boolean addReferencedFgNodes, Naturalizer naturalizer, Map<Set<NonTerminalMolecule>, Node> replacementMap, Graph fgNodesGraph) {
		for (Iterator iter = graph.iterator(); iter.hasNext();) {
			Triple triple = (Triple) iter.next();
			Node subject = triple.getSubject();
			if (subject instanceof FunctionallyGroundedNode) {
				if (addReferencedFgNodes) {
					Node replacement = replacementMap.get(((FunctionallyGroundedNode) subject).getGroundingMolecules()); // (Node)fg2NormalNodeMap.get(object);
					if (replacement == null) {
						Set<NonTerminalMolecule> groundingMolecules = ((FunctionallyGroundedNode) subject).getGroundingMolecules();
						subject = addFgNodeMolecules((FunctionallyGroundedNode) subject, naturalizer, fgNodesGraph);
						replacementMap.put(groundingMolecules, subject);
					} else {
						subject = replacement;
					}
				} else {
					Node replacement = replacementMap.get(((FunctionallyGroundedNode) subject).getGroundingMolecules());
					if (replacement == null) {
						log.error("no replacement found for the fg-node: "+ subject);
						throw new RuntimeException("no replacement found for the fg-node: "+ subject);
					}
					subject = replacement;
				}
			} else {
				if (subject instanceof GroundedNode) {
					if (!(subject instanceof NaturallyGroundedNode)) {
						throw new RuntimeException("Cannot naturalize subject node of type "+subject.getClass());
					}
				}
			}
			Node object = triple.getObject();
			if (object instanceof FunctionallyGroundedNode) {
				if (addReferencedFgNodes) {
					Node replacement = replacementMap.get(((FunctionallyGroundedNode) object).getGroundingMolecules()); // (Node)fg2NormalNodeMap.get(object);
					if (replacement == null) {
						Set<NonTerminalMolecule> groundingMolecules = ((FunctionallyGroundedNode) object).getGroundingMolecules();
						object = addFgNodeMolecules((FunctionallyGroundedNode) object, naturalizer, fgNodesGraph);
						replacementMap.put(groundingMolecules, object);
					} else {
						object = replacement;
					}
					
				} else {
					Node replacement = (replacementMap).get(new HashSet<NonTerminalMolecule>(((FunctionallyGroundedNode) object).getGroundingMolecules())); // (Node)fg2NormalNodeMap.get(object);
					if (replacement == null) {
						throw new NoReplacementFoundException("no replacement found for the fg-node: "+ object, (FunctionallyGroundedNode) object);
					}
					object = replacement;
				}
			} else {
				if (object instanceof GroundedNode) {
					if (!(object instanceof NaturallyGroundedNode)) {
						throw new RuntimeException("Cannot naturalize object node of type "+object.getClass());
					}
				}
			}
			doAdd(new TripleImpl(subject, triple.getPredicate(), object));
		}
		finalized = true;
	}

	
	private void doAdd(Triple triple) {
		super.add(triple);	
	}
	
	public boolean add(Triple triple) {
		if (finalized) {
			throw new UnsupportedOperationException("cannot add to naturalized graph");
		} else {
			return super.add(triple);
		}
		
	}

}
