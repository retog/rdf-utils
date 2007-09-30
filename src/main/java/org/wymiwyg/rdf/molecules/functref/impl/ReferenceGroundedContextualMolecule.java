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

import java.util.Map;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.molecules.ContextualMolecule;

/**
 * @author reto
 *
 */
public class ReferenceGroundedContextualMolecule extends
		AbstractReferenceGroundedMolecule implements ContextualMolecule {

	Node[] ubngn;
	/**
	 * @param molecule
	 * @param replacements
	 */
	public ReferenceGroundedContextualMolecule(ContextualMolecule molecule, Map<Node, FunctionallyGroundedNode> replacements) {
		super(molecule, replacements);
		Node[] origUbng = molecule.getUsedButNotGroundedNodes();
		ubngn = new Node[origUbng.length - replacements.size()];
		int i = 0;
		for (int j = 0; j < origUbng.length; j++) {
			Node node = origUbng[j];
			if (!replacements.containsKey(node)) {
				ubngn[i++] = node;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.molecules.ContextualMolecule#getUsedButNotGroundedNodes()
	 */
	public Node[] getUsedButNotGroundedNodes() {
		return ubngn;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.molecules.ContextualMolecule#usesButDoesntGround(org.wymiwyg.commons.molecules.Node)
	 */
	public boolean usesButDoesntGround(Node afgn) {
		for (int i = 0; i < ubngn.length; i++) {
			if (ubngn[i].equals(afgn)) {
				return true;
			}

		}
		return false;
	}

}
