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
package org.wymiwyg.rdf.molecules.impl;

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

	
	
	private Object tempIdentity = new Object();

	/**
	 * @param afgn the node that is to be replaced with NonTerminalMolecule.GROUNDED_NODE
	 * @param originalTriple
	 * @param nodeReplacementMap used to replace nodes in the triples 
	 */
	public NonTerminalMoleculeImpl(Node afgn, Triple originalTriple,
			Map<Node, FunctionallyGroundedNodeImpl> nodeReplacementMap) {
		super(processTriple(afgn, originalTriple, nodeReplacementMap));
	}
	/**
	 * 
	 * @param triple the triple must contain NonTerminalMolecule.GROUNDED_NODE
	 */
	public NonTerminalMoleculeImpl(Triple triple) {
		super(triple);
	}

	/**
	 * @param afgn
	 * @param originalTriple
	 * @param nodeReplacementMap
	 * @return
	 */
	private static Triple processTriple(Node afgn, Triple originalTriple,
			Map<Node, FunctionallyGroundedNodeImpl> nodeReplacementMap) {
		Triple triple;
		Node subject = originalTriple.getSubject();
		Node object = originalTriple.getObject();
		if (subject.equals(afgn)) {
			subject = GROUNDED_NODE;
		} else {
			if (nodeReplacementMap.containsKey(subject)) {
				subject = nodeReplacementMap.get(subject);
			}
		}
		if (object.equals(afgn)) {
			object = GROUNDED_NODE;
		} else {
			if (nodeReplacementMap.containsKey(object)) {
				object = nodeReplacementMap.get(object);
			}
		}
		triple = new TripleImpl(subject, originalTriple.getPredicate(), object);
		return triple;
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
