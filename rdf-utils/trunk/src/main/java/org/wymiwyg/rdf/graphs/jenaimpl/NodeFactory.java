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
package org.wymiwyg.rdf.graphs.jenaimpl;

import java.util.HashMap;
import java.util.Map;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionalAndInverseFunctionalPropertyNodeImpl;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionalPropertyNodeImpl;
import org.wymiwyg.rdf.graphs.fgnodes.impl.InverseFunctionalPropertyNodeImpl;
import org.wymiwyg.rdf.graphs.impl.PropertyNodeImpl;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Note that this class is not thread-safe
 * 
 * @author reto
 * 
 */
public class NodeFactory {

	private Model ontology;
	/*
	 * A map with jena-properties as key and PropertyNodes as values
	 * 
	 */
	private Map<Property, PropertyNode> predicateMap = new HashMap<Property, PropertyNode>();
	
	/**
	 * 
	 */
	public NodeFactory(Model ontology) {
		this.ontology = ontology;
	}

	public PropertyNode getPropertyNode(Property property) {
		PropertyNode result = predicateMap.get(property);
		if (result == null) {
			result = createPropertyNode(property);
			predicateMap.put(property, result);
		}
		return result;
	}

	private PropertyNode createPropertyNode(Property property) {
		boolean functional = ontology.contains(property, RDF.type,
				OWL.FunctionalProperty);
		boolean inverseFunctional = ontology.contains(property, RDF.type,
				OWL.InverseFunctionalProperty);
		if (!functional && !inverseFunctional) {
			return new PropertyNodeImpl(property.getURI());
		} else {
			if (functional && inverseFunctional) {
				return new FunctionalAndInverseFunctionalPropertyNodeImpl(property.getURI());
			} else {
				if (inverseFunctional) {
					return new InverseFunctionalPropertyNodeImpl(property.getURI());
				} else {
					return new FunctionalPropertyNodeImpl(property.getURI());
				}
			}
		}
	}

	public Node getNonLiteralNode(Resource resource) {
		if (resource.isAnon()) {
			return new JenaReferenceNode(resource);
		} else {
			return new NamedNodeImpl(resource.getURI());
		}
	}

	public Node getNode(RDFNode rdfNode) {
		if (rdfNode instanceof Literal) {
				if (((Literal)rdfNode).getDatatype() == null)	{
					return new PlainLiteralNodeImpl((Literal)rdfNode);
				} else {
					return new TypedLiteralNodeImpl((Literal)rdfNode);
				}
		} else {
			Resource resource = (Resource)rdfNode;
			if (resource.isAnon()) {
				return new JenaReferenceNode(resource);
			} else {
				return new NamedNodeImpl(resource.getURI());
			}
		}
	}

}
