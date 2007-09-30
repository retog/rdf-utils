package org.wymiwyg.rdf.graphs.impl;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;

public class TripleImpl implements Triple {

	private Node subject;
	private PropertyNode predicate;
	private Node object;

	public TripleImpl(Node subject, PropertyNode predicate, Node object) {
		if ((subject == null) || (object == null)|| (predicate == null)) {
			if (subject == null) {
				throw new NullPointerException("subject is null");
			}
			if (object == null) {
				throw new NullPointerException("object is null");
			}
			throw new NullPointerException();
		}
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public Node getSubject() {
		return subject;
	}

	public PropertyNode getPredicate() {
		return predicate;
	}

	public Node getObject() {
		return object;
	}
	
	public String toString() {
		return subject+" "+predicate+" "+object+".";// {"+getClass()+"}";
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		//if (!this.getClass().equals(obj.getClass())) {
		if (!(this instanceof Triple)) {
			return false;
		}
		TripleImpl other = (TripleImpl)obj;
		return (subject.equals(other.subject)) && (object.equals(other.object)) && (predicate.equals(other.predicate));
	}

	public int hashCode() {
        return (subject.hashCode() >> 1) ^  predicate.hashCode() ^ (object.hashCode() << 1);
	}

}
