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
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GraphTestBase;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionalPropertyNodeImpl;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition;
import org.wymiwyg.rdf.molecules.model.modelref.NonFunctionalModelReferencingTriple;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;

import com.hp.hpl.jena.ontology.impl.FunctionalPropertyImpl;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.shared.impl.JenaParameters;

/**
 * @author reto
 * 
 */
public class ReferenceGroundedDecompositionTest extends GraphTestBase {

	/**
	 * @param arg0
	 */
	public ReferenceGroundedDecompositionTest(String arg0) {
		super(arg0);
	}

	public void testDoubleIFP() {
		Model model = ModelFactory.createDefaultModel();
		model.read(getClass().getResource("double-ifp.n3.noauto").toString(),
				"N3");
		ReferenceGroundedDecomposition dec1 = new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model, true)));
		assertEquals("one fg", dec1.getFunctionallyGroundedNodes().size(), 1);
	}

	public void testIFPTurn() {
		JenaParameters.disableBNodeUIDGeneration = true;
		for (int i = 0; i < 5; i++) {
			Model model = ModelFactory.createDefaultModel();
			model
					.read(getClass().getResource("unlean-ifp-turn.rdf")
							.toString());
			ReferenceGroundedDecomposition dec1 = new ReferenceGroundedDecompositionImpl(
					new ModelReferencingDecompositionImpl(JenaUtil
							.getGraphFromModel(model, true)));
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
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model, true)));
		Set<FunctionallyGroundedNode> fgNodes = dec
				.getFunctionallyGroundedNodes();
		ReferenceGroundedDecomposition dec1 = new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model1, true)));
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
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model, true)));
		Set<FunctionallyGroundedNode> fgNodes = dec
				.getFunctionallyGroundedNodes();
		ReferenceGroundedDecomposition dec1 = new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model1, true)));
		Set<FunctionallyGroundedNode> fgNodes1 = dec1
				.getFunctionallyGroundedNodes();
		FunctionallyGroundedNode fgNode = selectFGNodeWithProperty(fgNodes, new FunctionalPropertyNodeImpl(FOAF.mbox.getURI()));
		FunctionallyGroundedNode fgNode1 = selectFGNodeWithProperty(fgNodes1, new FunctionalPropertyNodeImpl(FOAF.mbox.getURI()));
		System.out.println("Hash: " + fgNode.hashCode());
		System.out.println("Hash1: " + fgNode1.hashCode());
		System.out.println("Equals: " + fgNode.equals(fgNode1));
		assertDiffer("Hashsed", fgNode.hashCode(), fgNode1.hashCode());
	}

	/**
	 * @param fgNodes
	 * @param impl
	 * @return
	 */
	private FunctionallyGroundedNode selectFGNodeWithProperty(Set<FunctionallyGroundedNode> fgNodes, PropertyNode property) {
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
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model, true)));
		Set<FunctionallyGroundedNode> fgNodes = dec
				.getFunctionallyGroundedNodes();
		ReferenceGroundedDecomposition dec1 = new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model1, true)));
		Set<FunctionallyGroundedNode> fgNodes1 = dec1
				.getFunctionallyGroundedNodes();
		FunctionallyGroundedNode fgNode = fgNodes.iterator().next();
		FunctionallyGroundedNode fgNode1 = fgNodes1.iterator().next();
		System.out.println("Hash: " + fgNode.hashCode());
		System.out.println("Hash1: " + fgNode1.hashCode());
		System.out.println("Equals: " + fgNode.equals(fgNode1));
		assertEquals("fgnodes", 2, dec1.getFunctionallyGroundedNodes().size());
		assertEquals("Hashcodes", fgNode.hashCode(), fgNode1.hashCode());
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
		ModelReferencingDecomposition modelDec = new ModelReferencingDecompositionImpl(
				JenaUtil.getGraphFromModel(model, false));
		System.out.println("Conextual molecules in modelDec: "
				+ modelDec.getNonFunctionalTriples().size());
		for (Iterator<NonFunctionalModelReferencingTriple> iter = modelDec
				.getNonFunctionalTriples().iterator(); iter.hasNext();) {
			Triple current = iter.next();
			System.out.println(current.getClass() + ": " + current);
		}

		ReferenceGroundedDecomposition refDec = new ReferenceGroundedDecompositionImpl(
				modelDec);
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

	public void testChainedFunctional() {
		Set<FunctionallyGroundedNode> previous = null;
		Graph previousReconstructed = null;
		for (int i = 0; i < 4; i++) {
			Model origM = ModelFactory.createDefaultModel();
			origM
					.read(getClass().getResource("double-ifp.n3").toString(),
							"N3");
			Graph orgigG = JenaUtil.getGraphFromModel(origM, true);
			ModelReferencingDecomposition mDec = new ModelReferencingDecompositionImpl(
					orgigG);
			ReferenceGroundedDecomposition ref = new ReferenceGroundedDecompositionImpl(
					mDec);
			Set<FunctionallyGroundedNode> fgNodes = ref
					.getFunctionallyGroundedNodes();
			// System.out.println(fgNodes.size()) ;
			System.out.println(fgNodes);
			// TODO: not sure, if the second shouldn be allowed till
			// leanification
			assertEquals("fgnodes", 1, fgNodes.size());
			for (FunctionallyGroundedNode node : fgNodes) {
				Set<NonTerminalMolecule> groundingModelcules = node
						.getGroundingMolecules();

				for (NonTerminalMolecule molecule : groundingModelcules) {
					System.out.println("molecule:");
					System.out.println(molecule);
					assertEquals("nt-molecules statements", 1, molecule.size());
				}

				System.out.println(node.getGroundingMolecules().size());
			}
			Graph reconstructed = ReferenceGroundedUtil.reconstructGraph(ref);
			// System.out.println(reconstructed) ;
			JenaUtil.getModelFromGraph(reconstructed).write(System.out, "N3");

			// assertEquals("round "+i, 3, ntMolecules.size());
			if (previous != null) {
				System.out.println(fgNodes.hashCode());
				System.out.println(previous.hashCode());
				FunctionallyGroundedNode[] fgNodesArray = fgNodes
						.toArray(new FunctionallyGroundedNode[fgNodes.size()]);
				FunctionallyGroundedNode[] previousArray = previous
						.toArray(new FunctionallyGroundedNode[previous.size()]);
				System.out.println(fgNodesArray[0].equals(previousArray[0]));
				Set<NonTerminalMolecule> fgMolecules = fgNodesArray[0]
						.getGroundingMolecules();
				Set<NonTerminalMolecule> previousMolecules = previousArray[0]
						.getGroundingMolecules();
				System.out.println(fgMolecules.equals(previousMolecules));
				System.out.println(fgMolecules.toArray()[1]
						.equals(previousMolecules.toArray()[1]));
				System.out.println(fgNodesArray[0].hashCode());
				System.out.println(previousArray[0].hashCode());
				assertEquals("round " + i, fgNodes, previous);
				assertEquals("round " + i, fgNodes, previous);
			}
			previous = fgNodes;
			if (previousReconstructed != null) {
				assertEquals("reconstructed, round " + i, reconstructed,
						previousReconstructed);
			}
			previousReconstructed = reconstructed;
		}
	}

	protected Molecule getSingleMolecule(Model model) {
		ModelReferencingDecomposition modelDec = new ModelReferencingDecompositionImpl(
				JenaUtil.getGraphFromModel(model, false));
		ReferenceGroundedDecomposition refDec = new ReferenceGroundedDecompositionImpl(
				modelDec);
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
			Graph g = getGraphFromResource("test15.rdf");
			ModelReferencingDecomposition modelDec = new ModelReferencingDecompositionImpl(
					g);
			System.out.println(modelDec.getCandidateNonTerminalPartTriples());
			ReferenceGroundedDecomposition refDec = new ReferenceGroundedDecompositionImpl(
					modelDec);
			IntSet sizes = new IntOpenHashSet();
			for (FunctionallyGroundedNode fgNode : refDec
					.getFunctionallyGroundedNodes()) {
				sizes.add(fgNode.getGroundingMolecules().size());
			}
			System.out.println(sizes);
			IntSet expectedSizes = new IntOpenHashSet();
			expectedSizes.add(1); //for two nodes
			expectedSizes.add(3); //for the node with the 2 mboxes
			assertEquals(expectedSizes, sizes);
			// System.out.println(refDec.getFunctionallyGroundedNodes());
		}
	}
}
