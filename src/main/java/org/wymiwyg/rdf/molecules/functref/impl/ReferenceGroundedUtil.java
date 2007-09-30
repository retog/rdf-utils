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
package org.wymiwyg.rdf.molecules.functref.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.NaturalizedGraph;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.molecules.ContextualMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;

/**
 * @author reto
 *
 */
public class ReferenceGroundedUtil {

	public static Graph reconstructGraph(ReferenceGroundedDecomposition dec) {
		Graph graph = new SimpleGraph();
		for (TerminalMolecule terminalMolecule : dec.getTerminalMolecules()) {
			graph.addAll(terminalMolecule);
		}
		for (ContextualMolecule contextualMolecule : dec.getContextualMolecules()) {
			graph.addAll(contextualMolecule);
		}
		return new NaturalizedGraph(graph, dec.getFunctionallyGroundedNodes());
	}
	
	public static void print(ReferenceGroundedDecomposition dec, Writer writer) throws IOException {
		PrintWriter out = new PrintWriter(writer);	
		out.println("Terminal molecules ("+dec.getTerminalMolecules().size()+") :");
		for (TerminalMolecule terminalMolecule : dec.getTerminalMolecules()) {
			out.println(terminalMolecule.toString());
		}
		out.println("Contextual molecules ("+dec.getContextualMolecules().size()+") :");
		for (ContextualMolecule contextualMolecule : dec.getContextualMolecules()) {
			out.println(contextualMolecule.toString());
		}
		out.println("FG-Nodes ("+dec.getFunctionallyGroundedNodes().size()+") :");
		for (FunctionallyGroundedNode fgNode : dec.getFunctionallyGroundedNodes()) {
			out.println(fgNode.toString());
		}
		out.flush();
		writer.flush();
	}

}
