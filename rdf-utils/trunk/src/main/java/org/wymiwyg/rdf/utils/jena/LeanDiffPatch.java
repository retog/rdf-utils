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
package org.wymiwyg.rdf.utils.jena;

import java.io.File;
import java.io.IOException;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.impl.AnonymizedGraph;
import org.wymiwyg.rdf.graphs.impl.DeAnonymizedGraph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.leanifier.MoleculeBasedLeanifier;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiffImpl;
import org.wymiwyg.rdf.molecules.diff.serializer.MoleculeDiffDeserialized;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;

import com.hp.hpl.jena.rdf.model.Model;


/** Some static methods designed to give easy access to diff, patch and leanify for jena users
 * 
 * @author reto
 * 
 */
public class LeanDiffPatch {

	private static Model ontology;

	private static boolean useDefaultOntology = true;

	public static MoleculeDiff getDiff(Model m1, Model m2) {
		return new MoleculeDiffImpl(
				(new ReferenceGroundedDecompositionImpl(
						new AnonymizedGraph(JenaUtil
								.getGraphFromModel(m1, ontology,
										useDefaultOntology)))),
				(new ReferenceGroundedDecompositionImpl(
						new AnonymizedGraph(JenaUtil
								.getGraphFromModel(m2, ontology,
										useDefaultOntology)))));
	}

	public static Model patch(Model m, MoleculeDiff diff) {
		ReferenceGroundedDecomposition resultDec = diff.patch(new ReferenceGroundedDecompositionImpl(
						new AnonymizedGraph(JenaUtil
								.getGraphFromModel(m, ontology,
										useDefaultOntology))));
		return JenaUtil.getModelFromGraph(new DeAnonymizedGraph(ReferenceGroundedUtil.reconstructGraph(resultDec)));
	}
	
	public static void serializeDiff(MoleculeDiff diff, File file) throws IOException {
		diff.serialize(file);
	}
	
	public static MoleculeDiff deserializeDiff(File file) throws IOException {
		return new MoleculeDiffDeserialized(file);
	}
	
	public static Model leanify(Model m) {
		Graph source = JenaUtil.getGraphFromModel(m, ontology,
				useDefaultOntology);
		Graph result = MoleculeBasedLeanifier.getLeanVersionOf(source);
		return JenaUtil.getModelFromGraph(result);
	}

	public static Model getOntology() {
		return ontology;
	}

	public static void setOntology(Model ontology) {
		LeanDiffPatch.ontology = ontology;
	}

	public static boolean isUseDefaultOntology() {
		return useDefaultOntology;
	}

	public static void setUseDefaultOntology(boolean useDefaultOntology) {
		LeanDiffPatch.useDefaultOntology = useDefaultOntology;
	}
	
	
}
