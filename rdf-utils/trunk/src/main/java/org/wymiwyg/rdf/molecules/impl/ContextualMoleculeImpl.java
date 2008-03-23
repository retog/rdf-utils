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

package org.wymiwyg.rdf.molecules.impl;

import java.util.HashSet;
import java.util.Set;

import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;

/**
 * 
 * @author reto
 */

public class ContextualMoleculeImpl extends SimpleGraph implements
		MaximumContextualMolecule {

	private Object tempIdentity = new Object();
	private Set<Node> ubngn = null;

	public Node[] getUsedButNotGroundedNodes() {
		if (!isFinalized()) {
			Set<Node> _ubngn = getUsedButNotGroundedNodeSet();
			return _ubngn.toArray(new Node[_ubngn.size()]);
		}
		if (ubngn == null) {
			ubngn = getUsedButNotGroundedNodeSet();
		}
		return ubngn.toArray(new Node[ubngn.size()]);
	}

	private Set<Node> getUsedButNotGroundedNodeSet() {
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

	/*
	 * the AbtractGraph implements AddAll (calling this method)
	 */
	@Override
	public boolean add(Triple triple) {
		// if (!(triple.getSubject() instanceof GroundedNode)) {
		// ubngn.add(triple.getSubject());
		// }
		// if (!(triple.getObject() instanceof GroundedNode)) {
		// ubngn.add(triple.getObject());
		// }
		return super.add(triple);
	}

	@Override
	public void markFinalized() {
		tempIdentity = null;
		super.markFinalized();
	}

	@Override
	public boolean equals(Object object) {
		if (tempIdentity == null) {
			return super.equals(object);
		} else {
			return this == object;
		}
	}

	@Override
	public int hashCode() {
		if (tempIdentity == null) {
			return super.hashCode();
		} else {
			return tempIdentity.hashCode();
		}
	}

}
