package org.wymiwyg.rdf.graphs;


public interface Triple {
	Node getSubject();
	PropertyNode getPredicate();
	Node getObject();
}
