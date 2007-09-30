package org.wymiwyg.rdf.molecules.impl;

import java.util.HashSet;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.molecules.ContextualMolecule;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

public class UnionContextualMolecule extends UnionMolecule implements Molecule,
		ContextualMolecule {

	private Node[] ubngn;

	public UnionContextualMolecule(Molecule molecule1,
			NonTerminalMolecule molecule2, Node[] ubngn) {
		super(molecule1, molecule2);
		this.ubngn = ubngn;
	}

	public UnionContextualMolecule(ContextualMolecule molecule1,
			ContextualMolecule molecule2) {
		super(molecule1, molecule2);
		Set<Node> newUbngnSet = new HashSet<Node>();
		Node[] ubngn1 = molecule1.getUsedButNotGroundedNodes();
		for (int i = 0; i < ubngn1.length; i++) {
			newUbngnSet.add(ubngn1[i]);
		}
		Node[] ubngn2 = molecule2.getUsedButNotGroundedNodes();
		for (int i = 0; i < ubngn2.length; i++) {
			newUbngnSet.add(ubngn2[i]);
		}
		this.ubngn = (Node[]) newUbngnSet.toArray(new Node[newUbngnSet
				.size()]);
	}


	public Node[] getUsedButNotGroundedNodes() {
		return ubngn;
	}

	public boolean usesButDoesntGround(Node afgn) {
		for (int i = 0; i < ubngn.length; i++) {
			if (ubngn[i].equals(afgn)) {
				return true;
			}

		}
		return false;
	}

	/*public Molecule getUnionWith(Molecule molecule) {
		if (molecule instanceof NonTerminalMolecule) {
			NonTerminalMolecule ntMolecule = (NonTerminalMolecule) molecule;
			return ntMolecule.getUnionWith(this);
		}
		if (molecule instanceof ContextualMolecule) {
			return new UnionContextualMolecule(this, (ContextualMolecule) molecule);
		}
		throw new RuntimeException("Unsupported union");
	}*/

}
