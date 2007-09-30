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

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.molecules.model.modelref.ModelRefGroundedNode;

/**
 * @author reto
 *
 */
public class InverseFunctionalNonTerminalTriple extends NonTerminalTripleImpl {

	private ModelRefGroundedNode afgn;

	/**
	 * @param object 
	 * @param predicate 
	 * @param subject 
	 * 
	 */
	public InverseFunctionalNonTerminalTriple(ModelRefGroundedNode subject, PropertyNode predicate, Node object) {
		super(subject, predicate, object);
		this.afgn = subject;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.NonTerminalTriple#getAfgn()
	 */
	public ModelRefGroundedNode getAfgn() {
		return afgn;
	}

	

}
