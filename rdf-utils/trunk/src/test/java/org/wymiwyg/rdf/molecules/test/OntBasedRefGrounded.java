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
package org.wymiwyg.rdf.molecules.test;

import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition;
import org.wymiwyg.rdf.molecules.model.modelref.impljena.JenaModelReferencingDecompositionImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 *
 */
public class OntBasedRefGrounded {

	/**
	 * 
	 */
	public OntBasedRefGrounded() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new OntBasedRefGrounded().testStuff();
	}
	
	public void testStuff() {
		Model model = ModelFactory.createDefaultModel();
		//model.read(getClass().getResourceAsStream("knobot-page.rdf"), "");
		model.read(getClass().getResource("small-foaf.n3").toString(), "N3");
		Model ontology = ModelFactory.createDefaultModel();
		ontology.read(getClass().getResourceAsStream("/org/wymiwyg/rdf/graphs/fgnodes/default-ontology.rdf"), "");
		ModelReferencingDecomposition modelDec = new JenaModelReferencingDecompositionImpl(model, ontology);
		ReferenceGroundedDecomposition refDec = new ReferenceGroundedDecompositionImpl(modelDec);
		System.out.println("Terminal molecules:");
		Set terminalMolecules = refDec.getTerminalMolecules();
		for (Iterator iter = terminalMolecules.iterator(); iter.hasNext();) {
			TerminalMolecule current = (TerminalMolecule) iter.next();
			System.out.println(current);
			System.out.println("-----------------");
		}
		System.out.println("Contextual molecules:");
		Set contextualMolecules = refDec.getContextualMolecules();
		for (Iterator iter = contextualMolecules.iterator(); iter.hasNext();) {
			MaximumContextualMolecule current = (MaximumContextualMolecule) iter.next();
			System.out.println(current);
			System.out.println("-----------------");
		}
		
	}

}
