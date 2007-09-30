package org.wymiwyg.rdf.graphs;

public interface NamedNode extends NaturallyGroundedNode {
	
	/** get the URIRef identifying the node. Note that while it would be tempting to user a java.net.URI instead of
	 * a String as value, according to RDF-Concepts different URIRefs resolving to the same URI identify distinct
	 * nodes.
	 * 
	 * @return the URIRef of this node
	 */
	public String getURIRef();

}
