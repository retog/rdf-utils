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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.LiteralNode;
import org.wymiwyg.rdf.graphs.NamedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.impl.InverseFunctionalPropertyNodeImpl;
import org.wymiwyg.rdf.molecules.diff.vocabulary.MODELDIFF;

/**
 * @author reto
 *
 */
public class AnonymizedGraph extends AbstractGraph {

	private Set<Triple> triples = new HashSet<Triple>();
	private Map<NamedNode, Node> replaceMap = new HashMap<NamedNode, Node>();
	
	private final static URI anyURIDataType;
	private final static PropertyNode nameProp = new InverseFunctionalPropertyNodeImpl(MODELDIFF.name.getURI());
	
	static {
	try {
		anyURIDataType = new URI("http://www.w3.org/2001/XMLSchema#anyURI");
	} catch (URISyntaxException e) {
		throw new RuntimeException(e);
	}
	}
	/**
	 * 
	 */
	public AnonymizedGraph(Graph base) {
		
		for (Triple triple : base) {
			boolean modified = false;
			Node subject = triple.getSubject();
			if (subject instanceof NamedNode) {
				subject = getReplacement((NamedNode)subject);
				modified = true;
			}
			Node object = triple.getObject();
			if (object instanceof NamedNode) {
				object = getReplacement((NamedNode)object);
				modified = true;
			}
			if (modified) {
				triples.add(new TripleImpl(subject, triple.getPredicate(), object));
			} else {
				triples.add(triple);
			}
		}
		addNameStmts();
		markFinalized();
	}

	/**
	 * @param replaceMap2
	 */
	private void addNameStmts() {
		for (Entry<NamedNode, Node> entry : replaceMap.entrySet()) {
			String uriString = entry.getKey().getURIRef();
			LiteralNode uriLit = new TypedLiteralNodeImpl(uriString, anyURIDataType);
			triples.add(new TripleImpl(entry.getValue(), nameProp, uriLit));
		}
		
	}

	/**
	 * @param node
	 * @return
	 */
	private Node getReplacement(NamedNode namedResource) {
		Node replacement = replaceMap.get(namedResource);
		if (replacement == null) {
			replacement = new NodeImpl();
			replaceMap.put(namedResource, replacement);
		}
		return replacement;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<Triple> iterator() {
		return triples.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return triples.size();
	}

}
