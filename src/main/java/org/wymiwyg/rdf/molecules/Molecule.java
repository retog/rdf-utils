package org.wymiwyg.rdf.molecules;

import org.wymiwyg.rdf.graphs.Graph;





/** A set of triples that cannot be decomposed without reducing its meaning. A Molecule is not necessarly 
 * a Graph, as the identity may be determined by the id of b-nodes, this is in a model-referencing decomposition
 * 
 * @author reto
 *
 */
//An alternative would be to have this be a Graph, and have "ModelGroundedNodes" as grounded node in model-referencing decompositions
public interface Molecule extends Graph {

	//public abstract Molecule getUnionWith(Molecule graph);
}