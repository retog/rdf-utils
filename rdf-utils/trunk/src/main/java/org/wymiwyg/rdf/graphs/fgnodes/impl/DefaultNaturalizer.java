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

import java.util.HashMap;
import java.util.Map;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.impl.GraphUtil;
import org.wymiwyg.rdf.graphs.impl.NodeImpl;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * @author reto
 *
 */
public class DefaultNaturalizer implements Naturalizer {

	Map<FunctionallyGroundedNode, Node> map = new HashMap<FunctionallyGroundedNode, Node>();

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.graphs.fgnodes.impl.Naturalizer#naturalize(org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode, org.wymiwyg.rdf.graphs.Graph)
	 */
	public Node naturalize(FunctionallyGroundedNode fgNode, Graph graph) {
		Node anonNode = map.get(fgNode);
		if (anonNode == null) {
			anonNode = new NodeImpl();
			map.put(fgNode,anonNode);
		}
		for (Molecule molecule : fgNode.getGroundingMolecules()) {
			for (Triple triple : molecule) {
				graph.add(GraphUtil.replaceInTriple(triple, NonTerminalMolecule.GROUNDED_NODE, anonNode));
			}
		}
		return anonNode;
	}

}
