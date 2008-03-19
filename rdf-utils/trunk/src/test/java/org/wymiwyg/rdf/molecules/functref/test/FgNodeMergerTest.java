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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.wymiwyg.commons.util.Util;
import org.wymiwyg.commons.vocabulary.FOAF;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.InverseFunctionalPropertyNodeImpl;
import org.wymiwyg.rdf.graphs.impl.AnonymizedGraph;
import org.wymiwyg.rdf.graphs.impl.NamedNodeImpl;
import org.wymiwyg.rdf.graphs.impl.NodeImpl;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.leanifier.MoleculeBasedLeanifier;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl2.FgNodeMerger2;
import org.wymiwyg.rdf.molecules.functref.impl2.ReferenceGroundedDecompositionImpl2;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.sun.org.apache.xalan.internal.xsltc.dom.AnyNodeCounter;

import static org.junit.Assert.*;

/**
 * @author reto
 * 
 */
public class FgNodeMergerTest {

	@Test
	public void simple() {
		SimpleGraph graph = new SimpleGraph();
		Node nanmedNode = new NamedNodeImpl("http://1.example.org/");
		graph.add(new TripleImpl(new NodeImpl(),
				new InverseFunctionalPropertyNodeImpl(
						"http://example.org/functional"), nanmedNode));
		Node fg2 = new NodeImpl();
		graph.add(new TripleImpl(fg2, new InverseFunctionalPropertyNodeImpl(
				"http://example.org/functional"), nanmedNode));
		graph.add(new TripleImpl(fg2, new InverseFunctionalPropertyNodeImpl(
				"http://example.org/f2"), new NamedNodeImpl(
				"http://2.example.org/")));
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl2(
				graph);
		Set<FunctionallyGroundedNode> functionallyGroundedNodes = dec
				.getFunctionallyGroundedNodes();
		assertEquals("Precondition", 2, functionallyGroundedNodes.size());
		Map<Object, FunctionallyGroundedNode> originalNodeMap = new HashMap<Object, FunctionallyGroundedNode>();
		int counter = 0;
		for (FunctionallyGroundedNode functionallyGroundedNode : functionallyGroundedNodes) {
			counter++;
			originalNodeMap.put(new Integer(counter), functionallyGroundedNode);
		}
		;
		assertEquals(1, new HashSet<FunctionallyGroundedNode>(FgNodeMerger2
				.mergeFgNodes(originalNodeMap).values()).size());
	}

	@Test
	public void testChainedIFP() {
		Model model = ModelFactory.createDefaultModel();
		for (int j = 0; j < 2; j++) {
			// creates a chain [:mbox [:mbox ... [:mbox foo] ... ]]
			Resource previousResource = model.createResource();
			for (int i = 0; i < 9; i++) {
				Resource resource;
				if ((i % 2) == 0) {
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
		// model.write(System.out);
		Graph graph = JenaUtil.getGraphFromModel(model, true);
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl2(
				graph);
		Set<FunctionallyGroundedNode> functionallyGroundedNodes = dec
				.getFunctionallyGroundedNodes();
		// System.out.println(functionallyGroundedNodes.size());
		Map<Object, FunctionallyGroundedNode> originalNodeMap = new HashMap<Object, FunctionallyGroundedNode>();
		int counter = 0;
		for (FunctionallyGroundedNode functionallyGroundedNode : functionallyGroundedNodes) {
			counter++;
			originalNodeMap.put(new Integer(counter), functionallyGroundedNode);
		}
		assertEquals(10, new HashSet<FunctionallyGroundedNode>(FgNodeMerger2
				.mergeFgNodes(originalNodeMap).values()).size());
	}

	/**
	 * checks that self-contained fg-nodes are consistently merged without
	 * exception
	 */
	@Test
	public void fileTest4() {
		for (int i = 0; i < 10; i++) {
			Model origM = ModelFactory.createDefaultModel();
			origM.read(getClass().getResource("test4.rdf").toString());

			Graph graph = new AnonymizedGraph(JenaUtil.getGraphFromModel(origM,
					true));
			ReferenceGroundedDecomposition ref = new ReferenceGroundedDecompositionImpl2(
					graph);
			Set<FunctionallyGroundedNode> fgNodes = ref
					.getFunctionallyGroundedNodes();
			Map<Object, FunctionallyGroundedNode> map = map(fgNodes);
			for (int j = 0; j < 20; j++) {
				map = FgNodeMerger2.mergeFgNodes(map);
			}
		}
	}

	
	
	@Test
	public void keySetUnmodified() {
		Model model = ModelFactory.createDefaultModel();
		for (int j = 0; j < 2; j++) {
			Resource previousResource = model.createResource();
			for (int i = 0; i < 10; i++) {
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
		ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl2(
				new AnonymizedGraph(graph));
		/*System.out.println(dec.getContextualMolecules().size() + " - "
				+ dec.getFunctionallyGroundedNodes().size() + " - "
				+ dec.getTerminalMolecules().size());*/
		Set<FunctionallyGroundedNode> functionallyGroundedNodes = dec
				.getFunctionallyGroundedNodes();
		Map<?, FunctionallyGroundedNode> nodeMap1 = map(functionallyGroundedNodes);
		System.out.println(new Date());
		Map<?, FunctionallyGroundedNode> nodeMap1Merged = FgNodeMerger2
				.mergeFgNodes(nodeMap1);
		System.out.println(new Date());
		assertEquals("key sets equals", nodeMap1.keySet(), nodeMap1Merged
				.keySet());
		Set<FunctionallyGroundedNode> functionallyGroundedNodesMerged1 = new HashSet<FunctionallyGroundedNode>(
				nodeMap1Merged.values());

		Map<?, FunctionallyGroundedNode> nodeMap2 = map(functionallyGroundedNodesMerged1);
		Set<FunctionallyGroundedNode> functionallyGroundedNodesMerged2 = new HashSet<FunctionallyGroundedNode>(
				FgNodeMerger2.mergeFgNodes(nodeMap2).values());
		assertEquals(functionallyGroundedNodesMerged1,
				functionallyGroundedNodesMerged2);

	}

	static <T> Map<Object, T> map(Set<T> values) {
		Map<Object, T> result = new HashMap<Object, T>();
		int counter = 0;
		for (T t : values) {
			result.put(new Integer(++counter), t);
		}
		return result;
	}

}
