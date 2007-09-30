package org.wymiwyg.rdf.graphs.jenaimpl;

import org.wymiwyg.rdf.graphs.Node;

import com.hp.hpl.jena.rdf.model.Resource;

public class JenaReferenceNode implements Node {

	private Resource jenaResource;

	public JenaReferenceNode(Resource jenaResource) {
		this.jenaResource = jenaResource;
	}

	public boolean equals(Object obj) {
		if (obj.getClass().equals(this.getClass())) {
			return jenaResource.equals(((JenaReferenceNode)obj).jenaResource);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return jenaResource.hashCode();
	}
	
	public String toString() {
		return jenaResource.toString();
	}

}
