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
package org.wymiwyg.rdf.molecules.functref.impl2;

import java.util.Map;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * @author reto
 *
 */
public class NonTerminalMoleculeImpl extends SingletonGraph implements NonTerminalMolecule, Finalizable {

	
	
	/**
	 * @param afgn
	 * @param originalTriple
	 * @param resultMap
	 */
	public NonTerminalMoleculeImpl(Node afgn, Triple originalTriple,
			Map<Node, FunctionallyGroundedNodeImpl> resultMap) {
		super(processTriple(afgn, originalTriple, resultMap));
	}

	/**
	 * @param afgn
	 * @param originalTriple
	 * @param resultMap
	 * @return
	 */
	private static Triple processTriple(Node afgn, Triple originalTriple,
			Map<Node, FunctionallyGroundedNodeImpl> resultMap) {
		Triple triple;
		Node subject = originalTriple.getSubject();
		Node object = originalTriple.getObject();
		if (subject.equals(afgn)) {
			subject = GROUNDED_NODE;
		} else {
			if (resultMap.containsKey(subject)) {
				subject = resultMap.get(subject);
			}
		}
		if (object.equals(afgn)) {
			object = GROUNDED_NODE;
		} else {
			if (resultMap.containsKey(object)) {
				object = resultMap.get(object);
			}
		}
		triple = new TripleImpl(subject, originalTriple.getPredicate(), object);
		return triple;
	}



}
