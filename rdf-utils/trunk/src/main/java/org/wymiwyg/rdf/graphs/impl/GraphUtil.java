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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;

/**
 * @author reto
 * @param <C>
 * 
 */
public class GraphUtil<C extends Collection<Triple>> {
	
	private static final Log log = LogFactory.getLog(GraphUtil.class);

	public <T extends Collection<? extends Triple>> C replaceNode(
			T tripleSet, Node source, Node target, C result)
			throws SourceNodeNotFoundException {
		boolean somethingChanged = false;
		for (Triple triple : tripleSet) {
			boolean tripleModified = false;
			Node subject = triple.getSubject();
			if (subject.equals(source)) {
				subject = target;
				tripleModified = true;
			}
			Node object = triple.getObject();
			if (object.equals(source)) {
				object = target;
				tripleModified = true;
			}
			if (tripleModified) {
				result
						.add(new TripleImpl(subject, triple.getPredicate(),
								object));
				somethingChanged = true;
			} else {
				result.add(triple);
			}
		}
		if (!somethingChanged) {
			throw new SourceNodeNotFoundException();
		}
		return result;
	}



	public static <T extends Collection<? extends Triple>, U extends Collection<? extends Node>> Graph replaceNode(
			T tripleSet, U sources, Node target)
			throws SourceNodeNotFoundException {
		boolean somethingChanged = false;
		SimpleGraph result = new SimpleGraph();
		for (Triple triple : tripleSet) {
			boolean tripleModified = false;
			Node subject = triple.getSubject();
			if (sources.contains(subject)) {
				subject = target;
				tripleModified = true;
			}
			Node object = triple.getObject();
			if (sources.contains(object)) {
				object = target;
				tripleModified = true;
			}
			if (tripleModified) {
				result
						.add(new TripleImpl(subject, triple.getPredicate(),
								object));
				somethingChanged = true;
			} else {
				result.add(triple);
			}
		}
		if (!somethingChanged) {
			throw new SourceNodeNotFoundException();
		}
		result.markFinalized();
		return result;
	}



	/**
	 * @param newNt
	 * @param fgn
	 * @return
	 */
	public static boolean contains(Collection<Triple> triples, GroundedNode node) {
		for (Triple triple : triples) {
			if (triple.getSubject().equals(node)) {
				return true;
			}
			if (triple.getObject().equals(node)) {
				return true;
			}
		}
		return false;
	}



	/**
	 * @param currentCandidate
	 * @param groundingNode
	 * @param impl
	 * @return
	 */
	public static Triple replaceInTriple(Triple triple, Node origNode,
			Node replacementNode) {
		Node subject = triple.getSubject();
		Node object = triple.getObject();
		PropertyNode predicate = triple.getPredicate();
		if (subject.equals(origNode)) {
			subject = replacementNode;
		}
		if (object.equals(origNode)) {
			object = replacementNode;
		}
		return new TripleImpl(subject, predicate, object);
	}
}
