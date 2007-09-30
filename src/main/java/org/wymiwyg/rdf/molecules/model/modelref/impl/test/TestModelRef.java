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
package org.wymiwyg.rdf.molecules.model.modelref.impl.test;

import junit.framework.TestCase;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.impl.AnonymizedGraph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 *
 */
public class TestModelRef extends TestCase {

	/** This test will pass with the implementation extracting the names of named resources to ifps
	 *
	 */
	public void testNamedUnnamedFuctional() {
		Model model = ModelFactory.createDefaultModel();
		model.read(getClass().getResource("named-unnamed.n3").toString(), "N3");
		Graph graph =JenaUtil.getGraphFromModel(model, true);
		graph = new AnonymizedGraph(graph);
		ModelReferencingDecomposition dec = new ModelReferencingDecompositionImpl(graph);
		assertEquals(2, dec.getNonFunctionalTriples().size());
		assertEquals(3, dec.getNonTerminalTriples().size());
	}
	
	public void testChainedFunctional() {
//		Set<NonTerminalMolecule> previous = null;
//		for (int i = 0; i < 1; i++) {
//			Model origM = ModelFactory.createDefaultModel();
//			origM.read(getClass().getResource("double-ifp.n3").toString(), "N3");
//			Graph orgigG = JenaUtil.getGraphFromModel(origM, true);
//			ModelReferencingDecomposition dec = new ModelReferencingDecompositionImpl(orgigG);
//			//System.out.println(dec.getNonTerminalMolecules().size()) ;
//			System.out.println(dec.getNonTerminalMolecules()) ;
//			Set<NonTerminalMolecule> ntMolecules = dec.getNonTerminalMolecules();
//			for (NonTerminalMolecule molecule : ntMolecules) {
//					assertEquals("nt-molecules stetemnets", 1, molecule.size());
//			}
//			assertEquals("round "+i, 3, ntMolecules.size());
//			if (previous != null) {
//				//assertEquals("round "+i, ntMolecules, previous);
//			}
//			previous = ntMolecules;
//		}
	}
}
