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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.wymiwyg.commons.jena.ModelCreationUtil;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.fgnodes.impl.NaturalizedGraph;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.leanifier.GraphLeanifier;
import org.wymiwyg.rdf.leanifier.MoleculeBasedLeanifier;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.impl.JenaParameters;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author reto
 * 
 */
public class GraphLeanifierTest {


	public static final Log log = LogFactory.getLog(GraphLeanifierTest.class);

	public static final Model ontology;
	static {
		ontology = ModelFactory.createDefaultModel();
		// ontology.read(GraphLeanifierTest.class.getResource("foaf.rdf").toString());
		ontology.read(GraphLeanifierTest.class.getResource(
				"/org/wymiwyg/rdf/graphs/fgnodes/default-ontology.rdf")
				.toString());
	}

	@Test
	public void testLeanFiles() {
		// in gcj getClass().getResource("./") returns null, litle work-around
		URL dirURL = getClass().getResource("GraphLeanifierTest.class");
		File dir;
		try {
			dir = new File(new URI(dirURL.toString())).getParentFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return (pathname.getPath().endsWith("-lean.n3"));
			}
		});
		for (int i = 0; i < files.length; i++) {
			File leanFile = files[i];
			Model leanModel = ModelFactory.createDefaultModel();
			Graph leanGraph = JenaUtil.getGraphFromModel(leanModel, ontology);
			try {
				leanModel.read(leanFile.toURL().toString(), "N3");
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			Model leanModelOrig = ModelFactory.createDefaultModel();
			Graph leanGraphOrig = JenaUtil.getGraphFromModel(leanModelOrig,
					ontology);
			try {
				leanModelOrig.read(leanFile.toURL().toString(), "N3");
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			leanGraph = makeLean(leanGraph);
			// leanModel.write(System.out, "N3");
			/*
			 * if (!leanGraphOrig.equals(leanGraph)) { Map mapping =
			 * SubGraphMatcher.getValidMapping(leanGraph, leanGraphOrig);
			 * leanGraphOrig.removeAll(org.wymiwyg.rdf.graphs.matcher.GraphMatcher.applyMapping(leanGraph,mapping));
			 * JenaUtil.getModelFromGraph(leanGraphOrig).write(System.out,
			 * "N3"); }
			 */
			// System.out.println("orig:");
			// JenaUtil.getModelFromGraph(leanGraphOrig).write(System.out);
			// System.out.println();
			// System.out.println("lean:");
			// JenaUtil.getModelFromGraph(leanGraph).write(System.out);
			assertTrue("checking" + leanFile + " remains unmodified",
					leanGraphOrig.equals(leanGraph));
		}

	}

	/**
	 * @param leanModel
	 */
	protected Graph makeLean(Graph leanModel) {
		long startTime = System.currentTimeMillis();
		long origSize = leanModel.size();
		
		GraphLeanifier.makeLean(leanModel);
		// leanModel = MoleculeBasedLeanifier.getLeanVersionOf(leanModel);
		long newSize = leanModel.size();
		long timeUsed = System.currentTimeMillis() - startTime;
		if (log.isDebugEnabled()) {
			log.debug("It took " + timeUsed + "ms to reduce a model from "
					+ origSize + " to " + newSize);
		}
		return leanModel;

	}

	public void testShowTime() throws SecurityException, IOException {
		int startSize = 20;
		for (int i = 0; i < 3; i++) {
			makeLean(JenaUtil.getGraphFromModel(ModelCreationUtil
					.createRandomModel(startSize + i), true));
		}
	}

	@Test
	public void testCompareWithLeanifiedFiles() {
		// in gcj getClass().getResource("./") returns null, litle work-around
		URL dirURL = getClass().getResource("GraphLeanifierTest.class");
		File dir;
		try {
			dir = new File(new URI(dirURL.toString())).getParentFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return (pathname.getPath().endsWith(".n3")
						&& !pathname.getPath().endsWith("-leanified.n3") && !pathname
						.getPath().endsWith("-lean.n3"));
			}
		});
		for (int i = 0; i < files.length; i++) {
			File unleanifiedFile = files[i];
			String unleanifiedPath = unleanifiedFile.getPath();
			File leanifiedFile = new File(unleanifiedPath.substring(0,
					unleanifiedPath.length() - 3)
					+ "-leanified.n3");
			if (!leanifiedFile.exists()) {
				log.info(leanifiedFile + " not found, ignoring "
						+ unleanifiedPath);
			}
			Model leanifiedModel = ModelFactory.createDefaultModel();
			try {
				leanifiedModel.read(leanifiedFile.toURL().toString(), "N3");
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			Graph leanifiedGraph = JenaUtil.getGraphFromModel(leanifiedModel,
					ontology);
			Model unleanifiedModel = ModelFactory.createDefaultModel();
			try {
				unleanifiedModel.read(unleanifiedFile.toURL().toString(), "N3");
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			Graph unleanifiedGraph = JenaUtil.getGraphFromModel(
					unleanifiedModel, ontology);
			unleanifiedGraph = makeLean(unleanifiedGraph);
			assertTrue("comparing leanified model " + unleanifiedFile
					+ " with model" + leanifiedFile, unleanifiedGraph
					.equals(leanifiedGraph));
		}

	}

	@Test
	public void testNamedUnnamed() {
		Model model = ModelFactory.createDefaultModel();
		model.add(model.createResource("http://foo"), RDFS.seeAlso, model
				.createResource("http://bar"));
		Graph g1 = JenaUtil.getGraphFromModel(model, false);
		g1.size(); // needed to actually copymodel into graph.
		model.add(model.createResource(), RDFS.seeAlso, model.createResource());
		Graph g2 = JenaUtil.getGraphFromModel(model, false);
		Graph g = makeLean(g2);
		assertEquals("remove unnamed resources obsole by named ones", g, g1);
	}

	
	protected Model getModelFromResource(String resName) {
		Model model = ModelFactory.createDefaultModel();
		model.read(getClass().getResource(resName).toString());
		return model;
	}

	
	protected Graph getGraphFromResource(String resName) {
		return JenaUtil.getGraphFromModel(getModelFromResource(resName), true);
	}
	
	@Test
	public void testFile3() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 1; i++) {
			Graph g = getGraphFromResource("test3-m1.rdf");
			Graph gLean = getGraphFromResource("test3-m1-lean.rdf");
			Graph gLeanified = makeLean(g);
			// JenaUtil.getModelFromGraph(gLeanified).write(System.out, "N3");
			assertEquals(gLeanified, gLean);
		}
	}

	@Test
	public void testFile4() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 1; i++) {
			Graph g = getGraphFromResource("test4.rdf");
			Graph gLean = makeLean(g);
			log.debug("made lean");
			Graph gLeanified = makeLean(gLean);
			log.debug("made lean again");
			assertEquals(gLeanified, gLean);
		}
	}

	@Test
	public void testFile5() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 1; i++) {
			Graph g = getGraphFromResource("test5.rdf");
			Graph gLeanified = makeLean(g);
			Graph doubleLeanifiedGraph = makeLean(gLeanified);
			boolean equals = gLeanified.equals(doubleLeanifiedGraph);
			assertTrue(equals);
		}
	}

	@Test
	public void testFile8() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 10; i++) {
			log.debug("round " + i);
			Graph g = getGraphFromResource("test8.rdf");
			Graph gLeanified = makeLean(g);
			Graph doubleLeanifiedGraph = makeLean(gLeanified);
			boolean equals = gLeanified.equals(doubleLeanifiedGraph);
			assertTrue(equals);
		}
	}

	/**
	 * brought back from gvs, fails with molecules faster when strong hashcode
	 * is computed with XOR than when computed with AND
	 */
	@Test
	public void testFile15() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 50; i++) {
			log.debug("round " + i);
			Graph g = getGraphFromResource("test15.rdf");
			// System.out.println("Graph with " + g.size() + " triples");
			// JenaUtil.getModelFromGraph(g).write(System.out, "N-TRIPLE");
			Graph gLeanified = makeLean(g);
			// System.out.println("Graph with " + gLeanified.size() + "
			// triples");
			// JenaUtil.getModelFromGraph(gLeanified)
			// .write(System.out, "N-TRIPLE");
			Graph doubleLeanifiedGraph = makeLean(gLeanified);
			boolean equals = gLeanified.equals(doubleLeanifiedGraph);
			assertTrue(equals);
		}
	}
	@Test
	public void testFile3Alt() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 1; i++) {
			Graph g = getGraphFromResource("test3-m1-alt.rdf");
			Graph gLean = getGraphFromResource("test3-m1-lean.rdf");
			Graph gLeanified = makeLean(g);
			// JenaUtil.getModelFromGraph(gLeanified).write(System.out, "N3");
			assertEquals(gLeanified, gLean);
		}
	}
	@Test
	public void testDec() throws IOException {
		Graph prob1Graph = getGraphFromResource("prob1.rdf");
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl(
				prob1Graph);
		// JenaUtil.getModelFromGraph(prob1Graph).write(System.out);
		// ReferenceGroundedUtil.print(dec, new PrintWriter(System.out));
		Graph prob1GraphRec = ReferenceGroundedUtil.reconstructGraph(dec);
		/*
		 * for (FunctionallyGroundedNode fgNode :
		 * dec.getFunctionallyGroundedNodes()) { for (NonTerminalMolecule
		 * ntMolecule : fgNode.getGroundingMolecules()) {
		 * System.out.println("Non-Terminal Molecule");
		 * JenaUtil.getModelFromGraph(ntMolecule).write(System.out); } }
		 */
		// JenaUtil.getModelFromGraph(prob1GraphRec).write(System.out);
		ReferenceGroundedDecomposition recDec = new ReferenceGroundedDecompositionImpl(
				prob1GraphRec);
		// ReferenceGroundedUtil.print(recDec, new PrintWriter(System.out));
		Graph recDecRec2 = new NaturalizedGraph(ReferenceGroundedUtil
				.reconstructGraph(recDec));
		Graph recDecRec = new NaturalizedGraph(new SimpleGraph(), recDec
				.getFunctionallyGroundedNodes());
		// System.out.println(recDecRec2.equals(recDecRec));
		// JenaUtil.getModelFromGraph(recDecRec).write(System.out);
		Graph recDecRecLean = MoleculeBasedLeanifier
				.getLeanVersionOf(recDecRec);
		// JenaUtil.getModelFromGraph(recDecRecLean).write(System.out);
	}

	@Test
	public void testProb1() throws IOException {
		for (int i = 0; i < 5; i++) {
			Graph prob1Graph = getGraphFromResource("prob1.rdf");
			ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl(
					prob1Graph);
			// JenaUtil.getModelFromGraph(prob1Graph).write(System.out);
			// ReferenceGroundedUtil.print(dec, new PrintWriter(System.out));
			Graph prob1GraphRec = ReferenceGroundedUtil.reconstructGraph(dec);
			// JenaUtil.getModelFromGraph(prob1GraphRec).write(System.out);

			Graph leanifiedGraph = makeLean(prob1Graph);
			dec = new ReferenceGroundedDecompositionImpl(leanifiedGraph);
			// ReferenceGroundedUtil.print(dec, new PrintWriter(System.out));
			Graph doubleLeanifiedGraph = makeLean(leanifiedGraph);
			dec = new ReferenceGroundedDecompositionImpl(doubleLeanifiedGraph);
			// ReferenceGroundedUtil.print(dec, new PrintWriter(System.out));
			boolean equals = leanifiedGraph.equals(doubleLeanifiedGraph);
			if (!equals) {
				JenaUtil.getModelFromGraph(leanifiedGraph).write(System.out);
				JenaUtil.getModelFromGraph(doubleLeanifiedGraph).write(
						System.out);
			}
			assertTrue(equals);
		}
	}
	
	@Test
	public void testConsistencyRandom() {
		Model randomModel = null;
		Graph randomGraph;
		try {
			for (int i = 0; i < 5; i++) {
				randomModel = ModelCreationUtil.createRandomModel((int) (Math
						.random() * 200));
				randomGraph = JenaUtil.getGraphFromModel(randomModel, true);
				Graph leanifiedGraph = makeLean(randomGraph);
				Graph doubleLeanifiedGraph = makeLean(leanifiedGraph);
				boolean equals = leanifiedGraph.equals(doubleLeanifiedGraph);
				if (!equals) {
					randomModel.write(System.out);
				}
				assertTrue(equals);

			}
		} catch (RuntimeException e) {
			randomModel.write(System.out);
			throw e;
		}
	}

}
