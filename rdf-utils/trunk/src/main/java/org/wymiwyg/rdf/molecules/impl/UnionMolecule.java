package org.wymiwyg.rdf.molecules.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.molecules.Molecule;

public abstract class UnionMolecule extends AbstractMolecule implements Molecule {

	protected Molecule molecule1;
	protected Molecule molecule2;

	public UnionMolecule(Molecule molecule1, Molecule molecule2) {
		super();
		this.molecule1 = molecule1;
		this.molecule2 = molecule2;
	}


	private Set<Triple> getStatements() {
		//could performance be increased as we know there are no duplicates?
		Set<Triple> resultSet = new HashSet<Triple>();
		resultSet.addAll(molecule1);
		resultSet.addAll(molecule2);
		return resultSet;
		
		/*Triple[] statements1 = molecule1.getStatements();
		Triple[] statements2 = molecule2.getStatements();
		Triple[] newStatements = new Triple[statements1.length + statements2.length];
		System.arraycopy(statements1,0, newStatements, 0, statements1.length);
		System.arraycopy(statements2,0, newStatements, statements1.length, statements2.length);
		return newStatements;*/
	}




	public Iterator<Triple> iterator() {
		return getStatements().iterator();
	}


	public int size() {
		return getStatements().size();
	}

}
