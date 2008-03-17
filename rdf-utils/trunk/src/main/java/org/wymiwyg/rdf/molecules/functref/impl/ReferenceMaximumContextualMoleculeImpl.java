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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.ContextualMolecule;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.model.modelref.ModelRefGroundedNode;


/**
 * @author reto
 * 
 */
public class ReferenceMaximumContextualMoleculeImpl extends AbstractGraph
		implements MaximumContextualMolecule {

	// private final static Log log =
	// LogFactory.getLog(ReferenceGroundedMoleculeImpl.class);

	private ContextualMolecule wrapped;
	private Set<Triple> modelDetachedTriples = new HashSet<Triple>();

	/**
	 * @param current
	 */
	public ReferenceMaximumContextualMoleculeImpl(ContextualMolecule wrapped) {
		this.wrapped = wrapped;
		for (Iterator<Triple> iter = wrapped.iterator(); iter.hasNext();) {
			Triple triple = iter.next();
			boolean modified = false;
			Node subject = triple.getSubject();
			Node object = triple.getObject();
			if (subject instanceof ModelRefGroundedNode) {
				subject = ((ModelRefGroundedNode)subject).getModelNode();
				modified = true;
			}
			if (object instanceof ModelRefGroundedNode) {
				object = ((ModelRefGroundedNode)object).getModelNode();
				modified = true;
			}
			if (modified) {
				modelDetachedTriples.add(new TripleImpl(subject, triple.getPredicate(), object));
			} else {
				modelDetachedTriples.add(triple);
			}
			
		}
		markFinalized();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.molecules.Molecule#getUnionWith(org.wymiwyg.commons.molecules.Molecule)
	 */
	public Molecule getUnionWith(Molecule molecule) {
		// TODO implement
		throw new RuntimeException("not implemented");
	}

	// inherited from abstract graph should be fine
	// public boolean equals(Object obj) {
	// if (this == obj) {
	// return true;
	// }
	// if (!obj.getClass().equals(getClass())) {
	// return false;
	// }
	// return GraphMatcher.getValidMapping(this, (Graph) obj) != null;
	// }

	public String toString() {
		// TODO make sure reference-grounded nodes outputs in N3 style like
		// [foaf:mbox <...>]
		return super.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	public int size() {
		return wrapped.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator<Triple> iterator() {
		return modelDetachedTriples.iterator();
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.ContextualMolecule#getUsedButNotGroundedNodes()
	 */
	public Node[] getUsedButNotGroundedNodes() {
		return wrapped.getUsedButNotGroundedNodes();
	}



}
