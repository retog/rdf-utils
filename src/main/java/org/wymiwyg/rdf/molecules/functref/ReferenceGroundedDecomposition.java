package org.wymiwyg.rdf.molecules.functref;

import java.util.Set;

import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;

public interface ReferenceGroundedDecomposition {
	public Set<TerminalMolecule> getTerminalMolecules();
	public Set<FunctionallyGroundedNode> getFunctionallyGroundedNodes();
	public Set<MaximumContextualMolecule> getContextualMolecules();

}
