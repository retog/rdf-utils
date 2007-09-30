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
import java.util.Set;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.model.modelref.ModelRefGroundedNode;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingTriple;

/**
 * @author reto
 *
 */
public class ModelReferencingTripleImpl extends TripleImpl implements ModelReferencingTriple {

	private Set<ModelRefGroundedNode> modelRefGroungetdedNodes = new HashSet<ModelRefGroundedNode>();
	
	public ModelReferencingTripleImpl(Node subject, PropertyNode predicate, Node object) {
		super(subject, predicate, object);
		if (subject instanceof ModelRefGroundedNode) {
			modelRefGroungetdedNodes.add((ModelRefGroundedNode) subject);
		}
		if (object instanceof ModelRefGroundedNode) {
			modelRefGroungetdedNodes.add((ModelRefGroundedNode) object);
		}
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingTriple#getModelRefGroungetdedNodes()
	 */
	public Set<ModelRefGroundedNode> getModelRefGroungetdedNodes() {
		return modelRefGroungetdedNodes;
	}

}
