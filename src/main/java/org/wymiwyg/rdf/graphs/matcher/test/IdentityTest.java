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

import org.wymiwyg.commons.util.Util;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaModelGraph;
import org.wymiwyg.rdf.graphs.matcher.GraphMatcher;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author reto
 *
 */
public class IdentityTest extends TestCase {


	
	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.molecules.test.MoleculesTestCase#createOntology()
	 */
	protected Model createOntology() {
		return ModelFactory.createDefaultModel();
	}


	public void testBigContextual() {
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		createSameContextualMolecules(model1, model2, 1500);
		assertIsomorphic("big contextual", model1, model2);
		
	}
	
/**
	 * @param model1
 * @param model2
	 */
	private void assertIsomorphic(String message, Model model1, Model model2) {
		long startTime = System.currentTimeMillis();
		assertTrue("Jena isomorphism", model1.isIsomorphicWith(model2));
		System.out.println(message+" took with jena "+(System.currentTimeMillis()-startTime));
		Graph g1 = new JenaModelGraph(model1, false);
		Graph g2 = new JenaModelGraph(model2, false);
		startTime = System.currentTimeMillis();
		assertNotNull(message, GraphMatcher.getValidMapping(g1, g2));
		System.out.println(message+" took "+(System.currentTimeMillis()-startTime));
		
	}


//	not actually a test
//	public void testFindFailingModel() {
//		for (int i = 2; i < 100; i++) {
//			for (int j = 0; j < 10000; j++) {
//				Model model1 = ModelFactory.createDefaultModel();
//				Model model2 = ModelFactory.createDefaultModel();
//				createSameContextualMolecules(model1, model2, i);
//				Graph g1 = new JenaModelGraph(model1);
//				Graph g2 = new JenaModelGraph(model2);
//				try {
//					if (GraphMatcher.getValidMapping(g1, g2) == null) {
//			
//					model1.write(System.out, "N3");
//					model1.write(System.out, "N-TRIPLES");
//					return;
//				}
//				} catch (Exception ex) {
//					System.out.println(ex.toString());
//						model1.write(System.out, "N3");
//					model1.write(System.out, "N-TRIPLES");
//					return;
//				}
//			}
//		}
//	}
	
	
	public void testComplexCircle() {
		//TODO hash collission occuring occasionally  
		//for (int i = 0; i < 200; i++) {
		Model model = createCircle(400);
		assertIsomorphic("complex circle", model, model);
		//}
	}
	
	Model createCircle(int size) {
		Model model = ModelFactory.createDefaultModel();
		createCircle(model, size);
		return model;
	}
	
	static void createCircle(Model model, int size) {
		Resource first = null;
		Resource last = null;
		Resource previous = null;
		for (int i = 0; i < size; i++) {
			last = model.createResource();
			if (first == null) {
				first = last;
			} else {
				previous.addProperty(RDFS.seeAlso, last);
			}
			previous = last;
		}
		last.addProperty(RDFS.seeAlso, first);
		
	}
	
	public void _testFoaf() {
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		model1.read("http://swordfish.rdfweb.org/people/libby/rdfweb/webwho.xrdf");
		model2.read("http://swordfish.rdfweb.org/people/libby/rdfweb/webwho.xrdf");
		assertIsomorphic("foaf", model1, model2);
	}
	
	
	

	/**
	 * @param model1
	 * @param model2
	 */
	static void createSameContextualMolecules(Model model1, Model model2, int size) {
		Resource currentAnon1 = model1.createResource();
		Resource currentAnon2 = model1.createResource();
		for (int i = 0; i < size; i ++) {
			boolean changeConnecting = false;
			Resource other1, other2;
			if (Math.random() > 0.5) {
				other1 = model1.createResource();
				other2 = model2.createResource();
				if (Math.random() > 0.9) {
					changeConnecting = true;
				}
			} else {
				String randumUri = Util.createURN5();
				other1 = model1.createResource(randumUri);
				other2 = model2.createResource(randumUri);
			}
			String propertyURI = "http://ex/"+Util.createRandomString(1);
			Property property1 = model1.createProperty(propertyURI);
			Property property2 = model1.createProperty(propertyURI);
			if (Math.random() > 0.5) {
				model1.add(currentAnon1, property1, other1);
				model2.add(currentAnon2, property2, other2);
			} else {
				model1.add(other1, property1, currentAnon1);
				model2.add(other2, property2, currentAnon2);
			}
			if (changeConnecting) {
				currentAnon1 = other1;
				currentAnon2 = other2;
			}
		}
		//model1.write(System.out);
	}

}
