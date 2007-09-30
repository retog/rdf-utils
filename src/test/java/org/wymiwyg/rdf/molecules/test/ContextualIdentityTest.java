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
package org.wymiwyg.rdf.molecules.test;

import org.wymiwyg.commons.util.Util;
import org.wymiwyg.rdf.molecules.Molecule;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author reto
 *
 */
public class ContextualIdentityTest extends MoleculesTestCase {


	
	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.molecules.test.MoleculesTestCase#createOntology()
	 */
	protected Model createOntology() {
		return ModelFactory.createDefaultModel();
	}


	public void testBigContextual() {
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		createSameContextualMolecules(model1, model2);
		long startTime = System.currentTimeMillis();
		assertTrue("Jena isomorphism", model1.isIsomorphicWith(model2));
		System.out.println("Jena comparison took "+(System.currentTimeMillis()-startTime));
		Molecule molecule1 = getSingleMolecule(model1);
		Molecule molecule2 = getSingleMolecule(model2);
		startTime = System.currentTimeMillis();
		assertTrue("contextaul molecules equality",molecule1.equals(molecule2));
		System.out.println("Molecule comparison took "+(System.currentTimeMillis()-startTime));
	}
	
	public void testSimpleCircle() {
		Model model1 = ModelFactory.createDefaultModel();
		Resource a1 = model1.createResource();
		Resource b1 = model1.createResource();
		a1.addProperty(RDFS.seeAlso, b1);
		b1.addProperty(RDFS.seeAlso, a1);
		Model model2 = ModelFactory.createDefaultModel();
		Resource a2 = model2.createResource();
		Resource b2 = model2.createResource();
		a2.addProperty(RDFS.seeAlso, b2);
		b2.addProperty(RDFS.seeAlso, a2);
		Molecule molecule1 = getSingleMolecule(model1);
		Molecule molecule2 = getSingleMolecule(model2);
		assertTrue("jena-test", model1.isIsomorphicWith(model2));
		assertTrue("contextaul molecules equality",molecule1.equals(molecule2));
	}

	
	
	

	/**
	 * @param model1
	 * @param model2
	 */
	private void createSameContextualMolecules(Model model1, Model model2) {
		Resource currentAnon1 = model1.createResource();
		Resource currentAnon2 = model1.createResource();
		for (int i = 0; i < 10; i ++) {
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
