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
package org.wymiwyg.rdf.graphs.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;

/**
 * A graph in which all not grounded node are instances of ContextNode
 * 
 * @author reto
 * 
 */
public class ContextGraph extends AbstractGraph implements Graph {

	private Set<Triple> statements = new HashSet<Triple>();

	private List<ContextNode> nodes;

	ContextGraph(Graph base) {
		Map<Node,ContextNode> map = new HashMap<Node,ContextNode>();
		for (Iterator iter = base.iterator(); iter.hasNext();) {
			Triple triple = (Triple) iter.next();
			Node subject = triple.getSubject();
			boolean subjectUbngn = !(subject instanceof GroundedNode);
			Node object = triple.getObject();
			boolean objectUbngn = !(object instanceof GroundedNode);
			ContextNode subjectContextNode = null;
			if (subjectUbngn) {
				
				subjectContextNode = map.get(subject);
				if (subjectContextNode == null) {
					subjectContextNode = new ContextNode(subject);
					map.put(subject, subjectContextNode);
				}
				
				subject = subjectContextNode;
			}
			ContextNode objectContextNode = null;
			if (objectUbngn) {
				
				objectContextNode = map.get(object);
				if (objectContextNode == null) {
					objectContextNode = new ContextNode(object);
					map.put(object, objectContextNode);
				}
				//contextNode.addUsage(triple, subjectUbngn);
				object = objectContextNode;
			}
			if (subjectUbngn || objectUbngn) {
				triple = new TripleImpl(subject, triple.getPredicate(),
						object);
				statements.add(triple);
				if (subjectUbngn) {
					subjectContextNode.addUsage(triple, objectUbngn);
				}
				if (objectUbngn) {
					objectContextNode.addUsage(triple, subjectUbngn);
				}
			} else {
				statements.add(triple);
			}
			markFinalized();
			
		}
		nodes = new ArrayList<ContextNode>(map.values());
		//Collections.sort(nodes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.graphs.Graph#getUnionWith(org.wymiwyg.commons.graphs.Graph)
	 */
	public Graph getUnionWith(Graph graph) {
		throw new RuntimeException("not implemented");
	}


	/**
	 * 
	 */
	public List<ContextNode> getContextNodes() {
		return nodes;
		
	}

	/**
	 * 
	 */
	public void refineNodeHashes() {
		for (Iterator<ContextNode> iter = nodes.iterator(); iter.hasNext();) {
			ContextNode current = iter.next();
			current.refineHash();
		}
		
	}

	/**
	 * 
	 */
	public void resetNodeHashes() {
		for (Iterator<ContextNode> iter = nodes.iterator(); iter.hasNext();) {
			ContextNode current = iter.next();
			current.resetHash();
		}
		
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	public int size() {
		return statements.size();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator<Triple> iterator() {
		return statements.iterator();
	}

}
