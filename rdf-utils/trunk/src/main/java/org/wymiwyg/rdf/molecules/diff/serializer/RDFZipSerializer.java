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
package org.wymiwyg.rdf.molecules.diff.serializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.wymiwyg.commons.util.MalformedURIException;
import org.wymiwyg.commons.util.URI;
import org.wymiwyg.commons.util.Util;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.NamedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.NaturalizedGraph;
import org.wymiwyg.rdf.graphs.impl.GraphUtil;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.SourceNodeNotFoundException;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.diff.CrossGraphFgNode;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiffImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class RDFZipSerializer implements DiffSerializer {

	/**
	 * @throws IOException
	 * 
	 */
	public RDFZipSerializer() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.serializer.DiffSerializer#serialize(org.wymiwyg.rdf.molecules.diff.MoleculeDiff,
	 *      java.io.OutputStream)
	 */
	public void serialize(MoleculeDiff diff, OutputStream out)
			throws IOException {
		/*
		 * if ((diff.getContextualMoleculesOnlyIn1().size() == 0) && (diff
		 * .getContextualMoleculesOnlyIn2().size() == 0) && ( diff
		 * .getTerminalMoleculesOnlyIn1().size() == 0) && ( diff
		 * .getTerminalMoleculesOnlyIn2().size() == 0) && ( diff
		 * .getFgNodesOnlyIn1().size() == 0) && (
		 * diff.getFgNodesOnlyIn2().size() == 0) && ( diff
		 * .getCrossGraphFgNodes().size() == 0)) { return; }
		 */
		ZipOutputStream zipOut = new ZipOutputStream(out);
		zipOut.setComment("A zipped molecule-diff");
		ReferencingNaturalizer naturalizer = new ReferencingNaturalizer(diff);
		// cg-fg nodes first so they components are independent of used-commons:
		// wrong, as fg-nodes appear as part of cg-fg node which are just
		// referenced
		// writeFgNodes(diff.getCrossGraphFgNodes(), naturalizer, zipOut);
		//naturalizer.setCurrentGraphLabel("used-common");
		writeFgNodes(diff.getCommonFgNodesInDiffMolecules(), naturalizer,
				zipOut);
		//naturalizer.setCurrentGraphLabel("");
		writeFgNodes(diff.getCrossGraphFgNodes(), naturalizer, zipOut);
		//naturalizer.setCurrentGraphLabel("onlyIn1");
		writeContextualMolecules(diff.getContextualMoleculesOnlyIn1(),
				"onlyIn1", naturalizer, zipOut);
		writeTerminalMolecules(diff.getTerminalMoleculesOnlyIn1(), "onlyIn1",
				naturalizer, zipOut);
		writeFgNodes(diff.getFgNodesOnlyIn1(), naturalizer, zipOut);
		//naturalizer.setCurrentGraphLabel("onlyIn2");
		writeContextualMolecules(diff.getContextualMoleculesOnlyIn2(),
				"onlyIn2", naturalizer, zipOut);
		writeTerminalMolecules(diff.getTerminalMoleculesOnlyIn2(), "onlyIn2",
				naturalizer, zipOut);
		writeFgNodes(diff.getFgNodesOnlyIn2(), naturalizer, zipOut);

		zipOut.putNextEntry(new ZipEntry("diff-readme.txt"));
		zipOut.write("this is a diff of two rdf-graphs\n".getBytes());
		zipOut.flush();
		zipOut.finish();
	}

	/**
	 * @param fgNodesOnlyIn1
	 * @param string
	 * @param naturalizer
	 * @param zipOut
	 * @throws IOException
	 */
	private void writeFgNodes(Set<? extends FunctionallyGroundedNode> fgNodes,
			ReferencingNaturalizer naturalizer, ZipOutputStream zipOut)
			throws IOException {
		for (FunctionallyGroundedNode fgNode : fgNodes) {
			NamedNode describingModel = naturalizer.getGroundedIn(fgNode);
			writeFgNode(fgNode, describingModel, naturalizer, zipOut);
		}
	}

	/**
	 * @param fgNode
	 * @param url
	 * @param naturalizer
	 * @param zipOut
	 * @throws IOException
	 */
	private void writeFgNode(FunctionallyGroundedNode fgNode,
			NamedNode namedNode, ReferencingNaturalizer naturalizer,
			ZipOutputStream zipOut) throws IOException {
		String relativePath;
		try {
			relativePath = new URI(ReferencingNaturalizer.rootURL).relativize(
					namedNode.getURIRef(), URI.SAMEDOCUMENT | URI.ABSOLUTE
							| URI.RELATIVE | URI.PARENT);
		} catch (MalformedURIException e) {
			throw new RuntimeException(e);
		}
		// System.out.println("writing " + fgNode + " to " +
		// namedNode.getURIRef());
		if (fgNode instanceof CrossGraphFgNode) {
			CrossGraphFgNode crossGraphFgNode = (CrossGraphFgNode) fgNode;
			//naturalizer.setCurrentGraphLabel("onlyIn1");
			// naturalizer.setCurrentGraphLabel(relativePath.substring("fgNodes/"
			// .length())
			// + "onlyIn1");
			for (FunctionallyGroundedNode node : crossGraphFgNode.getNodesIn1()) {
				writeFgNode(node, naturalizer.getGroundedIn(node), naturalizer,
						zipOut);
			}
			//naturalizer.setCurrentGraphLabel("onlyIn2");
			/*
			 * naturalizer.setCurrentGraphLabel(relativePath.substring("fgNodes/"
			 * .length()) + "onlyIn2");
			 */
			for (FunctionallyGroundedNode node : crossGraphFgNode.getNodesIn2()) {
				writeFgNode(node, naturalizer.getGroundedIn(node), naturalizer,
						zipOut);
			}
		} else {
			for (NonTerminalMolecule molecule : fgNode.getGroundingMolecules()) {
				StringBuffer moleculePath = new StringBuffer(relativePath);
				moleculePath.append(Util.createRandomString(8));
				moleculePath.append(".rdf");
				ZipEntry entry = new ZipEntry(moleculePath.toString());
				zipOut.putNextEntry(entry);
				Graph graph = new SimpleGraph();
				Node naturalNode = naturalizer.naturalize(fgNode, graph);
				Graph addition;
				try {
					addition = new GraphUtil<Graph>().replaceNode(molecule, fgNode,
							naturalNode, new SimpleGraph());
				} catch (SourceNodeNotFoundException e) {
					// this is in fact the normal case of non-self-referencing
					// fg-nodes (x mbox [mbox "foo"]. x mbox "foo")
					addition = molecule;
				}
				try {
					addition = new GraphUtil<Graph>().replaceNode(addition,
							NonTerminalMolecule.GROUNDED_NODE, naturalNode, new SimpleGraph());
				} catch (SourceNodeNotFoundException e) {
					// this is a bit strange ...
					throw new RuntimeException(e);
				}
				graph.addAll(addition);
				/*
				 * graph.addAll(molecule); graph.add(new
				 * TripleImp(naturalizer.naturalize(fgNode, null), new
				 * PropertyNodeImpl(MODELDIFF.functionallyGroundedIn .getURI()),
				 * namedNode));
				 */

				JenaUtil.getModelFromGraph(
						new NaturalizedGraph(graph, naturalizer)).write(
						zipOut,
						"RDF/XML",
						ReferencingNaturalizer.rootURL
								+ moleculePath.toString());
			}
		}

	}

	private void writeTerminalMolecules(Set<TerminalMolecule> molecules,
			String directoryName, ReferencingNaturalizer naturalizer,
			ZipOutputStream zipOut) throws IOException {
		writeMolecules(molecules, directoryName, naturalizer, zipOut,
				"terminal-molecules/");
	}

	private void writeContextualMolecules(
			Set<MaximumContextualMolecule> molecules, String directoryName,
			ReferencingNaturalizer naturalizer, ZipOutputStream zipOut)
			throws IOException {
		writeMolecules(molecules, directoryName, naturalizer, zipOut,
				"contextual-molecules/");
	}

	/**
	 * @param molecules
	 * @param naturalizer
	 * @param string
	 * @param zipOut
	 * @throws IOException
	 */
	private void writeMolecules(Set<? extends Molecule> molecules,
			String modelLabel, ReferencingNaturalizer naturalizer,
			ZipOutputStream zipOut, String directoryName) throws IOException {
		int counter = 0;
		for (Iterator<? extends Molecule> iter = molecules.iterator(); iter
				.hasNext();) {
			counter++;
			Molecule current = iter.next();
			StringBuffer fileName = new StringBuffer(directoryName);
			fileName.append(modelLabel);
			/*
			 * if (current instanceof ContextualMolecule) {
			 * fileName.append("/contextual-"); } else
			 */{
				fileName.append("/");// terminal-");
			}
			fileName.append(counter);
			fileName.append(".rdf");

			ZipEntry entry = new ZipEntry(fileName.toString());
			zipOut.putNextEntry(entry);
			JenaUtil.getModelFromGraph(
					new NaturalizedGraph(current, naturalizer)).write(zipOut,
					"RDF/XML",
					ReferencingNaturalizer.rootURL + fileName.toString());
		}
	}

	public static void main(String[] args) throws IOException {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(RDFZipSerializer.class.getResource(
				"../test/splitted-fg-node.n3").toString(), "N3");

		/*
		 * m2.read(DiffSerializer.class.getResource("../test/merged-fg-node.n3")
		 * .toString(), "N3");
		 * 
		 * m2.read(DiffSerializer.class
		 * .getResource("../test/person-plus-1-ifp.n3").toString(), "N3");
		 */

		m2.read(RDFZipSerializer.class.getResource("../test/test2.rdf")
				.toString(), "RDF/XML");
		MoleculeDiff diff = new MoleculeDiffImpl(m1, m2, true);
		System.out.println(diff);

		OutputStream out = new FileOutputStream("/home/reto/test.zip");
		new RDFZipSerializer().serialize(diff, out);
		out.close();
		MoleculeDiff reDeserialised = new MoleculeDiffDeserialized(new File(
				"/home/reto/test.zip"));

		System.out.println();
		System.out.println(reDeserialised);

		Model m2reconstructed = JenaUtil
				.getModelFromGraph(ReferenceGroundedUtil
						.reconstructGraph(reDeserialised
								.patch(new ReferenceGroundedDecompositionImpl(
										new ModelReferencingDecompositionImpl(
												JenaUtil.getGraphFromModel(m1,
														true))))));
		System.out.println(m2reconstructed.isIsomorphicWith(m2));
		System.out.println("m1");
		m1.write(System.out);
		System.out.println("m2");
		m2.write(System.out);
		System.out.println("m2reconstructed");
		m2reconstructed.write(System.out);
		System.out.println();
		if (!m2reconstructed.isIsomorphicWith(m2)) {
			System.out.println(new MoleculeDiffImpl(m2, m2reconstructed, true));
		}

	}

}
