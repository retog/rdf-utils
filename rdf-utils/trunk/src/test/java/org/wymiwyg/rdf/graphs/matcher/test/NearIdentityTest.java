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
package org.wymiwyg.rdf.graphs.matcher.test;

import junit.framework.TestCase;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaModelGraph;
import org.wymiwyg.rdf.graphs.matcher.GraphMatcher;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author reto
 *
 */
public class NearIdentityTest extends TestCase {


	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.molecules.test.MoleculesTestCase#createOntology()
	 */
	protected Model createOntology() {
		return ModelFactory.createDefaultModel();
	}

	
	public void testInverseRelation() {
		assertNotIsomorphic("similar but not equal molecules",createMolecule11(),createMolecule12());
	}
	
	private Model createMolecule11() {
		Model model = ModelFactory.createDefaultModel();
		Resource a  = model.createResource().addProperty(RDFS.label, "foo");
		Resource b  = model.createResource().addProperty(RDFS.label, "bar");
		a.addProperty(RDFS.seeAlso, b);
		return model;
	}
	
	private Model createMolecule12() {
		Model model = ModelFactory.createDefaultModel();
		Resource a  = model.createResource().addProperty(RDFS.label, "foo");
		Resource b  = model.createResource().addProperty(RDFS.label, "bar");
		b.addProperty(RDFS.seeAlso, a);
		return model;
	}
	
	public void testInverseRelation2() {
		assertNotIsomorphic("similar but not equal molecules",createMolecule21(),createMolecule22());
	}

	private Model createMolecule21() {
		Model model = ModelFactory.createDefaultModel();
		Resource a  = model.createResource().addProperty(RDFS.seeAlso, model.createResource()).addProperty(RDFS.label, "foo");
		Resource b  = model.createResource().addProperty(RDFS.seeAlso, model.createResource()).addProperty(RDFS.label, "bar");
		a.addProperty(RDFS.seeAlso, b);
		return model;
	}
	
	private Model createMolecule22() {
		Model model = ModelFactory.createDefaultModel();
		Resource a  = model.createResource().addProperty(RDFS.seeAlso, model.createResource()).addProperty(RDFS.label, "foo");
		Resource b  = model.createResource().addProperty(RDFS.seeAlso, model.createResource()).addProperty(RDFS.label, "bar");
		b.addProperty(RDFS.seeAlso, a);
		return model;
	}

	public void testBiforkedChain() {
		assertNotIsomorphic("biforked chain", createModel31(), createModel32());
	}

	private Model createModel31() {
		Model model = ModelFactory.createDefaultModel();
		//Resource a  = model.createResource();
		Resource b  = model.createResource();
		Resource c  = model.createResource();
		Resource d  = model.createResource();
		Resource d1 = model.createResource();
		//a.addProperty(RDFS.seeAlso, b);
		b.addProperty(RDFS.seeAlso, c);
		c.addProperty(RDFS.seeAlso, d);
		c.addProperty(RDFS.seeAlso, d1);
		return model;
	}
	
	private Model createModel32() {
		Model model = ModelFactory.createDefaultModel();
		//Resource a  = model.createResource();
		Resource b  = model.createResource();
		Resource c  = model.createResource();
		Resource d  = model.createResource();
		Resource c1 = model.createResource();
		//a.addProperty(RDFS.seeAlso, b);
		b.addProperty(RDFS.seeAlso, c);
		c.addProperty(RDFS.seeAlso, d);
		b.addProperty(RDFS.seeAlso, c1);
		return model;
	}
	
	public void testSameNodeSignatures() {
		assertNotIsomorphic("same first level signatures", createModel41(),createModel42());
	}
	
	
	private Model createModel41() {
		Model model = ModelFactory.createDefaultModel();
		//this is the type of the anon-resources used just to keep stuff within one contextual molecule
		Resource anonType = model.createResource();
		Resource a  = model.createResource(anonType);
		Resource b  = model.createResource(anonType);
		Resource c  = model.createResource(anonType);
		Resource d  = model.createResource(anonType);
		Resource e  = model.createResource(anonType);
		Resource f  = model.createResource(anonType);
		a.addProperty(RDFS.seeAlso, b);
		b.addProperty(RDFS.seeAlso, c);
		d.addProperty(RDFS.seeAlso, e);
		d.addProperty(RDFS.seeAlso, f);
		return model;
	}
	
	private Model createModel42() {
		Model model = ModelFactory.createDefaultModel();
		//this is the type of the anon-resources used just to keep stuff within one contextual molecule
		Resource anonType = model.createResource();
		Resource a  = model.createResource(anonType);
		Resource b  = model.createResource(anonType);
		Resource c  = model.createResource(anonType);
		Resource d  = model.createResource(anonType);
		Resource e  = model.createResource(anonType);
		Resource f  = model.createResource(anonType);
		a.addProperty(RDFS.seeAlso, b);
		c.addProperty(RDFS.seeAlso, d);
		c.addProperty(RDFS.seeAlso, e);
		e.addProperty(RDFS.seeAlso, f);
		return model;
	}
	
	private void assertNotIsomorphic(String message, Model model1, Model model2) {
		long startTime = System.currentTimeMillis();
		assertFalse("Jena isomorphism", model1.isIsomorphicWith(model2));
		System.out.println(message+" took with jena "+(System.currentTimeMillis()-startTime));
		Graph g1 = new JenaModelGraph(model1, false);
		Graph g2 = new JenaModelGraph(model2, false);
		startTime = System.currentTimeMillis();
		assertNull(message, GraphMatcher.getValidMapping(g1, g2));
		System.out.println(message+" took "+(System.currentTimeMillis()-startTime));
		
	}
}
