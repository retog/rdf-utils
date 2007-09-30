package org.wymiwyg.rdf.molecules.functref.impl;

import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;

/** A terminal molecule only uses grounded
nodes and/or functionally grounded BNodes, and all its BNodes are close.
A BNode bn in a molecule n has two states, namely ‘open’ and ‘close’. bn is
said ‘close’ if it is functionally grounded and being used by exact one more
triple in m, otherwise it is ‘open’.*/


public class SETerminalMolecule extends SingleEdgeMolecule implements TerminalMolecule {


	public SETerminalMolecule(Triple triple) {
		super(triple);
	}

	public Molecule getUnionWith(Molecule molecule) {
		throw new RuntimeException("The union of terminal molecules is nor a molecule");
	}


}
