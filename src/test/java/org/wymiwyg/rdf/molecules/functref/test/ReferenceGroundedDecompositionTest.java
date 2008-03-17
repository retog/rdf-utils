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

import java.util.Set;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class ReferenceGroundedDecompositionTest extends
		ReferenceGroundedDecompositionTestBase {

	/**
	 * @param arg0
	 */
	public ReferenceGroundedDecompositionTest(String arg0) {
		super(arg0);
	}

	protected ReferenceGroundedDecomposition getDecomposition(Model model,
			boolean useDefaultOntology) {
		return new ReferenceGroundedDecompositionImpl(
				new ModelReferencingDecompositionImpl(JenaUtil
						.getGraphFromModel(model, useDefaultOntology)));
	}

	/**
	 * This has to do with leanification more than decomposition
	 */
	public void testDoubleIFP() {
		Model model = ModelFactory.createDefaultModel();
		model.read(getClass().getResource("double-ifp.n3.noauto").toString(),
				"N3");
		ReferenceGroundedDecomposition dec1 = getDecomposition(model, true);
		assertEquals("one fg", 1, dec1.getFunctionallyGroundedNodes().size());
	}

	/**
	 * Assumes leanification
	 */
	public void testChainedFunctional() {
		Set<FunctionallyGroundedNode> previous = null;
		Graph previousReconstructed = null;
		for (int i = 0; i < 4; i++) {
			Model origM = ModelFactory.createDefaultModel();
			origM
					.read(getClass().getResource("double-ifp.n3").toString(),
							"N3");

			ReferenceGroundedDecomposition ref = getDecomposition(origM);
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

}
