package org.wymiwyg.rdf.molecules.model.modelref.implgraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.NaturallyGroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionalPropertyNode;
import org.wymiwyg.rdf.graphs.fgnodes.InverseFunctionalPropertyNode;
import org.wymiwyg.rdf.molecules.model.modelref.CandidateNonTerminalPartTriple;
import org.wymiwyg.rdf.molecules.model.modelref.ModelRefGroundedNode;
import org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition;
import org.wymiwyg.rdf.molecules.model.modelref.NonFunctionalModelReferencingTriple;
import org.wymiwyg.rdf.molecules.model.modelref.NonTerminalTriple;
import org.wymiwyg.rdf.molecules.model.modelref.impl.ModelRefGroundedNodeImpl;

public class ModelReferencingDecompositionImpl implements
		ModelReferencingDecomposition {
	private final static Log log = LogFactory.getLog(ModelReferencingDecompositionImpl.class);
	
	private Set<Triple> naturallyGroundedTriples = new HashSet<Triple>();
	private Set<NonTerminalTriple> nonTerminalTriples = new HashSet<NonTerminalTriple>();
	private Set<CandidateNonTerminalPartTriple> candidateNonTerminalPartTriples = new HashSet<CandidateNonTerminalPartTriple>();
	private Set<NonFunctionalModelReferencingTriple> nonFunctionalModelReferencingTriples = new HashSet<NonFunctionalModelReferencingTriple>();
	//note that the identity of the the molecules in contextualMolecules depends on anon-node id
	//private Set contextualMolecules = new HashSet();
	//private Set candidateNTPartsMolecules = new HashSet();
	
	public ModelReferencingDecompositionImpl(Graph graph) {
		//maximal decomposition
		prepareMoleculesForEdges(graph);
		log.info("ModelRefDecomposition created");
		//log.info("Prepared "+moleculesSet.size()+" molecules, of which "+terminalMolecules.size()+" terminal");
		//creation of compount NT-Molecules
		//while (generateAllNT()) {
		//	log.debug("Generated non terminal molecules. Total: "+nonTerminalTriples.size());
		//}
	}
	
	private void prepareMoleculesForEdges(org.wymiwyg.rdf.graphs.Graph graph) {
		for (Triple triple : graph) {
			Node subject = triple.getSubject();
		    Node object = triple.getObject();
			boolean subjectNG = subject instanceof NaturallyGroundedNode;
			boolean objectNG = object instanceof NaturallyGroundedNode;
			boolean reflexive = subject.equals(object);
			PropertyNode predicate = triple.getPredicate();
			if (subjectNG) {
				if (objectNG) {
					naturallyGroundedTriples.add(triple);
					continue;
				} else {
					ModelRefGroundedNode modelRefGroundedObject = new ModelRefGroundedNodeImpl(object);
					if ((predicate instanceof FunctionalPropertyNode) && ! reflexive) {
						nonTerminalTriples.add(new FunctionalNonTerminalTriple(subject, predicate, modelRefGroundedObject));
						continue;
					} else {
						nonFunctionalModelReferencingTriples.add(new NonFunctionalModelReferencingTripleImpl(subject, predicate, modelRefGroundedObject));
						continue;
					}
				}
			} else {
				ModelRefGroundedNode modelRefGroundedSubject = new ModelRefGroundedNodeImpl(subject);
				if (objectNG) {
					// subject anon object naturaly grounded
					if ((predicate instanceof InverseFunctionalPropertyNode) && ! reflexive)  {
						nonTerminalTriples.add(new InverseFunctionalNonTerminalTriple(modelRefGroundedSubject, predicate, object));
						continue;
					} else {
						nonFunctionalModelReferencingTriples.add(new NonFunctionalModelReferencingTripleImpl(modelRefGroundedSubject, predicate, object));
						continue;
					}			
				} else {
					//two anons
					ModelRefGroundedNode modelRefGroundedObject = new ModelRefGroundedNodeImpl(object);
					//making CandidateNonTerminalPartMolecule if Functional or InverseFunctional, so multiple of them can be possibly
					//be joined to a NonTerminalMolecule
					if (!(predicate instanceof FunctionalPropertyNode) || reflexive) {
						if (!(predicate instanceof InverseFunctionalPropertyNode) || reflexive) {
							nonFunctionalModelReferencingTriples.add(new NonFunctionalModelReferencingTripleImpl(modelRefGroundedSubject, predicate, modelRefGroundedObject));
							continue;
						} else {
							candidateNonTerminalPartTriples.add(new InverseFunctionalNonTerminalPartCandidateTriple(modelRefGroundedSubject, predicate, modelRefGroundedObject));
							continue;
						}
					} else {
						if ((predicate instanceof InverseFunctionalPropertyNode) && ! reflexive) {
							//TODO implement
							//not sure if add both type of candidate is enough or should have special class
							throw new RuntimeException("impl!!!");
						} else {
							candidateNonTerminalPartTriples.add(new FunctionalNonTerminalPartCandidateTriple(modelRefGroundedSubject, predicate, modelRefGroundedObject));
							continue;
						}
					}
				}
			}
		}
	}
	

//	private boolean generateAllNT() {
//		for (Iterator iter = candidateNTPartsMolecules.iterator(); iter
//				.hasNext();) {
//			CandidateNonTerminalPartMolecule currentCan = (CandidateNonTerminalPartMolecule) iter
//					.next();
//			Node grounding = currentCan.getGroundingNode();
//			for (Iterator iterator = nonTerminalMolecules.iterator(); iterator.hasNext();) {
//				NonTerminalMolecule currentNT = (NonTerminalMolecule) iterator.next();
//				if (currentNT.getAfgn().equals(grounding)) {
//					//NonTerminalMolecule newNT = new UnionNonTerminalMolecule(currentNT, currentCan);
//					NonTerminalMolecule newNT = new SimpleNonTerminalMolecule(currentCan.getAfgn());
//					newNT.addAll(currentCan);
//					nonTerminalMolecules.add(newNT);
//					
//					candidateNTPartsMolecules.remove(currentCan);
//					contextualMolecules.remove(currentCan);
//					return true;
//				}
//			}
//		}
//		return false;
//	}



	public Set<Triple> getNaturallyGroundedTriples() {
		return naturallyGroundedTriples;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition#getNonTerminalTriples()
	 */
	public Set<NonTerminalTriple> getNonTerminalTriples() {
		return Collections.unmodifiableSet(nonTerminalTriples);
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition#getCandidateNonTerminalPartTriples()
	 */
	public Set<CandidateNonTerminalPartTriple> getCandidateNonTerminalPartTriples() {
		return Collections.unmodifiableSet(candidateNonTerminalPartTriples);
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.model.modelref.ModelReferencingDecomposition#getNonFunctionalTriples()
	 */
	public Set<NonFunctionalModelReferencingTriple> getNonFunctionalTriples() {
		return Collections.unmodifiableSet(nonFunctionalModelReferencingTriples);
	}

}
