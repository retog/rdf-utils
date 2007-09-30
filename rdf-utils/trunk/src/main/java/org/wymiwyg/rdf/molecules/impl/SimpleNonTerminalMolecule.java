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

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.NodeImpl;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * @author reto
 *
 */
public class SimpleNonTerminalMolecule extends SimpleGraph implements NonTerminalMolecule {

	private Node afgn = new NodeImpl();
	/**
	 * 
	 */
	public SimpleNonTerminalMolecule(Node afgn) {
		this.afgn = afgn;
	}

	public boolean add(Triple triple) {
		boolean modified = false;
		Node subject = triple.getSubject();
		Node object = triple.getObject();
		if (subject.equals(afgn)) {
			subject = GROUNDED_NODE;
			modified = true;
		}
		if (object.equals(afgn)) {
			object = GROUNDED_NODE;
			modified = true;
		}
		if (modified) {
			return super.add(new TripleImpl(subject, triple.getPredicate(), object));
		} else {
			return super.add(triple);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.Molecule#getUnionWith(org.wymiwyg.rdf.molecules.Molecule)
	 */
	public Molecule getUnionWith(Molecule graph) {
		throw new RuntimeException("Not implemented yet");
	}


}
