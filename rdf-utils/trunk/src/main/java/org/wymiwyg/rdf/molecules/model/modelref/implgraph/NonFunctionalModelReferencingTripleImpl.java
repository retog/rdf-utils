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
package org.wymiwyg.rdf.molecules.model.modelref.implgraph;

import java.util.HashSet;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.model.modelref.ModelRefGroundedNode;
import org.wymiwyg.rdf.molecules.model.modelref.NonFunctionalModelReferencingTriple;

/**
 * @author reto
 *
 */
public class NonFunctionalModelReferencingTripleImpl extends TripleImpl implements
		NonFunctionalModelReferencingTriple {

	Set<ModelRefGroundedNode> modelRefGroundedNodes = new HashSet<ModelRefGroundedNode>();
	/**
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	public NonFunctionalModelReferencingTripleImpl(Node subject,
			PropertyNode predicate, ModelRefGroundedNode object) {
		super(subject, predicate, object);
		modelRefGroundedNodes.add(object);
	}
	
	public NonFunctionalModelReferencingTripleImpl(ModelRefGroundedNode subject,
			PropertyNode predicate, ModelRefGroundedNode object) {
		super(subject, predicate, object);
		modelRefGroundedNodes.add(subject);
		modelRefGroundedNodes.add(object);
	}
	
	public NonFunctionalModelReferencingTripleImpl(ModelRefGroundedNode subject,
			PropertyNode predicate, Node object) {
		super(subject, predicate, object);
		modelRefGroundedNodes.add(subject);
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.NonFunctionalModelReferencingTriple#getModelRefGroungetdedNodes()
	 */
	public Set<ModelRefGroundedNode> getModelRefGroungetdedNodes() {
		return modelRefGroundedNodes;
	}

}
