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
package org.wymiwyg.rdf.molecules.diff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.commons.util.io.IndentWriter;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.diff.serializer.DiffSerializer;
import org.wymiwyg.rdf.molecules.diff.serializer.RDFZipSerializer;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;

/**
 * @author reto
 *
 */
public abstract class MoleculeDiffBase implements MoleculeDiff {

	private DiffSerializer serializer = new RDFZipSerializer();

	public void print(PrintWriter out) {
		Set<MaximumContextualMolecule> contextualMoleculesOnlyIn1 = getContextualMoleculesOnlyIn1();

		Set<MaximumContextualMolecule> contextualMoleculesOnlyIn2 = getContextualMoleculesOnlyIn2();
		
		Set<TerminalMolecule >terminalMoleculesOnlyIn1= getTerminalMoleculesOnlyIn1();
		
		Set<TerminalMolecule >terminalMoleculesOnlyIn2= getTerminalMoleculesOnlyIn2();

		Set<FunctionallyGroundedNode> commonFgNodes = getCommonFgNodesInDiffMolecules();

		Set<CrossGraphFgNode> crossGraphFgNodes = getCrossGraphFgNodes();

		Set<FunctionallyGroundedNode> fgNodesOnlyIn1 = getFgNodesOnlyIn1();

		Set<FunctionallyGroundedNode> fgNodesOnlyIn2 = getFgNodesOnlyIn2();

		//Set<Molecule> commonMolecules = getCommonMolecules();


		out.println();
		out.println("Cross-Graph FG-Nodes: " + crossGraphFgNodes.size());
		for (Iterator<CrossGraphFgNode> iter = crossGraphFgNodes.iterator(); iter
				.hasNext();) {
			CrossGraphFgNode current = iter.next();
			out.print("-");
			PrintWriter iOut = new PrintWriter(new IndentWriter(out));
			current.print(iOut);
			iOut.flush();
			// out.println();
		}
		out.println();
		out.println("Functionally grounded nodes only in 1: "
				+ fgNodesOnlyIn1.size());
		for (Iterator<FunctionallyGroundedNode> iter = fgNodesOnlyIn1
				.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode current = iter.next();
			out.print("-");
			out.println(current);
		}
		out.println();
		out.println("Functionally grounded nodes only in 2: "
				+ fgNodesOnlyIn2.size());
		for (Iterator<FunctionallyGroundedNode> iter = fgNodesOnlyIn2
				.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode current = iter.next();
			out.print("-");
			out.println(current);
		}
		out.println();
		out.println("Common functionally grounded nodes used in diff-molecules/fg-nodes: "
				+ commonFgNodes.size());
		for (Iterator<FunctionallyGroundedNode> iter = commonFgNodes
				.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode current = iter.next();
			out.print("-");
			out.println(current);
		}

		out.println("Maximum contextual molecules only in 1: " + contextualMoleculesOnlyIn1.size());
		for (Iterator<MaximumContextualMolecule> iter = contextualMoleculesOnlyIn1.iterator(); iter
				.hasNext();) {
			Molecule current = iter.next();
			out.print("-");
			out.println(current);
		}
		out.println("Maximum contextual molecules only in 2: " + contextualMoleculesOnlyIn2.size());
		for (Iterator<MaximumContextualMolecule> iter = contextualMoleculesOnlyIn2.iterator(); iter
				.hasNext();) {
			Molecule current = iter.next();
			out.print("-");
			out.println(current);
		}
		out.println("Terminal molecules only in 1: " + terminalMoleculesOnlyIn1.size());
		for (Iterator<TerminalMolecule> iter = terminalMoleculesOnlyIn1.iterator(); iter
				.hasNext();) {
			Molecule current = iter.next();
			out.print("-");
			out.println(current);
		}
		out.println("Terminal molecules only in 2: " + terminalMoleculesOnlyIn2.size());
		for (Iterator<TerminalMolecule> iter = terminalMoleculesOnlyIn2.iterator(); iter
				.hasNext();) {
			Molecule current = iter.next();
			out.print("-");
			out.println(current);
		}
	}

	public String toString() {
		StringWriter swriter = new StringWriter();
		PrintWriter pwriter = new PrintWriter(swriter);
		print(pwriter);
		pwriter.flush();
		return swriter.toString();
	}

	public ReferenceGroundedDecomposition patch(final ReferenceGroundedDecomposition dec) {
		return new PatchedDecomposition(dec, this);
	}

	public void setDiffSerializer(DiffSerializer serializer) {
		this.serializer  = serializer;
	}
	
	public void serialize(OutputStream out) throws IOException {
		serializer.serialize(this, out);
	}

	public void serialize(File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		serialize(out);
		out.close();
		
	}

}
