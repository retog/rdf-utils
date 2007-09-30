package org.wymiwyg.rdf.graphs.jenaimpl;

import org.wymiwyg.rdf.graphs.NamedNode;

public class NamedNodeImpl implements NamedNode {

	private String uriRef;

	public NamedNodeImpl(String uriRef) {
		this.uriRef = uriRef;
	}

	public String getURIRef() {
		return uriRef;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof NamedNode) {
			return uriRef.equals(((NamedNode)obj).getURIRef());
		} else {
			return false;
		}
	}

	public int hashCode() {
		return uriRef.hashCode();
	}
	
	public String toString() {
		return "<"+uriRef+">";
	}

}
