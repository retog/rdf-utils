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
package org.wymiwyg.rdf.graphs.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.NamedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.TypedLiteralNode;
import org.wymiwyg.rdf.molecules.diff.vocabulary.MODELDIFF;

import com.hp.hpl.jena.vocabulary.OWL;

/**
 * @author reto
 *
 */
public class DeAnonymizedGraph extends AbstractGraph {

	
	/** a set with a defaul-value ehich is the NamedNode with is first when alphabetically sorting by uri
	 * 
	 * @author reto
	 *
	 */
	private class NamedNodeAlternatives {
		NamedNode canonical;
		Set<NamedNode> others = new HashSet();
		
		NamedNodeAlternatives(NamedNode node) {
			canonical =  node;
		}
		
		void addName(NamedNode node) {
			multiNodeAlternatives.add(this);
			if (canonical.getURIRef().compareTo(canonical.toString()) < 0) {
				others.add(canonical);
				canonical = node;
			} else {
				others.add(node);
			}
		}
		
	}
	
	private final Set<NamedNodeAlternatives> multiNodeAlternatives = new HashSet<NamedNodeAlternatives>();
	private final static PropertyNode nameProp = new PropertyNodeImpl(MODELDIFF.name.getURI());
	private final static PropertyNode sameAsProp = new PropertyNodeImpl(OWL.sameAs.getURI());

	private Map<Node, NamedNodeAlternatives> replaceMap = new HashMap<Node,NamedNodeAlternatives>();
	
	private Set<Triple> triples = new HashSet<Triple>();
	/**
	 * 
	 */
	public DeAnonymizedGraph(Graph base) {
		Set<Triple> nonMappingTriples = prepareMappingTable(base);
		for (Triple triple : nonMappingTriples) {
			boolean modified = false;
			Node subject = triple.getSubject();
			NamedNodeAlternatives subjectReplacementAlt = replaceMap.get(subject);
			if (subjectReplacementAlt != null) {
				subject = subjectReplacementAlt.canonical;
				modified = true;
			}
			Node object = triple.getObject();
			NamedNodeAlternatives objectReplacementAlt = replaceMap.get(object);
			if (objectReplacementAlt != null) {
				object = objectReplacementAlt.canonical;
				modified = true;
			}
			if (modified) {
				triples.add(new TripleImpl(subject, triple.getPredicate(), object));
			} else {
				triples.add(triple);
			}
		}
		addOwlSameAs();
		markFinalized();
	}


	/**
	 * 
	 */
	private void addOwlSameAs() {
		for (NamedNodeAlternatives alternatives : multiNodeAlternatives) {
			NamedNode canonical = alternatives.canonical;
			for (NamedNode other : alternatives.others) {
				triples.add(new TripleImpl(canonical, sameAsProp, other));
			}
		}
	}
	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<Triple> iterator() {
		return triples.iterator();
	}

	/** For every name-triple in base it put it in the mapping, all other are put in the triples set
	 * 
	 * @param base
	 */
	private Set<Triple> prepareMappingTable(Graph base) {
		Set<Triple> result = new HashSet<Triple>();
		for (Triple triple : base) {
			if (triple.getPredicate().equals(nameProp)) {
				NamedNode namedNode = new NamedNodeImpl(((TypedLiteralNode)triple.getObject()).getLexicalForm());
				Node subject = triple.getSubject();
				NamedNodeAlternatives namedNodeAlternatives = replaceMap.get(subject);
				if (namedNodeAlternatives == null) {
					namedNodeAlternatives = new NamedNodeAlternatives(namedNode);
					replaceMap.put(subject, namedNodeAlternatives);
				} else {
					namedNodeAlternatives.addName(namedNode);
				}
			} else {
				result.add(triple);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return triples.size();
	}

}
