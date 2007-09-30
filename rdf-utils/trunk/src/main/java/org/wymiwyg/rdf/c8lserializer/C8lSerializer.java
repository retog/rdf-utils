/*
 * Copyright  2002-2006 WYMIWYG (http://wymiwyg.org)
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
package org.wymiwyg.rdf.c8lserializer;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;


/**
 * @author reto
 *
 */
public class C8lSerializer {


	/**
	 * @author reto
	 *
	 */
	public class LabelableNode implements Node {
		private int labelID = 0;

		public int getLabelID() {
			return labelID;
		}

		public void setLabelID(int labelID) {
			this.labelID = labelID;
		}
		

	}

	public static void serialize(Graph graph, OutputStream out) {
		Graph labelableGraph = getLabelableGraph(graph);
		
	}

	/**
	 * @param graph
	 * @return
	 */
	private static Graph getLabelableGraph(Graph graph) {
		Map<Node, LabelableNode> nodeMap = new HashMap<Node, LabelableNode>();
		Graph labelableGraph = new SimpleGraph();
		for (Triple triple : graph) {
			labelableGraph.add(getLabelableTriple(triple, nodeMap));
		}
		return labelableGraph;
	}

	/**
	 * @param triple
	 * @param nodeMap
	 * @return
	 */
	private static Triple getLabelableTriple(Triple triple, Map<Node, LabelableNode> nodeMap) {
		Node subject = getLabelableNode(triple.getSubject(), nodeMap);
		Node object = getLabelableNode(triple.getObject(), nodeMap);
		TripleImpl result = new TripleImpl(subject, triple.getPredicate(), object);
		return null;
	}

	/** Gets a labelable replacement for non-grounded nodes
	 * @param subject
	 * @param nodeMap
	 * @return
	 */
	private static Node getLabelableNode(Node subject, Map<Node, LabelableNode> nodeMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
