package org.wymiwyg.rdf.molecules.model.modelref;

import java.util.Set;

import org.wymiwyg.rdf.graphs.Triple;

public interface ModelReferencingDecomposition {
	public Set<Triple> getNaturallyGroundedTriples();
	public Set<NonTerminalTriple> getNonTerminalTriples();
	public Set<CandidateNonTerminalPartTriple> getCandidateNonTerminalPartTriples();
	public Set<NonFunctionalModelReferencingTriple> getNonFunctionalTriples(); // was: public Set<ContextualMolecule> getContextualMolecules();

}
