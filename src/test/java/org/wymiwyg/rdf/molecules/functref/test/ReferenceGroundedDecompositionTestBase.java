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
package org.wymiwyg.rdf.molecules.functref.test;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.commons.vocabulary.FOAF;
import org.wymiwyg.rdf.graphs.GraphTestBase;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionalPropertyNodeImpl;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.impl.JenaParameters;

/**
 * @author reto
 * 
 */
public abstract class ReferenceGroundedDecompositionTestBase extends
		GraphTestBase {

	/**
	 * @param arg0
	 */
	public ReferenceGroundedDecompositionTestBase(String arg0) {
		super(arg0);
	}

	public void testIFPTurn() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 5; i++) {
			Model model = ModelFactory.createDefaultModel();
			model
					.read(getClass().getResource("unlean-ifp-turn.rdf")
							.toString());
			ReferenceGroundedDecomposition dec1 = getDecomposition(model);
			assertEquals("no contextual molecules",
					new HashSet<MaximumContextualMolecule>(), dec1
							.getContextualMolecules());
		}
	}

	public void testIFPHash() {
		Model model = modelWithStatements("_:1 http://xmlns.com/foaf/0.1/mbox _:1;"
				+ " _:2 http://xmlns.com/foaf/0.1/homepage _:1;"
				+ " _:1 http://xmlns.com/foaf/0.1/mbox_sha1sum 'urn:urn-5:WNjqsnamsLlMJNMLDxjg-nLG0A8=''");
		Model model1 = modelWithStatements(" _:1 http://xmlns.com/foaf/0.1/mbox_sha1sum 'urn:urn-5:WNjqsnamsLlMJNMLDxjg-nLG0A8='';"
				+ "_:1 http://xmlns.com/foaf/0.1/mbox _:1;"
				+ " _:2 http://xmlns.com/foaf/0.1/homepage _:1;");
		ReferenceGroundedDecomposition dec = getDecomposition(model);
		Set<FunctionallyGroundedNode> fgNodes = dec
				.getFunctionallyGroundedNodes();
		ReferenceGroundedDecomposition dec1 = getDecomposition(model1);
		Set<FunctionallyGroundedNode> fgNodes1 = dec1
				.getFunctionallyGroundedNodes();
		FunctionallyGroundedNode fgNode = fgNodes.iterator().next();
		FunctionallyGroundedNode fgNode1 = fgNodes1.iterator().next();
		System.out.println("Hash: " + fgNode.hashCode());
		System.out.println("Hash1: " + fgNode1.hashCode());
		System.out.println("Equals: " + fgNode.equals(fgNode1));
		assertEquals("fgnodes", 2, dec1.getFunctionallyGroundedNodes().size());
		assertEquals("Hashsed", fgNode.hashCode(), fgNode1.hashCode());
	}

	public void testSimilarIFPHash() {
		Model model = modelWithStatements("_:1 http://xmlns.com/foaf/0.1/mbox _:2;"
				+ " _:2 http://xmlns.com/foaf/0.1/homepage _:3;"
				+ " _:3 http://xmlns.com/foaf/0.1/mbox_sha1sum 'foo';"
				+ " _:2 http://xmlns.com/foaf/0.1/homepage _:3;"
				+ " _:3 http://xmlns.com/foaf/0.1/mbox_sha1sum 'bar'");
		model.write(System.out);
		Model model1 = modelWithStatements("_:1 http://xmlns.com/foaf/0.1/mbox _:2;"
				+ " _:1 http://xmlns.com/foaf/0.1/mbox _:4;"
				+ " _:2 http://xmlns.com/foaf/0.1/homepage _:3;"
				+ " _:3 http://xmlns.com/foaf/0.1/mbox_sha1sum 'foo';"
				+ " _:4 http://xmlns.com/foaf/0.1/homepage _:5;"
				+ " _:5 http://xmlns.com/foaf/0.1/mbox_sha1sum 'bar'");
		ReferenceGroundedDecomposition dec = getDecomposition(model);
		Set<FunctionallyGroundedNode> fgNodes = dec
				.getFunctionallyGroundedNodes();
		ReferenceGroundedDecomposition dec1 = getDecomposition(model1);
		Set<FunctionallyGroundedNode> fgNodes1 = dec1
				.getFunctionallyGroundedNodes();
		FunctionallyGroundedNode fgNode = selectFGNodeWithProperty(fgNodes,
				new FunctionalPropertyNodeImpl(FOAF.mbox.getURI()));
		FunctionallyGroundedNode fgNode1 = selectFGNodeWithProperty(fgNodes1,
				new FunctionalPropertyNodeImpl(FOAF.mbox.getURI()));
		System.out.println("Hash: " + fgNode.hashCode());
		System.out.println("Hash1: " + fgNode1.hashCode());
		System.out.println("Equals: " + fgNode.equals(fgNode1));
		assertDiffer("Hashsed", fgNode.hashCode(), fgNode1.hashCode());
	}

	protected abstract ReferenceGroundedDecomposition getDecomposition(
			Model model, boolean useDefaultOntology);

	protected ReferenceGroundedDecomposition getDecomposition(Model model) {
		return getDecomposition(model, true);
	}

	/**
	 * @param fgNodes
	 * @param impl
	 * @return
	 */
	private FunctionallyGroundedNode selectFGNodeWithProperty(
			Set<FunctionallyGroundedNode> fgNodes, PropertyNode property) {
		for (FunctionallyGroundedNode node : fgNodes) {
			for (NonTerminalMolecule molecule : node.getGroundingMolecules()) {
				Triple triple = molecule.iterator().next();
				if (triple.getPredicate().equals(property)) {
					return node;
				}
			}
		}
		return null;
	}

	/*
	 * X0 <http://xmlns.com/foaf/0.1/homepage> { X1
	 * <http://xmlns.com/foaf/0.1/mbox> X0 X1
	 * <http://xmlns.com/foaf/0.1/mbox_sha1sum>
	 * "urn:urn-5:WNjqsnamsLlMJNMLDxjg-nLG0A8=" }
	 */

	public void testIFPHash2() {
		Model model = modelWithStatements("_:1 http://xmlns.com/foaf/0.1/mbox _:2;"
				+ " _:2 http://xmlns.com/foaf/0.1/homepage _:1;"
				+ " _:1 http://xmlns.com/foaf/0.1/mbox_sha1sum 'urn:urn-5:WNjqsnamsLlMJNMLDxjg-nLG0A8=''");
		Model model1 = modelWithStatements(" _:2 http://xmlns.com/foaf/0.1/mbox_sha1sum 'urn:urn-5:WNjqsnamsLlMJNMLDxjg-nLG0A8='';"
				+ "_:2 http://xmlns.com/foaf/0.1/mbox _:1;"
				+ " _:1 http://xmlns.com/foaf/0.1/homepage _:2;");
		assertTrue(model1.isIsomorphicWith(model));
		ReferenceGroundedDecomposition dec = getDecomposition(model);
		Set<FunctionallyGroundedNode> fgNodes = dec
				.getFunctionallyGroundedNodes();
		ReferenceGroundedDecomposition dec1 = getDecomposition(model1);
		Set<FunctionallyGroundedNode> fgNodes1 = dec1
				.getFunctionallyGroundedNodes();
		System.out.println("Nodes in fgNodes");
		for (FunctionallyGroundedNode fgNode : fgNodes) {
			System.out.println("Hash: " + fgNode.hashCode());
			System.out.println("Node: " + fgNode);
			System.out.println();
		}
		System.out.println("Nodes in fgNodes1");
		for (FunctionallyGroundedNode fgNode : fgNodes1) {
			System.out.println("Hash: " + fgNode.hashCode());
			System.out.println("Node: " + fgNode);
			System.out.println();
		}

		System.out.println("Set Equals: " + fgNodes.equals(fgNodes1));
		assertEquals("fgnodes", 2, dec1.getFunctionallyGroundedNodes().size());
		assertEquals("Hashcodes", fgNodes.hashCode(), fgNodes1.hashCode());
	}

	/*
	 * Test method for
	 * 'org.wymiwyg.commons.molecules.ModelAnalysis.splitModel()'
	 */
	public void testSplitModelEnptyOntology() {
		Model model = ModelFactory.createDefaultModel();
		// add one molecule
		model.read(getClass().getResourceAsStream("molecule1.rdf"), "");
		// add second
		model.read(getClass().getResourceAsStream("molecule2.rdf"), "");
		// add thrid
		model.createResource(FOAF.Person);
		ReferenceGroundedDecomposition refDec = getDecomposition(model, false);
		Collection<Molecule> moleculeSet = new HashSet<Molecule>(refDec
				.getContextualMolecules());
		moleculeSet.addAll(refDec.getTerminalMolecules());
		System.out.println("Total molecules: " + moleculeSet.size());
		for (Iterator iter = moleculeSet.iterator(); iter.hasNext();) {
			Molecule current = (Molecule) iter.next();
			System.out.println(current.getClass() + ": " + current);
		}
		Molecule molecule1 = getSingleMolecule("molecule1.rdf");
		Molecule molecule2 = getSingleMolecule("molecule2.rdf");
		assertTrue("Molecule 1 contained", moleculeSet.contains(molecule1));
		assertTrue("Molecule 2 contained", moleculeSet.contains(molecule2));
		for (Iterator iter = moleculeSet.iterator(); iter.hasNext();) {
			Molecule current = (Molecule) iter.next();
			System.out.println("---------------- Molecule ("
					+ current.getClass() + ") :");
			System.out.println(current);
		}
		assertEquals("contains 2 molecules", 3, moleculeSet.size());
	}

	/**
	 * @param string
	 * @return
	 */
	protected Molecule getSingleMolecule(String resourceName) {
		Model model = ModelFactory.createDefaultModel();
		model.read(getClass().getResourceAsStream(resourceName), "");
		return getSingleMolecule(model);
	}

	protected Molecule getSingleMolecule(Model model) {
		ReferenceGroundedDecomposition refDec = getDecomposition(model, false);
		Collection<Molecule> moleculeSet = new HashSet<Molecule>(refDec
				.getContextualMolecules());
		moleculeSet.addAll(refDec.getTerminalMolecules());
		assertEquals("Single molecule model contains one molecule", 1,
				moleculeSet.size());
		return (Molecule) moleculeSet.iterator().next();
	}

	/**
	 * one of the mbox statements occasionally goes missing
	 */
	public void test15() {
		for (int i = 0; i < 20; i++) {
			Model model = getModelFromResource("test15.rdf");
			ReferenceGroundedDecomposition refDec = getDecomposition(model);
			IntSet sizes = new IntOpenHashSet();
			for (FunctionallyGroundedNode fgNode : refDec
					.getFunctionallyGroundedNodes()) {
				sizes.add(fgNode.getGroundingMolecules().size());
			}
			System.out.println(sizes);
			IntSet expectedSizes = new IntOpenHashSet();
			expectedSizes.add(1); // for two nodes
			expectedSizes.add(3); // for the node with the 2 mboxes
			assertEquals(expectedSizes, sizes);
			// System.out.println(refDec.getFunctionallyGroundedNodes());
		}
	}

	public void testContextual() {
		Model model = ModelFactory.createDefaultModel();
		model.read(
				getClass().getResource("contextual-molecules.n3").toString(),
				"N3");

		ReferenceGroundedDecomposition ref = getDecomposition(model);
		assertEquals("counting contextula molecules", 2, ref
				.getContextualMolecules().size());

	}

	public void testSingleContextual() {
		for (int i = 0; i < 10; i++) {
			System.out.println("round "+i);
			Model model = ModelFactory.createDefaultModel();
			model.read(getClass().getResource("single-contextual.rdf")
					.toString());

			ReferenceGroundedDecomposition ref = getDecomposition(model);
			assertEquals("counting contextula molecules", 1, ref
					.getContextualMolecules().size());
		}

	}

}
