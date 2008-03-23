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

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 *
 */
public abstract class MoleculesTestCase extends TestCase {

	protected Model ontology = createOntology();

	
	protected Molecule getSingleMolecule(String resourceName) {
		Model model = ModelFactory.createDefaultModel();
		model.read(getClass().getResourceAsStream(resourceName), "");
		return getSingleMolecule(model);
	}
	
	protected Molecule getSingleMolecule(Model model) {
		
		ReferenceGroundedDecomposition refDec = new ReferenceGroundedDecompositionImpl(JenaUtil.getGraphFromModel(model, true));
		Collection<Molecule> moleculeSet = new HashSet<Molecule>(refDec.getContextualMolecules());
		moleculeSet.addAll(refDec.getTerminalMolecules());
		assertEquals("Single molecule model contains one molecule", 1, moleculeSet.size());
		return (Molecule) moleculeSet.iterator().next();
	}


	/**
	 * @return
	 */
	protected abstract Model createOntology();

}
