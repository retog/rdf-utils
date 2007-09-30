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

import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.molecules.model.modelref.ModelRefGroundedNode;

/**
 * @author reto
 *
 */
public class FunctionalNonTerminalPartCandidateTriple extends NonTerminalPartCandidateTripleImpl {

	private ModelRefGroundedNode afgn;
	private ModelRefGroundedNode grounding;

	/**
	 * @param object 
	 * @param predicate 
	 * @param subject 
	 * 
	 */
	public FunctionalNonTerminalPartCandidateTriple(ModelRefGroundedNode subject, PropertyNode predicate, ModelRefGroundedNode object) {
		super(subject, predicate, object);
		this.afgn = object;
		this.grounding = subject;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.CandidateNonTerminalPartTriple#getCandidateAfgn()
	 */
	public ModelRefGroundedNode getCandidateAfgn() {
		return afgn;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.CandidateNonTerminalPartTriple#getGroundingNode()
	 */
	public ModelRefGroundedNode getGroundingNode() {
		return grounding;
	}

	

}
