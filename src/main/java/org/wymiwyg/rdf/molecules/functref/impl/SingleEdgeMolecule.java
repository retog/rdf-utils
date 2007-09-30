package org.wymiwyg.rdf.molecules.functref.impl;

import java.util.Collections;
import java.util.Iterator;

import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.impl.AbstractMolecule;

public abstract class SingleEdgeMolecule extends AbstractMolecule implements Molecule  {

	protected Triple statement;

	//protected Model ontology;

	protected SingleEdgeMolecule(Triple statement) {
		this.statement = statement;
		markFinalized();
		//this.ontology = ontology;
	}

	public Iterator<Triple> iterator() {
		return Collections.singleton(statement).iterator();
	}

	public int size() {
		return 1;
	}
	

}
