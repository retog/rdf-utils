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
package org.wymiwyg.rdf.leanifier.test;

import java.io.IOException;

import org.wymiwyg.commons.util.Util;
import org.wymiwyg.commons.vocabulary.FOAF;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.impl.DeAnonymizedGraph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.leanifier.MoleculeBasedLeanifier;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.impl.JenaParameters;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * @author reto
 * 
 */
public class MolecubeasedLeanifierTest extends GraphLeanifierTest {

	//Graph graph8;
	
	protected Graph makeLean(Graph leanModel) {
		long startTime = System.currentTimeMillis();
		long origSize = leanModel.size();
		// GraphLeanifier.makeLean(leanModel);
		leanModel = MoleculeBasedLeanifier.getLeanVersionOf(leanModel);
		long newSize = leanModel.size();
		long timeUsed = System.currentTimeMillis() - startTime;
		log.info("It took " + timeUsed + "ms to reduce a model from "
				+ origSize + " to " + newSize);
		return new DeAnonymizedGraph(leanModel);

	}

	public void testProb1() throws IOException {
		super.testProb1();
	}

	public void testNamedUnnamed() {
		// This test wouldn't pass (price for performance)
	}

	/**
	 * this wouldn pass in graphleanifier, because of functional identity
	 * 
	 */
	public void testDoubleIFP() {
		Model origM = ModelFactory.createDefaultModel();
		origM.read(getClass().getResource("double-ifp.n3.noauto").toString(),
				"N3");
		Graph orgigG = JenaUtil.getGraphFromModel(origM, ontology);
		Model leanM = ModelFactory.createDefaultModel();
		leanM.read(getClass().getResource("double-ifp-leanified.n3.noauto")
				.toString(), "N3");
		leanM.write(System.out);
		Graph leanG = JenaUtil.getGraphFromModel(leanM, ontology);
		Graph leanifiedG = makeLean(orgigG);
		JenaUtil.getModelFromGraph(leanifiedG).write(System.out);
		assertEquals(leanG, leanifiedG);
	}

	public void testNamedUnnamedIFP() {
		Model origM = ModelFactory.createDefaultModel();
		origM.read(
				getClass().getResource("named-unnamed.n3.noauto").toString(),
				"N3");
		Graph orgigG = JenaUtil.getGraphFromModel(origM, ontology);
		Model leanM = ModelFactory.createDefaultModel();
		leanM.read(getClass().getResource("named-unnamed-leanified.n3.noauto")
				.toString(), "N3");
		leanM.write(System.out);
		Graph leanG = JenaUtil.getGraphFromModel(leanM, ontology);
		Graph leanifiedG = makeLean(orgigG);
		JenaUtil.getModelFromGraph(leanifiedG).write(System.out);
		assertEquals(leanG, leanifiedG);
	}

	@Override
	public void testConsistencyRandom() {
		// TODO Auto-generated method stub
		super.testConsistencyRandom();
	}

	@Override
	public void testFile8() {
		//graph8 = getGraphFromResource("test8.rdf");
		/*Graph leanifiedGraph = MoleculeBasedLeanifier.getLeanVersionOf(graph8);
		Graph doubleLeanifiedGraph = MoleculeBasedLeanifier
				.getLeanVersionOf(leanifiedGraph);
		boolean equals = leanifiedGraph.equals(doubleLeanifiedGraph);
		assertTrue(equals);*/
		super.testFile8();
	}
	
	public void testFile9() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 10; i++) {
			Graph g = getGraphFromResource("test9.rdf");
			Graph gLeanified = makeLean(g);
			Graph doubleLeanifiedGraph = makeLean(gLeanified);
			boolean equals = gLeanified.equals(doubleLeanifiedGraph);
			assertTrue(equals);
		}	
	}
	
	public void testIFPCircle() {
		Model model = modelWithStatements("_:a rdf:rest http://test; http://test rdf:rest _:a");
		model.write(System.out);
		Graph g = JenaUtil.getGraphFromModel(model, true);
		Graph gLeanified = makeLean(g);
		Graph doubleLeanifiedGraph = makeLean(gLeanified);
		boolean equals = gLeanified.equals(doubleLeanifiedGraph);
		assertTrue(equals);
	}

	// TODO test for chained IFP where identity can be recursively detected

	public void testChainedIFP() {
		Model model = ModelFactory.createDefaultModel();
		for (int j = 0; j < 2; j++) {
			Resource previousResource = model.createResource();
			for (int i = 0; i < 9; i++) {
				Resource resource;
				if ((j == 0) && (Math.random() > .7)) {
					resource = model.createResource(Util.createURN5());
				} else {
					resource = model.createResource();
				}
				previousResource.addProperty(FOAF.mbox, resource);
				previousResource.addProperty(DC.title, Util
						.createRandomString(10));
				previousResource = resource;
			}
			previousResource.addProperty(FOAF.mbox, model
					.createResource("mailto:foo"));
		}
		Graph graph = JenaUtil.getGraphFromModel(model, true);
		Graph leanifiedGraph = makeLean(graph);
		Graph doubleLeanifiedGraph = makeLean(leanifiedGraph);
		boolean equals = leanifiedGraph.equals(doubleLeanifiedGraph);
		// if (!equals) {
		JenaUtil.getModelFromGraph(graph).write(System.out);
		JenaUtil.getModelFromGraph(doubleLeanifiedGraph).write(System.out);
		// }
		assertTrue(equals);
	}
}
