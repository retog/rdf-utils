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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.NamedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PlainLiteralNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.TypedLiteralNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.NaturalizedGraph;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * @author reto
 * 
 */
public class JenaUtil {
	/**
	 * @author reto
	 * 
	 */
	public static class AnonymousMapper {

		private Map<Node, Resource> nodeResourceMap = new HashMap<Node, Resource>();

		private Model model;

		/**
		 * @param model
		 */
		public AnonymousMapper(Model model) {
			this.model = model;
		}

		/**
		 * @param subject
		 * @return
		 */
		public Resource getResource(Node node) {
			Resource result = nodeResourceMap.get(node);
			if (result == null) {
				result = model.createResource();
				nodeResourceMap.put(node, result);
			}
			return result;
		}

	}

	public static Graph getGraphFromModel(Model model, boolean useDefaultOntology) {
		return new JenaModelGraph(model, useDefaultOntology);
	}

	public static Graph getGraphFromModel(Model model, Model ontology) {
		return new JenaModelGraph(model, ontology);
	}
	/**
	 * @param model
	 * @param ontology
	 * @param useDefaultOntology
	 * @return
	 */
	public static Graph getGraphFromModel(Model model, Model ontology, boolean useDefaultOntology) {
		return new JenaModelGraph(model, ontology, useDefaultOntology);
	}

	public static Model getModelFromGraph(Graph graph) {
		Model result = ModelFactory.createDefaultModel();
		addGraphToModel(new NaturalizedGraph(graph), result);
		return result;
	}

	/**
	 * @param graph
	 * @param result
	 */
private static void addGraphToModel(Graph graph, Model model) {
		AnonymousMapper mapper = new AnonymousMapper(model);
		// int i = 0;
		for (Iterator iter = graph.iterator(); iter.hasNext();) {
			Triple currentTriple = (Triple) iter.next();
			Node subject = currentTriple.getSubject();
			Resource targetSubject;
			if (subject instanceof NamedNode) {
				targetSubject = model.createResource(((NamedNode)subject).getURIRef());
			} else {
				targetSubject  = mapper.getResource(subject);
			}
			Property targetPredicate = model.createProperty(currentTriple.getPredicate().getURIRef());
			Node object = currentTriple.getObject();
			RDFNode targetObject;
			if (object instanceof GroundedNode) {
				if (object instanceof TypedLiteralNode) {
					TypedLiteralNode literal = (TypedLiteralNode)object;
					targetObject = model.createTypedLiteral(literal.getLexicalForm(),  TypeMapper.getInstance().getSafeTypeByName(literal.getDataType().toString()));
				} else {
					if (object instanceof PlainLiteralNode) {
						PlainLiteralNode literal = (PlainLiteralNode)object;
						Locale locale = literal.getLocale(); 
						if (locale != null) {
							targetObject = model.createLiteral(literal.getLexicalForm(),  literal.getLocale().toString());
						} else {
							targetObject = model.createLiteral(literal.getLexicalForm());
						}
					} else {
						targetObject = model.createResource(((NamedNode)object).getURIRef());
					}
				}
			} else {
				targetObject  = mapper.getResource(object);
			}
			Statement statement = model.createStatement(targetSubject, targetPredicate, targetObject);
			if (model.contains(statement)) {
				throw new RuntimeException("redundant stmt "+statement);
			}
			model.add(statement);
			// i++;
			// System.out.println("Added "+ i+" size: "+model.size());
		}
	}


}
