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
package org.wymiwyg.rdf.molecules.diff.test;

import java.io.PrintWriter;

import junit.framework.TestCase;

import org.wymiwyg.commons.vocabulary.FOAF;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiffImpl;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl2.ReferenceGroundedDecompositionImpl2;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author reto
 * 
 */
public class MoleculesDiffTest extends TestCase {

	public void testFgNodes() {
		Model model1 = createFgNodeTestModel1(); // ModelFactory.createDefaultModel();
		Model model2 = createFgNodeTestModel2();
		ReferenceGroundedDecomposition dec1 = new ReferenceGroundedDecompositionImpl2(
				JenaUtil.getGraphFromModel(model1, true));
		ReferenceGroundedDecomposition dec2 = new ReferenceGroundedDecompositionImpl2(
				JenaUtil.getGraphFromModel(model2, true));
		MoleculeDiff diff = new MoleculeDiffImpl(dec1, dec2);
		diff.print(new PrintWriter(System.out, true));
		assertEquals("cross-graph nodset", 1, diff.getCrossGraphFgNodes()
				.size());
		assertEquals("fgnodes only in 1", 0, diff.getFgNodesOnlyIn1().size());
		assertEquals("fgnodes only in 2", 1, diff.getFgNodesOnlyIn2().size());
		assertEquals("molecules only in 1", 1, diff
				.getContextualMoleculesOnlyIn1().size());
		assertEquals("terminal molecules only in 1", 1, diff
				.getTerminalMoleculesOnlyIn1().size());
		assertEquals("molecules only in 2", 1, diff
				.getContextualMoleculesOnlyIn2().size());
		assertEquals("molecules only in 2", 1, diff
				.getTerminalMoleculesOnlyIn2().size());
	}

	public void testFgNodesAllwayReplaces() {
		Model model1 = createFgNodeTestModel1(); // ModelFactory.createDefaultModel();
		Model model2 = createFgNodeTestModel2();
		ReferenceGroundedDecomposition dec1 = new ReferenceGroundedDecompositionImpl2(
				JenaUtil
						.getGraphFromModel(model1, true));
		ReferenceGroundedDecomposition dec2 = new ReferenceGroundedDecompositionImpl2(
				JenaUtil
						.getGraphFromModel(model2, true));
		MoleculeDiff diff = new MoleculeDiffImpl(dec1, dec2, true);
		diff.print(new PrintWriter(System.out, true));
		assertEquals("cross-graph nodset", 1, diff.getCrossGraphFgNodes()
				.size());
		assertEquals("fgnodes only in 1", 0, diff.getFgNodesOnlyIn1().size());
		assertEquals("fgnodes only in 2", 1, diff.getFgNodesOnlyIn2().size());
		assertEquals("molecules only in 1", 0, diff
				.getContextualMoleculesOnlyIn1().size());
	}

	/*
	 * _:a foaf:homepage <http://a> _:a foaf:mbox <mailto:a> _:b
	 * foaf:isPrimaryTopicOf <http://b>
	 */
	/**
	 * @return
	 */
	private Model createFgNodeTestModel2() {
		Model result = ModelFactory.createDefaultModel();
		Resource a = result.createResource();
		Resource b = result.createResource();
		Resource c = result.createResource();
		Resource d = result.createResource();
		d.addProperty(FOAF.mbox, result.createResource("mailto:d"));
		a.addProperty(FOAF.homepage, result.createResource("http://a"));
		a.addProperty(FOAF.mbox, result.createResource("mailto:a"));
		b.addProperty(FOAF.isPrimaryTopicOf, result.createResource("http://b"));
		c.addProperty(FOAF.mbox, result.createResource("mailto:c"));
		b.addProperty(DC.title, "b");
		b.addProperty(RDFS.seeAlso, result.createResource());
		return result;
	}

	/*
	 * _:a foaf:isPrimaryTopicOf <http://b> _:a foaf:homepage <http://a> _:b
	 * foaf:mbox <mailto:a>
	 */
	/**
	 * @return
	 */
	private Model createFgNodeTestModel1() {
		Model result = ModelFactory.createDefaultModel();
		Resource a = result.createResource();
		Resource b = result.createResource();
		Resource d = result.createResource();
		d.addProperty(FOAF.mbox, result.createResource("mailto:d"));
		a.addProperty(FOAF.homepage, result.createResource("http://a"));
		b.addProperty(FOAF.mbox, result.createResource("mailto:a"));
		a.addProperty(FOAF.isPrimaryTopicOf, result.createResource("http://b"));
		a.addProperty(DC.title, "b");
		a.addProperty(RDFS.seeAlso, result.createResource());
		return result;
	}

	public void testMergedSplittedFgNodes() {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(getClass().getResource("splitted-fg-node.n3").toString(), "N3");
		m2.read(getClass().getResource("merged-fg-node.n3").toString(), "N3");
		MoleculeDiff diff = new MoleculeDiffImpl(m1, m2, true);
		diff.print(new PrintWriter(System.out, true));
		assertEquals("molecules only in 1", 0, diff
				.getContextualMoleculesOnlyIn1().size());
		assertEquals("t-molecules only in 1", 2, diff
				.getTerminalMoleculesOnlyIn1().size());
		assertEquals("molecules only in 2", 1, diff
				.getTerminalMoleculesOnlyIn2().size());
	}

	public void testJustOneMoreIFP() {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(getClass().getResource("person.n3").toString(), "N3");
		m2
				.read(
						getClass().getResource("person-plus-1-ifp.n3")
								.toString(), "N3");
		MoleculeDiff diff = new MoleculeDiffImpl(m1, m2, true);
		diff.print(new PrintWriter(System.out, true));
		assertEquals("molecules only in 1", 0, diff
				.getTerminalMoleculesOnlyIn1().size());
		assertEquals("molecules only in 2", 0, diff
				.getTerminalMoleculesOnlyIn2().size());
	}

	public void testPlusDistinctPerson() {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(getClass().getResource("person.n3").toString(), "N3");
		m2.read(getClass().getResource("person-plus-another-person.n3")
				.toString(), "N3");
		MoleculeDiff diff = new MoleculeDiffImpl(m1, m2, true);
		diff.print(new PrintWriter(System.out, true));
		assertEquals("terminal molecules only in 1", 0, diff
				.getTerminalMoleculesOnlyIn1().size());
		assertEquals("terminal molecules only in 2", 1, diff
				.getTerminalMoleculesOnlyIn2().size());
		assertEquals("used common fg-nodes", 0, diff
				.getCommonFgNodesInDiffMolecules().size());
	}

	public void testCross() {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(getClass().getResource("splitted-fg-node.n3").toString(), "N3");
		/*
		 * m2.read(DiffSerializer.class.getResource("../test/merged-fg-node.n3")
		 * .toString(), "N3");
		 */
		m2
				.read(
						getClass().getResource("person-plus-1-ifp.n3")
								.toString(), "N3");
		MoleculeDiff diff = new MoleculeDiffImpl(m1, m2, true);
		diff.print(new PrintWriter(System.out, true));
		assertEquals("molecules only in 1", 0, diff
				.getContextualMoleculesOnlyIn1().size());
		assertEquals("molecules only in 2", 0, diff
				.getContextualMoleculesOnlyIn2().size());
	}
}
