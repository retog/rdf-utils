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
package org.wymiwyg.rdf.molecules.impl;

import java.util.HashSet;
import java.util.Set;

import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;

/**
 * @author reto
 *
 */
public class SimpleContextualMolecule extends SimpleGraph implements
		MaximumContextualMolecule {

	/**
	 * 
	 */
	public SimpleContextualMolecule() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.Molecule#getUnionWith(org.wymiwyg.rdf.molecules.Molecule)
	 */
	public Molecule getUnionWith(Molecule graph) {
		throw new RuntimeException("Not implemented yet");
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.ContextualMolecule#getUsedButNotGroundedNodes()
	 */
	public Node[] getUsedButNotGroundedNodes() {
		return getUsedButNotGroundedNodeSet().toArray(new Node[0]);
	}
	
	public Set<Node> getUsedButNotGroundedNodeSet() {
		Set<Node> resultSet = new HashSet<Node>();
		for (Triple triple : this) {
			Node subject = triple.getSubject(); 
			if (!(subject instanceof GroundedNode)) {
				resultSet.add(subject);
			}
			Node object = triple.getObject();
			if (!(object instanceof GroundedNode)) {
				resultSet.add(object);
			}
		}
		return resultSet;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.ContextualMolecule#usesButDoesntGround(org.wymiwyg.rdf.graphs.Node)
	 */
	public boolean usesButDoesntGround(Node afgn) {
		throw new RuntimeException("Not implemented yet");
	}

}
