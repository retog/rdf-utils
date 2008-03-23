/*
 * Copyright  2002-2006 WYMIWYG (http://wymiwyg.org)
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

import java.util.Map;
import java.util.Set;

import org.wymiwyg.commons.vocabulary.FOAF;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.impl.AnonymizedGraph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.FgNodeMerger;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;

/**
 * @author reto
 *
 */
public class PerformanceScaling {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long previousResult = -1;
		int size = 100;
		float sum = 0;
		for (int i = 1; i < 50; i++) {
			System.out.print("size "+size+":");
			long performanceRound = performanceRound(size);
			System.out.print(performanceRound+"ms ");
			if (previousResult != -1) {
				float factorToSizeIncrease = (float)performanceRound*(float)(size-100)/((float)previousResult*(float)size);
				System.out.print(factorToSizeIncrease);
				sum += factorToSizeIncrease;
				System.out.print("(~"+(sum/(float)(i-1))+")");
			}
			previousResult = performanceRound;
			System.out.println();
			size += 100;
		}
	}
	
	private static long performanceRound(int size) {
		Model model = ModelFactory.createDefaultModel();
		for (int j = 0; j < 1; j++) {
			Resource previousResource = model.createResource();
			for (int i = 0; i < size; i++) {
				Resource resource;
				if ((i % 2) == 1) {
					resource = model
							.createResource("urn:urn-5:" + j + "--" + i);// Util.createURN5().toString());//"http://example.org/"+i);
				} else {
					resource = model.createResource();
				}
				previousResource.addProperty(FOAF.mbox, resource);
				previousResource.addProperty(DC.title, "title-" + i);
				previousResource = resource;
			}
			previousResource.addProperty(FOAF.mbox, model
					.createResource("mailto:foo"));
		}
		Graph graph = JenaUtil.getGraphFromModel(model, true);
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl(
				new AnonymizedGraph(graph));
		/*System.out.println(dec.getContextualMolecules().size() + " - "
				+ dec.getFunctionallyGroundedNodes().size() + " - "
				+ dec.getTerminalMolecules().size());*/
		Set<FunctionallyGroundedNode> functionallyGroundedNodes = dec
				.getFunctionallyGroundedNodes();
		//System.out.println(functionallyGroundedNodes);
		Map<?, FunctionallyGroundedNode> nodeMap1 = FgNodeMergerTest.map(functionallyGroundedNodes);
		long startDate = System.currentTimeMillis();
		Map<?, FunctionallyGroundedNode> nodeMap1Merged = FgNodeMerger
				.mergeFgNodes(nodeMap1);
		long endDate = System.currentTimeMillis();
		return (endDate - startDate);
	}
}
