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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Set;

import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.diff.serializer.DiffSerializer;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;

/**
 * @author reto
 *
 */
public interface MoleculeDiff {

	public abstract Set<MaximumContextualMolecule> getContextualMoleculesOnlyIn1();

	public abstract Set<MaximumContextualMolecule> getContextualMoleculesOnlyIn2();
	
	public abstract Set<TerminalMolecule> getTerminalMoleculesOnlyIn1();

	public abstract Set<TerminalMolecule> getTerminalMoleculesOnlyIn2();

	public abstract Set<CrossGraphFgNode> getCrossGraphFgNodes();

	public abstract void print(PrintWriter out);

	//public abstract Set<FunctionallyGroundedNode> getCommonFgNodes();

	public abstract Set<FunctionallyGroundedNode> getFgNodesOnlyIn1();

	public abstract Set<FunctionallyGroundedNode> getFgNodesOnlyIn2();


	/**
	 * @return
	 */
	
	public abstract Set<FunctionallyGroundedNode> getCommonFgNodesInDiffMolecules();
	
	/**
	 * @deprecated use DiffUtil instead
	 */
	@Deprecated
	public abstract ReferenceGroundedDecomposition patch(ReferenceGroundedDecomposition dec);
	//public abstract ReferenceGroundedDecomposition unPatch(ReferenceGroundedDecomposition dec);
	
	public abstract void setDiffSerializer(DiffSerializer serializer);
	
	public abstract void serialize(OutputStream out) throws IOException;

	/**
	 * @param file
	 * @throws IOException 
	 */
	public abstract void serialize(File file) throws IOException;

}