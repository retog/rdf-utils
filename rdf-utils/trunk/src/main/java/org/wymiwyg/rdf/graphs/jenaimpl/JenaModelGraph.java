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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author reto
 *
 */
public class JenaModelGraph extends AbstractGraph implements Graph {
	
	private Model jenaModel;
	private Set<Triple> statements;
	private Model ontology;
	private static Model defaultOntology = ModelFactory.createDefaultModel();
	private static boolean defaultOntologyRead = false;
	
	public JenaModelGraph(Model jenaModel, boolean useDeafaultOntology) {
		this.jenaModel = jenaModel;	
		if (useDeafaultOntology) {
			if (!defaultOntologyRead) {
				synchronized (defaultOntology) {
					if (!defaultOntologyRead) {
						defaultOntology.read(getClass().getResourceAsStream("/org/wymiwyg/rdf/graphs/fgnodes/default-ontology.rdf"), "");
						defaultOntologyRead = true;
					}
				}
				
			}
			this.ontology = defaultOntology;
		} else {
			this.ontology = ModelFactory.createDefaultModel();
		}
		markFinalized();
	}
	
	public JenaModelGraph(Model jenaModel, Model ontology) {
		this(jenaModel, ontology, false);
	}
	
	public JenaModelGraph(Model jenaModel, Model ontology, boolean useDeafaultOntology) {
		this.jenaModel = jenaModel;
		if (ontology == null) {
			this.ontology = ModelFactory.createDefaultModel();
		} else {
			this.ontology = ontology;
		}
		if (useDeafaultOntology) {
			if (!defaultOntologyRead) {
				synchronized (defaultOntology) {
					if (!defaultOntologyRead) {
						defaultOntology.read(getClass().getResourceAsStream("/org/wymiwyg/rdf/graphs/fgnodes/default-ontology.rdf"), "");
						defaultOntologyRead = true;
					}
				}
				
			}
			this.ontology.add(defaultOntology);
		}
		markFinalized();
	}


	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.graphs.Graph#getStatements()
	 */
	private Set<Triple> getStatements() {
		if (statements == null) {
			NodeFactory nodeFactory = new NodeFactory(ontology);
			statements = new HashSet<Triple>();
			StmtIterator iter = jenaModel.listStatements();
			while (iter.hasNext()) {
				Statement current = iter.nextStatement();
				
				Resource jenaSubject = current.getSubject();
				Node subject = nodeFactory.getNonLiteralNode(jenaSubject);
				/*if (jenaSubject.isAnon()) {
					subject = new JenaReferenceNode(jenaSubject);
				} else {
					subject = new NamedNodeImpl(jenaSubject.getURI());
				}*/
				Property jenaProperty = current.getPredicate();
				PropertyNode predicate = nodeFactory.getPropertyNode(jenaProperty);
					//new PropertyNodeImpl(jenaProperty.getURI(), false, false);
				RDFNode jenaObject = current.getObject();
				Node object = nodeFactory.getNode(jenaObject);
				/*
				if (jenaObject instanceof Literal) {
					if (((Literal)jenaObject).getDatatype() == null)	{
						object = new PlainLiteralNodeImpl((Literal)jenaObject);
					} else {
						object = new TypedLiteralNodeImpl((Literal)jenaObject);
					}
				} else {
					Resource jenaObjectRes = (Resource)jenaObject;
					if (jenaObjectRes.isAnon()) {
						object = new JenaReferenceNode(jenaObjectRes);
					} else {
						object = new NamedNodeImpl(jenaObjectRes.getURI());
					}
				}*/
				statements.add(new TripleImpl(subject, predicate, object));
			}

		}
		return statements;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	public int size() {
		return getStatements().size();
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator<Triple> iterator() {
		return getStatements().iterator();
	}



}
