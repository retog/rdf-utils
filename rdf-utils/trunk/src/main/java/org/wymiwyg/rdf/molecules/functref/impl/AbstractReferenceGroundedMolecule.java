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
import java.util.Map;
import java.util.Set;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.ContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;

/**
 * @author reto
 *
 */
public abstract class AbstractReferenceGroundedMolecule extends AbstractGraph implements Molecule {

	private ContextualMolecule wrapped;
	private Map replacements;

	/**
	 * @param molecule
	 * @param replacements
	 */
	public AbstractReferenceGroundedMolecule(ContextualMolecule molecule, Map replacements) {
		this.wrapped = molecule;
		this.replacements = replacements;
		markFinalized();
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.molecules.Molecule#getUnionWith(org.wymiwyg.commons.molecules.Molecule)
	 */
	public Molecule getUnionWith(Molecule molecule) {
		// TODO implement, well actually the union is not a molecule...
		return null;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.molecules.Molecule#getStatements()
	 */
	private Set<Triple> getStatements() {
		Set<Triple> result = new HashSet<Triple>(wrapped.size());
		for (Iterator iter = wrapped.iterator(); iter.hasNext();) {
			Triple current = (Triple) iter.next();
			Node subject = current.getSubject();
			PropertyNode predicate = current.getPredicate();
			Node object = current.getObject();			
			Node subjectReplacement = (Node) replacements.get(subject); 
			Node objectReplacement = (Node) replacements.get(object);
			if ((subjectReplacement != null) || (objectReplacement != null)) {
				if (subjectReplacement == null) {
					subjectReplacement = subject;
				}
				if (objectReplacement == null) {
					objectReplacement = object;
				}
				result.add(new TripleImpl(subjectReplacement, predicate, objectReplacement));
			} else {
				result.add(current);
			}
		}
		return result;
	}

	public Iterator<Triple> iterator() {
		return getStatements().iterator();
	}

	public int size() {
		return getStatements().size();
	}

}
