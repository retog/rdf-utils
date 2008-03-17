package org.wymiwyg.rdf.molecules;

import org.wymiwyg.rdf.graphs.Node;


public interface ContextualMolecule extends Molecule {

	public abstract Node[] getUsedButNotGroundedNodes();


}