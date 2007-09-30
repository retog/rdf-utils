package org.wymiwyg.rdf.molecules.impl;

import java.util.Iterator;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.matcher.GraphMatcher;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;


public class UnionTerminalMolecule extends UnionMolecule implements TerminalMolecule {

	
	public UnionTerminalMolecule(Molecule molecule1, NonTerminalMolecule molecule2) {
		super(molecule1, molecule2);
	}

	public Molecule getUnionWith(Molecule molecule) {
		throw new RuntimeException("The union of terminal molecules is nor a molecule");
	}

}
